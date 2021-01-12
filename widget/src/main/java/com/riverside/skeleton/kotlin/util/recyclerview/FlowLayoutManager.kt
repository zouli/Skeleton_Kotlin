package com.riverside.skeleton.kotlin.util.recyclerview

import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.getOrElse
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

/**
 * RecyclerView的瀑布布局    1.1
 *
 * b_e  2020/12/04
 * 1.1  添加分割设置  2021/01/12
 */
class FlowLayoutManager(private val divider: Int = 0) : RecyclerView.LayoutManager() {
    // 显示内容的高度
    var totalHeight = 0

    // 显示最大宽度
    var maxWidth = 0

    // 竖直方向上的偏移量
    private var verticalScrollOffset = 0

    var allItemFrames = SparseArray<Rect>()
    private var row = Row()
    private var col = mutableListOf<Row>()

    /**
     * 获取每一个item在屏幕上占据的位置
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            verticalScrollOffset = 0
            return
        }
        if (state.isPreLayout) return
        if (childCount == 0) maxWidth = width - paddingLeft - paddingRight
        totalHeight = 0
        var currentLineTop = paddingTop
        // 当前行的宽度
        var currentLineWidth = 0
        var maxItemHeight = 0
        row = Row()
        col.clear()
        allItemFrames.clear()
        (0 until itemCount).map { recycler.getViewForPosition(it) }
            .filter { it.visibility != View.GONE }
            .forEachIndexed { index, view ->
                measureChildWithMargins(view, 0, 0)
                val itemLeft: Int
                val itemTop: Int
                val viewWidth = getDecoratedMeasuredWidth(view)
                val viewHeight = getDecoratedMeasuredHeight(view)
                if (currentLineWidth + viewWidth <= maxWidth) {
                    itemLeft = paddingLeft + currentLineWidth
                    itemTop = currentLineTop
                    currentLineWidth += viewWidth + divider
                    maxItemHeight = max(viewHeight, maxItemHeight)
                } else {
                    formatAboveRow()
                    currentLineTop += maxItemHeight + divider
                    totalHeight += maxItemHeight + divider
                    itemLeft = paddingLeft
                    itemTop = currentLineTop
                    currentLineWidth = viewWidth + divider
                    maxItemHeight = viewHeight + divider
                }
                val frame = allItemFrames.getOrElse(index) { Rect() }.apply {
                    set(itemLeft, itemTop, itemLeft + viewWidth, itemTop + viewHeight)
                }

                allItemFrames.put(index, frame)
                row.views.add(Item(viewHeight, view, frame))
                row.currentTop = currentLineTop
                row.maxHeight = maxItemHeight

                //最后一行刷新下布局
                if (index == itemCount - 1) {
                    formatAboveRow()
                    totalHeight += maxItemHeight
                }
            }
        totalHeight = max(totalHeight, verticalSpace)
        fillLayout(recycler, state)
    }

    /**
     * 对出现在屏幕上的item进行展示，超出屏幕的item回收到缓存中
     */
    private fun fillLayout(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.isPreLayout) return

        // 当前scroll offset状态下的显示区域
        val displayFrame = Rect(
            paddingLeft, paddingTop + verticalScrollOffset,
            width - paddingRight, verticalScrollOffset + height - paddingBottom
        )

        col.forEach { row ->
            if (row.currentTop < displayFrame.bottom && displayFrame.top < (row.currentTop + row.maxHeight))
            //如果该行在屏幕中，进行放置item
                row.views.forEach { item ->
                    measureChildWithMargins(item.view, 0, 0)
                    addView(item.view)
                    layoutDecoratedWithMargins(
                        item.view,
                        item.rect.left, item.rect.top - verticalScrollOffset,
                        item.rect.right, item.rect.bottom - verticalScrollOffset
                    )
                }
            else
            //将不在屏幕中的item放到缓存中
                row.views.forEach { view ->
                    removeAndRecycleView(view.view, recycler)
                }
        }
    }

    /**
     * 计算每一行没有居中的ViewGroup，让居中显示
     */
    private fun formatAboveRow() {
        row.views.forEach { item ->
            val position = getPosition(item.view)
            //如果该item的位置不在该行中间位置的话，进行重新放置
            with(allItemFrames.getOrElse(position) { Rect() }) {
                if (this.top < row.currentTop + (row.maxHeight - item.useHeight) / 2) {
                    this.set(
                        this.left, row.currentTop + (row.maxHeight - item.useHeight) / 2,
                        this.right,
                        row.currentTop + (row.maxHeight - item.useHeight) / 2
                                + getDecoratedMeasuredHeight(item.view)
                    )
                    allItemFrames.put(position, this)
                    item.rect = this
                }
            }
        }
        col.add(row)
        row = Row()
    }

    /**
     * 监听竖直方向滑动的偏移量
     */
    override fun scrollVerticallyBy(
        dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State
    ): Int {
        var offset = dy
        if (verticalScrollOffset + dy < 0)
            offset = -verticalScrollOffset  // 限制滑动到顶部之后，不让继续向上滑动了
        else if (verticalScrollOffset + dy > totalHeight - verticalSpace)
            offset = totalHeight - verticalSpace - verticalScrollOffset

        // 将竖直方向的偏移量+offset
        verticalScrollOffset += offset

        // 平移容器内的item
        offsetChildrenVertical(-offset)
        fillLayout(recycler, state)
        return offset
    }

    /**
     * 适应RecyclerView高度和宽度为wrap_content
     */
    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
    )

    /**
     * 竖直方向滚动
     */
    override fun canScrollVertically() = true

    /**
     * 纵向空间
     */
    private val verticalSpace get() = height - paddingBottom - paddingTop

    /**
     * 横向空间
     */
    private val horizontalSpace get() = width - paddingLeft - paddingRight

    init {
        //设置主动测量规则,适应recyclerView高度为wrap_content
        isAutoMeasureEnabled = true
    }

    data class Item(val useHeight: Int, val view: View, var rect: Rect)

    /**
     * 行信息
     */
    class Row {
        var currentTop = 0
        var maxHeight = 0
        var views = mutableListOf<Item>()
    }
}