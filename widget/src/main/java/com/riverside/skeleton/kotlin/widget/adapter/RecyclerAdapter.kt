package com.riverside.skeleton.kotlin.widget.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.riverside.skeleton.kotlin.widget.adapter.viewholders.RecyclerViewHolder

/**
 * 用于RecyclerView的Adapter
 *
 * b_e      2020/11/16
 */
class RecyclerAdapter<D>() : RecyclerView.Adapter<RecyclerViewHolder>() {
    private var mItemLayout: Int? = null

    //单击事件
    var mOnItemClickListener: ((position: Int) -> Unit)? = null

    //数据集
    private var mDataSet = ArrayList<D>()

    private var _onGetView: ((viewGroup: ViewGroup, viewType: Int) -> View)? = null

    private var _onBindData: ((viewHolder: RecyclerViewHolder, position: Int, item: D) -> Unit)? =
        null

    constructor(
        data: List<D> = listOf(),
        onGetView: (viewGroup: ViewGroup, viewType: Int) -> View,
        onBindData: (viewHolder: RecyclerViewHolder, position: Int, item: D) -> Unit
    ) : this() {
        mDataSet.addAll(data)
        _onGetView = onGetView
        _onBindData = onBindData
    }

    constructor(
        layoutId: Int, data: List<D> = listOf(),
        onBindData: (viewHolder: RecyclerViewHolder, position: Int, item: D) -> Unit
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

    /**
     * 根据View Type返回布局资源
     *
     * @param type
     * @return
     */
    private fun getItemLayout(type: Int) = mItemLayout

    /**
     * 解析布局资源
     *
     * @param viewGroup
     * @param viewType
     * @return
     */
    private fun inflateItemView(viewGroup: ViewGroup, viewType: Int): View =
        (_onGetView?.let { it(viewGroup, viewType) } ?: getItemLayout(viewType)?.let {
            LayoutInflater.from(viewGroup.context).inflate(it, viewGroup, false)
        }) as View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder =
        RecyclerViewHolder(inflateItemView(parent, viewType))

    override fun getItemCount(): Int = mDataSet.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        // 绑定数据
        _onBindData?.let { it(holder, position, mDataSet[position]) }
        // 设置单击事件
        setupItemClickListener(holder, position)
    }

    private fun setupItemClickListener(holder: RecyclerViewHolder, position: Int) {
        holder.itemView.setOnClickListener { _ ->
            mOnItemClickListener?.let { it(position) }
        }
    }
}