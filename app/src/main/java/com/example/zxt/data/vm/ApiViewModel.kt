package com.tezwez.club.data.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zxt.data.api.DataObserver
import com.example.zxt.data.repository.SingleLiveEvent
import com.tezwez.base.helper.applySchedulersOnSingle
import com.tezwez.base.net.dto.Req
import com.tezwez.club.data.api.Api
import com.tezwez.club.data.dto.CountBean
import com.tezwez.club.data.dto.DataInfo

class ApiViewModel(private val api: Api) : ViewModel() {
    val getList = SingleLiveEvent<DataInfo>()

    val getCount = SingleLiveEvent<List<CountBean>>()
    val getCountHistory = SingleLiveEvent<List<CountBean>>()
    val getListByMonth = SingleLiveEvent<List<CountBean>>()
    val getListByYear = SingleLiveEvent<List<CountBean>>()

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


}