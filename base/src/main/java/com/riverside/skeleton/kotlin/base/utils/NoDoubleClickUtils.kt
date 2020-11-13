package com.riverside.skeleton.kotlin.base.utils

import com.riverside.skeleton.kotlin.slog.SLog

/**
 * 二次点击判断类  1.0
 * b_e  2019/08/29
 */
object NoDoubleClickUtils {
    private var lastClickTime: Long = 0
    private val SPACE_TIME = 2000

    /**
     * 是否为二次点击
     *
     * @return
     */
    val isDoubleClick: Boolean
        @Synchronized get() {
            val currentTime = System.currentTimeMillis()
            val isClick2: Boolean

            SLog.d(
                "start currentTime=$currentTime,lastClickTime=$lastClickTime,c-l=${currentTime - lastClickTime}"
            )
            isClick2 = currentTime - lastClickTime <= SPACE_TIME
            lastClickTime = currentTime
            SLog.d(
                "end currentTime=$currentTime,lastClickTime=$lastClickTime,c-l=${currentTime - lastClickTime},isClick2=$isClick2"
            )

            return isClick2
        }

    /**
     * 初始化点击时间
     */
    fun initLastClickTime() {
        lastClickTime = 0
    }
}