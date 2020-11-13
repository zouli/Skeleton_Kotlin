package com.riverside.skeleton.kotlin.util.resource

import android.content.Context
import android.view.View
import android.view.ViewGroup
import java.util.concurrent.atomic.AtomicInteger

/**
 * ID帮助    1.0
 * b_e  2019/05/09
 */
private fun getResourceID(context: Context, type: String, id: String): Int =
    context.resources.getIdentifier(id, type, context.packageName)

fun Context.getDrawableID(id: String): Int = getResourceID(this, "drawable", id)

fun Context.getID(id: String): Int = getResourceID(this, "id", id)

fun Context.getLayoutID(id: String): Int = getResourceID(this, "layout", id)

fun Context.getStyleID(id: String): Int = getResourceID(this, "style", id)

fun Context.getSringID(id: String): Int = getResourceID(this, "string", id)

fun Context.getColorID(id: String): Int = getResourceID(this, "color", id)

private val sNextGeneratedId = AtomicInteger(1)

val Context.generateViewId: Int
    get() {
        while (true) {
            val result = sNextGeneratedId.get()
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            var newValue = result + 1
            if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result
            }
        }
    }

fun ViewGroup.updateChildId(srcId: Int): Int =
    this.context.generateViewId.apply {
        this@updateChildId.findViewById<View>(srcId).let { id = this }
    }