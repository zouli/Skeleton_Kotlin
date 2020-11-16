package com.riverside.skeleton.kotlin.widgettest.xml

import com.github.yoojia.inputs.StaticScheme
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.kotlinnextinputs.AndroidToastMessageDisplay
import com.riverside.skeleton.kotlin.kotlinnextinputs.messageDisplay
import com.riverside.skeleton.kotlin.kotlinnextinputs.nextInput
import com.riverside.skeleton.kotlin.util.notice.toast
import kotlinx.android.synthetic.main.activity_captcha_xml.*

class CaptchaActivity : SBaseActivity() {
    override fun setLayoutID() = R.layout.activity_captcha_xml

    override fun initView() {
        icv.setOnResultListener {
            icvValidate().apply { }
        }

        bcv.setCharNumber(5)
        bcv.setOnInputChangedListener {
            it.toast(this@CaptchaActivity)
        }
    }

    private fun icvValidate() = nextInput {
        messageDisplay = AndroidToastMessageDisplay()

        add(et_phone.nextInput, StaticScheme.Required().msg("请输入手机号码"))
    }.test()
}