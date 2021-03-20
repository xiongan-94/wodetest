package com.atguigu.java4;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/*
    Druid （数据库连接池）：
 */
public class DruidTest {
    @Test
    public void test() throws SQLException {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/myemployees");
        ds.setUsername("root");
        ds.setPassword("123321");
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        Connection connection = ds.getConnection();
        System.out.println(connection);
    }


    @Test
    public void test2() throws Exception {
        //读取配置信息
        Properties pro = new Properties();
        pro.load(new FileInputStream("druid.properties"));
        //通过Druid的数据源的工厂类创建DataSource的对象。通过该对象就可以获取数据库连接对象
        DataSource ds = DruidDataSourceFactory.createDataSource(pro);
        //获取数据库的连接对象
        Connection conn = ds.getConnection();
        System.out.println(conn);
    }
}
