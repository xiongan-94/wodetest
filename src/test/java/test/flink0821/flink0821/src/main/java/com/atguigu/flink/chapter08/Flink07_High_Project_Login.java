package com.atguigu.flink.chapter08;

import com.atguigu.flink.bean.LoginEvent;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/29 9:20
 */
public class Flink07_High_Project_Login {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        // 创建WatermarkStrategy
        WatermarkStrategy<LoginEvent> wms = WatermarkStrategy
            .<LoginEvent>forMonotonousTimestamps()
            .withTimestampAssigner(new SerializableTimestampAssigner<LoginEvent>() {
                @Override
                public long extractTimestamp(LoginEvent element, long recordTimestamp) {
                    return element.getEventTime();
                }
            });

        env
            .readTextFile("input/LoginLog.csv")
            .map(line -> {
                String[] data = line.split(",");
                return new LoginEvent(Long.valueOf(data[0]),
                                      data[1],
                                      data[2],
                                      Long.parseLong(data[3]) * 1000L);
            })
            .assignTimestampsAndWatermarks(wms)
            .keyBy(LoginEvent::getUserId)
            .process(new KeyedProcessFunction<Long, LoginEvent, String>() {

                private ListState<Long> failTsState;

                @Override
                public void open(Configuration parameters) throws Exception {
                    failTsState = getRuntimeContext().getListState(new ListStateDescriptor<Long>("failTsState", Long.class));
                }

                @Override
                public void processElement(LoginEvent value, Context ctx, Collector<String> out) throws Exception {
                    /*
                    判断两个失败的时间戳是不是相差2s
                    1. 有一个列表存储两个时间戳
                    2. 当列表的长度达到2的时候,进行判断是否恶意
                    3. 让列表的长度用于保持2
                    4. 如果一旦出现成功, 则需要情况list
                    5. 如果里面两个失败的时间间隔大于2, 删除第一个
                     */

                    if ("fail".equals(value.getEventType())) {
                        // 1. 把时间戳存入到状态
                        failTsState.add(value.getEventTime());
                        // 2. 把状态中的时间戳转存到ArrayList中,方便处理
                        ArrayList<Long> tss = new ArrayList<>();
                        for (Long ts : failTsState.get()) {
                            tss.add(ts);
                        }
                        // 3. 判断是否恶意登录
                        if (tss.size() == 2) { // 3.1 表示已经有了两次连续失败
                            long delta = tss.get(1) - tss.get(0);
                            if(delta / 1000 <= 2){
                                out.collect(value.getUserId() + " 正在恶意登录!!!");
                            }else{ // 3.2 两个失败的时间间隔大于2, 把第一个失败去掉
                                tss.remove(0);
                                // 更新状态
                                failTsState.update(tss);
                            }
                        }
                    }else{
                        failTsState.clear();
                    }
                }
            })
            .print();


        env.execute();

    }
}
