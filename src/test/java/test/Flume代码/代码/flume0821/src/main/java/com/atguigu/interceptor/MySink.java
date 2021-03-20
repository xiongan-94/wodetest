package com.atguigu.interceptor;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;

public class MySink extends AbstractSink implements Configurable {

    private String msgPrefix;
    /**
     * sink保存数据
     * @return
     * @throws EventDeliveryException
     */
    public Status process() throws EventDeliveryException {

        //获取sink对应的channel
        Channel ch = getChannel();

        Transaction txn = ch.getTransaction();

        try{
            txn.begin();
            //从channel中后去数据
            Event event = ch.take();
            //模拟数据保存
            System.out.println(msgPrefix+":"+new String(event.getBody()));
            txn.commit();
            return Status.READY;
        }catch (Exception e){
            txn.rollback();
        }finally {
            txn.close();
        }
        return Status.BACKOFF;
    }

    /**
     * 获取sink对应的属性
     * @param context
     */
    public void configure(Context context) {

        msgPrefix = context.getString("msg.prefix");
    }
}
