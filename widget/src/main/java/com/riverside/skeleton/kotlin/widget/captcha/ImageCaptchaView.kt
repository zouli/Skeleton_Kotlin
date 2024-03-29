package com.riverside.skeleton.kotlin.widget.captcha

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.updatePadding
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.util.looper.runOnUi
import com.riverside.skeleton.kotlin.widget.R
import kotlinx.android.synthetic.main.view_image_captcha.view.*

/**
 * 图片验证码控件  1.1
 *
 * b_e  2021/01/26
 * 1.1  移除访问网络功能    2021/02/03
 */
class ImageCaptchaView(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs), CaptchaView {

    constructor(context: Context) : this(context, null)

    //验证码输入框长度
    @Attr(AttrType.INTEGER)
    private val maxLength: Int by AttributeSetInfo(
        context, attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_maxLength, 0
    )

    fun setMaxLength(value: Int) {
        if (value > 0) et_vc?.let { it.filters = arrayOf(InputFilter.LengthFilter(value)) }
    }

    //设置Hint
    @Attr(AttrType.STRING)
    private val textHint: String by AttributeSetInfo(
        context, attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_textHint, ""
    )

    fun setTextHint(value: String) {
        et_vc?.let { it.hint = value }
    }

    //输入框右侧空白
    @Attr(AttrType.DIMENSION)
    private val editTextMarginEnd: Int by AttributeSetInfo(
        context, attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_editTextMarginEnd, 0
    )

    fun setEditTextMarginEnd(start: Int) {
        (et_vc?.layoutParams as? LayoutParams)?.rightMargin = start
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            (et_vc?.layoutParams as? LayoutParams)?.marginEnd = start
        }
    }

    //输入框PaddingTop
    @Attr(AttrType.DIMENSION)
    private val editPaddingTop: Int by AttributeSetInfo(
        context, attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_editPaddingTop, -1
    )

    fun setEditPaddingTop(top: Int) {
        if (top > -1) et_vc?.updatePadding(top = top)
    }

    //输入框PaddingBottom
    @Attr(AttrType.DIMENSION)
    private val editPaddingBottom: Int by AttributeSetInfo(
        context, attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_editPaddingBottom, -1
    )

    fun setEditPaddingBottom(bottom: Int) {
        if (bottom > -1) et_vc?.updatePadding(bottom = bottom)
    }

    //输入框PaddingStart
    @Attr(AttrType.DIMENSION)
    private val editPaddingStart: Int by AttributeSetInfo(
        context, attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_editPaddingStart, -1
    )

    fun setEditPaddingStart(start: Int) {
        if (start > -1) et_vc?.updatePadding(left = start)
    }

    //输入框PaddingEnd
    @Attr(AttrType.DIMENSION)
    private val editPaddingEnd: Int by AttributeSetInfo(
        context, attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_editPaddingEnd, -1
    )

    fun setEditPaddingEnd(end: Int) {
        if (end > -1) et_vc?.updatePadding(right = end)
    }

    //输入框背景
    @Attr(AttrType.REFERENCE)
    private val editBackground: Int by AttributeSetInfo(
        context, attrs, R.styleable.ImageCaptchaView,
        R.styleable.ImageCaptchaView_icv_editBackground, 0
    )

    fun setEditBackground(resId: Int) {
        if (resId != 0) et_vc.setBackgroundResource(resId)
    }

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_image_captcha, this@ImageCaptchaView)
        setMaxLength(maxLength)
        setTextHint(textHint)
        setEditBackground(editBackground)
        setEditTextMarginEnd(editTextMarginEnd)
        setEditPaddingTop(editPaddingTop)
        setEditPaddingBottom(editPaddingBottom)
        setEditPaddingStart(editPaddingStart)
        setEditPaddingEnd(editPaddingEnd)
    }

    fun setCaptchaImage(bitmap: Bitmap) {
        runOnUi {
            iv_vc.setImageBitmap(bitmap)
        }
    }

    override val text get() = et_vc.text.toString()
}