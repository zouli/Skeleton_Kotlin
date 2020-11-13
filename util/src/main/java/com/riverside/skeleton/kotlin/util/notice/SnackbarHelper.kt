package com.riverside.skeleton.kotlin.util.notice

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.google.android.material.snackbar.Snackbar
import com.riverside.skeleton.kotlin.util.R

/**
 * Snackbar帮助  1.0
 *
 * b_e                            2019/05/21
 * 修改为Builder模式               2020/11/12
 */
fun String.snackbar(
    context: Context, init: SnackbarHelper.() -> Unit = {}
) = SnackbarHelper(context, this).apply(init)

class SnackbarHelper constructor(context: Context, private val message: String) {
    //当前Activity的根View
    private var rootView: View =
        (context as Activity).window.decorView.findViewById(android.R.id.content)

    //显示时长
    var duration = LENGTH_LONG

    //Snackbar的显示回调
    private lateinit var snackbarCallback: (sb: Snackbar?) -> Unit

    //Action的文字
    private lateinit var actionTitle: String

    //Action文字的颜色
    private var actionTitleColor: Int? = null

    //Action的点击事件
    private lateinit var actionListener: (view: View) -> Unit

    //文字的颜色
    var textColor: Int? = null

    fun setDuration(dur: Int): SnackbarHelper {
        duration = dur
        return this
    }

    fun setTextColor(color: Int?): SnackbarHelper {
        textColor = color
        return this
    }

    fun action(
        title: String,
        @ColorInt color: Int? = null,
        listener: (view: View) -> Unit = {}
    ): SnackbarHelper {
        actionTitle = title
        actionTitleColor = color
        actionListener = listener
        return this
    }

    fun callback(callback: (sb: Snackbar?) -> Unit): SnackbarHelper {
        snackbarCallback = callback
        return this
    }

    /**
     * 显示Snackbar
     */
    fun show() {
        val snackbar = Snackbar.make(rootView, message, duration)

        textColor?.let {
            snackbar.view.findViewById<TextView>(R.id.snackbar_text).setTextColor(it)
        }

        if (::actionTitle.isInitialized) {
            actionTitleColor?.let { snackbar.setActionTextColor(it) }
            snackbar.setAction(actionTitle) { view -> actionListener(view) }
        }

        if (::snackbarCallback.isInitialized) {
            snackbar.addCallback(object : Snackbar.Callback() {
                override fun onShown(sb: Snackbar?) {
                    snackbarCallback(sb)
                }
            })
        }

        snackbar.show()
    }

    companion object {
        const val LENGTH_SHORT = 1777
        const val LENGTH_LONG = 3034
    }
}


