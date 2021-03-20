package com.atguigu.phoenix;

import org.apache.phoenix.queryserver.client.Driver;
import org.apache.phoenix.queryserver.client.ThinClientUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.Properties;


public class PhoenixTest {

    private Connection connection;

    private PreparedStatement statement;
    /**
     * 初始化
     * @throws Exception
     */
    @Before
    public void init() throws Exception{
        //加载驱动
        Class.forName("org.apache.phoenix.queryserver.client.Driver");
        //1、创建Connect对象
        String url = ThinClientUtil.getConnectionUrl("hadoop102", 8765);
        //客户端开启namespace映射
        Properties props = new Properties();
        props.setProperty("phoenix.schema.isNamespaceMappingEnabled","true");
        props.setProperty("phoenix.schema.mapSystemTablesToNamespace","true");
        connection = DriverManager.getConnection(url,props);
        //自动提交事务
        connection.setAutoCommit(true);
    }

    @After
    public void close() throws Exception{

        if( statement!=null) statement.close();
        if(connection!=null) connection.close();
    }
    /**
     * 创建表
     * @throws Exception
     */
    @Test
    public void createTable() throws Exception{


        //2、创建statement对象
        String sql = "create table user(" +
                "id varchar primary key," +
                "name varchar," +
                "age varchar)COLUMN_ENCODED_BYTES=0";
        statement = connection.prepareStatement(sql);
        //3、执行
        statement.execute();

    }

    /**
     * 插入数据
     * @throws Exception
     */
    @Test
    public void upsert() throws Exception{

        //1、获取statement对象
        String sql = "upsert into user values(?,?,?)";
        statement = connection.prepareStatement(sql);

        //2、参数赋值
        statement.setString(1,"1001");
        statement.setString(2,"zhangsan");
        statement.setString(3,"25");

        //3、插入
        statement.executeUpdate();

        //connection.commit();
    }

    /**
     * 批量插入
     * @throws Exception
     */
    @Test
    public void upsertBatch() throws Exception{

        //1、获取statement对象
        String sql = "upsert into user values(?,?,?)";
        statement = connection.prepareStatement(sql);
        //2、对参数赋值
        for(int i=1;i<=1000;i++){

            statement.setString(1,"1001"+i);
            statement.setString(2,"zhangsan-"+i);
            statement.setString(3,(20+i)+"");
            //3、将本条加入一个批次中
            statement.addBatch();
            if(i%300==0){
                //4、执行一个批次
                statement.executeBatch();
                //清空该批次
                statement.clearBatch();
            }
        }
        //5、执行最后一个不满300条数据的批次
        statement.executeBatch();
    }

    /**
     * 删除数据
     * @throws Exception
     */
    @Test
    public void delete() throws Exception{

        String sql = "delete from user where id=?";
        statement = connection.prepareStatement(sql);

        statement.setString(1,"1001");

        statement.executeUpdate();

    }

    /**
     * 查询数据
     * @throws Exception
     */
    @Test
    public void query() throws Exception{
        //1、获取statement对象
        String sql = "select * from user where id>?";
        statement = connection.prepareStatement(sql);
        //2、封装参数
        statement.setString(1,"100111");
        //3、执行查询
        ResultSet resultSet = statement.executeQuery();

        //4、结果展示
        while (resultSet.next()){

            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            String age = resultSet.getString("age");
            System.out.println("id="+id+",name="+name+",age="+age);
        }
    }

    /**
     * 删除表
     * @throws Exception
     */
    @Test
    public void dropTable() throws Exception{

        statement = connection.prepareStatement("drop table user");

        statement.execute();
    }
}
