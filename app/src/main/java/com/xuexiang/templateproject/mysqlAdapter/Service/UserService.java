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

package com.xuexiang.templateproject.mysqlAdapter.Service;

import com.xuexiang.templateproject.mysqlAdapter.Dao.UserDao;
import com.xuexiang.templateproject.mysqlAdapter.Entry.User;
import com.xuexiang.templateproject.mysqlAdapter.MysqlDataUtils.DBOpenHelper;
import com.xuexiang.templateproject.utils.XToastUtils;

import java.sql.*;


/**
 * 服务调用封装，方便进行其他操作
 * */
public class UserService {
    public User SelectByUsername(String username){
        User user = null;
        Connection connection=null;
        Statement statement=null;
        try {
            connection= DBOpenHelper.getConnection();
            statement=connection.createStatement();
            user = new UserDao().SelectByUsername(username, statement, connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            DBOpenHelper.release(connection,statement,null);
        }
        return user;
    }


    public boolean signByEmail(String username,String password){
        boolean flag = false;
        Connection connection=null;
        Statement statement=null;
        try {
            connection= DBOpenHelper.getConnection();
            statement=connection.createStatement();
            User user = new UserDao().SelectByUsername(username, statement, connection);
            if(user.getUsername()==null){
                new UserDao().signByEmail(username, password,statement, connection);
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            DBOpenHelper.release(connection,statement,null);
        }
        return flag;
    }


    public boolean updatePassword(String username,String password){
        boolean flag = false;
        Connection connection=null;
        Statement statement=null;
        try {
            connection= DBOpenHelper.getConnection();
            statement=connection.createStatement();
            User user = new UserDao().SelectByUsername(username, statement, connection);
            if(user.getUsername()!=null){
                new UserDao().updatePassword(username, password,statement, connection);
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            DBOpenHelper.release(connection,statement,null);
        }
        return flag;
    }
}
