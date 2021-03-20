package com.atguigu.java5;

import com.atguigu.java2.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

/*
    通过使用DBUtils操作数据库：
 */
public class DBUtilsTest {
    //创建QueryRunner通过该对象可以对数据库进行 增，删，改，查的操作
    private QueryRunner qr = new QueryRunner();
    /*
        查询数据
     */
    @Test
    public void test3() throws SQLException {
        /*
         query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params)
         conn: 数据库连接对象
         sql : sql语句
         rsh :ResultSetHandler的实现类的对象
         params ：占位符所需要赋值的内容
         */
        String sql = "select id,name from user";
        //返回一个集合 - 查询语句的结果有多条
        List<User> query = qr.query(JDBCUtils.getConnection(), sql,
                new BeanListHandler<User>(User.class));

        //返回单个对象 - 查询语句的结果只有一条
//        User user = qr.query(JDBCUtils.getConnection(), sql,
//                new BeanHandler<User>(User.class));

        for (User user : query) {
            System.out.println(user);
        }
    }

    /*
        向数据库插入一条数据
     */
    @Test
    public void test() throws SQLException {
        /*
         update(Connection conn, String sql, Object... params)
         conn : 数据库的连接对象
         sql : sql语句
         params : 给占位符赋值的内容
         */
        String sql = "insert into user(id,name) values(?,?)";
        int result = qr.update(JDBCUtils.getConnection(), sql, 1, "longge");
        System.out.println("共有" + result + "条数据受到影响");
    }
    /*
        删除数据库中的数据
     */
    @Test
    public void test2() throws SQLException {
        String sql = "delete from user where id=?";
        int result = qr.update(JDBCUtils.getConnection(), sql, 1);
        System.out.println("共有" + result + "条数据受到影响");
    }
}
