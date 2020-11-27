package com.riverside.skeleton.kotlin.widget.gallery.pager

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.core.view.setPadding
import com.riverside.skeleton.kotlin.util.converter.dip

/**
 * 分页显示器    1.0
 * b_e      2020/11/27
 */
interface PagerView {
    fun getView(context: Context): View

    fun onPageScrolled(position: Int, size: Int, positionOffset: Float, positionOffsetPixels: Int)

    fun onPageScrollStateChanged(state: Int)

    fun onPageSelected(position: Int, size: Int)
}

/**
 * 分页显示器数字版
 */
class NumberPagerView : PagerView {
    private lateinit var tv_pager_num: TextView

    override fun getView(context: Context): View =
        TextView(context, null).apply {
            this.setPadding(2.dip)
            this.setTextColor("#FFFFFF".toColorInt())
            this.setBackgroundColor("#99000000".toColorInt())
            this.gravity = Gravity.CENTER
            this.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            }
            tv_pager_num = this
        }


    override fun onPageScrolled(
        position: Int, size: Int, positionOffset: Float, positionOffsetPixels: Int
    ) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageSelected(position: Int, size: Int) {
        tv_pager_num.text = "${position + 1} / $size"
    }
}