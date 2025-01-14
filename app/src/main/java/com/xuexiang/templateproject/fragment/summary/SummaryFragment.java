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

package com.xuexiang.templateproject.fragment.summary;

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

import com.github.mikephil.charting.animation.Easing;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentSummaryBinding;
import com.xuexiang.templateproject.fragment.data.DataBaseUtils;
import com.xuexiang.templateproject.fragment.data.Summary;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.templateproject.fragment.summary.Adapter.SummaryItemAdapter;
import com.xuexiang.templateproject.fragment.todo.EditFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;

/**
 * 总结页面，对数据进行绑定
 *
 * @author zheng
 * @since 2022.6.23
 * */
@Page(anim = CoreAnim.none)
public class SummaryFragment extends BaseFragment<FragmentSummaryBinding> implements SummaryItemAdapter.SummaryItemListener {
    private static final int[] COLORS = new int[]{0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800};//配置下拉刷新颜色
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private SummaryItemAdapter summaryAdapter;
    RecyclerView summaryList;
    SwipeRefreshLayout swipeRefreshLayout;

    @NonNull
    @Override
    protected FragmentSummaryBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentSummaryBinding.inflate(inflater, container, false);
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
        summaryList = binding.summaryItem;
        swipeRefreshLayout = binding.refreshLayoutSummary;

        swipeRefreshLayout.setColorSchemeColors(COLORS);
        /**
         * 数据库连接配置
         * */
        TodoHelper helper = new TodoHelper(getContext());
        dbRead = helper.getReadableDatabase();
        dbWrite = helper.getWritableDatabase();


        Cursor summaryItemCursor = DataBaseUtils.getSummaryItemCursor(dbRead);
        summaryAdapter = new SummaryItemAdapter(summaryItemCursor, getContext(),this);
        summaryList.setAdapter(summaryAdapter);
        summaryList.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            refresh();
        } else {
            // 相当于Fragment的onPause
            // System.out.println("ChatFragment ---setUserVisibleHint---isVisibleToUser - FALSE");
        }
    }


    @Override
    protected void initListeners() {
        binding.summaryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPageForResult(true,"添加总结",null,CoreAnim.present,121);
            }
        });
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
            summaryAdapter.swapCursor(DataBaseUtils.getSummaryItemCursor(dbRead));
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void onDetailClick(long id, Summary summary) {
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);
        bundle.putString("head",summary.getHead());
        bundle.putString("nickname",summary.getNickname());
        bundle.putString("title",summary.getTitle());
        bundle.putString("text",summary.getText());
        bundle.putString("editTime",summary.getEditTime());
        openPageForResult(true,"详细总结",bundle,CoreAnim.slide,120);
    }


    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        refresh();
    }
}
