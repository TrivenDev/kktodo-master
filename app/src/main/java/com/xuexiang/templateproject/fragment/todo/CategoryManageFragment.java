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
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentCategoryManageBinding;
import com.xuexiang.templateproject.fragment.data.DataBaseUtils;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.templateproject.fragment.todo.Adapter.Category.CategoryManageItemAdapter;
import com.xuexiang.templateproject.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.ColorUtils;
import com.xuexiang.xui.widget.textview.badge.Badge;

import java.util.TreeMap;

/**
 * 待办类别管理页面
 *
 * @author zheng
 * @since 2022.6.23
 */
@Page(name = "待办类别管理")
public class CategoryManageFragment extends BaseFragment<FragmentCategoryManageBinding> implements CategoryManageItemAdapter.CategoryManageItemListener{
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private CategoryManageItemAdapter categoryAdapter;
    RecyclerView categoryList;
    @Override
    protected void initViews() {
// 将这些数据绑定给Item（XML）对象
        categoryList = binding.categoryManage;

        /**
         * 数据库连接配置
         * */
        TodoHelper helper = new TodoHelper(getContext());
        dbRead = helper.getReadableDatabase();
        dbWrite = helper.getWritableDatabase();


        Cursor categoryCursor = DataBaseUtils.getCategoryCursor(dbRead);
        categoryAdapter = new CategoryManageItemAdapter(categoryCursor, getContext(), this);
        categoryList.setAdapter(categoryAdapter);
        categoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.categoryAddColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectorColorDialog(getContext(), color -> {
                    binding.categoryAddColor.setBackgroundColor(color);
                }).show();
            }
        });

        binding.categoryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeMap data = DataBaseUtils.getCategoryData(dbRead);
                String input = binding.categoryAddName.getText().toString();
                if(data.containsKey(input)){
                    XToastUtils.error("该类别已存在");
                    return;
                }
                ContentValues cv = new ContentValues();
                cv.put(TodoContract.Category.COLUMN_CATEGORY_COLOR, ((ColorDrawable)binding.categoryAddColor.getBackground()).getColor());
                cv.put(TodoContract.Category.COLUMN_CATEGORY_NAME, input);
                dbWrite.insert(TodoContract.Category.TABLE_NAME, null, cv);
                XToastUtils.success("添加成功");
                categoryAdapter.swapCursor(DataBaseUtils.getCategoryCursor(dbRead));
            }
        });


        binding.addCategoryTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.addCategory.isExpanded()){
                    binding.addCategory.collapse();
                }else{
                    binding.addCategory.expand();
                }
            }
        });
    }

    @NonNull
    @Override
    protected FragmentCategoryManageBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentCategoryManageBinding.inflate(inflater, container, false);
    }

    @Override
    public void onDeleteClick(long id,String category) {
        if("默认".equals(category)){
            XToastUtils.error("默认类别不能删除");
            return;
        }
        String selection = TodoContract.Category._ID + "=" + id;
        dbWrite.delete(TodoContract.Category.TABLE_NAME, selection, null);
        categoryAdapter.swapCursor(DataBaseUtils.getCategoryCursor(dbRead));
        XToastUtils.success("删除成功");
    }
}
