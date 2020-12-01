package com.riverside.skeleton.kotlin.widget.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import com.riverside.skeleton.kotlin.base.activity.ActivityStackManager
import com.riverside.skeleton.kotlin.widget.R
import kotlinx.android.synthetic.main.view_sbase_toolbar.view.*

/**
 * Toolbar基类    1.0
 * b_e      2020/12/01
 */
abstract class SBaseToolbar(context: Context, attrs: AttributeSet?) : AppBarLayout(context, attrs) {
    constructor(context: Context) : this(context, null)

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_sbase_toolbar, this@SBaseToolbar)
        toolbar.title = ""
        (ActivityStackManager.currentActivity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    /**
     * 设置标题
     */
    var title = ""
        set(value) {
            onSetTitle(value)
            field = value
        }

    internal abstract fun onSetTitle(title: String)

    internal fun setView(view: View) {
        toolbar.addView(view)
    }

    /**
     * 设置回退键
     */
    fun setNavigation(resId: Int, callback: (v: View) -> Unit) {
        toolbar.setNavigationIcon(resId)
        toolbar.setNavigationOnClickListener {
            callback(it)
        }
    }
}