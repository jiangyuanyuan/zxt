package com.tezwez.base.common

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaFormat.KEY_LANGUAGE
import android.os.Build
import android.os.Bundle

import android.view.*
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.head_layout.*
import me.yokeyword.fragmentation.SupportActivity


import java.util.*

import me.yokeyword.fragmentation.anim.DefaultNoAnimator


open class BaseActivity : SupportActivity()  {


    //权限
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        mToolbar?.setNavigationOnClickListener { finish() }
//        fragmentAnimator = DefaultHorizontalAnimator() //横向动画
//        fragmentAnimator = DefaultVerticalAnimator() //竖向动画
        fragmentAnimator = DefaultNoAnimator() //没有动画
        setStatusBar()
        initLocaleLanguage()
    }

    private fun initLocaleLanguage() {
        val resources = this.getResources()
        val configuration = resources.getConfiguration()
        configuration.locale = Hawk.get(KEY_LANGUAGE, Locale.getDefault())
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());//更新配置

    }

    private fun setStatusBar() {
        with(window) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            statusBarColor = Color.TRANSPARENT
//            addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }


}