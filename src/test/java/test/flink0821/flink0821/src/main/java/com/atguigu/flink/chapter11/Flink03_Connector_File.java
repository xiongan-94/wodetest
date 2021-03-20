package com.atguigu.flink.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.FileSystem;
import org.apache.flink.table.descriptors.Schema;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/30 15:15
 */
public class Flink03_Connector_File {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tenv = StreamTableEnvironment.create(env);

        Schema schema = new Schema()
            .field("id", DataTypes.STRING())
            .field("ts", DataTypes.BIGINT())
            .field("vc", DataTypes.INT());

        tenv
            .connect(new FileSystem().path("input/sensor.txt"))
            .withFormat(new Csv().fieldDelimiter(',').lineDelimiter("\n"))
            .withSchema(schema)
            .createTemporaryTable("sensor");
        // table API  参数: 临时表的表名
        Table sensor = tenv.from("sensor");

        Table result = sensor
            .groupBy($("id"))
            .select($("id"), $("id").count().as("count"));

        //        result.printSchema();
        result.execute().print();

    }
}
