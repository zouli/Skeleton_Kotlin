package com.riverside.skeleton.kotlin.base.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.lang.ref.WeakReference


/**
 * 软键盘帮助类   1.0
 * b_e  2019/08/29
 */
class KeyboardHelper private constructor(private val activity: WeakReference<Activity>) {

    /**
     * 取得InputMethodManager
     *
     * @return
     */
    private val inputMethodManager
        get() = activity.get()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    /**
     * 键盘是否显示
     *
     * @return
     */
    val isShow: Boolean
        get() = activity.get()?.currentFocus?.let {
            inputMethodManager.isActive
        } ?: false

    /**
     * 隐藏键盘
     */
    fun hideKeyboard() = activity.get()?.currentFocus?.let {
        inputMethodManager.hideSoftInputFromWindow(
            it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    /**
     * 显示虚拟键盘
     */
    fun showKeyboard() = activity.get()?.currentFocus?.let {
        inputMethodManager.showSoftInput(it, InputMethodManager.SHOW_FORCED)
    }

    companion object {
        fun init(activity: Activity) = KeyboardHelper(WeakReference(activity))
    }
}