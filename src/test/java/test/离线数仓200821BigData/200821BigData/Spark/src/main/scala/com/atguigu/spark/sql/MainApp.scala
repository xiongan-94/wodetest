package com.atguigu.spark.sql

import org.apache.spark.sql.{DataFrame, SparkSession, functions}

/**
 * Created by VULCAN on 2020/12/7
 *
 *    需求：
 *          各区域热门(点击次数)商品 Top3
 *
 *     分析：  ①先过滤出点击的数据，再Join
 *                  将需要的字段，拼到一起
 *               user_visit_action表：      click_product_id，city_id
 *
 *               product_info表：     product_id，product_name
 *
 *               city_info 表 ：   area，city_name，city_id
 *
 *               要的是用户的行为，需要将行为中的click_product_id，替换为product_name，将city_id替换为 city_name
 *                user_visit_action  left join  product_info left join   city_info
 *
 *
 *             ② 将数据按照地区和商品分组，统计每一个地区每个商品点击了多少次
 *
 *             ③将上述数据，按照地区分组，分组后，统计每个商品点击量的排名(rank,row_number)
 *
 *             ④取每个地区排名前三的商品
 *
 *
 *
 *    输出：
 *          地区	商品名称	点击次数	城市备注
 *
 *
 *          城市备注 使用UDAF函数实现！
 *
 */
object MainApp {

  def main(args: Array[String]): Unit = {

    val sparkSession: SparkSession = SparkSession.builder
      .master("local[*]")
      .appName("sql")
      //启动访问hive的支持
      .enableHiveSupport()
      .config("spark.sql.warehouse.dir","hdfs://hadoop102:9820/user/hive/warehouse")
      .getOrCreate()


    val sql1=
      """
        |
        |SELECT
        |    c.*,p.product_id ,p.product_name
        |from
        |(SELECT
        |    click_product_id ,city_id
        |from user_visit_action
        |--先过滤出点击的数据
        |where click_product_id != -1 ) t
        |left JOIN city_info c on t.city_id = c.city_id
        |left  JOIN  product_info p on t.click_product_id = p.product_id
        |
        |""".stripMargin

    val sql2=
      """
        |SELECT
        |   product_name,area,count(*) clickcount ,cityInfo(city_name) cityInfo
        |from t1
        |group by product_id,product_name,area
        |
        |""".stripMargin


    val sql3=
      """
        |select
        |    product_name,area,clickcount,cityInfo, rank() over(PARTITION  by area order by clickcount desc) rn
        |from t2
        |""".stripMargin

    val sql4=
      """
        |select
        |    area,product_name,clickcount,cityInfo
        |from t3
        |where rn <=3
        |""".stripMargin

    //创建函数
    val uDAF = new MyUDAF
    //注册函数
    sparkSession.udf.register("cityInfo",functions.udaf(uDAF))


    //切库
    sparkSession.sql("use gmall")

    //执行sql
    val df: DataFrame = sparkSession.sql(sql1)

    df.createTempView("t1")

    val df2: DataFrame = sparkSession.sql(sql2)

    df2.createTempView("t2")

    val df3: DataFrame = sparkSession.sql(sql3)

    df3.createTempView("t3")

     sparkSession.sql(sql4).show(100000,false)


    sparkSession.close()

  }



}
