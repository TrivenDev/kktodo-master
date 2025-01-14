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

package com.xuexiang.templateproject.fragment.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import androidx.annotation.Nullable;

import com.xuexiang.templateproject.fragment.data.TodoContract.Category;
import com.xuexiang.templateproject.fragment.data.TodoContract.TodoEntry;
import com.xuexiang.templateproject.fragment.data.TodoContract.Summary;
import com.xuexiang.templateproject.fragment.data.TodoContract.CompletionRate;
import com.xuexiang.xutil.data.DateUtils;

/**
 * 数据库操作帮助类，在类初始化的时候就完成了数据库的建立
 * */
public class TodoHelper extends SQLiteOpenHelper {
    public static final int version = 1;
    public static final String name = "kk_todo.db"; // file_todo

    public TodoHelper(@Nullable Context context) {
        super(context, name, null, version);
    }
    /**
     * 创建数据库表格todo
     * create table `tablename` (
     *      _id integer primary key,
     *      finish integer,
     *      category text,
     *      things text,
     *      fire integer,
     *      begin text(time),
     *      deadline text(time),
     *      repeat integer,
     *      delete integer,
     *      handleTime text,
     * )
     * */
    private static final String create = "CREATE TABLE " + TodoEntry.TABLE_NAME + " (" +
            TodoEntry._ID + " INTEGER PRIMARY KEY,"
            + TodoEntry.COLUMN_FINISH + " INTEGER,"
            + TodoEntry.COLUMN_CATEGORY + " TEXT,"
            + TodoEntry.COLUMN_THINGS + " TEXT,"
            + TodoEntry.COLUMN_FIRE + " INTEGER,"
            + TodoEntry.COLUMN_BEGIN_TIME + " TEXT,"
            + TodoEntry.COLUMN_DEADLINE + " TEXT,"
            + TodoEntry.COLUMN_REPEAT + " INTEGER,"
            + TodoEntry.COLUMN_DELETE + " TEXT,"
            + TodoEntry.COLUMN_HANDLE_TIME + " INTEGER)";


    /**
     * 创建数据库表格category
     * create table `tablename` (
     *      _id integer primary key,
     *      category_name text,
     *      category_color text,
     * )
     * */
    private static final String create_category = "CREATE TABLE " + Category.TABLE_NAME + " (" +
            TodoContract.TodoEntry._ID + " INTEGER PRIMARY KEY,"
            + Category.COLUMN_CATEGORY_NAME + " TEXT,"
            + Category.COLUMN_CATEGORY_COLOR + " TEXT)";

    /**
     * 创建数据库表格summary
     * create table `tablename` (
     *      _id integer primary key,
     *      category_name text,
     *      category_color text,
     * )
     * */
    private static final String create_summary = "CREATE TABLE " + Summary.TABLE_NAME + " (" +
            TodoContract.TodoEntry._ID + " INTEGER PRIMARY KEY,"
            + Summary.COLUMN_SUMMARY_HEAD + " TEXT,"
            + Summary.COLUMN_SUMMARY_NICKNAME + " TEXT,"
            + Summary.COLUMN_SUMMARY_TITLE + " TEXT,"
            + Summary.COLUMN_SUMMARY_TEXT + " TEXT,"
            + Summary.COLUMN_SUMMARY_EDIT_TIME + " TEXT)";

    /**
     * 创建数据库表格summary
     * create table `tablename` (
     *      _id integer primary key,
     *      day1 integer,
     *      day2 integer,
     *      day3 integer,
     *      day4 integer,
     *      day5 integer,
     *      day6 integer,
     *      day7 integer,
     *      update_time text,
     * )
     * */
    private static final String create_completion = "CREATE TABLE " + CompletionRate.TABLE_NAME + " (" +
            TodoContract.TodoEntry._ID + " INTEGER PRIMARY KEY,"
            + CompletionRate.COLUMN_DAY1 + " INTEGER,"
            + CompletionRate.COLUMN_DAY2 + " INTEGER,"
            + CompletionRate.COLUMN_DAY3 + " INTEGER,"
            + CompletionRate.COLUMN_DAY4 + " INTEGER,"
            + CompletionRate.COLUMN_DAY5 + " INTEGER,"
            + CompletionRate.COLUMN_DAY6 + " INTEGER,"
            + CompletionRate.COLUMN_DAY7 + " INTEFER,"
            + CompletionRate.COLUMN_UPDATE_TIME + " TEXT)";


    private static final String delete = "DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME;
    private static final String delete_category = "DROP TABLE IF EXISTS " + Category.TABLE_NAME;
    private static final String delete_summary = "DROP TABLE IF EXISTS " + Summary.TABLE_NAME;
    private static final String delete_completion = "DROP TABLE IF EXISTS " + CompletionRate.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建4个数据库表
        db.execSQL(create);
        db.execSQL(create_category);
        db.execSQL(create_summary);
        db.execSQL(create_completion);

