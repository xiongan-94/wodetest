package com.atguigu.java5;

import com.atguigu.java2.JDBCUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*

    不用敲看看就得了
    sql注入的语句：
    SELECT id,NAME FROM USER WHERE id=100 AND NAME='cc' OR 1=1;
 */
public class TestStatement {
    public static void main(String[] args) throws SQLException {
        //获取数据库连接对象
        Connection connection = JDBCUtils.getConnection();
        //创建statement对象
        Statement statement = connection.createStatement();

        String username = "xxx";
        String password = "ccc or 1=1";

        String sql = "select id,user from user " +
                "where id=" + username + " user="+password;

        //执行sql语句
        ResultSet rs = statement.executeQuery(sql);
        while(rs.next()){
            System.out.println(rs.getString("username"));
            System.out.println(rs.getString("pwd"));
        }

    }
}
