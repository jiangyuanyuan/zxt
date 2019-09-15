package com.tezwez.club.data.api

import com.tezwez.base.net.dto.Rep
import com.tezwez.base.net.dto.Req
import com.tezwez.club.data.dto.DataInfo
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST


interface Api {
    /**
     * 换一批 名师推荐列表
     */
    @POST("/mall/alarmMsg/getList.do")
    fun getList(@Body req: Req): Single<Rep<DataInfo>>

}