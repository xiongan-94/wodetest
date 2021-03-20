package com.atguigu.java2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JDBCUtils {

    private static String driver;
    private static String url;
    private static String username;
    private static String password;


    static{
        InputStream fis = null;
        try {
            Properties properties = new Properties();
            fis = new FileInputStream("jdbc.properties");
            properties.load(fis);

            driver = properties.getProperty("driver");
            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取Mysql的连接对象
     * @return
     */
    public static Connection getConnection(){
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        }catch (Exception e){
            //将编译时转成运行时异常
            throw new RuntimeException(e.getMessage());
        }
    }

    /*
         关闭资源
     */
    public static void close(Connection connection, PreparedStatement ps) {
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (ps != null){
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void close(Connection connection, PreparedStatement ps, ResultSet rs) {
       close(connection,ps);
       if (rs != null){
           try {
               rs.close();
           } catch (SQLException throwables) {
               throwables.printStackTrace();
           }
       }
    }
}
