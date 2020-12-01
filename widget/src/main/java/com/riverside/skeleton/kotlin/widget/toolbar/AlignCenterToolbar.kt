package com.riverside.skeleton.kotlin.widget.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.riverside.skeleton.kotlin.widget.R

/**
 * 居中显示标题Toolbar    1.0
 * b_e      2020/12/01
 */
class AlignCenterToolbar(context: Context, attrs: AttributeSet?) : SBaseToolbar(context, attrs) {
    constructor(context: Context) : this(context, null)

    private lateinit var toolbarTitle: TextView

    init {
        initView()
    }

    private fun initView() {
        toolbarTitle = TextView(ContextThemeWrapper(context, R.style.AlignCenterToolbar_Title))
        toolbarTitle.layoutParams =
            Toolbar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
                this.gravity = Gravity.CENTER
            }
        setView(toolbarTitle)
    }

    override fun onSetTitle(title: String) {
        toolbarTitle.text = title
    }
}
