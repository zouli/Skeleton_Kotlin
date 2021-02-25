package com.riverside.skeleton.kotlin.widget.containers.refreshview

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.annotation.RequiresApi
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout

/**
 * 可刷新的AbsListview  1.1
 *
 * b_e  2020/11/19
 * 1.1  抽象RefreshView   2021/01/25
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
abstract class RefreshAbsListView(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
) : RefreshView<AbsListView>(context, attrs, defStyleAttr, defStyleRes) {
    private var pageNum = 1

    private lateinit var clearData: () -> Unit
    private lateinit var getList: (pageNum: Int) -> Unit

    private lateinit var srlList: SwipyRefreshLayout
    private lateinit var listView: AbsListView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    open fun setAdapter(adapter: BaseAdapter) {
        listView.adapter = adapter
    }

    open fun setOnItemClickListener(listener: (parent: AdapterView<*>, view: View, position: Int, id: Long) -> Unit) =
        listView.setOnItemClickListener { parent, view, position, id ->
            listener(parent, view, position, id)
        }

    open fun setEmptyView(emptyView: View) {
        listView.emptyView = emptyView
    }

    fun getListView() = listView

    override fun getView(): AbsListView = initListView().apply { listView = this }

    /**
     * 初始化List控件
     *
     * @return
     */
    abstract fun initListView(): AbsListView
}