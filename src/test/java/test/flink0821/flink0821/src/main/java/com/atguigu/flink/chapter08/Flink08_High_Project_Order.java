package com.atguigu.flink.chapter08;

import com.atguigu.flink.bean.OrderEvent;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/29 14:12
 */
public class Flink08_High_Project_Order {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        WatermarkStrategy<OrderEvent> wms = WatermarkStrategy
            .<OrderEvent>forBoundedOutOfOrderness(Duration.ofSeconds(20))
            .withTimestampAssigner(new SerializableTimestampAssigner<OrderEvent>() {
                @Override
                public long extractTimestamp(OrderEvent element, long recordTimestamp) {
                    return element.getEventTime() * 1000;
                }
            });

        // 1. 获取订单数据的流
        env
            .readTextFile("input/OrderLog.csv")
            .map(line -> {
                String[] datas = line.split(",");
                return new OrderEvent(
                    Long.valueOf(datas[0]),
                    datas[1],
                    datas[2],
                    Long.valueOf(datas[3]));

            })
            .assignTimestampsAndWatermarks(wms)
            .keyBy(OrderEvent::getOrderId)
            .process(new KeyedProcessFunction<Long, OrderEvent, String>() {

                private ValueState<Long> timerTs;
                private ValueState<OrderEvent> payState;
                private ValueState<OrderEvent> createState;

                @Override
                public void open(Configuration parameters) throws Exception {
                    createState = getRuntimeContext()
                        .getState(new ValueStateDescriptor<OrderEvent>("createState", OrderEvent.class));
                    payState = getRuntimeContext()
                        .getState(new ValueStateDescriptor<OrderEvent>("payState", OrderEvent.class));

                    timerTs = getRuntimeContext().getState(new ValueStateDescriptor<Long>("timerTs", Long.class));
                }

                @Override
                public void processElement(OrderEvent value, Context ctx, Collector<String> out) throws Exception {
                    String eventType = value.getEventType();
                    if ("create".equals(eventType)) {
                        // 判断这个订单的支付信息是否已经在状态中
                        if (payState.value() == null) { // 支付信息还没有来
                            //把创建信息保存到自己的状态中
                            createState.update(value);

                        } else { // 支付信息已经来了
                            // 订单创建时间和支付时间有没有超时
                            if (payState.value().getEventTime() - value.getEventTime() < 15 * 60) {
                                // 在正常的时间完成了支付
                                out.collect("订单: " + value.getOrderId() + " 正常支付");
                            } else {
                                // 在超时时间内完成了支付
                                out.collect("订单: " + value.getOrderId() + " 在超时时间后支付, 请检查系统是否有漏洞!!!");
                            }
                        }

                    } else {
                        // 是支付
                        if (createState.value() == null) { // 订单创建信息还没有到
                            payState.update(value);
                        } else {  // 订单的创建信息已经到了
                            if (value.getEventTime() - createState.value().getEventTime() < 15 * 60) {
                                out.collect("订单: " + value.getOrderId() + " 正常支付");
                            } else {
                                out.collect("订单: " + value.getOrderId() + " 在超时时间后支付, 请检查系统是否有漏洞!!!");
                            }
                        }
                    }

                    // 如果一个订单只有create或者只有pay的时候的处理
                    if (timerTs.value() == null) {
                        long ts = value.getEventTime() * 1000 + 20 * 60 * 1000;
                        // 注册定时器
                        ctx.timerService().registerEventTimeTimer(ts);

                        timerTs.update(ts);
                    } else {
                        // 删除定时器
                        ctx.timerService().deleteEventTimeTimer(timerTs.value());
                    }

                }

                @Override
                public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                    //
                    if(payState.value() == null){
                        // pay没有来: 只有订单创建, 没有支付
                        out.collect("订单: " + createState.value().getOrderId() + " 支付超时, 订单自动取消!");
                    }else{
                        // create没有来, 只有支付没有创建
                        out.collect("订单: " + payState.value().getOrderId() + " 检测到有支付信息, 但是没有下单信息, 请检查系统是否有bug!");
                    }
                }
            })
            .print();

        env.execute();

    }
}
// byte short int char string(1.7增加) enum
