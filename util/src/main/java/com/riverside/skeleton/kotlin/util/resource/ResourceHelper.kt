package com.riverside.skeleton.kotlin.util.resource

import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * 资源帮助类    1.0
 *
 * b_e  2021/01/28
 */
fun Context.getColorById(id: Int) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        ContextCompat.getColor(this, id) else this.resources.getColor(id)

fun Context.getDrawableById(id: Int) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        ContextCompat.getDrawable(this, id) else this.resources.getDrawable(id)