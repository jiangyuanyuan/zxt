package com.example.zxt.newpage;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Description : BarChartManager
 * Author      : jovelin
 * Version     : 2.0
 * Create Date : 2019-10-11 00:47
 */
public class BarChartManager  {

    private BarChart mBarChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private DecimalFormat mFormat;

    public BarChartManager(BarChart barChart) {
        this.mBarChart = barChart;
        leftAxis = mBarChart.getAxisLeft();
        rightAxis = mBarChart.getAxisRight();
        xAxis = mBarChart.getXAxis();
    }

    /**
     * 初始化LineChart
     */
    private void initLineChart() {
        mFormat = new DecimalFormat("#,###.##");
        //背景颜色
        mBarChart.setBackgroundColor(Color.WHITE);
        //是否显示网格背景
        mBarChart.setDrawGridBackground(false);
        //显示每条背景阴影
        mBarChart.setDrawBarShadow(false);
        //设置图标边框的颜色
        mBarChart.setBorderColor(Color.parseColor("#ff0000"));
//        mBarChart.setHighlightFullBarEnabled(false);
        mBarChart.setTouchEnabled(true); // 所有触摸事件,默认true
        mBarChart.setDragEnabled(true);    // 可拖动,默认true
        mBarChart.setScaleEnabled(false);   // 两个轴上的缩放,X,Y分别默认为true
        mBarChart.setScaleXEnabled(false);  // X轴上的缩放,默认true
        mBarChart.setScaleYEnabled(false);  // Y轴上的缩放,默认true
        mBarChart.setPinchZoom(false);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        mBarChart.setDoubleTapToZoomEnabled(false); // 双击缩放,默认true
        mBarChart.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true

        //显示边界
        mBarChart.setDrawBorders(false);
//      不显示描述信息
        mBarChart.getDescription().setEnabled(false);
//         图例设置
        Legend legend = mBarChart.getLegend();
        //不显示图例
        legend.setForm(Legend.LegendForm.NONE);
//        图例文字的大小
        legend.setTextSize(11f);
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        //XY轴的设置
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//      X轴最小间距
        xAxis.setGranularity(1f);
//      不绘制网格线
        xAxis.setDrawGridLines(false);
//      X轴字体样式
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);
//      设置X轴文字剧中对齐
        xAxis.setCenterAxisLabels(true);
//
//       保证Y轴从0开始，不然会上移一点
        leftAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.parseColor("#d5d5d5"));
