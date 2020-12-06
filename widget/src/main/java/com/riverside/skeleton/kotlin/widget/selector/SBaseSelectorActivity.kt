package com.riverside.skeleton.kotlin.widget.selector

import android.database.DataSetObserver
import android.view.Menu
import android.view.View
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.adapter.SelectorListViewAdapter
import com.riverside.skeleton.kotlin.widget.toolbar.AlignCenterToolbar
import kotlinx.android.synthetic.main.activity_sbase_common_selector.*

/**
 * 选择Activity基类   1.0
 *
 * b_e      2020/12/03
 */
abstract class SBaseSelectorActivity : SBaseActivity(), SearchView.OnQueryTextListener {
    private lateinit var searchView: SearchView

    val toolbar: AlignCenterToolbar by lazy { acToolbar }

    /**
     * 设置是否支持检索
     */
    open var isSearchVisible = true
        set(value) {
            field = value
            invalidateOptionsMenu()
        }

    override val layoutId: Int get() = R.layout.activity_sbase_common_selector

    override val menuId: Int get() = R.menu.menu_sbase_common_selector

    override fun initView() {
        // 初始化List
        lv_list.adapter = adapter
        lv_list.choiceMode = choiceMode
        lv_list.isTextFilterEnabled = false
        lv_list.setOnItemClickListener { _, _, position, _ ->
            adapter.setCheck(position, lv_list.isItemChecked(position))
            refreshAllCheck()

        }

        // 监视List内容变化
        adapter.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                (0 until lv_list.adapter.count).forEach { index ->
                    lv_list.setItemChecked(index, adapter.isCheck(index))
                }
                refreshAllCheck()
            }
        })

        ll_select_all.visibility = when (lv_list.choiceMode) {
            ListView.CHOICE_MODE_SINGLE -> View.GONE
            ListView.CHOICE_MODE_MULTIPLE, ListView.CHOICE_MODE_MULTIPLE_MODAL -> View.VISIBLE
            else -> View.GONE
        }

        cb_select_all.setOnClickListener {
            (0 until lv_list.adapter.count).forEach { index ->
                lv_list.setItemChecked(index, cb_select_all.isChecked)
                adapter.setCheck(index, lv_list.isItemChecked(index))
            }
        }

        tv_submit.setOnClickListener {
            onSubmitClick(lv_list.checkedItemIds.map { it.toInt() }.toList())
        }
    }

    /**
     * 刷新全选CheckBox状态
     */
    private fun refreshAllCheck() {
        cb_select_all.isChecked =
            (0 until lv_list.adapter.count).map { lv_list.isItemChecked(it) }
                .fold(true) { isCheck, isCheck1 -> isCheck && isCheck1 }
    }

    /**
     * 设置Adapter
     */
    abstract val adapter: SelectorListViewAdapter<*>

    /**
     * 设置选择模式
     */
    abstract val choiceMode: Int

    /**
     * 设置确定按钮事件
     */
    abstract fun onSubmitClick(checkedItemIds: List<Int>)

    /**
     * 设置Empty控件
     */
    fun setEmptyView(emptyView: View) {
        ll_list.addView(emptyView)
        lv_list.emptyView = emptyView
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean = super.onCreateOptionsMenu(menu).apply {
        menu?.findItem(R.id.menu_search)?.let {
            // 初始化检索控件
            searchView = it.actionView as SearchView
            with(searchView) {
                this.setOnQueryTextListener(this@SBaseSelectorActivity)
                this.isSubmitButtonEnabled = false
                this.setOnSearchClickListener { adapter.saveOriginalData() }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menu_search)?.isVisible = isSearchVisible
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        // 调用Adapter的过滤方法
        adapter.filter.filter(newText)
        return false
    }
}