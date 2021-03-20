package com.atguigu.interceptor;

import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;

import java.util.HashMap;

public class MySource extends AbstractSource implements Configurable, PollableSource {

    private String msgPrefix;
    /**
     * 采集数据的方法[ 执行多次 ]
     *
     * @return
     * @throws EventDeliveryException
     */
    public Status process() throws EventDeliveryException {

        try{

            for(int i=0;i<=5;i++){
                SimpleEvent event = new SimpleEvent();
                event.setHeaders(new HashMap<String, String>());
                event.setBody((msgPrefix+":"+i).getBytes());
                getChannelProcessor().processEvent(event);
            }
            Thread.sleep(2000);
            return Status.READY;
        }catch (Exception e){

            return Status.BACKOFF;
        }
    }

    /**
     *  如果没有数据,定义在上一次的等待时间的基础上增加多长时间之后再去采集
     * @return
     */
    public long getBackOffSleepIncrement() {
        return 0;
    }

    /**
     * 定义间隔多少采集一次数据
     * @return
     */
    public long getMaxBackOffSleepInterval() {
        return 0;
    }

    /**
     * 获取Source对应的属性
     * @param context
     */
    public void configure(Context context) {
        msgPrefix = context.getString("msg.prefix");
    }
}
