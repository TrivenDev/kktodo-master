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

package com.xuexiang.templateproject.fragment.todo.Adapter.TodoMain;

import android.content.Context;
import android.database.Cursor;

import com.xuexiang.constant.TimeConstants;
import com.xuexiang.templateproject.fragment.data.DataBaseUtils;
import com.xuexiang.templateproject.fragment.data.Edit;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.xui.widget.button.SwitchIconView;
import com.xuexiang.xutil.data.DateUtils;

import java.util.TreeMap;


/**
 * 今日待办栏目
 * 已完成部分
 * */
public class TodoItemAdapter extends RecyclerView.Adapter<TodoItemViewHolder>{

    private Cursor cursor;
    private Context context;
    final private TodoItemListener listener;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;

    public TodoItemAdapter(Cursor cursor, Context context, TodoItemListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.listener = listener;
    }
    public interface TodoItemListener {
        void onDeleteClick(long id,int finish,int repeat);

        void onFinishClick(long id, int before);

        void onFireClick(long id, SwitchIconView fire,int before);
        void onEditClick(Edit edit);
    }


    @NonNull
    @Override
    public TodoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.mini_todo, parent, false);
        TodoHelper helper = new TodoHelper(parent.getContext());
        dbRead = helper.getReadableDatabase();
        dbWrite = helper.getWritableDatabase();
        return new TodoItemViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull TodoItemViewHolder holder, int position) {
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
        int delete = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DELETE));
        String beginTime = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_BEGIN_TIME));
        String handleTime = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_HANDLE_TIME));
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
                listener.onDeleteClick(id,holder.todoFinish.isChecked()?1:0,repeat);
            }
        });
        holder.todoFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFireClick(id,holder.todoFire,fire);
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

        holder.todoFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkState = holder.todoFinish.isChecked();
                listener.onFinishClick(id,checkState?1:0);
                holder.todoFinish.setChecked(!checkState);
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
        if (fire==1){
            holder.todoFire.setIconEnabled(true);
        }else {
            holder.todoFire.setIconEnabled(false);
        }
        if (finish==1){
            holder.todoFinish.setChecked(true);
        }else if (finish == 0) {
            holder.todoFinish.setChecked(false);
        }


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
            if(finish==1){
                holder.todoPercent.setProgressTintList( context.getResources().getColorStateList(R.color.progress_success_color));
            }
        }




        // 重复待办的进度条刷新
        if(repeat!=0&&!deadline.equals("0")){
            System.out.println("pppppppppppppppppppppp");
            holder.todoPercent.setProgressTintList( context.getResources().getColorStateList(R.color.colorPrimary));
            if(finish==1){
                percent_number = 100;
                holder.todoPercent.setProgressTintList( context.getResources().getColorStateList(R.color.progress_success_color));
            }
            String now = DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMddHHmm.get());
            if(repeat==1){
                long spanNToD = DateUtils.getTimeSpan(now, DateUtils.date2String(DateUtils.getEndOfDay(DateUtils.getNowDate()),DateUtils.yyyyMMddHHmm.get()), DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
                long spanBToD = DateUtils.getTimeSpan(DateUtils.date2String(DateUtils.getStartOfDay(DateUtils.getNowDate()),DateUtils.yyyyMMddHHmm.get()), DateUtils.date2String(DateUtils.getEndOfDay(DateUtils.getNowDate()),DateUtils.yyyyMMddHHmm.get()), DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
                percent_number = (int)(((float)spanNToD/spanBToD)*100);
            }else if (repeat == 2){
                long spanNToD = DateUtils.getTimeSpan(now, DateUtils.date2String(DateUtils.getEndOfDay(DateUtils.string2Date(handleTime,DateUtils.yyyyMMddHHmm.get()),6),DateUtils.yyyyMMddHHmm.get()), DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
                long spanBToD = DateUtils.getTimeSpan(DateUtils.date2String(DateUtils.getStartOfDay(DateUtils.string2Date(handleTime,DateUtils.yyyyMMddHHmm.get())),DateUtils.yyyyMMddHHmm.get()), DateUtils.date2String(DateUtils.getEndOfDay(DateUtils.string2Date(handleTime,DateUtils.yyyyMMddHHmm.get()),6),DateUtils.yyyyMMddHHmm.get()), DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
                percent_number = (int)(((float)spanNToD/spanBToD)*100);
            }else if (repeat == 3){
                long spanNToD = DateUtils.getTimeSpan(now, DateUtils.date2String(DateUtils.getEndOfDay(DateUtils.string2Date(handleTime,DateUtils.yyyyMMddHHmm.get()),29),DateUtils.yyyyMMddHHmm.get()), DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
                long spanBToD = DateUtils.getTimeSpan(DateUtils.date2String(DateUtils.getStartOfDay(DateUtils.string2Date(handleTime,DateUtils.yyyyMMddHHmm.get())),DateUtils.yyyyMMddHHmm.get()), DateUtils.date2String(DateUtils.getEndOfDay(DateUtils.string2Date(handleTime,DateUtils.yyyyMMddHHmm.get()),29),DateUtils.yyyyMMddHHmm.get()), DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
                percent_number = (int)(((float)spanNToD/spanBToD)*100);
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
