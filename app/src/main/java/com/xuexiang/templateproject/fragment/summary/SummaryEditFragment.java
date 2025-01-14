package com.xuexiang.templateproject.fragment.summary;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentSummaryEditBinding;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.utils.ViewUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xutil.data.DateUtils;
import com.xuexiang.xutil.display.Colors;

/**
 * 总结编辑页面
 *
 * @author zheng
 * @since 2022.6.23
 */
@Page(name = "修改总结")
public class SummaryEditFragment extends BaseFragment<FragmentSummaryEditBinding> {
    Toolbar toolbar;
    CollapsingToolbarLayout collapseLayout;
    AppBarLayout appbarLayout;
    MultiLineEditText editText;
    EditText editTitle;
    private SQLiteDatabase dbWrite;


    @NonNull
    @Override
    protected FragmentSummaryEditBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentSummaryEditBinding.inflate(inflater, container, false);
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
        Bundle bundle = getArguments();
        String title = bundle.getString("title");
        String text = bundle.getString("text");
        long id = bundle.getLong("id");


        toolbar = binding.summaryEditAppbarLayoutToolbar;
        collapseLayout = binding.summaryEditCollapseLayout;
        appbarLayout = binding.summaryEditAppbarLayout;
        editTitle = binding.summaryEditTitle;

        editText = binding.summaryEditText;
        toolbar.inflateMenu(R.menu.menu_summary_edit);
        toolbar.setNavigationOnClickListener(v -> popToBack());
        ViewUtils.setToolbarLayoutTextFont(collapseLayout);
        editText.setContentText(text);
        editTitle.setText(title);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if(itemId==R.id.action_summary_add){
                    String selection = TodoContract.Summary._ID + " IN(?)";
                    String[] selectionArgs = {id+""};
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TodoContract.Summary.COLUMN_SUMMARY_EDIT_TIME, DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMdd.get()));
                    contentValues.put(TodoContract.Summary.COLUMN_SUMMARY_TITLE,editTitle.getText().toString());
                    contentValues.put(TodoContract.Summary.COLUMN_SUMMARY_TEXT,editText.getContentText());
                    dbWrite.update(TodoContract.Summary.TABLE_NAME,contentValues, selection, selectionArgs);
                    // 设置返回的数据，类似Activity里的setResult
                    Intent intent = new Intent();
                    intent.putExtra("title",editTitle.getText().toString());
                    intent.putExtra("text",editText.getContentText());
                    setFragmentResult(501, intent);
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
