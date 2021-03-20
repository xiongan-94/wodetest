package com.atguigu.flink.chapter06;

import com.atguigu.flink.bean.OrderEvent;
import com.atguigu.flink.bean.TxEvent;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 16:04
 */
public class Flink04_Project_Order {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        // 1. 获取订单数据的流
        SingleOutputStreamOperator<OrderEvent> orderStream = env
            .readTextFile("input/OrderLog.csv")
            .map(line -> {
                String[] datas = line.split(",");
                return new OrderEvent(
                    Long.valueOf(datas[0]),
                    datas[1],
                    datas[2],
                    Long.valueOf(datas[3]));

            });

        // 2. 读取交易流
        SingleOutputStreamOperator<TxEvent> txDS = env
            .readTextFile("input/ReceiptLog.csv")
            .map(line -> {
                String[] datas = line.split(",");
                return new TxEvent(datas[0], datas[1], Long.valueOf(datas[2]));
            });

        orderStream
            .connect(txDS)
            .keyBy("txId", "txId")
            .process(new KeyedCoProcessFunction<String, OrderEvent, TxEvent, String>() {

                HashMap<String, OrderEvent> orderMap = new HashMap<>();
                HashMap<String, TxEvent> txMap = new HashMap<>();

                @Override
                public void processElement1(OrderEvent value, Context ctx, Collector<String> out) throws Exception {
                    // 订单相关数据
                    String txId = value.getTxId();
                    if (txMap.containsKey(txId)) {
                        out.collect("订单: " + value + " 对账成功!");
                        txMap.remove(txId);
                    }else{
                        orderMap.put(txId, value);
                    }
                }

                @Override
                public void processElement2(TxEvent value, Context ctx, Collector<String> out) throws Exception {
                    // 交易相关数据
                    String txId = value.getTxId();
                    if (orderMap.containsKey(txId)) {
                        out.collect("订单: " + orderMap.get(txId) + " 对账成功!");
                        // 删除对账成功之后的信息
                        orderMap.remove(txId);
                    } else {
                        // 交易信息到的时候, 订单数据还没有到, 把交易数据存入到txMap中
                        txMap.put(txId, value);
                    }

                }
            })
            .print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
