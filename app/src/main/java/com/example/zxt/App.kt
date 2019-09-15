package com.example.zxt

import android.app.Application
import com.orhanobut.hawk.Hawk
import com.tezwez.club.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import kotlin.properties.Delegates

/**
 * Description : App
 * Author      : jovelin
 * Version     : 2.0
 * Create Date : 2019-09-15 11:15
 */
class App : Application() {

    companion object {
        private var instance: App by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Hawk.init(this).build()
        initInjector()
    }

    private fun initInjector() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(
                appModule
            ))
        }
    }
}