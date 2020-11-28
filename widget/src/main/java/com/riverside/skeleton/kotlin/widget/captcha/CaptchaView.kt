package com.riverside.skeleton.kotlin.widget.captcha

/**
 * 验证码控件    1.0
 * b_e      2020/11/14
 */
interface CaptchaView {
    /**
     * 取得已输入验证码
     */
    val text: String

    /**
     * 输入变更接口
     */
    interface InputChangedListener {
        fun inputChanged(text: String)
    }

    /**
     * 结果回调接口
     */
    interface OnSendListener {
        fun onClick(): Boolean
    }
}