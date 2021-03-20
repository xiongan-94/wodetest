package com.atgugiu.realtime.util

import redis.clients.jedis.Jedis

/**
 * Author atguigu
 * Date 2021/1/5 10:16
 */
object MyRedisUtil {
    def getClient() = {
        new Jedis("hadoop162", 6379)
    }
}
