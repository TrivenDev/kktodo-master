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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentEditBinding;
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
 * 编辑页面
 *
 * @author zheng
 * @since 2022.6.23
 */
@Page(name = "编辑页面")
public class EditFragment extends BaseFragment<FragmentEditBinding> implements View.OnClickListener {
    private TimePickerView mDatePicker;
    private TimePickerView mTimePicker;
    private int repeatSelectOption = 0;
    private String[] mRepeatOption;
    String dateTime = "0";
    Date date_pre_deadline;
    int fire = 0;
    int repeatText = TodoContract.REPEAT_NONE;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    Bundle bundle;

    @NonNull
    @Override
    protected FragmentEditBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentEditBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initViews() {
        bundle = getArguments();
        TodoHelper helper = new TodoHelper(getContext());
        dbRead = helper.getReadableDatabase();
        dbWrite = helper.getWritableDatabase();
        mRepeatOption = ResUtils.getStringArray(R.array.repeat_value);
        binding.editSubmit.setOnClickListener(this);
        binding.editDeadlineDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        binding.editDeadlineTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        binding.repeatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepeatPickerView();
            }
        });


        //      类别选择监听器
        binding.editCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeMap<String,Integer> data = DataBaseUtils.getCategoryData(dbRead);
                new MaterialDialog.Builder(getContext())
                        .title("待办类别选择")
                        .items(data.keySet())
                        .itemsCallbackSingleChoice(
                                0,
                                (dialog, itemView, which, text) -> {
                                    binding.editCategoryName.setText(text);
                                    binding.editCategoryColor.setBackgroundColor(data.get(text));
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

        binding.editDeadlineTitle.setSwitchCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(binding.editDeadline.isExpanded()){
                    binding.editDeadline.collapse();
                }else{
                    binding.editDeadline.expand();
                }
            }
        });
        binding.editDeadlineTitle.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                superTextView.setSwitchIsChecked(!superTextView.getSwitchIsChecked(), false);
            }
        });
        binding.editRepeatTitle.setSwitchCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(binding.editRepeat.isExpanded()){
                    binding.editRepeat.collapse();
                    repeatText = TodoContract.REPEAT_NONE;
                }else{
                    binding.editRepeat.expand();
                    repeatText = TodoContract.REPEAT_DAY;
                }
            }
        });
        binding.editRepeatTitle.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                superTextView.setSwitchIsChecked(!superTextView.getSwitchIsChecked(), false);
            }
        });




        /**
         * 根据调用者传来的bundle参数来设置页面
         * */
        String pre_things = bundle.getString("things");
        binding.editThings.setText(pre_things);

        int pre_fire = bundle.getInt("fire");
        try {
            binding.editFire.setDefaultSelection(pre_fire);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String category = bundle.getString("category");
        binding.editCategoryName.setText(category);
        TreeMap<String,Integer> data = DataBaseUtils.getCategoryData(dbRead);
        binding.editCategoryColor.setBackgroundColor(data.get(category));
        String pre_deadline = bundle.getString("deadline");
        boolean hasSetDeadline = "0".equals(pre_deadline)?false:true;
        binding.editDeadlineTitle.setSwitchIsChecked(hasSetDeadline);
        if (hasSetDeadline){
            binding.editDeadline.expand();
            date_pre_deadline = DateUtils.string2Date(pre_deadline,DateUtils.yyyyMMddHHmm.get());
            binding.editDeadlineDate.setText(DateUtils.date2String(date_pre_deadline,DateUtils.yyyyMMdd.get()));
            binding.editDeadlineTime.setText(DateUtils.date2String(date_pre_deadline,DateUtils.HHmm.get()));
        }else {
            binding.editDeadline.collapse();
            binding.editDeadlineDate.setText("当日");
            binding.editDeadlineTime.setText("24:00");
            date_pre_deadline = DateUtils.getNowDate();
        }


        int pre_repeat = bundle.getInt("repeat");
        repeatText = pre_repeat;
        boolean hasSetRepeat = (0== pre_repeat ?false:true);
        binding.editRepeatTitle.setSwitchIsChecked(hasSetRepeat);
        if(hasSetRepeat){
            binding.editRepeat.expand();
            binding.repeatText.setText(mRepeatOption[pre_repeat-1]);
            repeatSelectOption = pre_repeat-1;
        }else {
            binding.editRepeat.collapse();
            binding.repeatText.setText(mRepeatOption[0]);
            repeatSelectOption = 0;
        }
    }

    @SingleClick
    @Override
    public void onClick(View superTextView) {
        String things = binding.editThings.getText().toString();
        if (things.trim().length() == 0) {
            XToastUtils.info("内容不能为空！");
            return;
        }
        if(binding.editFire.getChecked().equals("加急")){
            fire=1;
        }

        /**
         * 把数据写入数据库
         */
        TodoHelper todoHelper = new TodoHelper(getContext());
        SQLiteDatabase db = todoHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TodoEntry.COLUMN_CATEGORY,binding.editCategoryName.getText().toString());
        cv.put(TodoEntry.COLUMN_FINISH,0);
        cv.put(TodoEntry.COLUMN_THINGS, things);
        cv.put(TodoEntry.COLUMN_FIRE, fire);
        //开始时间
        if(binding.editDeadlineTitle.getSwitchIsChecked()){
            String date = (String) binding.editDeadlineDate.getText();
            if ("当日".equals(binding.editDeadlineDate.getText())){
                date = DateUtils.date2String(DateUtils.getNowDate(),DateUtils.yyyyMMdd.get());
            }
            dateTime = date + " " + binding.editDeadlineTime.getText();
        }
        cv.put(TodoEntry.COLUMN_DEADLINE,dateTime);
        cv.put(TodoEntry.COLUMN_REPEAT,repeatText);
        cv.put(TodoEntry.COLUMN_DELETE,0);

        long id = bundle.getLong("id");
        String selection = TodoEntry._ID + " IN(?)";
        String[] selectionArgs = {id+""};
        dbWrite.update(TodoEntry.TABLE_NAME,cv, selection, selectionArgs);
        db.close();
        XToastUtils.success("修改成功");
        // 设置返回的数据，类似Activity里的setResult
        Intent intent = new Intent();
        intent.putExtra("res", "ok");
        setFragmentResult(500, intent);
        popToBack();
    }



    private void showDatePicker() {
        if (mDatePicker == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date_pre_deadline);
            mDatePicker = new TimePickerBuilder(getContext(), (date, v) -> binding.editDeadlineDate.setText(DateUtils.date2String(date, DateUtils.yyyyMMdd.get())))
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
            calendar.setTime(date_pre_deadline);
            mTimePicker = new TimePickerBuilder(getContext(), (date, v) -> binding.editDeadlineTime.setText(DateUtils.date2String(date, DateUtils.HHmm.get())))
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
            binding.repeatText.setText(mRepeatOption[options1]);
            repeatText = options1+1;
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
