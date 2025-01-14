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

package com.xuexiang.templateproject.fragment.todo.Adapter.Repeat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.constant.TimeConstants;
import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.fragment.data.DataBaseUtils;
import com.xuexiang.templateproject.fragment.data.Edit;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.xutil.data.DateUtils;

import java.util.TreeMap;


/**
 * 今日待办栏目
 * 已完成部分
 * */
public class RepeatAdapter extends RecyclerView.Adapter<RepeatViewHolder>{

    private Cursor cursor;
    private Context context;
    final private RepeatListener listener;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;

    public RepeatAdapter(Cursor cursor, Context context, RepeatListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.listener = listener;
    }
    public interface RepeatListener {
        void onDeleteClick(long id);

        void onEditClick(Edit edit);
    }


    @NonNull
    @Override
    public RepeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.mini_repeat, parent, false);
        TodoHelper helper = new TodoHelper(parent.getContext());
        dbRead = helper.getReadableDatabase();
        return new RepeatViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RepeatViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        int percent_number;
        /**
         * 解析数据库数据
         * */
        String category = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_CATEGORY));
        String things = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_THINGS));
        int finish = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_FINISH));
        int fire = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_FIRE));
        long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry._ID));
        String beginTime = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_BEGIN_TIME));
        String deadline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DEADLINE));
        int repeat = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_REPEAT));

        // 删除过期重复待办，为了体现加急普通待办的已经过期的状态，故不对非重复待办进行删除
        if(repeat!=0){

            if(!deadline.equals("0")&&((DateUtils.string2Millis(deadline,DateUtils.yyyyMMddHHmm.get()))<=(System.currentTimeMillis()))){
                String selection = TodoContract.TodoEntry._ID + " IN(?)";
                String[] selectionArgs = {id+""};
                dbWrite.delete(TodoContract.TodoEntry.TABLE_NAME,selection,selectionArgs);
            }
        }

        /**
         * 设置监听器
         * */
        holder.todoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteClick(id);
            }
        });

        //String finalDeadline = deadline;
        holder.todoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Edit edit = new Edit(id,category,finish,things,fire, deadline,beginTime,repeat);
                listener.onEditClick(edit);
            }
        });
        /**
         * 显示数据
         * */

        holder.todoCategory.setBackgroundColor(Color.parseColor("#299EE3"));
        holder.todoCategoryName.setText("默认");

        TreeMap<String,Integer> data = DataBaseUtils.getCategoryData(dbRead);
        System.out.println(data+"====================================="+category);
        if(data.get(category)!=null){
            holder.todoCategory.setBackgroundColor(data.get(category));
            holder.todoCategoryName.setText(category);
        }
        holder.todoThings.setText(things);
        holder.itemView.setTag(id);


        System.out.println(System.currentTimeMillis());
        // 更新进度条的进度和颜色
        if(deadline.equals("0")){ // 没有设置截止时间（满灰）
            percent_number = 100;
            holder.todoPercent.setProgressTintList( context.getResources().getColorStateList(R.color.xui_btn_gray_normal_color));
        }else {
            String now = DateUtils.date2String(DateUtils.getNowDate(), DateUtils.yyyyMMddHHmm.get());
            long spanBToN = DateUtils.getTimeSpan(now, beginTime, DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
            long spanNToD = DateUtils.getTimeSpan(now, deadline, DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
            long spanBToD = DateUtils.getTimeSpan(beginTime, deadline, DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
            if(spanNToD==(spanBToN+spanBToD)&&spanBToN!=0){//截止时间设置在开始时间前面（满红）
                percent_number = -1;
            } else if(spanBToN==(spanBToD+spanNToD)){//已经结束（满红）
                percent_number = -1;
            } else {
                percent_number = (int)(((float)spanNToD/spanBToD)*100);
                holder.todoPercent.setProgressTintList( context.getResources().getColorStateList(R.color.colorPrimary));
            }
            if(percent_number<20){ // 包含 = -1的特色情况
                holder.todoPercent.setProgressTintList( context.getResources().getColorStateList(R.color.xui_config_color_red));
            }
        }
        if(percent_number!=-1){
            holder.todoPercent.setProgress(percent_number);
        }else {
            holder.todoPercent.setProgress(100);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    /**
     * 刷新数据
     * */
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }
}
