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

package com.xuexiang.templateproject.fragment.statistic;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentStatisticBinding;

import com.xuexiang.templateproject.fragment.data.DataBaseUtils;
import com.xuexiang.templateproject.fragment.data.TodoContract;
import com.xuexiang.templateproject.fragment.data.TodoHelper;
import com.xuexiang.templateproject.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * 统计页面，调用了MPAndroidChart
 * 登录页面
 *
 * @author wen
 * @since 2024.12.23
 * */
@Page(anim = CoreAnim.none)
public class StatisticFragment extends BaseFragment<FragmentStatisticBinding>{
    PieChart pieChart;
    LineChart lineChart;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;

    @NonNull
    @Override
    protected FragmentStatisticBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentStatisticBinding.inflate(inflater, container, false);
    }

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //当此fragment正当前显示是，执行该操作，
            initTotalText();
            flashPieData();
            flashLineData();
            pieChart.animateY(1500, Easing.EaseInOutQuad);
            lineChart.animateY(1500, Easing.EaseInCubic);
        } else {
            // 相当于Fragment的onPause
            // System.out.println("ChatFragment ---setUserVisibleHint---isVisibleToUser - FALSE");
        }
    }




    /**
         * 初始化控件
         */
    @Override
    protected void initViews() {
        TodoHelper helper = new TodoHelper(getContext());
        dbRead = helper.getReadableDatabase();
        dbWrite =helper.getWritableDatabase();
        pieChart = binding.pieChart;
        lineChart = binding.lineChart;

        initTotalText();
        initPieChartStyle();
        initPieChartLabel();
        setPieChartData();

        initLineChartStyle();
        initLineChartLabel();
        setLineChartData();



        pieChart.animateY(1500, Easing.EaseInOutQuad);
        lineChart.animateY(1500, Easing.EaseInCubic);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                pieChart.animateY(1500, Easing.EaseInOutQuad);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                lineChart.animateY(1500, Easing.EaseInCubic);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }


    /**
     * 初始化图表的样式
     */
    protected void initPieChartStyle() {
        //使用百分百显示
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        //设置拖拽的阻尼，0为立即停止
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        //设置图标中心文字
        pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setDrawCenterText(true);
        //设置图标中心空白，空心
        pieChart.setDrawHoleEnabled(true);
        //设置空心圆的弧度百分比，最大100
        pieChart.setHoleRadius(58f);
        pieChart.setHoleColor(Color.WHITE);
        //设置透明弧的样式
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setTransparentCircleRadius(61f);

        //设置可以旋转
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setNoDataText("没有数据");
    }

    /**
     * 初始化图表的 标题
     */
    protected void initPieChartLabel() {
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
    }

    /**
     * 设置图表数据
     *
     */
    protected void setPieChartData() {
        flashPieData();
        pieChart.setNoDataText("没有数据");

        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }


    /**
     * 生成饼图中间的文字
     *
     * @return
     */
    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("待办类别分布情况");
        s.setSpan(new RelativeSizeSpan(1.9f), 0, 4, 0);
        return s;
    }

    private void flashPieData(){
        boolean flag = false;
        TreeMap<String, Integer> categories = DataBaseUtils.getCategoryData(dbRead);
        Iterator<String> iterator = categories.keySet().iterator();
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        while (iterator.hasNext()){
            String category = iterator.next();
            int count = DataBaseUtils.getCategoryCount(dbRead,category);
            if (count!=0){
                flag = true;
                entries.add(new PieEntry(count, category));
                colors.add(categories.get(category));
            }
        }
        if(!flag){
            entries.add(new PieEntry(1,"无待办"));
            colors.add(categories.get("默认"));
        }
        PieDataSet dataSet = new PieDataSet(entries, "Todo category");

        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);



        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
    }


    /**
     * 初始化图表的样式
     */
    protected void initLineChartStyle() {
        lineChart.setBackgroundColor(Color.WHITE);
        //关闭描述
        lineChart.getDescription().setEnabled(false);
        //设置不画背景网格
        lineChart.setDrawGridBackground(false);


        //开启所有触摸手势
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        initXYAxisStyle();
    }

    /**
     * 初始化图表X、Y轴的样式
     */
    private void initXYAxisStyle() {
        XAxis xAxis = lineChart.getXAxis();
        // 设置垂直的网格线
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis yAxis = lineChart.getAxisLeft();
        // 设置水平的网格线
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        // axis range
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);
        // 关闭Y轴右侧
        lineChart.getAxisRight().setEnabled(false);
        // draw limit lines behind data instead of on top
        xAxis.setDrawLimitLinesBehindData(true);
        yAxis.setDrawLimitLinesBehindData(true);

    }

    /**
     * 初始化图表的 标题 样式
     */
    protected void initLineChartLabel() {
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);
    }


    protected void setLineChartData() {
        flashLineData();
    }
    private  void flashLineData(){
        HashMap<String, Integer> rates = DataBaseUtils.getCompletionRateData(dbRead,dbWrite,true);
        List<Entry> values = new ArrayList<>();
        //设置数据源
        for (int i = 1; i <= rates.size(); i++) {
            values.add(new Entry(i, rates.get("day"+i)));
        }
        LineDataSet set1;
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "—— 待办完成率(%)");
            set1.setDrawIcons(false);
            set1.setDrawHorizontalHighlightIndicator(false);  // 画水平高亮指示器，默认true
            set1.setDrawVerticalHighlightIndicator(false);    // 垂直方向高亮指示器,默认true
            // 设置线的样式
            set1.setColor(Color.BLACK);
            set1.setLineWidth(1f);
            //设置点的样式
            set1.setCircleColor(Color.BLACK);
            set1.setCircleRadius(3f);
            // 设置不画空心圆
            set1.setDrawCircleHole(false);
            // 设置数据组 标题的样式
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            // text size of values
            set1.setValueTextSize(9f);
            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            // 设置折线图的填充区域
            set1.setDrawFilled(true);
            set1.setFillFormatter((dataSet, dataProvider) -> {
                if (lineChart == null) {
                    return 0;
                }
                return lineChart.getAxisLeft().getAxisMinimum();
            });
            set1.setFillColor(R.color.colorPrimary);
            List<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            lineChart.setData(data); }
    }
    private void initTotalText(){
        HashMap<String, Integer> data = DataBaseUtils.getStatisticTotal(dbRead);
        int totalNumber = data.get("totalNumber");
        int finishNumber = data.get("finishNumber");
        int percentNumber;
        if(totalNumber!=0){
            percentNumber = (finishNumber*100) / totalNumber;
        }else {
            percentNumber = 0;
        }
        binding.statisticTotalNumber.setText(totalNumber+"");
        binding.statisticFinishNumber.setText(finishNumber+"");
        binding.statisticPercentNumber.setText(percentNumber+"");


        ContentValues contentValues = new ContentValues();
        contentValues.put(TodoContract.CompletionRate.COLUMN_DAY7,percentNumber);
        dbWrite.update(TodoContract.CompletionRate.TABLE_NAME,contentValues,null,null);
    }
}
