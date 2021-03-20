package com.atguigu.flink.chapter06;

import com.atguigu.flink.bean.MarketingUserBehavior;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.apache.flink.api.common.typeinfo.Types.*;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 16:04
 */
public class Flink02_Project_AppStat_WithChannel {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        env
            .addSource(new AppMarketSource())
            // install_huawei, 1
            .map(behavior -> Tuple2.of(behavior.getChannel() + "_" + behavior.getBehavior(), 1L))
            .returns(TUPLE(STRING, LONG))
            .keyBy(t -> t.f0)
            .sum(1)
            .print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class AppMarketSource implements SourceFunction<MarketingUserBehavior> {
        Random random = new Random();
        List<String> behaviors = Arrays.asList("download", "install", "update", "uninstall");
        List<String> channels = Arrays.asList("huawwei", "xiaomi", "apple", "baidu", "qq", "oppo", "vivo");
        boolean canRun = true;

        @Override
        public void run(SourceContext<MarketingUserBehavior> ctx) throws Exception {
            while (canRun) {
                Long userId = (long) random.nextInt(1000);
                String behavior = behaviors.get(random.nextInt(behaviors.size()));
                String channel = channels.get(random.nextInt(channels.size()));
                Long timestamp = System.currentTimeMillis();
                ctx.collect(new MarketingUserBehavior(userId, behavior, channel, timestamp));
                Thread.sleep(random.nextInt(2) * 1000 + 500);
            }
        }

        @Override
        public void cancel() {
            canRun = false;
        }
    }
}
