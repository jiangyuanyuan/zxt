package com.tezwez.club.di


import com.example.zxt.API_BASE_URL
import com.orhanobut.hawk.Hawk
import com.tezwez.base.net.okhttp
import com.tezwez.base.net.retrofit
import com.tezwez.club.data.api.Api
import com.tezwez.club.data.repository.ApiRepository
import com.tezwez.club.data.vm.ApiViewModel
import okhttp3.Call
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {

    single<Call.Factory> { okhttp() }

    single { retrofit(get(),if(Hawk.get<String>("IP").isNullOrBlank())  "http://120.79.2.255:8080" else Hawk.get<String>("IP")) }

    single { get<Retrofit>().create(Api::class.java) }

    single { ApiRepository(get()) }

    viewModel { ApiViewModel(get()) }
}

