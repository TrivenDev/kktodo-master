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
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentRepeatManageBinding;
import com.xuexiang.templateproject.fragment.data.DataBaseUtils;
import com.xuexiang.templateproject.fragment.data.Edit;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.templateproject.fragment.todo.Adapter.Repeat.RepeatAdapter;
import com.xuexiang.templateproject.fragment.todo.Adapter.TodoMain.TodoItemAdapter;
import com.xuexiang.templateproject.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xui.widget.dialog.DialogLoader;

/**
 * 重复待办管理页面
 *
 * @author zheng
 * @since 2022.6.23
 */
@Page(name = "重复待办管理")
public class RepeatManageFragment extends BaseFragment<FragmentRepeatManageBinding> implements RepeatAdapter.RepeatListener{
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private RepeatAdapter dayAdapter;
    private RepeatAdapter weekAdapter;
    private RepeatAdapter monthAdapter;
    RecyclerView dayList;
    RecyclerView weekList;
    RecyclerView monthList;

    @Override
    protected void initViews() {
        // 将这些数据绑定给Item（XML）对象
        dayList = binding.repeatManageDay;
        weekList = binding.repeatManageWeek;
        monthList = binding.repeatManageMonth;
        /**
         * 数据库连接配置
         * */
        TodoHelper helper = new TodoHelper(getContext());
        dbRead = helper.getReadableDatabase();
        dbWrite = helper.getWritableDatabase();


        Cursor dayCursor = DataBaseUtils.getRepeatCursorDay(dbRead);
        dayAdapter = new RepeatAdapter(dayCursor, getContext(), this);
        dayList.setAdapter(dayAdapter);
        dayList.setLayoutManager(new LinearLayoutManager(getContext()));

        Cursor weekCursor = DataBaseUtils.getRepeatCursorWeek(dbRead);
        weekAdapter = new RepeatAdapter(weekCursor, getContext(), this);
        weekList.setAdapter(weekAdapter);
        weekList.setLayoutManager(new LinearLayoutManager(getContext()));

        Cursor monthCursor = DataBaseUtils.getRepeatCursorMonth(dbRead);
        monthAdapter = new RepeatAdapter(monthCursor, getContext(), this);
        monthList.setAdapter(monthAdapter);
        monthList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @NonNull
    @Override
    protected FragmentRepeatManageBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentRepeatManageBinding.inflate(inflater, container, false);
    }

    @Override
    public void onDeleteClick(long id) {
        /**
         * 简单的确认对话框
         */
        DialogLoader.getInstance().showConfirmDialog(
                getContext(),
                "这里的重复待办会直接删除，你确定吗？",
                getString(R.string.lab_yes),
                (dialog, which) -> {
                    String selection = TodoContract.TodoEntry._ID + " IN(?)";
                    String[] selectionArgs = {id+""};
                    dbWrite.delete(TodoContract.TodoEntry.TABLE_NAME, selection, selectionArgs);
                    dayAdapter.swapCursor(DataBaseUtils.getRepeatCursorDay(dbRead));
                    weekAdapter.swapCursor(DataBaseUtils.getRepeatCursorWeek(dbRead));
                    monthAdapter.swapCursor(DataBaseUtils.getRepeatCursorMonth(dbRead));
                    dialog.dismiss();
                    XToastUtils.success("删除成功！");
                },
                getString(R.string.lab_no),
                (dialog, which) -> {
                    dialog.dismiss();
                    XToastUtils.success("取消删除！");
                }
        );
    }

    @Override
    public void onEditClick(Edit edit) {
        //openNewPage(EditFragment.class,"edit",edit);
        Bundle bundle = new Bundle();
        bundle.putLong("id",edit.getId());
        bundle.putString("things",edit.getThings());
        bundle.putString("category",edit.getCategory());
        bundle.putInt("finish",edit.getFinish());
        bundle.putInt("fire",edit.getFire());
        bundle.putString("beginTime",edit.getBeginTime());
        bundle.putString("deadline",edit.getDeadline());
        bundle.putInt("repeat",edit.getRepeat());
        openPageForResult(EditFragment.class,bundle,100);
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (data != null&&resultCode == 500) {
            dayAdapter.swapCursor(DataBaseUtils.getRepeatCursorDay(dbRead));
            weekAdapter.swapCursor(DataBaseUtils.getRepeatCursorWeek(dbRead));
            monthAdapter.swapCursor(DataBaseUtils.getRepeatCursorMonth(dbRead));
        }
    }
}
