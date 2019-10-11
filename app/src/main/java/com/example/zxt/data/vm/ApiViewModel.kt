package com.tezwez.club.data.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zxt.data.api.DataObserver
import com.example.zxt.data.repository.SingleLiveEvent
import com.tezwez.base.helper.applySchedulersOnSingle
import com.tezwez.base.net.dto.Req
import com.tezwez.club.data.api.Api
import com.tezwez.club.data.dto.CountBean
import com.tezwez.club.data.dto.DataInfo
import com.tezwez.club.data.dto.GetCaveat
import com.tezwez.club.data.dto.MyData

class ApiViewModel(private val api: Api) : ViewModel() {
    val getList = SingleLiveEvent<DataInfo>()
    val getNewest = SingleLiveEvent<List<MyData>>()

    val getCount = SingleLiveEvent<List<CountBean>>()
    val getCountHistory = SingleLiveEvent<List<CountBean>>()
    val getListByMonth = SingleLiveEvent<List<CountBean>>()
    val getListByYear = SingleLiveEvent<List<CountBean>>()
    val getCaveat = SingleLiveEvent<List<GetCaveat>>()



    /**
     * 获取最新ID
     */
    fun getNewest(
    ): LiveData<List<MyData>> = let {
        val req = Req()
        api.getNewest(req).compose(applySchedulersOnSingle())
            .subscribe(DataObserver(getNewest))
        return getNewest
    }


    /**
     * 获取列表接口
     */
    fun getList(
        pageNum: Int? = null,
        pageSize: Int? = null
    ): LiveData<DataInfo> = let {
        val req = Req()
        if (pageNum != null)
            req.putParams("pageNum", pageNum.toString())
        if (pageSize != null)
            req.putParams("pageSize", pageSize.toString())
        req.putParams("sortType",0.toString())
        api.getList(req).compose(applySchedulersOnSingle())
            .subscribe(DataObserver(getList))
        return getList
    }


    fun getListByHour(
        timeNumber: Int? = null
    ): LiveData<List<CountBean>> = let {
        val req = Req()
        if (timeNumber != null)
            req.putParams("timeNumber", timeNumber)
        req.putParams("sortType",1.toString())
        api.getListByHour(req).compose(applySchedulersOnSingle())
            .subscribe(DataObserver(getCount))
        return getCount
    }





    /**
     * 获取列表接口
     */
    fun getListByTime(
        timeNumber: Int? = null
    ): LiveData<List<CountBean>> = let {
        val req = Req()
        if (timeNumber != null)
            req.putParams("timeNumber", timeNumber)
        req.putParams("sortType",1.toString())
        api.getListByTime(req).compose(applySchedulersOnSingle())
            .subscribe(DataObserver(getCount))
        return getCount
    }



    fun getListByTimeHistory(
        timeNumber: Int? = null
    ): LiveData<List<CountBean>> = let {
        val req = Req()
        if (timeNumber != null)
            req.putParams("timeNumber", timeNumber)
        req.putParams("sortType",1.toString())
        api.getListByTime(req).compose(applySchedulersOnSingle())
            .subscribe(DataObserver(getCountHistory))
        return getCountHistory
    }

    fun getListByMonth(
        timeNumber: Int? = null
    ): LiveData<List<CountBean>> = let {
        val req = Req()
        if (timeNumber != null)
            req.putParams("timeNumber", timeNumber)
        req.putParams("sortType",1.toString())
        api.getListByMonth(req).compose(applySchedulersOnSingle())
            .subscribe(DataObserver(getListByMonth))
        return getListByMonth
    }

    fun getListByYear(
        timeNumber: Int? = null
    ): LiveData<List<CountBean>> = let {
        val req = Req()
        if (timeNumber != null)
            req.putParams("timeNumber", timeNumber)
        req.putParams("sortType",1.toString())
        api.getListByYear(req).compose(applySchedulersOnSingle())
            .subscribe(DataObserver(getListByYear))
        return getListByYear
    }


    fun getCaveat(timeNumber:Int,type:Int): LiveData<List<GetCaveat>> = let {
        val req = Req()
        req.putParams("timeNumber", timeNumber)
        req.putParams("type",type)
        api.getCaveat(req).compose(applySchedulersOnSingle())
            .subscribe(DataObserver(getCaveat))
        return getCaveat
    }
}