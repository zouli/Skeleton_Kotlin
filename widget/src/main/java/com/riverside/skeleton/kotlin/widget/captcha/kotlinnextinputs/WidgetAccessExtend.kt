package com.riverside.skeleton.kotlin.widget.captcha.kotlinnextinputs

import com.github.yoojia.inputs.Input
import com.github.yoojia.inputs.WidgetAccess
import com.riverside.skeleton.kotlin.kotlinnextinputs.WidgetProvidersExtend

/**
 * 自定义控件访问扩展   1.0
 * b_e      2020/11/28
 */
fun WidgetAccess.findInputCaptchaView(viewId: Int): Input =
    WidgetProvidersExtend.inputCaptchaView(findView(viewId))

fun WidgetAccess.findBoxCaptchaView(viewId: Int): Input =
    WidgetProvidersExtend.boxCaptchaView(findView(viewId))