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

package com.xuexiang.templateproject.fragment.summary.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.fragment.data.Edit;
import com.xuexiang.templateproject.fragment.data.Summary;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.todo.Adapter.TodoMain.TodoItemAdapter;
import com.xuexiang.templateproject.utils.XToastUtils;
import com.xuexiang.xui.widget.button.SwitchIconView;


/**
 * 总结的RecyclerView的数据填充
 * */
public class SummaryItemAdapter extends RecyclerView.Adapter<SummaryItemViewHolder>{

    private Cursor cursor;
    private Context context;
    final private SummaryItemListener listener;


    public SummaryItemAdapter(Cursor cursor, Context context,SummaryItemListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.listener = listener;
    }
    public interface SummaryItemListener {
        void onDetailClick(long id, Summary summary);
    }

    @NonNull
    @Override
    public SummaryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.mini_summary, parent, false);
        return new SummaryItemViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull SummaryItemViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;
        /**
         * 解析数据库数据
         * */
        String head = cursor.getString(cursor.getColumnIndex(TodoContract.Summary.COLUMN_SUMMARY_HEAD));
        String nickname = cursor.getString(cursor.getColumnIndex(TodoContract.Summary.COLUMN_SUMMARY_NICKNAME));
        String title = cursor.getString(cursor.getColumnIndex(TodoContract.Summary.COLUMN_SUMMARY_TITLE));
        String text = cursor.getString(cursor.getColumnIndex(TodoContract.Summary.COLUMN_SUMMARY_TEXT));
        String editTime = cursor.getString(cursor.getColumnIndex(TodoContract.Summary.COLUMN_SUMMARY_EDIT_TIME));
        long id = cursor.getLong(cursor.getColumnIndex(TodoContract.Summary._ID));

        /**
         * 设置监听器
         * */
        holder.total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Summary summary = new Summary(id, head, nickname, title, text, editTime);
                listener.onDetailClick(id,summary);
            }
        });

        
        
        /**
         * 显示数据
         * */
        holder.title.setText(title);
        holder.text.setText(text);
        holder.editTime.setText(editTime);
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
