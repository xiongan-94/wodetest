package com.atguigu.spark.project.util


import java.sql._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object MyJdbcUtil {
    
    def readFormJdbc(url: String, sql: String, args: scala.Array[Any]): ListBuffer[Map[String, Object]] = {
        val conn: Connection = DriverManager.getConnection(url)
        val ps: PreparedStatement = conn.prepareStatement(sql)
        for (i <- args.indices) { // 替换每行的占位符
            ps.setObject(i + 1, args(i))
        }
        
        val result = ListBuffer[Map[String, Object]]()
        val resultSet: ResultSet = ps.executeQuery()
        val metaData: ResultSetMetaData = resultSet.getMetaData
        while (resultSet.next()) {
            // 每一行一个Map
            val map: mutable.HashMap[String, Object] = mutable.HashMap[String, Object]()
            for (columnIndex <- 1 to metaData.getColumnCount) {
                val key: String = metaData.getColumnName(columnIndex)
                val value = resultSet.getObject(columnIndex)
                
                map += key -> value
            }
            result += map.toMap
        }
        result
    }
    
    // insert into user_ad_count values(?,?,?,?)
    // on duplicate key update COUNT=count+?
    // jdbc:mysql://hadoop162:3306/spark0821?user=root&password=aaaaaa
    def writeToJdbc(url: String,
                    sql: String,
                    args: Iterator[scala.Array[Any]]): Unit = {
        
        // 先获取连接
        val conn: Connection = DriverManager.getConnection(url)
        // PrepareState
        val ps: PreparedStatement = conn.prepareStatement(sql)
        // 执行
        args.foreach(rowArgs => {
            for (i <- 0 until rowArgs.length) { // 替换每行的占位符
                ps.setObject(i + 1, rowArgs(i))
            }
            ps.execute()
        })
        // 关闭ps和连接
        ps.close()
        conn.close()
    }
}
