/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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

package com.xuexiang.templateproject.fragment.todo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentRecycleBinBinding;
import com.xuexiang.templateproject.fragment.data.DataBaseUtils;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.templateproject.fragment.todo.Adapter.RecycleBin.RecycleBinItemAdapter;
import com.xuexiang.xpage.annotation.Page;

/**
 * 回收站页面
 *
 * @author zheng
 * @since 2022.6.23
 */
@Page(name = "回收站")
public class RecycleBinFragment extends BaseFragment<FragmentRecycleBinBinding> implements RecycleBinItemAdapter.RecycleItemListener {
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private RecycleBinItemAdapter recycleAdapter;
    RecyclerView recycleList;


    @Override
    protected void initViews() {
        // 将这些数据绑定给Item（XML）对象
        recycleList = binding.recycleItem;

        /**
         * 数据库连接配置
         * */
        TodoHelper helper = new TodoHelper(getContext());
        dbRead = helper.getReadableDatabase();
        dbWrite = helper.getWritableDatabase();


        Cursor recycleCursor = DataBaseUtils.getRecycleCursor(dbRead);
        recycleAdapter = new RecycleBinItemAdapter(recycleCursor, getContext(), this);
        recycleList.setAdapter(recycleAdapter);
        recycleList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    @NonNull
    @Override
    protected FragmentRecycleBinBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentRecycleBinBinding.inflate(inflater, container, false);
    }

    @Override
    public void onDeleteClick(long id) {
        String selection = TodoContract.TodoEntry._ID + "=" + id;
        dbWrite.delete(TodoContract.TodoEntry.TABLE_NAME, selection, null);
        recycleAdapter.swapCursor(DataBaseUtils.getRecycleCursor(dbRead));
    }

    @Override
    public void onRecycleClick(long id) {
        String selection = TodoContract.TodoEntry._ID + " IN(?)";
        String[] selectionArgs = {id+""};
        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoContract.TodoEntry.COLUMN_DELETE,0);
        dbWrite.update(TodoContract.TodoEntry.TABLE_NAME,contentValues, selection, selectionArgs);

        recycleAdapter.swapCursor(DataBaseUtils.getRecycleCursor(dbRead));
    }
}
