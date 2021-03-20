package com.atguigu.java2;

import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
    对数据库中数据的增，删，改，查
 */
public class CRUDTest {
    /*
        查询数据
     */
    @Test
    public void test4() throws SQLException {
        List<User> list = getResultSet();
        for (User user : list) {
            System.out.println(user);
        }
    }

    public List<User> getResultSet() throws SQLException {
        //1.获取数据库的连接对象
        Connection connection = JDBCUtils.getConnection();
        //2.预编译
        String sql = "select id,name from user";
        PreparedStatement ps = connection.prepareStatement(sql);
        //3.执行sql语句
        ResultSet rs = ps.executeQuery();
        List<User> list = new ArrayList<>();
        //4.遍历结果集
        while(rs.next()){
            /*
             getInt(String columnLabel) : 根据字段名获取对应的数据
             getInt(int columnIndex) ：根据字段的位置获取对应的数据
             */
            int id = rs.getInt("id");
            String name = rs.getString("name");
            //封装对象
            list.add(new User(id, name));
        }
        //5.关资源
        JDBCUtils.close(connection,ps,rs);
        return list;
    }

    /*
        插入数据
     */
    @Test
    public void test() throws SQLException {
        //1.获取数据库的连接对象
        Connection connection = JDBCUtils.getConnection();
        //2.预编译
        String sql = "insert into user(id,name) values(?,?)";//?:占位符
        PreparedStatement ps = connection.prepareStatement(sql);
        /*
        setInt(int parameterIndex, int x)
        第一个参数 ：第几个占位符
        第二个参数 ：给占位符止赋值的内容
         */
        ps.setInt(1,100);
        ps.setString(2,"xiaolongge");
        //3.执行sql
        /*
            executeUpdate()用来执行增，删，改的sql语句
            返回值 ：有几条数据受到影响
         */
        int i = ps.executeUpdate();
        System.out.println("共有" + i + "条数据受到影响");
        //4.关闭资源
       JDBCUtils.close(connection,ps);
    }

    /**
     * 修改数据
     */
    @Test
    public void test2() throws SQLException {
        //1.获取数据库的连接对象
        Connection connection = JDBCUtils.getConnection();
        //2.预编译
        String sql = "update user set id=? where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        //2.1给占位符赋值
        ps.setInt(1,1);
        ps.setInt(2,100);
        //3.执行sql
        int i = ps.executeUpdate();
        System.out.println("共有" + i + "条数据受到影响");
        //4.关闭资源
        JDBCUtils.close(connection,ps);
    }

    /**
     * 删除数据
     */
    @Test
    public void test3() throws SQLException {
        //1.获取数据库的连接对象
        Connection connection = JDBCUtils.getConnection();
        //2.预编译
        String sql = "delete from user where id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        //2.1给占位符赋值
        ps.setInt(1,1);
        //3.执行sql
        int i = ps.executeUpdate();
        System.out.println("共有" + i + "条数据受到影响");
        //4.关闭资源
        JDBCUtils.close(connection,ps);
    }
}
