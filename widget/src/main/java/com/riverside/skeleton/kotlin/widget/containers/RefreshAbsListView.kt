package com.riverside.skeleton.kotlin.widget.containers

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection

/**
 * 可刷新的AbsListview  1.0
 * b_e      2020/11/19
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
abstract class RefreshAbsListView(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var pageNum = 1

    private lateinit var clearData: () -> Unit
    private lateinit var getList: (pageNum: Int) -> Unit

    private lateinit var srlList: SwipyRefreshLayout
    private lateinit var listView: AbsListView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    /**
     * 初始化控件
     */
    fun initView() {
        //初始化刷新控件
        srlList = SwipyRefreshLayout(context).apply {
            this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            this.direction =
                SwipyRefreshLayoutDirection.getFromInt(this@RefreshAbsListView.updateDirection())
            this@RefreshAbsListView.addView(this)

            listView = initListView()
            this.addView(listView)
        }
    }

    /**
     * 设置事件
     */
    fun setListener() {
        srlList.setOnRefreshListener { direction ->
            when (direction) {
                SwipyRefreshLayoutDirection.TOP -> reloadList()
                SwipyRefreshLayoutDirection.BOTTOM -> if (::getList.isInitialized) getList(++pageNum)
                else -> Unit
            }
        }
    }

    /**
     * 刷新数据
     */
    open fun reloadList() {
        pageNum = 1
        if (::clearData.isInitialized) clearData()
        if (::getList.isInitialized) getList(pageNum)
    }

    open var isRefreshing: Boolean = false
        get() = srlList.isRefreshing
        set(refreshing) {
            srlList.isRefreshing = refreshing
            field = refreshing
        }

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

    open fun doClear(block: () -> Unit) {
        clearData = block
    }

    var direction = SwipyRefreshLayoutDirection.BOTH
        get() = if (::srlList.isInitialized)
            srlList.direction else SwipyRefreshLayoutDirection.BOTH
        set(value) {
            srlList.direction = value
            field = value
        }

    fun getListView() = listView

    /**
     * 读取下一页数据
     *
     * @param pageNum
     */
    open fun bindList(block: (pageNum: Int) -> Unit) {
        getList = block
    }

    /**
     * 取得刷新模式
     *
     * @return
     */
    abstract fun updateDirection(): Int


    /**
     * 初始化List控件
     *
     * @return
     */
    abstract fun initListView(): AbsListView
}