package com.riverside.skeleton.kotlin.util.screen

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * 屏幕信息工具  1.0
 * b_e  2019/05/09
 */
val Context.deviceSize: Point
    get() {
        val manager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        manager.defaultDisplay.getSize(point)
        return point
    }

val Context.deviceWidth: Int get() = this.deviceSize.x

val Context.deviceHeight: Int get() = this.deviceSize.y

val Activity.topBarHeight: Int get() = this.window.findViewById<View>(Window.ID_ANDROID_CONTENT).top