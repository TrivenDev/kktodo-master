package com.xuexiang.templateproject.fragment.summary;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentSummaryAddBinding;
import com.xuexiang.templateproject.databinding.FragmentSummaryEditBinding;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.utils.ViewUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xutil.data.DateUtils;
import com.xuexiang.xutil.display.Colors;

/**
 * 总结添加界面
 *
 * @author zheng
 * @since 2022.6.23
 */
@Page(name = "添加总结")
public class SummaryAddFragment extends BaseFragment<FragmentSummaryAddBinding> {
    Toolbar toolbar;
    CollapsingToolbarLayout collapseLayout;
    AppBarLayout appbarLayout;
    MultiLineEditText editText;
    EditText editTitle;
    private SQLiteDatabase dbWrite;


    @NonNull
    @Override
    protected FragmentSummaryAddBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentSummaryAddBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initArgs() {
        super.initArgs();
        StatusBarUtils.translucent(getActivity(), Colors.TRANSPARENT);
        StatusBarUtils.setStatusBarLightMode(getActivity());
    }

    @Override
    protected TitleBar initTitle() {

        return null;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        TodoHelper helper = new TodoHelper(getContext());
        dbWrite = helper.getWritableDatabase();



        toolbar = binding.summaryAddAppbarLayoutToolbar;
        collapseLayout = binding.summaryAddCollapseLayout;
        appbarLayout = binding.summaryAddAppbarLayout;
        editTitle = binding.summaryAddTitle;

        editText = binding.summaryAddText;
        toolbar.inflateMenu(R.menu.menu_summary_edit);
        toolbar.setNavigationOnClickListener(v -> popToBack());
        ViewUtils.setToolbarLayoutTextFont(collapseLayout);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if(itemId==R.id.action_summary_add){
                    String addTitle = editTitle.getText().toString();
                    String addText = editText.getContentText();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TodoContract.Summary.COLUMN_SUMMARY_EDIT_TIME, DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMdd.get()));
                    contentValues.put(TodoContract.Summary.COLUMN_SUMMARY_TITLE,addTitle);
                    contentValues.put(TodoContract.Summary.COLUMN_SUMMARY_TEXT,addText);
                    dbWrite.insert(TodoContract.Summary.TABLE_NAME,null,contentValues);
                    popToBack();
                }
                return false;
            }
        });
        appbarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                StatusBarUtils.setStatusBarDarkMode(getActivity());
            } else {
                StatusBarUtils.setStatusBarLightMode(getActivity());
            }
        });


    }

}
