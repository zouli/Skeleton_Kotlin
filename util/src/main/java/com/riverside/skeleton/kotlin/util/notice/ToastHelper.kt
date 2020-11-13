package com.riverside.skeleton.kotlin.util.notice

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.widget.Toast
import android.os.Handler

/**
 * Toast共通类 1.0
 * b_e  2010/05/17
 */
private var toast_: Toast? = null

@SuppressLint("ShowToast")
fun String.toast(context: Context, duration: Int = Toast.LENGTH_LONG) {
    Handler(Looper.getMainLooper()).post {
        toast_?.cancel()
        toast_ = Toast.makeText(context, this, duration).also { it.show() }
    }
}