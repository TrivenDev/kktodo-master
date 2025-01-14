/*
 * Copyright (C) 2022 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.templateproject.mysqlAdapter.MysqlDataUtils;

import java.sql.*;

/**
 * 远程Mysql连接助手，在这里进行配置，方便调用，不用每次进行连接配置
 * */
public class DBOpenHelper {
   private static String diver = "com.mysql.jdbc.Driver";
   //加入utf-8是为了后面往表中输入中文，表中不会出现乱码的情况
   private static String url = "jdbc:mysql://119.3.150.207:3306/firetodo?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8&useSSL=false";
   private static String user = "firetodo";//用户名firetodo
   private static String password = "WrhWdXNP5XhEfwzy";//密码WrhWdXNP5XhEfwzy
    /*
    * 连接数据库
    * */
   public static Connection getConnection(){
       Connection connction = null;
       try {
           Class.forName(diver);
           connction = DriverManager.getConnection(url,user,password);//获取连接
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return connction;
   }

    public static void release(Connection connection, Statement statement, ResultSet resultSet){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(statement!=null){
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}