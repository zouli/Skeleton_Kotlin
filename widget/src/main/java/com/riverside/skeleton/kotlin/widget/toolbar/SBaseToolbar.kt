package com.riverside.skeleton.kotlin.widget.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
        tb_toolbar.title = ""
        if (!isInEditMode)
            (ActivityStackManager.currentActivity as AppCompatActivity)
                .setSupportActionBar(tb_toolbar)
    }

    val toolbar: Toolbar = tb_toolbar

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
        tb_toolbar.addView(view)
    }

    /**
     * 设置回退键
     */
    fun setNavigation(resId: Int, callback: (v: View) -> Unit) {
        tb_toolbar.setNavigationIcon(resId)
        tb_toolbar.setNavigationOnClickListener {
            callback(it)
        }
    }
}