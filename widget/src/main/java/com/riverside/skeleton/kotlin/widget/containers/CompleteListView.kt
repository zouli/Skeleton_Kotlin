package com.riverside.skeleton.kotlin.widget.containers

import android.content.Context
import android.database.DataSetObserver
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R

/**
 * 完全显示的ListView    1.0
 * b_e          2020/11/20
 */
class CompleteListView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    constructor(context: Context) : this(context, null)

    @Attr(AttrType.DRAWABLE)
    private val _divider: Drawable? by AttributeSetInfo(
        attrs, R.styleable.CompleteListView,
        R.styleable.CompleteListView_clv_divider, null
    )

    @Attr(AttrType.DIMENSION)
    private val _dividerHeight: Int by AttributeSetInfo(
        attrs, R.styleable.CompleteListView,
        R.styleable.CompleteListView_clv_dividerHeight, 0
    )

    var divider = _divider
        set(value) {
            field = value
            dividerHeight = value?.intrinsicHeight ?: 0
        }

    var dividerHeight = _dividerHeight
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    /**
     * 设置Adapter
     */
    var adapter: BaseAdapter? = null
        set(value) {
            field = value
            field?.registerDataSetObserver(AdapterDataSetObserver())
        }

    /**
     * 设置无数据时显示的View
     */
    var emptyView: View? = null
        set(value) {
            field = value
            updateEmptyStatus(adapter?.isEmpty ?: true)
        }

    private lateinit var itemClickListener: (view: View, position: Int) -> Unit

    /**
     *  设置Item点击监听
     */
    fun setOnItemClickListener(block: (view: View, position: Int) -> Unit) {
        itemClickListener = block
    }

    /**
     * 更新无数据View显示状态
     *
     * @param empty
     */
    private fun updateEmptyStatus(empty: Boolean) {
        if (empty) {
            emptyView?.visibility = View.VISIBLE
            this.visibility = if (emptyView != null) View.GONE else View.VISIBLE
        } else {
            emptyView?.visibility = View.GONE
            this.visibility = View.VISIBLE
        }
    }

    /**
     * 绑定显示控件
     */
    private fun bindView() = adapter?.let {
        // 清除现有View
        removeAllViews()

        (0 until it.count).forEach { i ->
            addView(it.getView(i, null, this@CompleteListView).apply {
                this.setOnClickListener {
                    if (::itemClickListener.isInitialized) itemClickListener(it, i)
                }
            })

            //添加分割线
            if (divider != null && dividerHeight > 0 && it.count > 1 && i < it.count - 1) {
                addView(View(context).apply {
                    this.layoutParams =
                        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        this.background = divider
                    } else {
                        this.setBackgroundDrawable(divider)
                    }
                })
            }
        }
    }

    /**
     * Adapter数据监听类
     */
    inner class AdapterDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            // 绑定显示控件
            bindView()
            emptyView?.let { updateEmptyStatus(adapter?.isEmpty ?: true) }
        }

        override fun onInvalidated() {
            super.onInvalidated()
            emptyView?.let { updateEmptyStatus(adapter?.isEmpty ?: true) }
        }
    }
}