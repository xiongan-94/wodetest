package com.atguigu.chapter07

import scala.io.Source

object $16_Home {

  /**
    * 1、求出哪些省份没有农产品市场
    * 2、获取菜的种类最多的三个省份
    * 3、获取每个省份菜的种类最多的三个农贸市场
    */
  def main(args: Array[String]): Unit = {

    val products = Source.fromFile("datas/product.txt","utf-8").getLines().toList

    val allprovince = Source.fromFile("datas/allprovince.txt").getLines().toList

    //m1(allprovince,products)
    //m2(products)
    m3(products)
  }

  /**
    * 获取每个省份菜的种类最多的三个农贸市场
    */
  def m3(products:List[String]) = {

    //1、过滤非法数据
    products.filter(_.split("\t").size==6)

    //2、列裁剪- 选择 菜名、省份、农产品市场
      .map(line=>{
        val arr = line.split("\t")
      (arr(4),arr(3),arr(0))
    })
    //3、去重
      .distinct
    //4、按照省份、农产品市场分组
      .groupBy(x=> (x._1,x._2) )
    /**
      * Map(
      *     (湖南省,长沙市马王堆批发市场) -> List(  (湖南省,长沙市马王堆批发市场,大白菜),(湖南省,长沙市马王堆批发市场,青椒),... )
      *     (湖南省,长沙市aa) -> List(  (湖南省,长沙市aa,大白菜),(湖南省,长沙市aa,青椒),... )
      *     ....
      * )
      */
    //5、统计每个省份、每个农产品市场的菜的种类数
      .map(x=>{
      //x = (湖南省,长沙市马王堆批发市场) -> List(  (湖南省,长沙市马王堆批发市场,大白菜),(湖南省,长沙市马王堆批发市场,青椒),... )
      (x._1._1,x._1._2,x._2.size)
    })

    /**
      * List(
      *   (湖南省,长沙市马王堆批发市场,10)
      *   (湖南省,长沙市aa,20)
      *   (湖北省,武汉市bb,30)
      *   ....
      * )
      */
    //6、按照省份分组
      .groupBy(_._1)

    /**
      * Map(
      *     湖南省 -> List( (湖南省,长沙市马王堆批发市场,10),(湖南省,长沙市aa,20),..)
      *     湖北省 -> List( (湖北省,武汉市bb,30),... )
      *     ....
      * )
      */
    //7、对每个省份农产品种类数进行排序，取前三

      .map(x=>{
      //x = 湖南省 -> List( (湖南省,长沙市马王堆批发市场,10),(湖南省,长沙市aa,20),..)

      (x._1,x._2.toList.sortBy(_._3).reverse.take(3))
    })
    //8、结果展示
      .foreach(println)
  }

  /**
    * 获取菜的种类最多的三个省份
    */
  def m2(products:List[String]) = {

    //1、过滤非法数据
    products.filter(_.split("\t").length==6)
    //2、列裁剪-选择菜名-省份
      .map(line=>{
      val arr = line.split("\t")
      (arr(4),arr(0))
    })
    //List( ( 湖南省,大白菜),( 湖南省,大白菜),( 湖南省,青椒)，...)
    //3、去重【每个省份有多个农产品市场可能有多个同名的菜】
      .distinct
    //List( ( 湖南省,大白菜),( 湖南省,青椒)，...)
    //4、按照省份分组
      .groupBy(_._1)
    /**
      * Map[
      *    湖南省 -> List( ( 湖南省,大白菜),( 湖南省,青椒),...)
      *    湖北省 -> List( ( 湖北省,大白菜),( 湖北省,青椒),...)
      * ]
      */
    //5、统计每个省份的菜的种类数量
      .map(x=>{
      //x = 湖南省 -> List( ( 湖南省,大白菜),( 湖南省,青椒),...)
      (x._1,x._2.size)
    })
    //List( 湖南省->10,湖北省->5,..)
    //6、排序
      .toList
      .sortBy(_._2)
    //List(湖北省->5,湖南省->10,广东省->20,..)
    //7、取前三
      .reverse
      .take(3)
    //8、结果展示
      .foreach(println)
  }

  /**
    * 1、求出哪些省份没有农产品市场
    */
  def m1(allprovince:List[String],products:List[String]) ={

    //三要素: 过滤、去重、列裁剪

    //1、过滤
    val filterProduct = products.filter(line=>line.split("\t").length==6)
    //2、获取农产品市场数据的省份
    val productProvinces = filterProduct.map(line=> {
      line.split("\t")(4)
    })

    //3、去重
    val distincProvince = productProvinces.distinct
    //4、将农产品数据省份与全国所有省份对比，得到哪些省份没有农产品市场【求差集】
    val result = allprovince.diff(distincProvince)
    //5、结果展示
    result.foreach(println)
  }
}
