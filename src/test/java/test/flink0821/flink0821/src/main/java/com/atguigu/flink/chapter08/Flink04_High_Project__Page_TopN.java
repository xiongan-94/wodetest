package com.atguigu.flink.chapter08;

import com.atguigu.flink.bean.ApacheLog;
import com.atguigu.flink.bean.PageCount;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.TreeSet;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 15:10
 */
public class Flink04_High_Project__Page_TopN {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);
        // 滚动窗口长度为1小个小时
        WatermarkStrategy<ApacheLog> wms = WatermarkStrategy
            .<ApacheLog>forBoundedOutOfOrderness(Duration.ofSeconds(5))
            .withTimestampAssigner(new SerializableTimestampAssigner<ApacheLog>() {
                @Override
                public long extractTimestamp(ApacheLog element, long recordTimestamp) {
                    return element.getEventTime();
                }
            });
        env
            .readTextFile("input/apache.log")
            .map(new MapFunction<String, ApacheLog>() {
                @Override
                public ApacheLog map(String line) throws Exception {
                    //                    System.out.println("map: " + line);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
                    String[] data = line.split(" ");
                    return new ApacheLog(data[0],
                                         formatter.parse(data[3]).getTime(),
                                         data[5],
                                         data[6]);
                }
            })
            .assignTimestampsAndWatermarks(wms)
            .keyBy(ApacheLog::getUrl)
            .window(SlidingEventTimeWindows.of(Time.minutes(10), Time.seconds(5)))
            .aggregate(new AggregateFunction<ApacheLog, Long, Long>() {
                @Override
                public Long createAccumulator() {
                    return 0L;
                }

                @Override
                public Long add(ApacheLog value, Long accumulator) {
//                    System.out.println("add: " + value);
                    return accumulator + 1L;
                }

                @Override
                public Long getResult(Long accumulator) {
                    return accumulator;
                }

                @Override
                public Long merge(Long a, Long b) {
                    return a + b;
                }
            }, new ProcessWindowFunction<Long, PageCount, String, TimeWindow>() {
                @Override
                public void process(String url, Context context, Iterable<Long> elements, Collector<PageCount> out) throws Exception {
//                    System.out.println("process:" + url);
                    out.collect(new PageCount(url, elements.iterator().next(), context.window().getEnd()));
                }
            })
            .keyBy(PageCount::getWindowEnd)
            .process(new KeyedProcessFunction<Long, PageCount, String>() {

                private ValueState<Long> timerTs;
                private ListState<PageCount> pageCountList;

                @Override
                public void open(Configuration parameters) throws Exception {
                    pageCountList = getRuntimeContext().getListState(new ListStateDescriptor<PageCount>("pageCountList", PageCount.class));
                    timerTs = getRuntimeContext().getState(new ValueStateDescriptor<Long>("timerTs", Long.class));
                }

                @Override
                public void processElement(PageCount value, Context ctx, Collector<String> out) throws Exception {
//                    System.out.println("processElement: " + value);
                    pageCountList.add(value);
                    if (timerTs.value() == null) {
                        long ts = value.getWindowEnd() + 1000L;
                        ctx.timerService().registerEventTimeTimer(ts);
                        timerTs.update(ts);
                    }
                }

                @Override
                public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                    // 换个排序的思路
                    // TreeSet 存入的元素会自动排序
                    TreeSet<PageCount> pageCounts = new TreeSet<>((o1, o2) -> {
                        if (o1.getCount() < o2.getCount()) return 1;
                        else return -1;
                    });
                    for (PageCount pageCount : pageCountList.get()) {
                        // 数据一添加, 立即有序
                        pageCounts.add(pageCount);
                        if (pageCounts.size() > 3) {
                            pageCounts.pollLast();  // 删除最后一个元素
                        }

                    }
                    pageCountList.clear();
                    timerTs.clear();
                    System.out.println(pageCounts);
                    StringBuilder sb = new StringBuilder();
                    sb.append("窗口结束时间: ").append(timestamp - 1000L).append("\n");
                    sb.append("---------------------------------\n");
                    for (PageCount pageCount : pageCounts) {
                        sb.append(pageCount).append("\n");
                    }
                    sb.append("---------------------------------\n\n");
                    out.collect(sb.toString());

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
/*
统计入门商品
1. 计算出来每个商品被pv的次数
    0-1h
        在这个窗口内进行计算
2. 安装次数降序排列, topN


 */