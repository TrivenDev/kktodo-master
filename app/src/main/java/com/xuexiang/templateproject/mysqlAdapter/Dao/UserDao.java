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

package com.xuexiang.templateproject.mysqlAdapter.Dao;

import com.xuexiang.templateproject.mysqlAdapter.Entry.User;
import com.xuexiang.templateproject.mysqlAdapter.MysqlDataUtils.DBOpenHelper;

import java.sql.*;

/**
 * 数据库操作层
 * 进行数据的增删改查API的编写，方便其他地方对其进行调用
 * */
public class UserDao {


    /**
     * 通过username进行数据的查询
     * @param username 待查询用户名
     * @return 返回User对象
     * */
    public User SelectByUsername(String username,Statement statement,Connection connection) throws SQLException {
        User user = new User();
        String sql="select * from users where username = '"+username+"'";
        ResultSet res = statement.executeQuery(sql);
        while (res.next()){
            user.setId(res.getInt("id"));
            user.setUsername(res.getString("username"));
            user.setPassword(res.getString("password"));
            user.setNickname(res.getString("nickname"));
            user.setProfile(res.getString("profile"));
        }
        DBOpenHelper.release(null,null,res);
        return user;
    }


    /**
     * 通过邮箱进行注册
     * */
    public void signByEmail(String username,String password,Statement statement,Connection connection) throws SQLException {
        String sql="INSERT users(username,PASSWORD) VALUE('"+username+"','"+password+"')";
        statement.executeUpdate(sql);
    }

    /**
     * 忘记密码的操作
     * */
    public void updatePassword(String username,String password,Statement statement,Connection connection) throws SQLException {
        String sql="UPDATE users SET `password` = '"+password+"' WHERE `username` = '"+username+"'";
        statement.executeUpdate(sql);
    }
}
