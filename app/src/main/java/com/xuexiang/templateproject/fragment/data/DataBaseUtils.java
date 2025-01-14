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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import com.xuexiang.xutil.data.DateUtils;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * 数据库操作语句，进行API封装，方便外部调用
 * */
public class DataBaseUtils {
    /**
     * 查询待办的数据，返回Cursor
     *
     * */
    public static Cursor getTodoItemCursor(SQLiteDatabase dbRead) {
        String selection = TodoContract.TodoEntry.COLUMN_DELETE + " IN(?)";
        String[] selectionArgs = {"0"};
        String orderBy = TodoContract.TodoEntry.COLUMN_FIRE + " DESC," + TodoContract.TodoEntry.COLUMN_CATEGORY+ " DESC";

        Cursor cursor = dbRead.query(TodoContract.TodoEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy);
        return cursor;
    }

    /**
     * 查询已经删除（伪删除）的数据，返回Cursor
     *
     * */
    public static Cursor getRecycleCursor(SQLiteDatabase dbRead) {
        String selection = TodoContract.TodoEntry.COLUMN_DELETE + " IN(?)";
        String[] selectionArgs = {"1"};
        String orderBy = TodoContract.TodoEntry.COLUMN_FIRE + " DESC," + TodoContract.TodoEntry.COLUMN_CATEGORY+ " DESC";

        Cursor cursor = dbRead.query(TodoContract.TodoEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy);
        return cursor;
    }


