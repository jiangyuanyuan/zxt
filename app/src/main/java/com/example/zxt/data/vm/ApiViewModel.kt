package com.tezwez.club.data.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zxt.data.api.DataObserver
import com.tezwez.base.helper.applySchedulersOnSingle
import com.tezwez.base.net.dto.Req
import com.tezwez.club.data.api.Api
import com.tezwez.club.data.dto.DataInfo

class ApiViewModel(private val api: Api) : ViewModel() {
    val getList = MutableLiveData<DataInfo>()

    /**
     * 随机推荐名师接口
     */
    fun getList(
        pageNum: Int? = null,
        pageSize: Int? = null
    ): LiveData<DataInfo> = let {
        val req = Req()
        if (pageNum != null)
            req.putParams("pageNum", pageNum)
        if (pageSize != null)
            req.putParams("pageSize", pageSize)
        api.getList(req).compose(applySchedulersOnSingle())
            .subscribe(DataObserver(getList))
        return getList
    }

}