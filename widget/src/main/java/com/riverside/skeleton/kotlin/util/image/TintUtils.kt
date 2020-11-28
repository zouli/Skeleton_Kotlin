package com.riverside.skeleton.kotlin.util.image

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.DrawableCompat
import com.riverside.skeleton.kotlin.util.resource.ContextHolder

/**
 * 染色工具类    1.0
 * b_e      2020/11/28
 */
fun Drawable.tintDrawable(colors: ColorStateList): Drawable =
    DrawableCompat.wrap(this).apply {
        DrawableCompat.setTintList(this, colors)
    }

fun Drawable.tintDrawable(colorID: Int) =
    this.tintDrawable(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ContextHolder.applicationContext.resources.getColorStateList(
                colorID, ContextHolder.applicationContext.theme
            )
        else
            ContextHolder.applicationContext.resources.getColorStateList(colorID)
    )
