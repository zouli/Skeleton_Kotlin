package com.riverside.skeleton.kotlin.util.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import java.lang.ref.WeakReference

/**
 * 解决多行TextView或EditView与Scroller冲突类   1.0
 * b_e      2020/11/28
 */
fun TextView.strongRequestTouch() = TextViewScrollTouchWrapper(WeakReference(this.context), this)

@SuppressLint("ClickableViewAccessibility")
class TextViewScrollTouchWrapper(
    private val context: WeakReference<Context>, private val textView: TextView
) : View.OnTouchListener {

    init {
        textView.isVerticalScrollBarEnabled = true
        textView.movementMethod = ScrollingMovementMethod.getInstance()
        textView.setOnTouchListener(this)
        textView.setTextIsSelectable(true)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        //触摸的是TextView并且当前TextView可以滚动则将事件交给TextView处理；否则将事件交由其父类处理
        if (v.id == textView.id && canVerticalScroll(textView)) {
            v.parent.requestDisallowInterceptTouchEvent(event.action != MotionEvent.ACTION_UP)
        }
        return false
    }

    /**
     * TextView竖直方向是否可以滚动
     *
     * @param textView 需要判断的TextView
     * @return true：可以滚动   false：不可以滚动
     */
    private fun canVerticalScroll(textView: TextView): Boolean {
        //控件内容总高度与实际显示高度的差值
        val scrollDifference =
            textView.layout.height - textView.height - textView.compoundPaddingTop - textView.compoundPaddingBottom

        return scrollDifference != 0 && (textView.scaleY > 0 || textView.scaleY < scrollDifference - 1)
    }
}