//        // 线跟数据都不显示
        rightAxis.setEnabled(false); //右侧Y轴不显示
    }

    /**
     * 展示柱状图(一条)
     */
    public void showBarChart(List<BarEntry> yVals, String label, int color) {
        initLineChart();

        // 每一个BarDataSet代表一类柱状图
        BarDataSet barDataSet = new BarDataSet(yVals, label);
        barDataSet.setColor(color);
        //是否显示顶部的值
        barDataSet.setDrawValues(true);
//        文字的大小
        barDataSet.setValueTextSize(9f);

        barDataSet.setFormLineWidth(1f);
        barDataSet.setFormSize(15.0f);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        BarData data = new BarData(dataSets);
//      设置宽度
        data.setBarWidth(0.3f);
        //设置X轴的刻度数
        String[] xValues = {"1", "2", "3", "4", "5", "6", "7","8","9","10","11","12"};
        String[] yValues = {"91%", "92%", "93%", "94%", "95%", "96%"};
        xAxis.setLabelCount(yVals.size() + 1, true);
        xAxis.setDrawLabels(true);
        XAxisValueFormatter xAxisFormatter = new XAxisValueFormatter(xValues);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextColor(Color.parseColor("#d5d5d5"));
        xAxis.setAxisLineColor(Color.parseColor("#d5d5d5"));
        MyYAxisValueFormatter custom = new MyYAxisValueFormatter(yValues);
        leftAxis.setValueFormatter(custom);
//        leftAxis.setLabelCount(yValues.length + 1, false);
        leftAxis.setAxisLineColor(Color.parseColor("#d5d5d5"));
//        设置Y轴的最小值和最大值
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        mBarChart.setData(data);
    }


    public class MyYAxisValueFormatter extends ValueFormatter {

        private String[] xValues;

        public MyYAxisValueFormatter(String[] yValues) {
            xValues = yValues;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
//            Log.e("TAG", "xValues[(int) value]====="+xValues[(int) value]);
            return mFormat.format(value) + "%";
        }
    }


    public class XAxisValueFormatter extends ValueFormatter{

        private String[] xValues;

        public XAxisValueFormatter(String[] xValues) {
            this.xValues = xValues;
        }

        @Override
        public String getFormattedValue(float value) {
            Log.e("TAG", "============"+ (((int) (value + 1)) -1));
            if(((int) (value + 1)) -1 == xValues.length)
                return "";
            return xValues[((int) (value + 1)) -1];
        }

    }

    /**
     * 展示柱状图(多条)
     * +-+
     *
     * @param xAxisValues
     * @param yAxisValues
     * @param labels
     * @param colours
     */
    public void showMoreBarChart(final List<Float> xAxisValues, List<List<Float>> yAxisValues, List<String> labels, List<Integer> colours) {
        initLineChart();
        BarData data = new BarData();
        for (int i = 0; i < yAxisValues.size(); i++) {
            ArrayList<BarEntry> entries = new ArrayList<>();
            for (int j = 0; j < yAxisValues.get(i).size(); j++) {

                entries.add(new BarEntry(xAxisValues.get(j), yAxisValues.get(i).get(j)));
            }
            BarDataSet barDataSet = new BarDataSet(entries, labels.get(i));

            barDataSet.setColor(colours.get(i));
            barDataSet.setValueTextColor(colours.get(i));
            barDataSet.setValueTextSize(10f);
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            data.addDataSet(barDataSet);
        }
        int amount = yAxisValues.size();

        float groupSpace = 0.3f; //柱状图组之间的间距
        float barSpace = (float) ((1 - 0.12) / amount / 10); // x4 DataSet
        float barWidth = (float) ((1 - 0.3) / amount / 10 * 9); // x4 DataSet

        // (0.2 + 0.02) * 4 + 0.08 = 1.00 -> interval per "group"
        xAxis.setLabelCount(xAxisValues.size() - 1, false);
        data.setBarWidth(barWidth);
        final String[] xValues = {"小学", "初中", "高中", "专科", "本科"};
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                for (int i = 0; i < xAxisValues.size(); i++) {
                    if (value == (xAxisValues.get(i) - 1)) {
                        return xValues[i];
                    }
                }
                return "";
            }
        });
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                for (int i=0;i<xAxisValues.size();i++){
//                    if(value==(xAxisValues.get(i)-1)) {
//                        return xValues[i];
//                    }
//                }
//                return "";
//            }
//        });
        data.groupBars(0, groupSpace, barSpace);
        mBarChart.setData(data);
    }

    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);

        rightAxis.setAxisMaximum(max);
        rightAxis.setAxisMinimum(min);
        rightAxis.setLabelCount(labelCount, false);
        mBarChart.invalidate();
    }

    /**
     * 设置X轴的值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setXAxis(float max, float min, int labelCount) {
        xAxis.setAxisMaximum(max);
        xAxis.setAxisMinimum(min);
        xAxis.setLabelCount(labelCount, false);

        mBarChart.invalidate();
    }

    /**
     * 设置高限制线
     *
     * @param high
     * @param name
     */
    public void setHightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "高限制线";
        }
        LimitLine hightLimit = new LimitLine(high, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        leftAxis.addLimitLine(hightLimit);
        mBarChart.invalidate();
    }

    /**
     * 设置低限制线
     *
     * @param low
     * @param name
     */
    public void setLowLimitLine(int low, String name) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        leftAxis.addLimitLine(hightLimit);
        mBarChart.invalidate();
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        mBarChart.setDescription(description);
        mBarChart.invalidate();
    }
}