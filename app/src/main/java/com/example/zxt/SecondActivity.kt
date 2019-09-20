package com.example.zxt

import android.os.Bundle
import com.orhanobut.hawk.Hawk
import com.tezwez.base.common.BaseActivity
import com.tezwez.base.helper.click
import kotlinx.android.synthetic.main.activity_second.*
import org.jetbrains.anko.toast

class SecondActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        tvContent.text = "当前ip:$API_BASE_URL"
        btnFinish.click {
            if (etIp.text.toString().isNullOrBlank().not() && etIp.text.toString().contains("http://")) {
                Hawk.put("IP", etIp.text.toString())
                finish()
            }else{
                toast("请输入正确的Ip地址")
            }
        }

        btnCancle.click{
            finish()
        }
    }
}
