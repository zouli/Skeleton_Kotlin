package com.riverside.skeleton.kotlin.widget.captcha

import android.app.Activity
import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.updatePadding
import com.riverside.skeleton.kotlin.base.utils.KeyboardHelper
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.captcha.CaptchaView.OnSendListener
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
    private lateinit var onSendListener: OnSendListener

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
        if (value > 0) et_vc?.let { it.filters = arrayOf(InputFilter.LengthFilter(value)) }
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


    //输入框PaddingTop
    @Attr(AttrType.DIMENSION)
    private val editPaddingTop: Int by AttributeSetInfo(
        attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_editPaddingTop, -1
    )

    fun setEditPaddingTop(top: Int) {
        if (top > -1) et_vc?.updatePadding(top = top)
    }

    //输入框PaddingBottom
    @Attr(AttrType.DIMENSION)
    private val editPaddingBottom: Int by AttributeSetInfo(
        attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_editPaddingBottom, -1
    )

    fun setEditPaddingBottom(bottom: Int) {
        if (bottom > -1) et_vc?.updatePadding(bottom = bottom)
    }

    //输入框PaddingStart
    @Attr(AttrType.DIMENSION)
    private val editPaddingStart: Int by AttributeSetInfo(
        attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_editPaddingStart, -1
    )

    fun setEditPaddingStart(start: Int) {
        if (start > -1) et_vc?.updatePadding(left = start)
    }

    //输入框PaddingEnd
    @Attr(AttrType.DIMENSION)
    private val editPaddingEnd: Int by AttributeSetInfo(
        attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_editPaddingEnd, -1
    )

    fun setEditPaddingEnd(end: Int) {
        if (end > -1) et_vc?.updatePadding(right = end)
    }

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_input_captcha, this@InputCaptchaView)
        setMaxLength(maxLength)
        setTextHint(textHint)
        setSleepSecond(sleepSecond)
        setEditPaddingTop(editPaddingTop)
        setEditPaddingBottom(editPaddingBottom)
        setEditPaddingStart(editPaddingStart)
        setEditPaddingEnd(editPaddingEnd)

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
        if (::onSendListener.isInitialized)
            if (onSendListener.onClick()) //调用获取验证码点击事件
                startCount()    //开始计时
    }

    /**
     * 设置回调接口
     */
    fun setOnSendListener(block: () -> Boolean) {
        this.onSendListener = object : OnSendListener {
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