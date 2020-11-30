package com.riverside.skeleton.kotlin.widgettest.anko

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.github.yoojia.inputs.StaticScheme
import com.github.yoojia.inputs.ValueScheme
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.kotlinnextinputs.AndroidToastMessageDisplay
import com.riverside.skeleton.kotlin.kotlinnextinputs.messageDisplay
import com.riverside.skeleton.kotlin.kotlinnextinputs.nextInput
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.widget.captcha.BoxCaptchaView
import com.riverside.skeleton.kotlin.widget.captcha.InputCaptchaView
import com.riverside.skeleton.kotlin.widget.captcha.kotlinnextinputs.nextInput
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class CaptchaActivity : SBaseActivity() {
    lateinit var et_phone: EditText
    lateinit var icv: InputCaptchaView
    lateinit var bcv: BoxCaptchaView

    override fun initView() {
        title = "Captcha"

        verticalLayout {
            lparams(matchParent, matchParent)
            et_phone = editText {
                inputType = EditorInfo.TYPE_CLASS_PHONE
            }.lparams(matchParent, wrapContent)

            icv = inputCaptchaView {
                sleepSecond = 30
                textHint = "请输入手机验证码"

                onSend {
                    icvValidate()
                }
            }.lparams(matchParent, wrapContent)

            textView("Box").lparams(matchParent, wrapContent) {
                topMargin = 16.dip
            }

            bcv = boxCaptchaView {
                charNumber = 6
                divideWidth = 10.dip

                onInputChanged {
                    it.toast(this@CaptchaActivity)
                }
            }.lparams(matchParent, wrapContent)

            button("Validate") {
                onClick {
                    validate()
                }
            }.lparams(matchParent, wrapContent)
        }
    }

    private fun icvValidate() = nextInput {
        messageDisplay = AndroidToastMessageDisplay()

        add(et_phone.nextInput, StaticScheme.Required().msg("请输入手机号码"))
    }.test()

    override fun doValidate(): Boolean = nextInput {
        messageDisplay = AndroidToastMessageDisplay()

        add(icv.nextInput, StaticScheme.Required().msg("请输入验证码"))
        add(bcv.nextInput, StaticScheme.Required().msg("请输入Box验证码"))
        add(icv.nextInput, ValueScheme.EqualsTo("111111").msg("验证码输入错误"))
        add(bcv.nextInput, ValueScheme.EqualsTo("222222").msg("Box验证码输入错误"))
    }.test()
}