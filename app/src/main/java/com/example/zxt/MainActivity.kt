package com.example.zxt

import android.content.BroadcastReceiver
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.be.base.easy.EasyAdapter
import com.be.base.view.ErrorDialog
import com.example.zxt.DateUtils.datePattern
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.model.GradientColor
import com.tezwez.base.helper.click
import com.tezwez.base.helper.loge
import com.tezwez.club.data.dto.CountBean
import com.tezwez.club.data.dto.MyData
import com.tezwez.club.data.vm.ApiViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_msg.view.*
import org.jetbrains.anko.toast
import org.koin.android.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : PermissionActivity(), OnChartValueSelectedListener {
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
        setContentView(R.layout.activity_main)
        tfLight = Typeface.createFromAsset(assets, "OpenSans-Light.ttf")
        initRv()
        getDataInfo()
        initEvent()
        initReceiver()
        initChart1()
        initChart2()
    }

    fun initRv() {
        recycleView.layoutManager = LinearLayoutManager(this)
        easyAdapter = EasyAdapter(R.layout.item_msg, { itmeView, position, item ->
            itmeView.tvContent.text = getMyData(item.alarmType)
            itmeView.tvTime.text = DateUtils.convertTimeToString(item.alarmTime, datePattern)
            when (item.pictureType) {
                "1" -> {
                    itmeView.tvType.text = "一级警告"
                }
                "2" -> {
                    itmeView.tvType.text = "二级警告"
                }
                "3" -> {
                    itmeView.tvType.text = "三级警告"
                }
            }
            itmeView.tvShowImg.click {
                showDialog(item.alarmPictureName)
            }
        }, emptyList())
        recycleView.adapter = easyAdapter
    }


    fun initChart1() {
        initTu(chart, 4)
        mApiViewModel.getListByHour(24).observe(this, androidx.lifecycle.Observer {
            if (it?.isEmpty()?.not() == true) {
                var num = 0
                for (data in it) {
                    num = num + data.sum
                }
                tvNum.text = "$num"
                setData(chart, it, 4)
            }
        })
    }

    fun initChart2() {
        initTu(chart2, 1)
        day.isSelected = true
        mApiViewModel.getListByTimeHistory(30).observe(this, androidx.lifecycle.Observer {
            if (it?.isEmpty()?.not() == true) {
                var num = 0
                for (data in it) {
                    num = num + data.sum
                }
                tvNumAll.text = "$num"
                initTu(chart2, 1)
                setData(chart2, it, 1)
            }
        })
    }


    fun getNewest(){
        mApiViewModel.getNewest().observe(this,androidx.lifecycle.Observer {
            if (it?.isNotEmpty()==true){
                if (BigDecimal(it?.get(0)?.id)>newest){
                    if (isAuto == true) {
                        showDialog(it?.get(0)?.alarmPictureName)
                    }
                    newest = BigDecimal(it?.get(0)?.id)
                    getDataInfo()
                }
            }
        })
    }

    fun getDataInfo() {
        mApiViewModel.getList(pageNum, 10).observe(this, androidx.lifecycle.Observer {

            if (it != null) {
                hasNextPage = it.hasNextPage
                hasPreviousPage = it.hasPreviousPage
                total = it.total?.toInt()
                if (!it.list.isEmpty()) {
//                    if (BigDecimal(it.list?.get(0)?.id) > newest) {
//                        if (isAuto == true) {
//                            showDialog(it.list?.get(0)?.alarmPictureName)
//                        }
//                        initChart1()
//                        newest = BigDecimal(it.list?.get(0)?.id)
//                    }
                    initChart1()
                    mList.clear()
                    mList.addAll(it?.list)
                }
            }
            easyAdapter.submitList(mList)
        })
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

        if (lineChart.data != null && lineChart.data.dataSetCount > 0) {
            set1 = lineChart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()

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

            lineChart.data = data
            lineChart.setVisibleXRangeMaximum(20f)
        }
    }

    override fun onNothingSelected() {}
    override fun onValueSelected(e: Entry?, h: Highlight?) {}

    private fun initEvent() {
        btnFrist.click {
            if (hasPreviousPage == true) {
                isAuto = false
                temp = 0
                pageNum = 1
                getDataInfo()
            } else {
                toast("已经是第一页了")
            }
        }
        btnPre.click {
            if (hasPreviousPage == true) {
                isAuto = false
                temp = 0
                pageNum--
                getDataInfo()
            } else {
                toast("已经是第一页了")
            }
        }
        btnNext.click {
            if (hasNextPage == true) {
                isAuto = false
                temp = 0
                pageNum++
                getDataInfo()
            } else {
                toast("已经是最后一页了")
            }
        }
        btnLast.click {
            if (hasNextPage == false) {
                toast("已经是最后一页了")
            } else {
                isAuto = false
                temp = 0
                pageNum = total / 10 + 1
                getDataInfo()
            }
        }
        day.click {
            day.isSelected = true
            month.isSelected = false
            year.isSelected = false
            mApiViewModel.getListByTimeHistory(30).observe(this, androidx.lifecycle.Observer {
                if (it?.isEmpty()?.not() == true) {
                    var num = 0
                    for (data in it) {
                        num = num + data.sum
                    }
                    tvNumAll.text = "$num 次"
                    initTu(chart2, 1)
                    setData(chart2, it, 1)
                }
            })
        }
        month.click {
            day.isSelected = false
            month.isSelected = true
            year.isSelected = false
            mApiViewModel.getListByMonth(12).observe(this, androidx.lifecycle.Observer {
                if (it?.isEmpty()?.not() == true) {
                    var num = 0
                    for (data in it) {
                        num = num + data.sum
                    }
                    tvNumAll.text = "$num 次"
                    initTu(chart2, 2)
                    setData(chart2, it, 2)
                }
            })
        }
        year.click {
            day.isSelected = false
            month.isSelected = false
            year.isSelected = true
            mApiViewModel.getListByYear(10).observe(this, androidx.lifecycle.Observer {
                if (it?.isEmpty()?.not() == true) {
                    var num = 0
                    for (data in it) {
                        num = num + data.sum
                    }
                    tvNumAll.text = "$num"
                    initTu(chart2, 3)
                    setData(chart2, it, 3)
                }
            })
        }
        tvTitleClick.click {
            clicks++
            if (clicks > 7) {
                clicks = 0
                startActivity(Intent(this, SecondActivity::class.java))
            }
        }
    }

    fun initReceiver() {

        mCursorTimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    // 通过光标间歇性显示实现闪烁效果
                    clicks = 0
                    isAuto = true
                    pageNum = 1
                    temp = System.currentTimeMillis()
                    getNewest()
                }
            }
        }
        mCursorTimer = Timer()
        mCursorTimer?.scheduleAtFixedRate(mCursorTimerTask, 10000L, mCursorDuration)
    }


    fun showDialog(string: String) {

        var dialog = ErrorDialog.Builder(this)
            .message(string)
            .setNegativeButton { dialog ->
                dialog.dismiss()
            }.build()
        dialog.show()

        Observable.timer(5000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                dialog.dismiss()
            }
    }

    fun getMyData(resType: String): String {
        return when (resType) {
            "1" -> "未在指定时间休息"
            "2" -> "未在指定区域监督"
            "4" -> "厕所区域异常"
            "8" -> "窗户区域异常"
            "16" -> "高度异常"
            "32" -> "非休息时间休息"
            "64" -> "进入三角区域"
            "128" -> "内务不整"
            "512" -> "单人留仓"
            "1024" -> "吊拉窗户"
            "2048" -> "搭人梯"
            "4096" -> "站被子上做板报"
            else -> getOther(resType)
        }
    }


    fun getOther(resType: String): String {
        var resu: String = ""
        var string = Integer.toBinaryString(Integer.parseInt(resType))
        for ((index, value) in string.toCharArray().withIndex()) {
            if (value == '1') {
                loge("$resType --->>$index")
                resu = when (string.toCharArray().size - index) {
                    1 -> "未在指定时间休息"
                    2 -> "未在指定区域监督"
                    3 -> "厕所区域异常"
                    4 -> "窗户区域异常"
                    5 -> "高度异常"
                    6 -> "非休息时间休息"
                    7 -> "进入三角区域"
                    8 -> "内务不整"
                    10 -> "单人留仓"
                    11 -> "吊拉窗户"
                    12 -> "搭人梯"
                    13 -> "站被子上做板报"
                    else -> "未在指定时间休息"
                } + (if (resu.isNotEmpty()) ("+ $resu") else resu)
            }
        }
        return resu
    }

    override fun onResume() {
        super.onResume()
        //todo  初始化数据
//        initChart1()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCursorTimer?.cancel()
    }

    private fun initTu(lineChart: BarChart, type: Int) {
        // background color
        lineChart.setBackgroundColor(Color.WHITE)
        // disable description text
        lineChart.getDescription().setEnabled(false)
        // enable touch gestures
        lineChart.setTouchEnabled(true)
        // set listeners
        lineChart.setOnChartValueSelectedListener(this)
        lineChart.setDrawGridBackground(false)
        // create marker to display box when values are selected
        val mv = MyMarkerView(this, R.layout.custom_marker_view)
        mv.setType(type)
        // Set the marker to the lineChart
        mv.setChartView(lineChart)
        lineChart.marker = mv
        lineChart.setAutoScaleMinMaxEnabled(false)
        // enable scaling and dragging
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        // lineChart.setScaleXEnabled(true);
        // lineChart.setScaleYEnabled(true);


        // force pinch zoom along both axis
        lineChart.setPinchZoom(true)

        val xAxis: XAxis
        run {
            // // MyData-Axis Style // //
            xAxis = lineChart.xAxis
            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f)

        }

        xAxis.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (type) {
                    1 ->  "${value.toInt()}日"
                    2 -> "${value.toInt()}月"
                    3 -> "${if(value < 10) 0+(value.toInt()) else value.toInt()}年"
                    4 -> "${value.toInt()}时"
                    else ->"${value.toInt()}日"
                }
            }
        })


        val yAxis: YAxis
        run {
            // // Y-Axis Style // //
            yAxis = lineChart.axisLeft

            // disable dual axis (only use LEFT axis)
            lineChart.axisRight.isEnabled = false

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f)

            // axis range
            yAxis.axisMaximum = when (type) {
                1 -> 600f
                2 -> 6000f
                3 -> 60000f
                4 -> 60f
                else -> 600f
            }
            yAxis.axisMinimum = 0f
        }

        run {
            // // Create Limit Lines // //
            val llXAxis = LimitLine(9f, "Index 10")
            llXAxis.lineWidth = 4f
            llXAxis.enableDashedLine(10f, 10f, 0f)
            llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
            llXAxis.textSize = 10f
//            llXAxis.typeface = tfRegular

            val ll1 = LimitLine(350f, "Upper Limit")
            ll1.lineWidth = 4f
            ll1.enableDashedLine(10f, 10f, 0f)
            ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            ll1.textSize = 10f
//            ll1.typeface = tfRegular

            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true)
            xAxis.setDrawLimitLinesBehindData(true)
        }
        xAxis.setLabelCount(6)
        // draw points over time
        lineChart.animateX(1500)

        // get the legend (only possible after setting data)
        val l = lineChart.legend

        // draw legend entries as lines
        l.form = Legend.LegendForm.LINE
    }

}
