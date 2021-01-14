package com.riverside.skeleton.kotlin.widget.gallery.pager

import android.content.Context
import android.widget.RelativeLayout
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.widget.view.DotView

/**
 * 分页显示器点点版 1.0
 *
 * b_e  2020/01/11
 */
class DotPagerView : PagerView {
    private lateinit var dotView: DotView

    override fun getView(context: Context, size: Int) = DotView(context).apply {
        this.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, 20.dip
        ).also {
            it.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        }
        this.dotCount = size
        dotView = this
    }

    override fun onPageScrolled(
        position: Int, size: Int, positionOffset: Float, positionOffsetPixels: Int
    ) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageSelected(position: Int, size: Int) {
        if (::dotView.isInitialized) dotView.current = position
    }
}