package com.riverside.skeleton.kotlin.widget.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.riverside.skeleton.kotlin.widget.adapter.viewholders.ListViewHolder

/**
 * 适用于ListView的通用Adapter
 *
 * b_e      2020/11/16
 */
open class ListViewAdapter<D>() : BaseAdapter() {
    private var mItemLayout: Int? = null

    //数据集
    private var mDataSet = ArrayList<D>()

    private var _onGetView: ((position: Int, item: D) -> View)? = null

    private var _onBindData: ((viewHolder: ListViewHolder, position: Int, item: D) -> Unit)? = null

    constructor(
        data: List<D> = listOf(),
        onGetView: (position: Int, item: D) -> View
    ) : this() {
        mDataSet.addAll(data)
        _onGetView = onGetView
    }

    constructor(
        layoutId: Int, data: List<D> = listOf(),
        onBindData: (viewHolder: ListViewHolder, position: Int, item: D) -> Unit
    ) : this() {
        mItemLayout = layoutId
        mDataSet.addAll(data)
        _onBindData = onBindData
    }

    fun addItem(item: D) = mDataSet.add(item).apply {
        notifyDataSetChanged()
    }

    fun addItems(items: List<D>) = mDataSet.addAll(items).apply {
        notifyDataSetChanged()
    }

    fun addItemToHead(item: D) = mDataSet.add(0, item).apply {
        notifyDataSetChanged()
    }

    fun addItemsToHead(items: List<D>) = mDataSet.addAll(0, items).apply {
        notifyDataSetChanged()
    }

    fun remove(position: Int) = mDataSet.removeAt(position).apply {
        notifyDataSetChanged()
    }

    fun remove(item: D) = mDataSet.remove(item).apply {
        notifyDataSetChanged()
    }

    fun clear() = mDataSet.clear()

    fun clearAndNotify() = mDataSet.clear().apply {
        notifyDataSetChanged()
    }

    override fun getCount(): Int = mDataSet.size

    override fun getItem(position: Int) = mDataSet[position]

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
                _onBindData?.let { it(this, position, getItem(position)) }
            }.itemView

}