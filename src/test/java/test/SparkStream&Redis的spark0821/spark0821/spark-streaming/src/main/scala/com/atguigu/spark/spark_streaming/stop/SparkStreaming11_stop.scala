package com.atguigu.spark.spark_streaming.stop

import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext, StreamingContextState}

object SparkStreaming11_stop {
    
    def main(args: Array[String]): Unit = {
        
        //1.初始化Spark配置信息
        val sparkconf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkStreaming")
        // 设置优雅的关闭
        sparkconf.set("spark.streaming.stopGracefullyOnShutdown", "true")
        
        
        //2.初始化SparkStreamingContext
        val ssc: StreamingContext = new StreamingContext(sparkconf, Seconds(3))
        
        
        // 接收数据
        val lineDStream: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop162", 9999)
        // 执行业务逻辑
        lineDStream.flatMap(_.split(" "))
            .map((_, 1))
            .print()
        
        //4 启动SparkStreamingContext
        ssc.start()
        
        // 开启监控程序
        new Thread(new MonitorStop(ssc)).start()
        
        // 将主线程阻塞，主线程不退出
        ssc.awaitTermination()
        
    }
}


// 监控程序
class MonitorStop(ssc: StreamingContext) extends Runnable {
    
    override def run(): Unit = {
        // 获取HDFS文件系统
        val fs: FileSystem = FileSystem.get(new URI("hdfs://hadoop162:8020"), new Configuration(), "atguigu")
        
        while (true) {
            Thread.sleep(5000)
            // 获取/stopSpark路径是否存在
            val result: Boolean = fs.exists(new Path("hdfs://hadoop162:8020/stopSpark"))
            
            if (result) {
                
                val state: StreamingContextState = ssc.getState()
                // 获取当前任务是否正在运行
                if (state == StreamingContextState.ACTIVE) {
                    // 优雅关闭
                    ssc.stop(stopSparkContext = true, stopGracefully = true)
                    System.exit(0)
                }
            }
        }
    }
}