package com.riverside.skeleton.kotlin.widgettest.anko

import android.view.ViewManager
import com.riverside.skeleton.kotlin.widget.captcha.BoxCaptchaView
import com.riverside.skeleton.kotlin.widget.captcha.InputCaptchaView
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.inputCaptchaView(init: InputCaptchaView.() -> Unit): InputCaptchaView =
    ankoView({ InputCaptchaView(it) }, theme = 0, init = init)

fun InputCaptchaView.onClick(handler: () -> Boolean) = setOnResultListener {
    handler()
}

var InputCaptchaView.sleepSecond: Int get() = 0; set(value) = setSleepSecond(value)

var InputCaptchaView.maxLength: Int get() = 0; set(value) = setMaxLength(value)

var InputCaptchaView.textHint: String get() = ""; set(value) = setTextHint(value)


inline fun ViewManager.boxCaptchaView(init: BoxCaptchaView.() -> Unit): BoxCaptchaView =
    ankoView({ BoxCaptchaView(it) }, theme = 0, init = init)

fun BoxCaptchaView.onInputChanged(handler: (text: String) -> Unit) = setOnInputChangedListener {
    handler(it)
}

var BoxCaptchaView.charNumber: Int get() = 0; set(value) = setCharNumber(value)

var BoxCaptchaView.divideWidth: Int get() = 0; set(value) = setDivideWidth(value)

var BoxCaptchaView.itemStyle: Int get() = 0; set(value) = setItemStyle(value)
