package com.be.base.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.zxt.R
import com.orhanobut.hawk.Hawk
import com.tezwez.base.helper.loadFromUrl

import kotlinx.android.synthetic.main.dialog_one_text.*

class ErrorDialog
/*  ---------------------------------- 构造方法 -------------------------------------  */
private constructor(builder: Builder, context: Context) :
    Dialog(context, R.style.LightProgressDialog) {


    private var messageStr: String?
    private var canCannel: Boolean? = true
//    private var mTextView: TextView? = null
    /*  -------------------------------- 接口监听 -------------------------------------  */

    private var noOnclickListener: ((dialog: ErrorDialog) -> Unit?)? = null

    init {
        this.messageStr = builder.messageStr
        this.canCannel = builder.canCannel
        this.noOnclickListener = builder.mNoOnclickListener
    }

    class Builder(private val context: Context) {
        var messageStr: String? = null
        var canCannel: Boolean? = true
        var mNoOnclickListener: ((dialog: ErrorDialog) -> Unit?)? = null

        fun message(message: String): Builder {
            this.messageStr = message
            return this
        }

        fun canCannel(canCannel: Boolean): Builder {
            this.canCannel = canCannel
            return this
        }

        fun setNegativeButton(mOnclickListener: (dialog: ErrorDialog) -> Unit): Builder {
            this.mNoOnclickListener = mOnclickListener
            return this
        }


        fun build(): ErrorDialog {
            return ErrorDialog(this, context)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = View.inflate(context, R.layout.dialog_one_text, null)
        setContentView(view)//自定义布局
        //按空白处不能取消动画
        //        setCanceledOnTouchOutside(false);
        val window = this.window
        window?.setWindowAnimations(R.style.DialogAnimBottom)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.WRAP_CONTENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = params
        this?.setCanceledOnTouchOutside(this.canCannel == true)
        //初始化界面数据
        initData()
        //初始化界面控件的事件
        initEvent()

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private fun initEvent() {
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener {
            noOnclickListener?.invoke(this@ErrorDialog)
        }
    }

    /**
     * 初始化界面控件的显示数据
     */
    private fun initData() {
        //
        mImage.loadFromUrl(Hawk.get<String>("PHOTO") + "/" + messageStr)
//        image.loadFromUrl()
//        Toast.makeText(context,Hawk.get<String>("PHOTO") + "/" + messageStr,Toast.LENGTH_LONG).show()
    }
}
