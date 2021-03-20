package com.atgugiu.realtime.util

import java.sql.DriverManager

import com.atgugiu.realtime.bean.ProvinceInfo
import org.json4s.jackson.{JsonMethods, Serialization}

import scala.collection.mutable.ListBuffer

/**
 * Author atguigu
 * Date 2021/1/12 9:31
 */
object MyJDBCUtil {
    def main(args: Array[String]): Unit = {
        val url = "jdbc:phoenix:hadoop162,hadoop163,hadoop164:2181"
        val sql = "select * from GMALL_PROVINCE_INFO"
        val result = readFromJdbc_1[ProvinceInfo](url, sql)
        result.foreach(println)
    }

    def readFromJdbc_1[T](url: String, // 会自动根据url的协议找到合适的驱动   ?user=root@passs...
                          sql: String, // select x, y from where id in ()
                          args: Seq[Object] = Seq.empty[Object])(implicit mf: scala.reflect.Manifest[T]): ListBuffer[T] = {
        // 获取连接
        val conn = DriverManager.getConnection(url)
        // 预编译语句
        val ps = conn.prepareStatement(sql)
        // 给占位符赋值
        for (i <- 1 to args.length) {
            println(i)
            ps.setObject(i, args(i - 1))
        }

        // 执行
        val resultSet = ps.executeQuery()
        val meta = resultSet.getMetaData
        // 解析结果
        val result = ListBuffer[T]()
        while (resultSet.next()) {
            // 表示有一行数据
            var map = Map[String, Object]()
            // 获取每一列
            for (i <- 1 to meta.getColumnCount) {
                val columnName: String = meta.getColumnName(i).toLowerCase()
                val columnValue: Object = resultSet.getObject(i)
                map += columnName -> columnValue
            }
            // 把map转成样例类形式: 先转json, 使用json工具转成样例类
            implicit val f = org.json4s.DefaultFormats
            val json = Serialization.write(map)
            result += JsonMethods.parse(json).extract[T]
        }
        resultSet.close()
        ps.close()
        conn.close()
        // 关闭连接
        result
    }


    def readFromJdbc(url: String, // 会自动根据url的协议找到合适的驱动   ?user=root@passs...
                     sql: String, // select x, y from where id=? and name=?
                     args: Seq[Object] = Seq.empty[Object]): ListBuffer[Map[String, Object]] = {
        // 获取连接
        val conn = DriverManager.getConnection(url)
        // 预编译预警
        val ps = conn.prepareStatement(sql)
        // 给占位符赋值
        for (i <- 1 to args.length) {
            ps.setObject(i, args(i - 1))
        }
        // 执行
        val resultSet = ps.executeQuery()
        val meta = resultSet.getMetaData
        // 解析结果
        val result = ListBuffer[Map[String, Object]]()
        while (resultSet.next()) {
            // 表示有一行数据
            var map = Map[String, Object]()
            // 获取每一列
            for (i <- 1 to meta.getColumnCount) {
                val columnName: String = meta.getColumnName(i).toLowerCase()
                val columnValue: Object = resultSet.getObject(i)
                map += columnName -> columnValue
            }
            result += map
        }
        resultSet.close()
        ps.close()
        conn.close()
        // 关闭连接
        result
    }



}

/*
x    y
a    1
b    2

 */
