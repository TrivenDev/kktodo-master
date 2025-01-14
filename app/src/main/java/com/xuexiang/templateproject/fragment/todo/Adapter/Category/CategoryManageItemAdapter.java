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

package com.xuexiang.templateproject.fragment.todo.Adapter.Category;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
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
import com.xuexiang.templateproject.fragment.todo.SelectorColorDialog;

public class CategoryManageItemAdapter extends RecyclerView.Adapter<CategoryManageViewHolder>{
    private Cursor cursor;
    private Context context;
    final private CategoryManageItemListener listener;
    private SQLiteDatabase dbWrite;

    public CategoryManageItemAdapter(Cursor cursor, Context context, CategoryManageItemListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.listener = listener;
    }

    public interface CategoryManageItemListener {
        void onDeleteClick(long id,String category);
    }



    
    @NonNull
    @Override
    public CategoryManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TodoHelper helper = new TodoHelper(context);
        dbWrite = helper.getWritableDatabase();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.mini_category, parent, false);
        return new CategoryManageViewHolder(view);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CategoryManageViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;

        /**
         * 解析数据库数据
         * */
        String categoryName = cursor.getString(cursor.getColumnIndex(TodoContract.Category.COLUMN_CATEGORY_NAME));
        int categoryColor = cursor.getInt(cursor.getColumnIndex(TodoContract.Category.COLUMN_CATEGORY_COLOR));
        long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry._ID));

        /**
         * 设置监听器
         * */
        holder.categoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteClick(id,categoryName);
            }
        });
        holder.categoryColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectorColorDialog(context, color -> {
                    holder.categoryColor.setBackgroundColor(color);
                    String selection = TodoContract.Category._ID + " IN(?)";
                    String[] selectionArgs = {id+""};
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TodoContract.Category.COLUMN_CATEGORY_COLOR,((ColorDrawable)holder.categoryColor.getBackground()).getColor());
                    dbWrite.update(TodoContract.Category.TABLE_NAME,contentValues, selection, selectionArgs);
                }).show();
            }
        });
        /**
         * 显示数据
         * */
        holder.categoryName.setText(categoryName);
        holder.categoryColor.setBackgroundColor(categoryColor);
        holder.itemView.setTag(id);
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
