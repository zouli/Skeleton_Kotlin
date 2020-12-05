package com.riverside.skeleton.kotlin.util.looper

import android.os.Handler
import android.os.Looper

/**
 * 线程帮助类    1.0
 *
 * b_e      2020/12/05
 */

private val HANDLER = Handler(Looper.getMainLooper())

/**
 * 运行在UI线程
 */
fun <T> T.runOnUi(block: T.() -> Unit) {
    HANDLER.post { block() }
}