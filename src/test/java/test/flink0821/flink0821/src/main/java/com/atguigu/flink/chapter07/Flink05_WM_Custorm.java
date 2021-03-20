package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink05_WM_Custorm {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);

        WatermarkStrategy<WaterSensor> wms =
            new WatermarkStrategy<WaterSensor>() {
                @Override
                public WatermarkGenerator<WaterSensor> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
                    return new MyWMSPeriod(3);
                }
            }.withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                @Override
                public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                    return element.getTs() * 1000;
                }
            });

        env
            .socketTextStream("hadoop162", 9999)
            .map(line -> {
                String[] split = line.split(",");
                WaterSensor waterSensor = new WaterSensor(split[0],
                                                          Long.valueOf(split[1]),
                                                          Integer.valueOf(split[2]));
                return waterSensor;

            })
            .assignTimestampsAndWatermarks(wms)
            .keyBy(WaterSensor::getId)
            .window(TumblingEventTimeWindows.of(Time.seconds(5)))
            .process(new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
                @Override
                public void process(String key, Context context, Iterable<WaterSensor> elements, Collector<String> out) throws Exception {
                    int sum = 0;
                    for (WaterSensor element : elements) {
                        sum++;
                    }
                    out.collect("key=" + key +
                                    ", window=[" + context.window().getStart() + "," + context.window().getEnd() + ")" +
                                    ", 元素个数=" + sum);
                }
            })
            .print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class MyWMSPeriod implements WatermarkGenerator<WaterSensor> {
        private long maxTx;
        private long outOf;

        // 表示最大乱序能力: outOf 秒
        public MyWMSPeriod(int outOf) {
            this.outOf = outOf * 1000;
            maxTx = Long.MIN_VALUE + this.outOf + 1;
        }

        @Override
        public void onEvent(WaterSensor event, long eventTimestamp, WatermarkOutput output) {
            maxTx = Math.max(eventTimestamp, maxTx);
        }

        @Override
        public void onPeriodicEmit(WatermarkOutput output) {
            // 周期性的调用: 每200ms调用一次. 把向流中插入水印的动作写到这里
            output.emitWatermark(new Watermark(maxTx - this.outOf - 1));
        }
    }
}
