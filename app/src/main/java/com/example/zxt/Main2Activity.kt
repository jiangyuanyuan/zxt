package com.example.zxt

import android.content.BroadcastReceiver
import android.graphics.Typeface
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.be.base.easy.EasyAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.model.GradientColor
import com.tezwez.club.data.dto.CountBean
import com.tezwez.club.data.dto.MyData
import com.tezwez.club.data.vm.ApiViewModel
import kotlinx.android.synthetic.main.activity_image.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.util.*

class Main2Activity : PermissionActivity() , OnChartValueSelectedListener {
    override fun onNothingSelected() {}
    override fun onValueSelected(e: Entry?, h: Highlight?) {}
    lateinit var easyAdapter: EasyAdapter<MyData>
    var mList = mutableListOf<MyData>()
    val mApiViewModel: ApiViewModel by viewModel()
    var pageNum = 1

    var hasNextPage: Boolean? = true
    var isAuto: Boolean? = false //自动轮寻 查数据
    var hasPreviousPage: Boolean? = false
    var total = 1
    private var temp: Long = 0
    private var receiver: BroadcastReceiver? = null
    private var newest: BigDecimal = BigDecimal(0)
    private var clicks = 0

    private var mCursorTimerTask: TimerTask? = null
    private var mCursorTimer: Timer? = null
    private var mCursorDuration: Long = 10000
    protected lateinit var tfLight: Typeface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        tfLight = Typeface.createFromAsset(assets, "OpenSans-Light.ttf")
//        initTu(chart,4)
        initChart1()

    }

    fun initChart1() {
        initTu(chart, 1)
//        setData(chart, 4)
        mApiViewModel.getListByTimeHistory(24).observe(this, androidx.lifecycle.Observer {
            if (it?.isEmpty()?.not() == true) {
                var num = 0
                for (data in it) {
                    num = num + data.sum
                }
//                tvNum.text = "$num"
                setData(chart, it, 1)
            }
        })
    }

    fun initTu(lineChart: BarChart, type: Int){


        chart.setOnChartValueSelectedListener(this)

        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)

        chart.getDescription().setEnabled(false)

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false)

        chart.setDrawGridBackground(false)
        // chart.setDrawYLabels(false);

        val xAxisFormatter = DayAxisValueFormatter(chart)

        val xAxis = chart.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTypeface(tfLight)
        xAxis.setDrawGridLines(false)
        xAxis.setGranularity(1f) // only intervals of 1 day
        xAxis.setLabelCount(7)
        xAxis.setValueFormatter(xAxisFormatter)

        val custom = MyValueFormatter("$")

        val leftAxis = chart.getAxisLeft()
        leftAxis.setTypeface(tfLight)
        leftAxis.setLabelCount(8, false)
        leftAxis.setValueFormatter(custom)
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.setSpaceTop(15f)
        leftAxis.setAxisMinimum(0f) // this replaces setStartAtZero(true)
        leftAxis.mAxisMaximum = when (type) {
            1 -> 600f
            2 -> 6000f
            3 -> 60000f
            4 -> 60f
            else -> 600f
        }

        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.setDrawAxisLine(false)


        val rightAxis = chart.getAxisRight()
        rightAxis.setDrawAxisLine(false)
//        rightAxis.setDrawGridLines(false)
//        rightAxis.setTypeface(tfLight)
//        rightAxis.setLabelCount(8, false)
//        rightAxis.setValueFormatter(custom)
//        rightAxis.setSpaceTop(15f)
//        rightAxis.setAxisMinimum(0f) // this replaces setStartAtZero(true)

        val l = chart.getLegend()
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT)
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        l.setDrawInside(false)
        l.setForm(Legend.LegendForm.SQUARE)
        l.setFormSize(9f)
        l.setTextSize(11f)
        l.setXEntrySpace(4f)

        val mv = MyMarkerView(this, R.layout.custom_marker_view)
        mv.setChartView(chart) // For bounds control
        chart.setMarker(mv) // Set the marker to the chart

//        setData(60, 20f)
    }

    private fun setData(lineChart: BarChart, list: List<CountBean>, type: Int) {
        val values = ArrayList<BarEntry>()
        for (i in 0 until list.size) {
            var data = list.get(i)
            var sum = data.sum.toFloat()
            var value = data.day.substring(data.day.length-2,data.day.length)
            values.add(BarEntry(value.toFloat(), sum, resources.getDrawable(R.drawable.star)))
        }
        val set1: BarDataSet

        if (chart.data != null && chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()

        } else {
            set1 = BarDataSet(values, "告警次数")
            set1.setDrawIcons(false)
            val startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light)
            val startColor2 = ContextCompat.getColor(this, android.R.color.holo_blue_light)
            val startColor3 = ContextCompat.getColor(this, android.R.color.holo_orange_light)
            val startColor4 = ContextCompat.getColor(this, android.R.color.holo_green_light)
            val startColor5 = ContextCompat.getColor(this, android.R.color.holo_red_light)
            val endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark)
            val endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple)
            val endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark)
            val endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark)
            val endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark)

            val gradientColors = ArrayList<GradientColor>()
            gradientColors.add(GradientColor(startColor1, endColor1))
            gradientColors.add(GradientColor(startColor2, endColor2))
            gradientColors.add(GradientColor(startColor3, endColor3))
            gradientColors.add(GradientColor(startColor4, endColor4))
            gradientColors.add(GradientColor(startColor5, endColor5))

            set1.gradientColors = gradientColors

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.setValueTypeface(tfLight)
            data.barWidth = 0.9f

            chart.data = data
            chart.setVisibleXRangeMaximum(20f)
        }
    }
}
