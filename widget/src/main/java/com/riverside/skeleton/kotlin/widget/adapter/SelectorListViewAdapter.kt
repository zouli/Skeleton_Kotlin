package com.riverside.skeleton.kotlin.widget.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.riverside.skeleton.kotlin.widget.adapter.viewholders.ListViewHolder

/**
 * 适用于Select的ListView的Adapter
 *
 * b_e      2020/12/03
 */
open class SelectorListViewAdapter<D>() : BaseAdapter(), Filterable {
    private var mItemLayout: Int? = null

    //数据集
    private var mDataSet = mutableListOf<SelectorItem<D>>()
    private var originalData = mutableListOf<SelectorItem<D>>()

    private lateinit var filtering: (item: D, prefix: CharSequence) -> Boolean

    private var _onGetView: ((position: Int, item: D) -> View)? = null

    private var _onBindData: ((viewHolder: ListViewHolder, position: Int, item: D, isCheck: Boolean) -> Unit)? =
        null

    constructor(
        data: List<D> = listOf(),
        onGetView: (position: Int, item: D) -> View
    ) : this() {
        mDataSet.addAll(selectorItems(data))
        _onGetView = onGetView
    }

    constructor(
        layoutId: Int, data: List<D> = listOf(),
        onBindData: (viewHolder: ListViewHolder, position: Int, item: D, isCheck: Boolean) -> Unit
    ) : this() {
        mItemLayout = layoutId
        mDataSet.addAll(selectorItems(data))
        _onBindData = onBindData
    }

    fun addItem(item: D) = mDataSet.add(selectorItem(item)).apply {
        notifyDataSetChanged()
    }

    fun addItems(items: List<D>) = mDataSet.addAll(selectorItems(items)).apply {
        notifyDataSetChanged()
    }

    private fun addSelectorItems(items: List<SelectorItem<D>>) = mDataSet.addAll(items).apply {
        notifyDataSetChanged()
    }

    fun addItemToHead(item: D) = mDataSet.add(0, selectorItem(item)).apply {
        notifyDataSetChanged()
    }

    fun addItemsToHead(items: List<D>) = mDataSet.addAll(0, selectorItems(items)).apply {
        notifyDataSetChanged()
    }

    fun remove(position: Int) = mDataSet.removeAt(position).apply {
        notifyDataSetChanged()
    }

    fun clear() = mDataSet.clear()

    fun clearAndNotify() = mDataSet.clear().apply {
        notifyDataSetChanged()
    }

    /**
     * 设置指定子控件的选中状态
     */
    fun setCheck(position: Int, boolean: Boolean) {
        mDataSet[position].isCheck = boolean
    }

    /**
     * 取得指定子控件的选中状态
     */
    fun isCheck(position: Int) = mDataSet[position].isCheck

    override fun getCount(): Int = mDataSet.size

    override fun getItem(position: Int) = mDataSet[position].item

    override fun getItemId(position: Int): Long = position.toLong()

    /**
     * 根据View Type返回布局资源
     *
     * @param type
     * @return
     */
    open fun getItemLayout(type: Int) = mItemLayout

    /**
     * 封装getView逻辑,将根据viewType获取布局资源、解析布局资源、创建ViewHolder等逻辑封装起来,简化使用流程
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        _onGetView?.let { it(position, getItem(position)) }
            ?: ListViewHolder[convertView, parent, getItemLayout(getItemViewType(position))].apply {
                _onBindData?.let {
                    it(this, position, getItem(position), mDataSet[position].isCheck)
                }
            }.itemView

    override fun hasStableIds() = true

    /**
     * 保存筛选前的数据
     */
    fun saveOriginalData() {
        originalData.clear()
        originalData.addAll(mDataSet)
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(prefix: CharSequence): FilterResults? {
            return if (::filtering.isInitialized) {
                if (prefix.isEmpty()) originalData
                else {
                    // filter的lambda与Filter的filter()方法冲突，汗
                    originalData.filterNot { !filtering(it.item, prefix.toString()) }.toList()
                }.run {
                    FilterResults().apply {
                        this.values = this@run
                        this.count = this@run.size
                    }
                }
            } else null
        }

        override fun publishResults(prefix: CharSequence, results: FilterResults) {
            clear()
            addSelectorItems(results.values as List<SelectorItem<D>>)
        }
    }

    /**
     * 设置过滤条件
     */
    fun filter(predicate: (item: D, prefix: CharSequence) -> Boolean) {
        filtering = predicate
    }

    /**
     * 选中状态装饰器
     */
    data class SelectorItem<D>(val item: D, var isCheck: Boolean)

    private fun selectorItem(item: D) = SelectorItem(item, false)

    private fun selectorItems(items: List<D>) = items.map { selectorItem(it) }.toList()
}