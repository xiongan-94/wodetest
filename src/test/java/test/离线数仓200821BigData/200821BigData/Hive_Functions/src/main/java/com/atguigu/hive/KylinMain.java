package com.atguigu.hive;

import java.sql.*;

/**
 * Created by VULCAN on 2020/12/21
 */
public class KylinMain {

    public static void main(String[] args) throws Exception {

        //注册驱动可以省略

        // 获取连接
        Connection connection = DriverManager.getConnection("jdbc:kylin://hadoop103:7070/gmall", "ADMIN", "KYLIN");


        //编写sql
        String sql="select\n" +
                "    PROVINCE_NAME ,REGION_NAME,ACTIVITY_NAME ,sum(FINAL_TOTAL_AMOUNT) sum_benefit_amount\n" +
                "from DWD_FACT_ORDER_INFO t1\n" +
                "left join DWD_DIM_BASE_PROVINCE t2\n" +
                "on t1.PROVINCE_ID = t2.id\n" +
                "left join ACTIVITY_VIEW t3\n" +
                "on t1.ACTIVITY_ID = t3.id\n" +
                "group by PROVINCE_NAME , REGION_NAME ,ACTIVITY_NAME";

        //预编译
        PreparedStatement ps = connection.prepareStatement(sql);

        //执行查询遍历结果
        ResultSet resultSet = ps.executeQuery();

        while(resultSet.next()){

            System.out.println(resultSet.getString("PROVINCE_NAME") + " " +
                    resultSet.getString("REGION_NAME") + " " +
                    resultSet.getString("ACTIVITY_NAME") + " " +
                    resultSet.getBigDecimal("sum_benefit_amount"));

        }

        //关闭资源
        resultSet.close();
        ps.close();
        connection.close();
    }
}