        // 放入默认类别
        ContentValues cv = new ContentValues();
        cv.put(Category.COLUMN_CATEGORY_NAME,"默认");
        cv.put(Category.COLUMN_CATEGORY_COLOR, Color.parseColor("#299EE3"));
        db.insert(Category.TABLE_NAME,null,cv);

        // 放入初始的两个总结笔记
        ContentValues cv1 = new ContentValues();
        cv1.put(Summary.COLUMN_SUMMARY_TITLE,"无线传感网络笔记");
        cv1.put(Summary.COLUMN_SUMMARY_TEXT, "WSN复习重点\n"+
                "  信道模型\n" +
                "  MAC协议：SMAC、TMAC、BMAC、DMAC\n" +
                "  路由协议：AODV、LEACH、DD、SPIN，能量路由，能量多路径\n" +
                "  时间同步模型，偏移、漂移的计算，TPSN\n" +
                "  三边定位法、Dv-hop\n" +
                "  数据融合，网络管理，操作系统泛读\n\n" +
                "从三个方面全面讲授WSN技术\n" +
                " * 网络协议：物理层、MAC层、网络层、路由协议等\n" +
                " * 关键技术：时间同步、定位、数据融合、网络管理等\n" +
                " * 应用开发：WSN操作系统、WSN开发等");
        cv1.put(Summary.COLUMN_SUMMARY_EDIT_TIME, DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMdd.get()));
        db.insert(Summary.TABLE_NAME,null,cv1);

        ContentValues cv2 = new ContentValues();
        cv2.put(Summary.COLUMN_SUMMARY_TITLE,"该怎么学C++？");
        cv2.put(Summary.COLUMN_SUMMARY_TEXT, "  C++虽然入门很容易，但C++就是一种学得越深越复杂的语言，需要花一定是时间和精力去学。\n" +
                "  如果是0基础的学员，不知道如何部署C++， 推荐重庆邮电大学郑申海老师《C++程序设计》这门课。\n" +
                "  要知道C++是一门技术语言，在技术岗面试的时候主要看面试者技术能力过不过硬，学历占比没有非技术岗那么大；" +
                "C++编程语言主要讲究逻辑，用到数学的地方不多，但是在人工智能(机器学习)方面用到了大量的概率统计的数学知识，需要有一定的基础理解其中的原理，才能精进；学习时候要抄代码，边学边敲代码，然后再改代码，然后再自己写代码一步步进阶。\n" +
                "  建议先选择自己的学习方向，找到适合自己的C++课程，一般的教程分为C++编程基础、算法、游戏编程。很多人知道C++是一门相对更容易学习的编程语言，但C++同样也需要持续学习，才能看到效果。\n" +
                "  如何持续学习？可以利用OpenCV库实现人脸检测和目标检测项目，带你迈入计算机视觉的领域。OpenCV是一个强大的计算机视觉库，提供了多种人脸检测方法，如Haar特征级联分类器、LBP和HOG特征等\n" +
                "。此外，还可以通过C++与OpenCV的机器学习模块实现高效精准的目标检测，广泛应用于安防、自动驾驶等领域\n" +
                "。\n" +
                "  在掌握C++理论知识点的同时，也教会了你如何去做项目。具备学习C++所需的知识点、实用的项目、有趣的游戏于一身的教程。\n" +
                "  可以找C++相关的入门书籍，了解C++基础数据类型、熟悉各种类型的操作方法、理解函数与类的概念，最后就是通过不断的练习再练习巩固知识点。");
        cv2.put(Summary.COLUMN_SUMMARY_EDIT_TIME, DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMdd.get()));
        db.insert(Summary.TABLE_NAME,null,cv2);


        // 初始完成率数据
        ContentValues cv3 = new ContentValues();
        cv3.put(CompletionRate.COLUMN_DAY1,50);
        cv3.put(CompletionRate.COLUMN_DAY2,66);
        cv3.put(CompletionRate.COLUMN_DAY3,20);
        cv3.put(CompletionRate.COLUMN_DAY4,52);
        cv3.put(CompletionRate.COLUMN_DAY5,30);
        cv3.put(CompletionRate.COLUMN_DAY6,87);
        cv3.put(CompletionRate.COLUMN_DAY7,50);
        cv3.put(CompletionRate.COLUMN_UPDATE_TIME,DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMddHHmm.get()));
        db.insert(CompletionRate.TABLE_NAME,null,cv3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(delete);
        db.execSQL(delete_category);
        db.execSQL(delete_summary);
        db.execSQL(delete_completion);
        onCreate(db);
    }
}
