package com.riverside.skeleton.kotlin.widget.captcha.kotlinnextinputs

import com.github.yoojia.inputs.ViewInput
import com.riverside.skeleton.kotlin.kotlinnextinputs.WidgetProvidersExtend
import com.riverside.skeleton.kotlin.widget.captcha.BoxCaptchaView
import com.riverside.skeleton.kotlin.widget.captcha.InputCaptchaView

/**
 * 自定义控件选择器扩展    1.0
 * b_e      2020/11/28
 */
fun WidgetProvidersExtend.Companion.inputCaptchaView(inputCaptchaView: InputCaptchaView) =
    object : ViewInput<InputCaptchaView>(inputCaptchaView) {
        override fun getValue(): String = inputCaptchaView.text
    }

fun WidgetProvidersExtend.Companion.boxCaptchaView(boxCaptchaView: BoxCaptchaView) =
    object : ViewInput<BoxCaptchaView>(boxCaptchaView) {
        override fun getValue(): String = boxCaptchaView.text
    }