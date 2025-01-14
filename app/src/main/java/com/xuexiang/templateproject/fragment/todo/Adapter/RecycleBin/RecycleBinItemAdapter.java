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

package com.xuexiang.templateproject.fragment.todo.Adapter.RecycleBin;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.constant.TimeConstants;
import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.utils.XToastUtils;
import com.xuexiang.xutil.data.DateUtils;

public class RecycleBinItemAdapter extends RecyclerView.Adapter<RecycleBinViewHolder>{
    private Cursor cursor;
    private Context context;
    final private RecycleItemListener listener;

    public RecycleBinItemAdapter(Cursor cursor, Context context, RecycleItemListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.listener = listener;
    }
    

    public interface RecycleItemListener {
        void onDeleteClick(long id);

        void onRecycleClick(long id);
    }



    
    @NonNull
    @Override
    public RecycleBinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.mini_recycle_bin, parent, false);
        return new RecycleBinViewHolder(view);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecycleBinViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        int percent_number;

        /**
         * 解析数据库数据
         * */
        int category = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_CATEGORY));
        String things = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_THINGS));
        int finish = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_FINISH));
        int fire = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_FIRE));
        long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry._ID));
        int delete = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DELETE));
        String beginTime = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_BEGIN_TIME));
        String deadline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DEADLINE));
        int repeat = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_REPEAT));

        System.out.println("================================++++++++++++++++++++++++");

        /**
         * 设置监听器
         * */
        holder.recycleDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteClick(id);
            }
        });

        holder.recycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecycleClick(id);
            }
        });

        holder.recycleFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XToastUtils.error("请恢复之后进行勾选");
            }
        });
        /**
         * 显示数据
         * */
        holder.recycleThings.setText(things);
        holder.itemView.setTag(id);
        if (finish==1){
            holder.recycleFinish.setChecked(true);
        }else if (finish == 0) {
            holder.recycleFinish.setChecked(false);
        }
        // 更新进度条的进度和颜色
        if(finish==1){
            percent_number = 100;
            holder.recyclePercent.setProgressTintList( context.getResources().getColorStateList(R.color.toast_success_color));
        }else {
            if(deadline.equals("0")){
                percent_number = 100;
                holder.recyclePercent.setProgressTintList( context.getResources().getColorStateList(R.color.xui_btn_gray_normal_color));
            }else {
                String now = DateUtils.date2String(DateUtils.getNowDate(), DateUtils.yyyyMMddHHmm.get());
                long spanBToN = DateUtils.getTimeSpan(now, beginTime, DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
                long spanNToD = DateUtils.getTimeSpan(now, deadline, DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);
                long spanBToD = DateUtils.getTimeSpan(beginTime, deadline, DateUtils.yyyyMMddHHmm.get(), TimeConstants.MIN);

                if(spanNToD==(spanBToN+spanBToD)){//截止时间设置在开始时间前面
                    percent_number = -1;
                } else if(spanBToN==(spanBToD+spanNToD)){//已经结束
                    percent_number = -1;
                } else {
                    percent_number = (int)(((float)spanNToD/spanBToD)*100);
                }
                if(percent_number<20){
                    holder.recyclePercent.setProgressTintList( context.getResources().getColorStateList(R.color.xui_config_color_red));
                }else {
                    holder.recyclePercent.setProgressTintList( context.getResources().getColorStateList(R.color.colorPrimary));
                }
            }
        }
        if(percent_number!=-1){
            holder.recyclePercent.setProgress(percent_number);
        }else {
            holder.recyclePercent.setProgress(100);
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
