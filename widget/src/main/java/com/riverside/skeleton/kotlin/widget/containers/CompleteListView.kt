package com.riverside.skeleton.kotlin.widget.containers

import android.content.Context
import android.database.DataSetObserver
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Checkable
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R

/**
 * 完全显示的ListView    1.1
 *
 * b_e  2020/11/20
 * 1.1  添加单选、多选功能   2021/01/14
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
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
            clearChoices()
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

    private var mCheckStates = SparseBooleanArray(0)

    private var mCheckedIdStates = SparseIntArray(0)

    private val useActivated =
        (context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.HONEYCOMB)

    /**
     *  设置Item点击监听
     */
    fun setOnItemClickListener(block: (view: View, position: Int) -> Unit) {
        itemClickListener = block
    }

    private val mChildren = mutableListOf<View>()

    /**
     * 选择模式
     */
    var choiceMode: Int = CHOICE_MODE_NONE

    /**
     * 更新选中状态
     */
    private fun updateCheckedViews() {
        mChildren.forEachIndexed { index, view ->
            if (view is Checkable) view.isChecked = mCheckStates.get(index)
            else if (useActivated) view.isActivated = mCheckStates.get(index)
        }
    }

    /**
     * 取得选中位置
     */
    fun getCheckedItemPosition() =
        if (choiceMode == CHOICE_MODE_SINGLE && mCheckStates.size() == 1) mCheckStates.keyAt(0) else -1

    /**
     * 取得选中位置
     */
    fun getCheckedItemIds() =
        if (choiceMode == CHOICE_MODE_NONE || adapter == null) IntArray(0)
        else (0 until mCheckedIdStates.size()).map { mCheckedIdStates.keyAt(it) }.toIntArray()

    /**
     * 设置Item的选中状态
     */
    fun setItemChecked(position: Int, value: Boolean) {
        if (choiceMode == CHOICE_MODE_NONE) return

        if (choiceMode == CHOICE_MODE_MULTIPLE) {
            mCheckStates.put(position, value)
            adapter?.let {
                if (value)
                    mCheckedIdStates.put(it.getItemId(position).toInt(), position)
                else
                    mCheckedIdStates.delete(it.getItemId(position).toInt())
            }
        } else {
            mCheckStates.clear()
            mCheckStates.put(position, value)
        }
        updateCheckedViews()
    }

    /**
     * 清空选中状态
     */
    fun clearChoices() {
        mCheckStates.clear()
        mCheckedIdStates.clear()
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
                mChildren.add(this)

                if (this is Checkable) this.isChecked = mCheckStates.get(i)
                else if (useActivated) this.isActivated = mCheckStates.get(i)

                this.setOnClickListener { view ->
                    val position = mChildren.indexOf(view)
                    performItemClick(view, position)
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

    private fun performItemClick(view: View, position: Int) {
        var checkedStateChanged = false
        if (choiceMode == CHOICE_MODE_MULTIPLE && adapter != null) {
            mCheckStates.get(position, false).let { isCheck ->
                mCheckStates.put(position, !isCheck)
                if (!isCheck)
                    mCheckedIdStates.put(
                        adapter!!.getItemId(position).toInt(), position
                    )
                else
                    mCheckedIdStates.delete(adapter!!.getItemId(position).toInt())
            }

            checkedStateChanged = true
        } else if (choiceMode == CHOICE_MODE_SINGLE) {
            if (!mCheckStates.get(position, false)) {
                mCheckStates.clear()
                mCheckStates.put(position, true)
            }
            checkedStateChanged = true
        }

        if (checkedStateChanged) updateCheckedViews()

        if (::itemClickListener.isInitialized) itemClickListener(view, position)
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

    companion object {
        const val CHOICE_MODE_NONE = 0
        const val CHOICE_MODE_SINGLE = 1
        const val CHOICE_MODE_MULTIPLE = 2
    }
}