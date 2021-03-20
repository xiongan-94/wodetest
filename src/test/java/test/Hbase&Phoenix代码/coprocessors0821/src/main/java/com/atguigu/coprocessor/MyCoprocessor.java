package com.atguigu.coprocessor;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.wal.WALEdit;

import java.io.IOException;
import java.util.Optional;

public class MyCoprocessor implements RegionObserver, RegionCoprocessor {

    @Override
    public Optional<RegionObserver> getRegionObserver() {
        return Optional.of(this);
    }

    /**
     * 在插入数据之后执行对应的操作
     * @param c
     * @param put
     * @param edit
     * @param durability
     * @throws IOException
     */
    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> c, Put put, WALEdit edit, Durability durability) throws IOException {

        //1、获取hbase连接
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum","hadoop102:2181,hadoop103:2181,hadoop104:2181");
        Connection connection = ConnectionFactory.createConnection(conf);
        //2、获取table对象
        Table table = connection.getTable(TableName.valueOf("B"));
        //3、封装数据

        //4、插入
        table.put(put);

        //5、关闭
        table.close();
        connection.close();
    }
}
