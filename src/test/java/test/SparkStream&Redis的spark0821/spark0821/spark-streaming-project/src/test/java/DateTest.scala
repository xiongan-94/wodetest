


import com.atguigu.spark.project.util.MyJdbcUtil.readFormJdbc

/**
 * Author atguigu
 * Date 2020/12/23 15:45
 */
object DateTest {
    def main(args: Array[String]): Unit = {
        val url = "jdbc:mysql://hadoop162:3306/spark0821?user=root&password=aaaaaa"
        val querySql = "select userid, count from user_ad_count where dt=? and count>=?"
        println(readFormJdbc(url, querySql, Array("2020-12-23", 30)))
    }
}
