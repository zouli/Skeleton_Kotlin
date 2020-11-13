package com.riverside.skeleton.kotlin.util.converter

import android.content.res.Resources
import android.util.TypedValue

/**
 * 单位转换工具  1.0
 * b_e  2019/05/07
 */
val Float.dip: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

val Int.dip: Int get() = this.toFloat().dip.toInt()

val Float.px: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, this, Resources.getSystem().displayMetrics)

val Int.px: Int get() = this.toFloat().px.toInt()

val Float.sp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)

val Int.sp: Int get() = this.toFloat().sp.toInt()

val Float.fahrenheit: Float get() = this * 1.8f + 32f

val Float.celsius: Float get() = (this - 32f) / 1.8f