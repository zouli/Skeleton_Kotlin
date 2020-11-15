package com.riverside.skeleton.kotlin.widget.captcha

import android.app.Activity
import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.riverside.skeleton.kotlin.base.utils.KeyboardHelper
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.captcha.CaptchaView.ResultListener
import kotlinx.android.synthetic.main.view_input_captcha.view.*
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 * 获得验证码控件  1.0
 *
 * b_e  2020/11/14
 */
class InputCaptchaView(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs), CaptchaView {

    constructor(context: Context) : this(context, null)

    //回调函数
    private lateinit var onResultListener: ResultListener

    //获取验证码间隔时间（秒）
    var sleepSecond = 0
        set(value) {
            count = value
            field = value
        }

    //验证码输入框长度
    var maxLength = 0
        set(value) {
            if (value > 0) et_vc?.let { it.filters = arrayOf(InputFilter.LengthFilter(maxLength)) }
            field = value
        }

    //设置Hint
    var textHint = ""
        set(value) {
            et_vc?.let { it.hint = value }
            field = value
        }

    init {
        attrs?.let { getAttrs(it) }
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_input_captcha, this@InputCaptchaView)
        if (maxLength > 0)
            et_vc.filters = arrayOf(InputFilter.LengthFilter(maxLength))
        et_vc.hint = textHint

        setListener()
    }

    private fun getAttrs(attrs: AttributeSet) {
        with(context.obtainStyledAttributes(attrs, R.styleable.InputCaptchaView)) {
            sleepSecond = getInt(R.styleable.InputCaptchaView_icv_sleepSecond, 60)
            maxLength = getInt(R.styleable.InputCaptchaView_icv_maxLength, 0)
            getString(R.styleable.InputCaptchaView_icv_textHint)?.let { textHint = it }

            recycle()
        }
    }

    private fun setListener() {
        tv_send_vc.setOnClickListener {
            doClick(it)
        }
    }

    /**
     * 获取验证码点击事件
     *
     * @param v
     */
    private fun doClick(v: View) {
        KeyboardHelper.init(context as Activity).hideKeyboard()
        if (::onResultListener.isInitialized)
            if (onResultListener.onClick()) //调用获取验证码点击事件
                startCount()    //开始计时
    }

    /**
     * 设置回调接口
     */
    fun setOnResultListener(block: () -> Boolean) {
        this.onResultListener = object : ResultListener {
            override fun onClick(): Boolean = block()
        }
    }

    /**
     * 取得已输入验证码
     */
    override val text get() = et_vc.text.toString()

    private var count = 0
    private lateinit var timer: Timer

    /**
     * 开始计时
     */
    private fun startCount() {
        timer = fixedRateTimer("", false, 0, 1000) {
            if (count > 0) {
                (context as Activity).runOnUiThread {
                    tv_send_vc.isEnabled = false
                    tv_send_vc.text = resources.getString(
                        R.string.button_view_send_verification_code2,
                        count
                    )
                }
            } else {
                resetCount()
            }
            count--

        }
    }

    /**
     * 重新开始计时
     */
    private fun resetCount() {
        (context as Activity).runOnUiThread {
            tv_send_vc.isEnabled = true
            tv_send_vc.text = resources.getString(R.string.button_view_send_verification_code)
            count = sleepSecond
        }
        timer.cancel()
    }
}