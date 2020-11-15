package com.riverside.skeleton.kotlin.widget.captcha

import android.app.Activity
import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.riverside.skeleton.kotlin.base.utils.KeyboardHelper
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
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
    @Attr(AttrType.INTEGER)
    private val sleepSecond: Int by AttributeSetInfo(
        attrs, R.styleable.InputCaptchaView,
        R.styleable.InputCaptchaView_icv_sleepSecond, 60
    )

    fun setSleepSecond(value: Int) {
        count = value
    }

    //验证码输入框长度
    @Attr(AttrType.INTEGER)
    private val maxLength: Int by AttributeSetInfo(
        attrs, R.styleable.InputCaptchaView,
        R.styleable.InputCaptchaView_icv_maxLength, 0
    )

    fun setMaxLength(value: Int) {
        if (value > 0) et_vc?.let { it.filters = arrayOf(InputFilter.LengthFilter(maxLength)) }
    }

    //设置Hint
    @Attr(AttrType.STRING)
    private val textHint: String by AttributeSetInfo(
        attrs, R.styleable.InputCaptchaView,
        R.styleable.InputCaptchaView_icv_textHint, ""
    )

    fun setTextHint(value: String) {
        et_vc?.let { it.hint = value }
    }

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_input_captcha, this@InputCaptchaView)
        if (maxLength > 0)
            et_vc.filters = arrayOf(InputFilter.LengthFilter(maxLength))
        et_vc.hint = textHint

        count = sleepSecond

        setListener()
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