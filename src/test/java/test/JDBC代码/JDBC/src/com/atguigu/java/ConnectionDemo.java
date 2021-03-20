package com.atguigu.java;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*
    获取数据库的连接对象（通过该对象操作数据库）：
    1.数据库可以正常运行（mysql服务必须在运行）
    2.数据库的账号和密码是正确的
    3.必须保证mysql驱动和mysql的版本是对应的。
    4.加载驱动包
 */
public class ConnectionDemo {
    /*
        通过Driver获取数据库连接对象
     */
    @Test
    public void test() throws Exception {
        //1.创建Driver对象
        Driver driver = new com.mysql.jdbc.Driver();
        //2.调用connect方法获取连接对象
        /*
            jdbc:mysql: 协议
            localhost: mysql服务器的地址
            3306：mysql的端口号
            myemployees ：库的名称
         */
        String url = "jdbc:mysql://localhost:3306/myemployees";
        //创建一个Properties的集合对象，将账号密码放入到该集合中
        Properties properties = new Properties();
        //注意：key的值是固定的.
        properties.put("user","root");
        properties.put("password","123321");
        Connection connect = driver.connect(url, properties);
        System.out.println(connect);
    }

    /*
        通过DriverManager获取连接对象。
        作用 ：DriverManager可以帮助我们管理Driver对象。
        操作：
            1.先将Driver注册到DriverManager上面
            2.通过DriverManager获取连接对象
     */
    @Test
    public void test2() throws SQLException {
        //1.创建Driver对象
        Driver driver = new com.mysql.jdbc.Driver();
        //2.将driver注册到DriverManager上
        DriverManager.registerDriver(driver);
        //3.获取连接对象
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/myemployees",
                "root", "123321");
        System.out.println(connection);
    }


    /*
        下面的写法只针对mysql驱动
     */
    @Test
    public void test3() throws Exception {
        //1.加载com.mysql.jdbc.Driver类
        //Driver类创建了对象并将该对象注册到了DriverManager中
        Class.forName("com.mysql.jdbc.Driver");
        //2.获取连接对象
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/myemployees",
                "root", "123321");
        System.out.println(connection);
    }

    /*
        完整版 ：通过读取配置文件中的内容创建连接对象
     */
    @Test
    public void test4() throws Exception {
        InputStream fis = null;
        try {
            //读取配置文件中的内容
            /*
                Properties：
                    1.Properties是Hashtable的子类
                    2.我们一般向Properties存储的k,v都是字符串
                    3.一般我们使用Properties读取配置文件中的内容

                配置文件
                    K1=V1
                    K2=V2
                1.配置文件的扩展名其实可以是任意类型。一般我们都写成 文件名.properties
                2.配置文件中的内容。一个k,v就是一行。
                3.配置文件中的内容的类型全部是String（k,v）
                4.配置文件中的内容的k,v中间使用“=”分开。

                读取配置文件的流程：
                    ①创建Properties的对象
                    ②创建流
                    ③通过Properties的对象加载流
                    ④通过Properties的对象获取配置文件中的内容
                    ⑤关流
             */
            //①创建Properties的对象
            Properties properties = new Properties();
            //②创建流
            /*
                相对路径：相对于某个工程而言。
                绝对路径：包含盘符在内的完整路径
             */
            //相对路径 ：当前工程下
            fis = new FileInputStream("jdbc.properties");
            //相对路径 ：当前工程的src下/当前工程的字节码文件的工程的根目录下
            //放在src下的配置文件会自动在字节码文件中再放置一份。
//            fis = this.getClass().getClassLoader()
//                    .getResourceAsStream("jdbc.properties");
            //③通过Properties的对象加载流
            properties.load(fis);
            //④通过Properties的对象获取配置文件中的内容
            String driver = properties.getProperty("driver");
            String url = properties.getProperty("url");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            //1.加载com.mysql.jdbc.Driver类
            //Driver类创建了对象并将该对象注册到了DriverManager中
            Class.forName(driver);
            //2.获取连接对象
            Connection connection = DriverManager.getConnection(url,username,password);
            System.out.println(connection);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //⑤关流
            if (fis != null){
                fis.close();
            }
        }
    }
}
