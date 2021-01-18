package com.riverside.skeleton.kotlin.widget.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Checkable
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.graphics.toColorInt
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.widget.R

/**
 * PopupWindow  1.0
 * b_e  2020/01/18
 */
inline fun <reified T : PopupWindowView<*>> View.popupWindow(): T =
    T::class.java.getDeclaredConstructor(Context::class.java, View::class.java)
        .newInstance(context, this)

/**
 * PopupWindow基类
 */
abstract class PopupWindowView<T>(context: Context, private val triggerView: View) : View(context) {
    /**
     * 根视图
     */
    private val rootView = LinearLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        orientation = LinearLayout.VERTICAL
    }

    /**
     * 透明背景
     */
    private val mark = View(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1F
        )
        setBackgroundColor("#7F000000".toColorInt())
        setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private lateinit var popupWindow: PopupWindow

    private lateinit var listener: (selected: T) -> Unit

    /**
     * 显示PopupWindow
     */
    fun show() {
        if (triggerView is Checkable) triggerView.isChecked = true

        if (!::popupWindow.isInitialized) {
            popupWindow = PopupWindow(
                rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            ).apply {
                isOutsideTouchable = true
                softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                animationStyle = R.style.PopupWindowInOutAnimation
                setOnDismissListener {
                    if (triggerView is Checkable) triggerView.isChecked = false
                    if (::listener.isInitialized) listener(result)
                }
            }

            rootView.addView(view)
            rootView.addView(mark)
        }

        popupWindow.showAsDropDown(triggerView, 0, 1.dip)
    }

    /**
     * 关闭PopupWindow
     */
    fun close() {
        if (::popupWindow.isInitialized) popupWindow.dismiss()
    }

    /**
     * 关闭事件
     */
    fun setOnCloseListener(block: (selected: T) -> Unit) {
        listener = block
    }

    abstract val view: View

    abstract var result: T
}