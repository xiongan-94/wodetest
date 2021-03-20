package com.atgugiu.realtime.util

import io.searchbox.client.JestClientFactory
import io.searchbox.client.config.HttpClientConfig
import io.searchbox.core.{Bulk, Index}

import scala.collection.JavaConverters._

/**
 * Author atguigu
 * Date 2021/1/8 8:53
 */
object MyESUtil {
    val factory = new JestClientFactory
    val list = List("http://hadoop162:9200", "http://hadoop163:9200", "http://hadoop164:9200")
    val config = new HttpClientConfig.Builder(list.asJava)
        .connTimeout(10 * 1000)
        .readTimeout(10 * 1000)
        .multiThreaded(true)
        .maxTotalConnection(100)
        .build()
    factory.setHttpClientConfig(config)

    def insertSingle(indexStr: String, source: Object, id: String = null): Unit = {
        val client = factory.getObject

        val index = new Index.Builder(source)
            .index(indexStr)
            .`type`("_doc")
            .id(id)
            .build()
        client.execute(index)
        // 3. 关闭客户端
        client.close()
    }


    def insertBulk(indexStr: String, sources: Iterator[Object]): Unit = {
        val client = factory.getObject
        val bulkBuilder = new Bulk.Builder()
            .defaultIndex(indexStr)
            .defaultType("_doc")
        /* sources
             .map(source => {
                 new Index.Builder(source).build()
             })
             //.foreach(action => bulkBuilder.addAction(action))
             .foreach(bulkBuilder.addAction)*/
        for (source <- sources) {
            source match {
                case (id: String, data) =>

                    bulkBuilder.addAction(new Index.Builder(data).id(id).build())
                case data =>

                    bulkBuilder.addAction(new Index.Builder(data).build())
            }
        }
        client.execute(bulkBuilder.build())
        client.close()
    }


    def main(args: Array[String]): Unit = {
        val source =
            """{"name": "zs","age": 20,"girls":["a", "b", "c"]}"""

        //insertSingle("test1", source, "2")

        insertBulk("test3", Iterator(source, ("id1", User("abc", 30))))

    }
}

case class User(name: String, age: Int)

/*
 // 向es写数据
        // 1. 先创建一个es客户端


        val client = factory.getObject
        // source:表示要写入的数据, 一般有两种数据源: json格式和样例类(POJO)
        /*val source =
            """
              |{
              | "name": "zs",
              | "age": 20,
              | "girls":["a", "b", "c"]
              |}
              |""".stripMargin*/
        //val source = User("ww", 30)
        val source = Map("a" -> 97, "b" -> 98).asJava
        // 2. 向es写数据, 利用客户端

 */
