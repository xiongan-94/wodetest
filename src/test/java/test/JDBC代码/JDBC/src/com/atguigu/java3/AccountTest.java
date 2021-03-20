package com.atguigu.java3;

import com.atguigu.java2.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*

    CREATE TABLE account(
        NAME VARCHAR(20),
        balance INT
    )

    AA向CC账户转账：

 */
public class AccountTest {
    public static void main(String[] args) throws SQLException {
        //1.获取数据库连接对象
        Connection connection = JDBCUtils.getConnection();
        PreparedStatement ps = null;
        try {
            //禁止自动提交
            connection.setAutoCommit(false);
            //2.预编译
            String sql = "update account set balance=? where name=?";
            ps = connection.prepareStatement(sql);

            //操作aa账户减100
            ps.setInt(1, 900);
            ps.setString(2, "aa");
            ps.executeUpdate();

            //System.out.println(1 / 0);

            //操作cc账户加100
            ps.setInt(1, 1100);
            ps.setString(2, "cc");
            ps.executeUpdate();
            //提交
            connection.commit();
        }catch (Exception e){
            //一旦发生异常回滚
            connection.rollback();
            e.printStackTrace();
        }finally {
            //恢复自动提交
            connection.setAutoCommit(true);
            //关闭资源
            JDBCUtils.close(connection,ps);
        }



    }
}
