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

package com.xuexiang.templateproject.fragment.todo;

import com.github.mikephil.charting.animation.Easing;
import com.xuexiang.templateproject.fragment.data.Edit;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoContract.TodoEntry;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentTodoBinding;
import com.xuexiang.templateproject.fragment.data.DataBaseUtils;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.templateproject.fragment.todo.Adapter.TodoMain.TodoItemAdapter;
import com.xuexiang.templateproject.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.SwitchIconView;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xutil.data.DateUtils;


@Page(anim = CoreAnim.none)
public class TodoFragment extends BaseFragment<FragmentTodoBinding> implements TodoItemAdapter.TodoItemListener, View.OnClickListener {


    /**
     * 月，单位【s】
     */
    private static final int MONTH_S = 30 * 24 * 60 * 60;
    /**
     * 天，单位【s】
     */
    private static final int DAY_S = 24 * 60 * 60;

    private static final int[] COLORS = new int[]{0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800};//配置下拉刷新颜色
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private TodoItemAdapter todoAdapter;
    RecyclerView todoList;
    SwipeRefreshLayout swipeRefreshLayout;

    @NonNull
    @Override
    protected FragmentTodoBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentTodoBinding.inflate(inflater, container, false);
    }

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        // 将这些数据绑定给Item（XML）对象
        todoList = binding.todoItem;
        swipeRefreshLayout = binding.refreshLayout;

        swipeRefreshLayout.setColorSchemeColors(COLORS);
        /**
         * 数据库连接配置
         * */
        TodoHelper helper = new TodoHelper(getContext());
        dbRead = helper.getReadableDatabase();
        dbWrite = helper.getWritableDatabase();


        Cursor fireTodoCursor = DataBaseUtils.getTodoItemCursor(dbRead);
        todoAdapter = new TodoItemAdapter(fireTodoCursor, getContext(), this);
        todoList.setAdapter(todoAdapter);
        todoList.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&swipeRefreshLayout!=null) {
            //当此fragment正当前显示是，执行该操作，
            refresh();
        } else {
            // 相当于Fragment的onPause
            // System.out.println("ChatFragment ---setUserVisibleHint---isVisibleToUser - FALSE");
        }
    }


    @Override
    protected void initListeners() {
        binding.todoAddButton.setOnClickListener(this);
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
        refresh(); //第一次进入触发自动刷新，演示效果
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        loadData();
    }

    private void loadData() {
        new Handler().postDelayed(() -> {
            todoAdapter.swapCursor(DataBaseUtils.getTodoItemCursor(dbRead));
            handleRepeat(dbRead,dbWrite);
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }


    @Override
    public void onDeleteClick(long id,int finish,int repeat) {
        /**
         * 简单的确认对话框
         */
        DialogLoader.getInstance().showConfirmDialog(
                getContext(),
                "已完成项目会直接删除，未完成项目会放到回收站里面，你确定删除吗？",
                getString(R.string.lab_yes),
                (dialog, which) -> {
                    if(repeat==0){
                        if(finish==1){
                            String selection = TodoEntry._ID + "=" + id;
                            dbWrite.delete(TodoEntry.TABLE_NAME, selection, null);
                        }else {
                            String selection = TodoEntry._ID + " IN(?)";
                            String[] selectionArgs = {id+""};
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(TodoEntry.COLUMN_DELETE,1);
                            dbWrite.update(TodoEntry.TABLE_NAME,contentValues, selection, selectionArgs);
                        }
                    }else {
                        String selection = TodoEntry._ID + " IN(?)";
                        String[] selectionArgs = {id+""};
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(TodoEntry.COLUMN_DELETE,1);
                        dbWrite.update(TodoEntry.TABLE_NAME,contentValues, selection, selectionArgs);
                    }
                    todoAdapter.swapCursor(DataBaseUtils.getTodoItemCursor(dbRead));
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
    public void onFinishClick(long id, int before) {
        String selection = TodoEntry._ID + " IN(?)";
        String[] selectionArgs = {id+""};
        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoEntry.COLUMN_FINISH,before==1? 0:1);
        dbWrite.update(TodoEntry.TABLE_NAME,contentValues, selection, selectionArgs);
        todoAdapter.swapCursor(DataBaseUtils.getTodoItemCursor(dbRead));
    }

    @Override
    public void onFireClick(long id, SwitchIconView fire,int before) {
        fire.switchState();
        String selection = TodoEntry._ID + " IN(?)";
        String[] selectionArgs = {id+""};
        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoEntry.COLUMN_FIRE,before==1? 0:1);
        dbWrite.update(TodoEntry.TABLE_NAME,contentValues, selection, selectionArgs);
        refresh();
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
        openPageForResult(true,"编辑页面",bundle,CoreAnim.slide,111);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.todo_add_button){
            openPageForResult(true,"添加页面",null,CoreAnim.present,110);
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        refresh();
    }

    private void handleRepeat(SQLiteDatabase dbRead, SQLiteDatabase dbWrite){

        Cursor allCursor = DataBaseUtils.getAllTodoCursor(dbRead);
        allCursor.moveToFirst();
        for (int i = 0; i < allCursor.getCount(); i++,allCursor.moveToPosition(i)) {
            String handleTime = allCursor.getString(allCursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_HANDLE_TIME));
            int repeat = allCursor.getInt(allCursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_REPEAT));
            long id = allCursor.getLong(allCursor.getColumnIndex(TodoContract.TodoEntry._ID));
            long handleTimeM = DateUtils.string2Millis(handleTime,DateUtils.yyyyMMddHHmm.get());

            // 更新重复待办时间
            long todayTime = DateUtils.date2Millis(DateUtils.getEndOfDay(DateUtils.getNowDate()));
            // 与现在时间相差秒数
            long timeGap = (todayTime - handleTimeM) / 1000;
            if (repeat==1&&(Math.floor((float) timeGap / DAY_S)) > 0) {// 1天以上
                ContentValues cv = new ContentValues();
                String selection = TodoContract.TodoEntry._ID + " IN(?)";
                String[] selectionArgs = {id+""};
                cv.put(TodoContract.TodoEntry.COLUMN_FINISH,0);
                cv.put(TodoContract.TodoEntry.COLUMN_DELETE,0);
                cv.put(TodoContract.TodoEntry.COLUMN_HANDLE_TIME,DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMddHHmm.get()));
                dbWrite.update(TodoContract.TodoEntry.TABLE_NAME,cv,selection,selectionArgs);
            } else if (repeat==2&&(Math.floor((float) timeGap / DAY_S)) > 6) {// 一周以上
                ContentValues cv = new ContentValues();
                String selection = TodoContract.TodoEntry._ID + " IN(?)";
                String[] selectionArgs = {id+""};
                cv.put(TodoContract.TodoEntry.COLUMN_FINISH,0);
                cv.put(TodoContract.TodoEntry.COLUMN_DELETE,0);
                cv.put(TodoContract.TodoEntry.COLUMN_HANDLE_TIME,DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMddHHmm.get()));
                dbWrite.update(TodoContract.TodoEntry.TABLE_NAME,cv,selection,selectionArgs);
            } else if (repeat==3&&(Math.floor((float) timeGap / MONTH_S)) > 0) {// 1月以上
                ContentValues cv = new ContentValues();
                String selection = TodoContract.TodoEntry._ID + " IN(?)";
                String[] selectionArgs = {id+""};
                cv.put(TodoContract.TodoEntry.COLUMN_FINISH,0);
                cv.put(TodoContract.TodoEntry.COLUMN_DELETE,0);
                cv.put(TodoContract.TodoEntry.COLUMN_HANDLE_TIME,DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMddHHmm.get()));
                dbWrite.update(TodoContract.TodoEntry.TABLE_NAME,cv,selection,selectionArgs);
            }
        }
    }
}
