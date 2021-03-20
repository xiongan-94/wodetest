package com.atguigu.redis

import redis.clients.jedis.{HostAndPort, Jedis, JedisCluster}
import scala.collection.JavaConverters._
/**
 * Author atguigu
 * Date 2020/12/30 9:53
 */
object RedisDemo {
    def main(args: Array[String]): Unit = {
        // 先创建redis的客户端(到服务器的连接)
       // val client = new Jedis("hadoop218", 6384)
        val set = Set(new HostAndPort("hadoop218", 6379)).asJava
       val client =  new JedisCluster(set)
        // 通过redis的客户端提供各种方法操作数据
        // 1. string
        client.set("k1", "kkkkkkk")
        client.set("k3", "333333")
        //                println(client.get("k1"))
        //         client.incrByFloat("f1", 1.1)
        //client.set("k1", "中文")
        //println(client.get("k1"))
        // 2. list
        //        client.lpush("list1", "a", "b", "c")
        //        client.rpush("list1", "d", "e", "f")
        //        val list: ju.List[String] = client.lrange("list1", 0, -1)
        //        println(list)
        
        // 3. set
        //        client.sadd("set1", "a", "b", "a", "c")
        //        println(client.sadd("set1", "d"))
        //        println(client.sadd("set1", "a"))
        // 4. hash
        //        client.hset("h1", "f1", "v1")
        //
        //        val map = Map("f2" -> "v2", "f3" -> "v3").asJava
        //        client.hmset("h1", map)
        //        val jMap = client.hgetAll("h1").asScala
        //        println(jMap)
        // 5. zset
        //        client.zadd("z1", 11, "a")
        //        client.zadd("z1", 9, "b")
        //        client.zadd("z1", 10, "c")
        
        //        val set: util.Set[String] = client.zrange("z1", 0, -1)
        // val set: util.Set[String] = client.zrevrange("z1", 0, -1)
        // println(set)
        
        // 关闭客户端
        
        client.close()
    }
}
