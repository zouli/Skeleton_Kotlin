package com.riverside.skeleton.kotlin.util.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * RecyclerView间距装饰类    1.0
 * b_e              2020/11/28
 */
class SpacesItemDecoration(
    private val leftSpace: Int, private val rightSpace: Int,
    private val topSpace: Int, private val bottomSpace: Int
) : ItemDecoration() {
    constructor(space: Int) : this(space, space)

    constructor(leftRightSpace: Int, topBottomSpace: Int) : this(
        leftRightSpace, leftRightSpace, topBottomSpace, topBottomSpace
    )

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.left = leftSpace
        outRect.right = rightSpace
        outRect.top = topSpace
        outRect.bottom = bottomSpace
    }
}