package com.riverside.skeleton.kotlin.widgettest.anko

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.github.yoojia.inputs.StaticScheme
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.kotlinnextinputs.AndroidToastMessageDisplay
import com.riverside.skeleton.kotlin.kotlinnextinputs.messageDisplay
import com.riverside.skeleton.kotlin.kotlinnextinputs.nextInput
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.notice.toast
import org.jetbrains.anko.*

class CaptchaActivity : SBaseActivity() {
    lateinit var et_phone: EditText

    override fun initView() {
        verticalLayout {
            lparams(matchParent, matchParent)
            et_phone = editText {
                inputType = EditorInfo.TYPE_CLASS_PHONE
            }.lparams(matchParent, wrapContent)

            inputCaptchaView {
                sleepSecond = 30
                textHint = "请输入手机验证码"

                onClick {
                    icvValidate().apply { }
                }
            }.lparams(matchParent, wrapContent)

            textView("Box").lparams(matchParent, wrapContent) {
                topMargin = 16.dip
            }

            boxCaptchaView {
                charNumber = 6
                divideWidth = 10.dip

                onInputChanged {
                    it.toast(this@CaptchaActivity)
                }
            }.lparams(matchParent, wrapContent)
        }
    }

    private fun icvValidate() = nextInput {
        messageDisplay = AndroidToastMessageDisplay()

        add(et_phone.nextInput, StaticScheme.Required().msg("请输入手机号码"))
    }.test()
}