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
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentAddBinding;
import com.xuexiang.templateproject.fragment.data.DataBaseUtils;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.templateproject.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.templateproject.fragment.data.TodoContract.TodoEntry;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.configure.TimePickerType;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.data.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

/**
 * 添加待办页面
 *
 * @author zheng
 * @since 2022.6.23
 */
@Page(name = "添加页面")
public class AddFragment extends BaseFragment<FragmentAddBinding> implements View.OnClickListener {
    private TimePickerView mDatePicker;
    private TimePickerView mTimePicker;
    private int repeatSelectOption = 0;
    private String[] mRepeatOption;
    String dateTime = "0";
    int fire = 0;
    int repeat = TodoContract.REPEAT_NONE;
    private SQLiteDatabase dbRead;

    @NonNull
    @Override
    protected FragmentAddBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentAddBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initViews() {
        TodoHelper helper = new TodoHelper(getContext());
        dbRead = helper.getReadableDatabase();
        mRepeatOption = ResUtils.getStringArray(R.array.repeat_value);
        binding.addSubmit.setOnClickListener(this);
        binding.deadlineDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        binding.deadlineTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        binding.repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepeatPickerView();
            }
        });

//      类别选择监听器
        binding.addCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeMap<String,Integer> data = DataBaseUtils.getCategoryData(dbRead);
                    new MaterialDialog.Builder(getContext())
                            .title("待办类别选择")
                            .items(data.keySet())
                            .itemsCallbackSingleChoice(
                                    0,
                                    (dialog, itemView, which, text) -> {
                                        binding.addCategoryName.setText(text);
                                        binding.addCategoryColor.setBackgroundColor(data.get(text));
                                        return true;
                                    })
                            .positiveText("确定")
                            .negativeText("类别管理")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    openPage(CategoryManageFragment.class);
                                }
                            })
                            .show();

            }
        });

        binding.addDeadlineTitle.setSwitchCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(binding.addDeadline.isExpanded()){
                    binding.addDeadline.collapse();
                }else{
                    binding.addDeadline.expand();
                }
            }
        });
        binding.addDeadlineTitle.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                superTextView.setSwitchIsChecked(!superTextView.getSwitchIsChecked(), false);
            }
        });
        binding.addRepeatTitle.setSwitchCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(binding.addRepeat.isExpanded()){
                    binding.addRepeat.collapse();
                    repeat = TodoContract.REPEAT_NONE;
                }else{
                    binding.addRepeat.expand();
                    repeat = TodoContract.REPEAT_DAY;
                }
            }
        });
        binding.addRepeatTitle.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                superTextView.setSwitchIsChecked(!superTextView.getSwitchIsChecked(), false);
            }
        });
    }

    @SingleClick
    @Override
    public void onClick(View superTextView) {
        String things = binding.addThings.getText().toString();
        if (things.trim().length() == 0) {
            XToastUtils.info("内容不能为空！");
            return;
        }
        if(binding.addFire.getChecked().equals("加急")){
            fire=1;
        }
        /**
         * 把数据写入数据库
         */
        TodoHelper todoHelper = new TodoHelper(getContext());
        SQLiteDatabase db = todoHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TodoEntry.COLUMN_CATEGORY,binding.addCategoryName.getText().toString());
        cv.put(TodoEntry.COLUMN_FINISH,0);
        cv.put(TodoEntry.COLUMN_THINGS, things);
        cv.put(TodoEntry.COLUMN_FIRE, fire);
        //开始时间
        if(binding.addDeadlineTitle.getSwitchIsChecked()){
            String date = (String) binding.deadlineDate.getText();
            if ("当日".equals(binding.deadlineDate.getText())){
                date = DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMdd.get());
            }
            dateTime = date + " " + binding.deadlineTime.getText();
        }
        cv.put(TodoEntry.COLUMN_BEGIN_TIME,DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMddHHmm.get()));
        cv.put(TodoEntry.COLUMN_DEADLINE,dateTime);
        cv.put(TodoEntry.COLUMN_REPEAT,repeat);
        cv.put(TodoEntry.COLUMN_DELETE,0);
        cv.put(TodoEntry.COLUMN_HANDLE_TIME,DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMddHHmm.get()));
        db.insert(TodoEntry.TABLE_NAME, null, cv);
        db.close();
        XToastUtils.success("添加成功");
        popToBack();
    }



    private void showDatePicker() {
        if (mDatePicker == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtils.getNowDate());
            mDatePicker = new TimePickerBuilder(getContext(), (date, v) -> binding.deadlineDate.setText(DateUtils.date2String(date, DateUtils.yyyyMMdd.get())))
                    .setTimeSelectChangeListener(date -> Log.i("pvTime", "onTimeSelectChanged"))
                    .setType(TimePickerType.DEFAULT)
                    .setTitleText("截止日期")
                    .setDate(calendar)
                    .build();
        }
        mDatePicker.show();
    }
    private void showTimePicker() {
        if (mTimePicker == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtils.getNowDate());
            mTimePicker = new TimePickerBuilder(getContext(), (date, v) -> binding.deadlineTime.setText(DateUtils.date2String(date, DateUtils.HHmm.get())))
                    .setTimeSelectChangeListener(date -> Log.i("pvTime", "onTimeSelectChanged"))
                    .setType(new boolean[]{false, false, false, true, true, false})
                    .setTitleText("截止时间")
                    .setDate(calendar)
                    .build();
        }
        mTimePicker.show();
    }


    private void showRepeatPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), (v, options1, options2, options3) -> {
            binding.repeat.setText(mRepeatOption[options1]);
            repeat = options1+1;
            repeatSelectOption = options1;
            return false;
        })
                .setTitleText("重复周期选择")
                .setSelectOptions(repeatSelectOption)
                .build();
        pvOptions.setPicker(mRepeatOption);
        pvOptions.show();
    }
}
