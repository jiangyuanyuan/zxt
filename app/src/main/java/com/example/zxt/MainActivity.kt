package com.example.zxt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.be.base.easy.EasyAdapter
import com.be.base.view.ErrorDialog
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import com.tezwez.base.common.BaseActivity
import com.tezwez.base.helper.click
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

class MainActivity : BaseActivity(), OnChartValueSelectedListener {
    lateinit var easyAdapter : EasyAdapter<MyData>
    var mList = mutableListOf<MyData>()
    val mApiViewModel: ApiViewModel by viewModel()
    var pageNum = 1

    var hasNextPage: Boolean ?= true
    var isAuto: Boolean ?= false //自动轮寻 查数据
    var hasPreviousPage: Boolean?=false
    var lastPage = 1
    private var temp: Long = 0
    private var receiver: BroadcastReceiver? = null
    private var newest : BigDecimal = BigDecimal(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initTu(chart)
        initTu(chart2)
        recycleView.layoutManager = LinearLayoutManager(this)
        easyAdapter = EasyAdapter(R.layout.item_msg,{ itmeView,position,item->
            itmeView.tvContent.text = "警告原因：${item.alarmReason}"
            itmeView.click {
//                toast("${item.alarmPictureName}")
                showDialog(item.alarmPictureName)
            }
        }, emptyList())
        recycleView.adapter = easyAdapter
        getDataInfo()
        initEvent()
        initReceiver()
        //image.loadFromUrl("https://dpic.tiankong.com/00/x7/QJ6331726352.jpg?x-oss-process=style/794ws")

        mApiViewModel.getListByTime(30).observe(this,androidx.lifecycle.Observer {
            if(it.isEmpty().not()) {
                setData(chart,it)
            }
        })

        mApiViewModel.getListByTime(90).observe(this,androidx.lifecycle.Observer {
            if(it.isEmpty().not()) {
                setData(chart2,it)
            }
        })

    }



    fun getDataInfo(){
        mApiViewModel.getList(pageNum,10).observe(this,androidx.lifecycle.Observer {

            if(it !=null) {
                hasNextPage = it.hasNextPage
                hasPreviousPage = it.hasPreviousPage
                lastPage = it.lastPage?.toInt()
                if(!it.list.isEmpty()) {
                    if(BigDecimal(it.list?.get(0)?.id) > newest) {
                        if(isAuto == true){
                            showDialog(it.list?.get(0)?.alarmPictureName)
                        }
                        newest = BigDecimal(it.list?.get(0)?.id)
                    }
                    mList.clear()
                    mList.addAll(it?.list)
                }
            }
            easyAdapter.submitList(mList)
        })
    }
    private fun initTu(lineChart : LineChart) {
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

        // Set the marker to the lineChart
        mv.setChartView(lineChart)
        lineChart.marker = mv

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

        val yAxis: YAxis
        run {
            // // Y-Axis Style // //
            yAxis = lineChart.axisLeft

            // disable dual axis (only use LEFT axis)
            lineChart.axisRight.isEnabled = false

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f)

            // axis range
            yAxis.axisMaximum = 200f
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

            val ll1 = LimitLine(150f, "Upper Limit")
            ll1.lineWidth = 4f
            ll1.enableDashedLine(10f, 10f, 0f)
            ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            ll1.textSize = 10f
//            ll1.typeface = tfRegular

            val ll2 = LimitLine(-30f, "Lower Limit")
            ll2.lineWidth = 4f
            ll2.enableDashedLine(10f, 10f, 0f)
            ll2.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
            ll2.textSize = 10f
//            ll2.typeface = tfRegular

            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true)
            xAxis.setDrawLimitLinesBehindData(true)

            // add limit lines
            yAxis.addLimitLine(ll1)
            yAxis.addLimitLine(ll2)
            //xAxis.addLimitLine(llXAxis);
        }

        // add data
//        seekBarX.setProgress(45)
//        seekBarY.setProgress(180)
//        setData(45, 180f)

        // draw points over time
        lineChart.animateX(1500)

        // get the legend (only possible after setting data)
        val l = lineChart.legend

        // draw legend entries as lines
        l.form = Legend.LegendForm.LINE


    }

    private fun setData(lineChart : LineChart,list : List<CountBean>) {
        val values = ArrayList<Entry>()
        for (i in 0 until list.size) {
            var data = list.get(i)
            var sum = data.sum.toFloat()
            values.add(Entry(i.toFloat(), sum, resources.getDrawable(R.drawable.star)))
        }

        val set1: LineDataSet

        if (lineChart.data != null && lineChart.data.dataSetCount > 0) {
            set1 = lineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "DataSet 1")

            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f)

            // black lines and points
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> lineChart.axisLeft.axisMinimum }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.BLACK
            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            lineChart.data = data
        }
    }
    override fun onNothingSelected() {

    }
    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }
    private fun initEvent() {
        btnPre.click {
            if(hasPreviousPage == true){
                isAuto = false
                temp = 0
                pageNum--
                getDataInfo()
            }else{
                toast("已经是第一页了")
            }
        }

        btnNext.click {
            if(hasNextPage == true){
                isAuto = false
                temp = 0
                pageNum++
                getDataInfo()
            }else{
                toast("已经是最后一页了")
            }
        }

        btnLast.click {
            if(hasNextPage == false){
                toast("已经是最后一页了")
            }else{
                isAuto = false
                temp = 0
                pageNum = lastPage
                getDataInfo()
            }
        }
    }

    fun initReceiver(){
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action == Intent.ACTION_TIME_TICK) {
                    if (System.currentTimeMillis() - temp > 1000 * 60 * 2) {
                        isAuto = true
                        pageNum = 1
                        temp = System.currentTimeMillis()
                        getDataInfo()
                    }
                }
            }
        }

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(receiver, filter)
    }

    fun showDialog(string:String){

       var dialog =  ErrorDialog.Builder(this)
            .message(string)
            .setNegativeButton { dialog ->
                dialog.dismiss()
            }.build()
        dialog.show()

        Observable.timer(3000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                dialog.dismiss()
            }
    }



}
