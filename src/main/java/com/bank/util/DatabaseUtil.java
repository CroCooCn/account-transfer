package com.bank.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static String DRIVER;

    static {
        loadConfig();
    }    

    private static void loadConfig() {
        Properties properties=new Properties();
        InputStream inputStream=null;

        try {
            inputStream=DatabaseUtil.class.getClassLoader().getResourceAsStream("application.properties");

        
            if(inputStream==null) {
                throw new RuntimeException("找不到配置文件 application.properties");
            }
            //可能抛出IOException：配置文件格式不对
            properties.load(inputStream);

            //读取数据库配置
            URL=properties.getProperty("db.url");
            USERNAME=properties.getProperty("db.username");
            PASSWORD=properties.getProperty("db.password");
            DRIVER=properties.getProperty("db.driver");

            //执行db.driver类的静态方法来加载数据库驱动
            //可能抛出ClassNotFoundException：DRIVER内容错误
            Class.forName(DRIVER);
        }catch(Exception e) {
            
            System.out.println("无法连接数据库！"+e.getMessage());
            e.printStackTrace();
        }

    }

    //获取连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USERNAME,PASSWORD);
    }

    //关闭连接
    public static void closeConnection(Connection connection) {
        if(connection!=null) {
            try{
                connection.close();
            }catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
}