package com.riverside.skeleton.kotlin.widget.captcha

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.captcha.CaptchaView.InputChangedListener
import kotlinx.android.synthetic.main.view_box_captcha.view.*

/**
 * 验证码输入框控件  1.0
 *
 * b_e  2020/11/15
 */
class BoxCaptchaView(context: Context, attrs: AttributeSet?) :
    RelativeLayout(context, attrs), CaptchaView {

    constructor(context: Context) : this(context, null)

    //验证码显示控件
    private lateinit var tv_shows: List<TextView>

    //输入框个数
    @Attr(AttrType.INTEGER)
    private val charNumber: Int by AttributeSetInfo(
        attrs, R.styleable.BoxCaptchaView,
        R.styleable.BoxCaptchaView_bcv_charNumber, 6
    )

    fun setCharNumber(value: Int) {
        initTextViews(value, divideWidth, itemStyle)
    }

    //输入框间隔
    @Attr(AttrType.DIMENSION)
    private val divideWidth: Int by AttributeSetInfo(
        attrs, R.styleable.BoxCaptchaView,
        R.styleable.BoxCaptchaView_bcv_divideWidth, 0
    )

    fun setDivideWidth(value: Int) {
        initTextViews(charNumber, value, itemStyle)
    }

    //显示框样式
    @Attr(AttrType.REFERENCE)
    private val itemStyle: Int by AttributeSetInfo(
        attrs, R.styleable.BoxCaptchaView,
        R.styleable.BoxCaptchaView_bcv_itemStyle, R.style.BoxCaptcha_Item
    )

    fun setItemStyle(value: Int) {
        initTextViews(charNumber, divideWidth, value)
    }

    //回调函数
    private lateinit var inputChangedListener: InputChangedListener

    init {
        initView()
    }

    fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_box_captcha, this@BoxCaptchaView)

        initTextViews(charNumber, divideWidth, itemStyle)
        setListener()
    }

    private fun initTextViews(charNumber: Int, divideWidth: Int, itemStyle: Int) {
        et_input?.let {
            it.isCursorVisible = false
            it.filters = arrayOf(InputFilter.LengthFilter(charNumber))
            requestFocus()
        }

        ll_content?.let { ll_content ->
            ll_content.removeAllViews()

            tv_shows = (1..charNumber).map {
                TextView(ContextThemeWrapper(context, itemStyle)).also { tv ->
                    tv.isEnabled = false
                    if (it > 1)
                        ll_content.addView(View(context).apply {
                            this.layoutParams = LinearLayout.LayoutParams(divideWidth, 0)
                        })
                    ll_content.addView(tv)
                }
            }
        }
    }

    private fun setListener() {
        et_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                s.toString().takeIf { !TextUtils.isEmpty(it) }?.split("")
                    ?.forEachIndexed { i, str ->
                        if (i >= charNumber) return@afterTextChanged
                        setText(str)
                        et_input.setText("")
                    }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        })

        et_input.setOnKeyListener { _, keyCode, event ->
            when {
                keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN -> {
                    onKeyDelete()
                    true
                }
                else -> false
            }
        }
    }

    private fun setText(s: String) {
        tv_shows.firstOrNull { TextUtils.isEmpty(it.text) }?.text = s
        if (::inputChangedListener.isInitialized)
            inputChangedListener.inputChanged(text)
    }

    private fun onKeyDelete() {
        tv_shows.reversed().firstOrNull { !TextUtils.isEmpty(it.text) }?.text = ""
        if (::inputChangedListener.isInitialized)
            inputChangedListener.inputChanged(text)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        et_input.width = width
        et_input.height = height
    }

    /**
     * 设置回调接口
     */
    fun setOnInputChangedListener(block: (text: String) -> Unit) {
        this.inputChangedListener = object : InputChangedListener {
            override fun inputChanged(text: String) = block(text)
        }
    }

    /**
     * 取得已输入验证码
     */
    override val text: String
        get() = tv_shows.filter { !TextUtils.isEmpty(it.text) }
            .joinToString("") { it.text.toString() }
            .trim()

}