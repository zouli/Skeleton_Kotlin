package com.riverside.skeleton.kotlin.widget.containers

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView

/**
 * 完全显示的GridView    1.0
 * b_e    2020/11/20
 */
class CompleteGridView(context: Context, attrs: AttributeSet?) : GridView(context, attrs) {
    constructor(context: Context) : this(context, null)

    /**
     * @see android.widget.GridView#onMeasure(int, int)
     * 重写onMeasure方法
     * Int.MAX_VALUE shr 2  移两位 shr 2 == /4 得到Int可以表示的边界值
     * GridView之所以会滚动，是因为空间不足以显示全控件，需要更大的空间，
     * 不让他滑动，就把他的测量高度设置为足够大，这里用Int的边界值表示
     * MeasureSpec.AT_MOST是最大尺寸，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可。
     * 因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec =
            MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}