package com.riverside.skeleton.kotlin.widget.captcha.kotlinnextinputs

import com.riverside.skeleton.kotlin.kotlinnextinputs.WidgetProvidersExtend
import com.riverside.skeleton.kotlin.widget.captcha.BoxCaptchaView
import com.riverside.skeleton.kotlin.widget.captcha.ImageCaptchaView
import com.riverside.skeleton.kotlin.widget.captcha.InputCaptchaView

/**
 * WidgetProviders扩展
 */
val InputCaptchaView.nextInput get() = WidgetProvidersExtend.inputCaptchaView(this)
val BoxCaptchaView.nextInput get() = WidgetProvidersExtend.boxCaptchaView(this)
val ImageCaptchaView.nextInput get() = WidgetProvidersExtend.imageCaptchView(this)