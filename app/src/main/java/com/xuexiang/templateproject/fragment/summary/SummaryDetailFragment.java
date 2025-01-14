package com.xuexiang.templateproject.fragment.summary;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentSummaryDetailBinding;
import com.xuexiang.templateproject.fragment.data.Summary;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.templateproject.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.utils.ViewUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.display.Colors;

/**
 * 总结详情页
 *
 * @author zheng
 * @since 2022.6.23
 */
@Page(name = "详细总结")
public class SummaryDetailFragment extends BaseFragment<FragmentSummaryDetailBinding> {
    Toolbar toolbar;
    CollapsingToolbarLayout collapseLayout;
    AppBarLayout appbarLayout;
    TextView detailText;
    TextView detailTitle;
    private SQLiteDatabase dbWrite;


    @NonNull
    @Override
    protected FragmentSummaryDetailBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentSummaryDetailBinding.inflate(inflater, container, false);
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


        toolbar = binding.summaryDetailAppbarLayoutToolbar;
        collapseLayout = binding.summaryDetailCollapseLayout;
        appbarLayout = binding.summaryDetailAppbarLayout;
        detailTitle = binding.summaryDetailTitle;

        detailText = binding.summaryDetailText;
        toolbar.inflateMenu(R.menu.menu_summary);
        toolbar.setNavigationOnClickListener(v -> popToBack());
        ViewUtils.setToolbarLayoutTextFont(collapseLayout);
        detailText.setText(text);
        detailTitle.setText(title);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.action_summary_update){
                    Bundle bundle = new Bundle();
                    bundle.putLong("id",id);
                    bundle.putString("title",detailTitle.getText().toString());
                    bundle.putString("text",detailText.getText().toString());

                    openPageForResult(SummaryEditFragment.class,bundle,101);
                }
                if(itemId == R.id.action_summary_delete){
                    String selection = TodoContract.Summary._ID + "=" + id;
                    dbWrite.delete(TodoContract.Summary.TABLE_NAME, selection, null);
                    popToBack();
                }
                return  false;
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

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle extras = data.getExtras();
            detailText.setText(extras.getString("text"));
            detailTitle.setText(extras.getString("title"));
        }
    }
}
