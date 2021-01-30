package com.riverside.skeleton.kotlin.widget.containers

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.get
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R

/**
 * 对控件  1.0
 *
 * b_e  2021/01/14
 */
class PairView(context: Context, attrs: AttributeSet?) : CheckableLinearLayout(context, attrs) {
    constructor(context: Context) : this(context, null)

    /**
     * 第一个控件的样式
     */
    @Attr(AttrType.REFERENCE)
    private val firstViewStyle: Int by AttributeSetInfo(
        context, attrs, R.styleable.PairView,
        R.styleable.PairView_pv_firstViewStyle, 0
    )

    /**
     * 第二个控件的样式
     */
    @Attr(AttrType.REFERENCE)
    private val secondViewStyle: Int by AttributeSetInfo(
        context, attrs, R.styleable.PairView,
        R.styleable.PairView_pv_secondViewStyle, 0
    )

    /**
     * 把第一个控件设置为图片
     */
    @Attr(AttrType.DRAWABLE)
    private val firstImage: Drawable? by AttributeSetInfo(
        context, attrs, R.styleable.PairView,
        R.styleable.PairView_pv_firstImage, null
    )

    /**
     * 把第二个控件设置为图片
     */
    @Attr(AttrType.DRAWABLE)
    private val secondImage: Drawable? by AttributeSetInfo(
        context, attrs, R.styleable.PairView,
        R.styleable.PairView_pv_secondImage, null
    )

    /**
     * 第一个控件图片的大小
     */
    @Attr(AttrType.DIMENSION)
    private val firstImageSize: Int by AttributeSetInfo(
        context, attrs, R.styleable.PairView,
        R.styleable.PairView_pv_firstImageSize, LayoutParams.MATCH_PARENT
    )

    /**
     * 第二个控件图片的大小
     */
    @Attr(AttrType.DIMENSION)
    private val secondImageSize: Int by AttributeSetInfo(
        context, attrs, R.styleable.PairView,
        R.styleable.PairView_pv_secondImageSize, LayoutParams.MATCH_PARENT
    )

    /**
     * 把第一个控件设置为文字
     */
    @Attr(AttrType.STRING)
    private val firstText: String by AttributeSetInfo(
        context, attrs, R.styleable.PairView,
        R.styleable.PairView_pv_firstText, ""
    )

    /**
     * 把第二个控件设置为文字
     */
    @Attr(AttrType.STRING)
    private val secondText: String by AttributeSetInfo(
        context, attrs, R.styleable.PairView,
        R.styleable.PairView_pv_secondText, ""
    )

    /**
     * 创建文字控件
     */
    private fun createText(text: CharSequence, style: Int, position: Int) =
        newText(text, style).also {
            if (this.childCount > position) removeViewAt(position)
            addView(it, position)
        }

    /**
     * 把第一个控件设置为文字
     */
    fun createFirstText(text: CharSequence, style: Int) = createText(text, style, 0)

    /**
     * 把第二个控件设置为文字
     */
    fun createSecondText(text: CharSequence, style: Int) = createText(text, style, 1)

    /**
     * 创建图片控件
     */
    private fun createImage(image: Drawable, size: Int, position: Int, style: Int = 0) =
        newImage(image, style, size).also {
            if (this.childCount > position) removeViewAt(position)
            addView(it, position)
        }

    /**
     * 把第一个控件设置为图片
     */
    fun createFirstImage(image: Drawable, size: Int, style: Int = 0) =
        createImage(image, size, 0, style)

    /**
     * 把第二个控件设置为图片
     */
    fun createSecondImage(image: Drawable, size: Int, style: Int = 0) =
        createImage(image, size, 1, style)

    /**
     * 设置文字
     */
    private fun setText(text: CharSequence, position: Int) {
        if (this.childCount > position)
            (get(position) as TextView).text = text
        else
            addView(newText(text, 0), position)
    }

    fun setFirstText(text: CharSequence) = setText(text, 0)

    fun setSecondText(text: CharSequence) = setText(text, 1)

    fun setFirstText(textId: Int) = setFirstText(context.getString(textId))

    fun setSecondText(textId: Int) = setSecondText(context.getString(textId))

    init {
        this.childCheckable = true
        firstImage?.let { createFirstImage(it, firstImageSize, firstViewStyle) }
            ?: createFirstText(firstText, firstViewStyle)
        secondImage?.let { createSecondImage(it, secondImageSize, secondViewStyle) }
            ?: createSecondText(secondText, secondViewStyle)
    }

    private fun newImage(drawable: Drawable, style: Int, size: Int) =
        (if (style > 0) ImageView(ContextThemeWrapper(context, style), null, 0) else ImageView(
            context
        )).apply {
            layoutParams = LayoutParams(size, size).apply {
                gravity = Gravity.CENTER
            }
            setImageDrawable(drawable)
        }

    private fun newText(text: CharSequence, style: Int) =
        (if (style > 0) TextView(ContextThemeWrapper(context, style)) else TextView(context))
            .apply {
                layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                        gravity = Gravity.CENTER
                    }
                this.text = text
            }
}