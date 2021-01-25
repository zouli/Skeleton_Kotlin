package com.riverside.skeleton.kotlin.widget.containers

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView
import kotlin.math.abs

/**
 * 可控制NestedScrollView  1.0
 *  解决NestedScrollView与ViewPager冲突的问题
 *
 *  b_e 2021/01/25
 */
class ControllableNestedScrollView(context: Context, attrs: AttributeSet?) :
    NestedScrollView(context, attrs) {
    constructor(context: Context) : this(context, null)

    private var xDistance = 0F
    private var yDistance = 0F
    private var xLast = 0F
    private var yLast = 0F

    var isNeedScroll = true

    override fun onInterceptTouchEvent(ev: MotionEvent) = when (ev.action) {
        MotionEvent.ACTION_DOWN -> {
            xDistance = 0F
            yDistance = 0F
            xLast = ev.x
            yLast = ev.y
            super.onInterceptTouchEvent(ev)
        }
        MotionEvent.ACTION_MOVE -> {
            val curX = ev.x
            val curY = ev.y
            xDistance += abs(curX - xLast)
            yDistance += abs(curY - yLast)
            xLast = curX
            yLast = curY
            if (xDistance > yDistance) {
                false
            } else {
                isNeedScroll
            }
        }
        else -> super.onInterceptTouchEvent(ev)
    }
}