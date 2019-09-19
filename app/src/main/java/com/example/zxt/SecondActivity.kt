package com.example.zxt

import android.os.Bundle
import com.orhanobut.hawk.Hawk
import com.tezwez.base.common.BaseActivity
import com.tezwez.base.helper.click
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        btnFinish.click {
            Hawk.put("IP",etIp.text.toString())
            finish()
        }

    }
}
