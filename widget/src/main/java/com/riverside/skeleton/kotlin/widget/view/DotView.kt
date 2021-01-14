package com.riverside.skeleton.kotlin.widget.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.riverside.skeleton.kotlin.util.converter.dip

/**
 * 点点控件 1.0
 *
 * b_e  2020/01/11
 */
class DotView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    constructor(context: Context) : this(context, null)

    private val paintOther = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 255 / 2
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val paintCurrent = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private var startX = 0F
    private var itemY = 0F

    var divider = 16.dip.toFloat()
        set(value) {
            field = value
            invalidate()
        }

    var otherRadius = 2.dip.toFloat()
        set(value) {
            field = value
            invalidate()
        }

    var currentRadius = otherRadius * 1.5F
        set(value) {
            field = value
            invalidate()
        }

    var current = 0
        set(value) {
            field = value
            invalidate()
        }

    var dotCount = 0
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        itemY = height / 2F
        startX = (width - (dotCount - 1) * divider) / 2

        var itemX = startX
        (0 until dotCount).forEach { index ->
            canvas.drawCircle(
                itemX, itemY,
                if (current == index) currentRadius else otherRadius,
                if (current == index) paintCurrent else paintOther
            )
            itemX += divider
        }
    }
}