    /**
     * 查询已经待办类别的数据，返回Cursor
     *
     * */
    public static Cursor getCategoryCursor(SQLiteDatabase dbRead) {

        Cursor cursor = dbRead.query(TodoContract.Category.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        return cursor;
    }

    /**
     * 查询已经待办类别的数据，返回Cursor
     *
     * */
    public static TreeMap<String,Integer> getCategoryData(SQLiteDatabase dbRead) {

        TreeMap<String, Integer> data = new TreeMap<>();
        data.put("默认", Color.parseColor("#299EE3"));
        Cursor cursor = dbRead.query(TodoContract.Category.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++,cursor.moveToPosition(i)) {
            String categoryName = cursor.getString(cursor.getColumnIndex(TodoContract.Category.COLUMN_CATEGORY_NAME));
            int categoryColor = cursor.getInt(cursor.getColumnIndex(TodoContract.Category.COLUMN_CATEGORY_COLOR));
            data.put(categoryName,categoryColor);
        }
        return data;
    }


    /**
     * 查询待办的所以数据，返回Cursor
     *
     * */
    public static Cursor getAllTodoCursor(SQLiteDatabase dbRead) {
        String selection = TodoContract.TodoEntry.COLUMN_REPEAT + " IN(?,?,?)";
        String[] selectionArgs = {"1","2","3"};
        Cursor cursor = dbRead.query(TodoContract.TodoEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);
        return cursor;
    }




    /**
     * 查询每隔一日的数据，返回Cursor
     *
     * */
    public static Cursor getRepeatCursorDay(SQLiteDatabase dbRead) {
        String selection = TodoContract.TodoEntry.COLUMN_REPEAT + " IN(?)";
        String[] selectionArgs = {"1"};
        String orderBy = TodoContract.TodoEntry.COLUMN_CATEGORY+ " DESC";

        Cursor cursor = dbRead.query(TodoContract.TodoEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy);
        return cursor;
    }    /**
     * 查询每隔一周的数据，返回Cursor
     *
     * */
    public static Cursor getRepeatCursorWeek(SQLiteDatabase dbRead) {
        String selection = TodoContract.TodoEntry.COLUMN_REPEAT + " IN(?)";
        String[] selectionArgs = {"2"};
        String orderBy = TodoContract.TodoEntry.COLUMN_CATEGORY+ " DESC";

        Cursor cursor = dbRead.query(TodoContract.TodoEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy);
        return cursor;
    }
    /**
     * 查询每隔一月的数据，返回Cursor
     *
     * */
    public static Cursor getRepeatCursorMonth(SQLiteDatabase dbRead) {
        String selection = TodoContract.TodoEntry.COLUMN_REPEAT + " IN(?)";
        String[] selectionArgs = {"3"};
        String orderBy = TodoContract.TodoEntry.COLUMN_CATEGORY+ " DESC";

        Cursor cursor = dbRead.query(TodoContract.TodoEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy);
        return cursor;
    }/**
     * 查询每个待办类别下面的待办个数，返回个数int
     *
     * */
    public static int getCategoryCount(SQLiteDatabase dbRead,String category) {
        String selection = TodoContract.TodoEntry.COLUMN_CATEGORY + " in(?)";
        String[] selectionArgs = {category};
        Cursor cursor = dbRead.query(TodoContract.TodoEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);
        return cursor.getCount();
    }


    /**
     * 查询总结的数据，返回Cursor
     *
     * */
    public static Cursor getSummaryItemCursor(SQLiteDatabase dbRead) {
        String orderBy ="date("+TodoContract.Summary.COLUMN_SUMMARY_EDIT_TIME+ ") DESC";

        Cursor cursor = dbRead.query(TodoContract.Summary.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                orderBy);
        return cursor;
    }


    /**
     * 查询总结的数据，返回Cursor
     *
     * */
    public static HashMap<String,Integer> getStatisticTotal(SQLiteDatabase dbRead) {
        HashMap<String, Integer> data = new HashMap<String, Integer>();
        int finishNumber = 0;
        Cursor cursor = getTodoItemCursor(dbRead);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++,cursor.moveToPosition(i)) {
            int finish = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_FINISH));
            if(finish==1){
                finishNumber++;
            }
        }
        data.put("totalNumber",cursor.getCount());
        data.put("finishNumber",finishNumber);
        return data;
    }


    /**
     * 查询完成率的数据，返回HashMap<String,Integer>
     * @param flash 是否进行日期过期判断
     *
     * */
    public static HashMap<String,Integer> getCompletionRateData(SQLiteDatabase dbRead,SQLiteDatabase dbWrite,boolean flash) {
        HashMap<String, Integer> data = new HashMap<>();
        Cursor cursor = dbRead.query(TodoContract.CompletionRate.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToLast();// 由于只有一条数据
        long id = cursor.getLong(cursor.getColumnIndex(TodoContract.CompletionRate._ID));
        data.put("day1",cursor.getInt(cursor.getColumnIndex(TodoContract.CompletionRate.COLUMN_DAY1)));
        data.put("day2",cursor.getInt(cursor.getColumnIndex(TodoContract.CompletionRate.COLUMN_DAY2)));
        data.put("day3",cursor.getInt(cursor.getColumnIndex(TodoContract.CompletionRate.COLUMN_DAY3)));
        data.put("day4",cursor.getInt(cursor.getColumnIndex(TodoContract.CompletionRate.COLUMN_DAY4)));
        data.put("day5",cursor.getInt(cursor.getColumnIndex(TodoContract.CompletionRate.COLUMN_DAY5)));
        data.put("day6",cursor.getInt(cursor.getColumnIndex(TodoContract.CompletionRate.COLUMN_DAY6)));
        data.put("day7",cursor.getInt(cursor.getColumnIndex(TodoContract.CompletionRate.COLUMN_DAY7)));
        if (flash){
            String updateTime = cursor.getString(cursor.getColumnIndex(TodoContract.CompletionRate.COLUMN_UPDATE_TIME));
            long todayTime = DateUtils.date2Millis(DateUtils.getEndOfDay(DateUtils.getNowDate()));
            long handleTimeM = DateUtils.string2Millis(updateTime, DateUtils.yyyyMMddHHmm.get());
            long timeGap = (todayTime - handleTimeM) / 1000;
            int round = (int) Math.floor((float) timeGap / (24 * 60 * 60));
            if (round > 0) {//数据过期，round就是过期天数
                String selection = TodoContract.TodoEntry._ID + " IN(?)";
                String[] selectionArgs = {id+""};
                ContentValues cv = new ContentValues();
                for (int i = 1; i <=7 ; i++) {
                    if(i<=7-round){
                        cv.put("DAY"+i,data.get("day" + (i+round)%7));//数据循环左移
                    }else {
                        cv.put("DAY"+i,0);// 空数据填0
                    }
                }
                cv.put(TodoContract.CompletionRate.COLUMN_UPDATE_TIME,DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMddHHmm.get()));
                dbWrite.update(TodoContract.CompletionRate.TABLE_NAME,cv, selection, selectionArgs);
                data = getCompletionRateData(dbRead,dbWrite, false);
            }
        }
        return data;
    }
}
