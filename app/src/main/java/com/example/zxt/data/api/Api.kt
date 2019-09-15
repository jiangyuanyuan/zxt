package com.tezwez.club.data.api

import com.tezwez.base.net.dto.Rep
import com.tezwez.base.net.dto.Req
import com.tezwez.club.data.dto.CountBean
import com.tezwez.club.data.dto.DataInfo
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST


interface Api {
    /**
     * 获取列表
     */
    @POST("/mall/alarmMsg/getList.do")
    fun getList(@Body req: Req): Single<Rep<DataInfo>>

    @POST("/mall/alarmMsg/getListByTime.do")
    fun getListByTime(@Body req: Req): Single<Rep<List<CountBean>>>

}