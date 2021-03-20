package com.atguigu.java3;

import com.atguigu.java2.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
    批处理：
        1.mysql驱动的版本 ：5.1.3x这样的版本才支持批处理
        2.在url后面加上 rewriteBatchedStatements=true
        3.API
           addBatch();将需要执行的sql语句加入到批处理中
           executeBatch();执行一次批处理
           clearBatch();清空批处理
 */
public class BatchTest {
    public static void main(String[] args) throws SQLException {
        //1.获取数据库连接对象
        Connection connection = JDBCUtils.getConnection();
        //2.预编译
        String sql = "insert into user(id,name) values(?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        //3.给占位符赋值
        for (int i = 1; i <= 100000 ; i++) {
            ps.setInt(1,i);
            ps.setString(2,i+"aa");
            //将需要执行的sql语句加入到批处理中
            ps.addBatch();
            if (i % 1000 == 0){
                //执行一次批处理
                ps.executeBatch();
                //清空批处理
                ps.clearBatch();
            }
        }
        //4.关资源
        JDBCUtils.close(connection,ps);
    }
}
