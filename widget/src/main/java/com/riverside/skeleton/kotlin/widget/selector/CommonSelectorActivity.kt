package com.riverside.skeleton.kotlin.widget.selector

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.ListView
import com.riverside.skeleton.kotlin.util.extras.Extra
import com.riverside.skeleton.kotlin.util.extras.finishResult
import com.riverside.skeleton.kotlin.util.extras.finishResultOK
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.adapter.SelectorListViewAdapter
import kotlinx.android.synthetic.main.activity_sbase_common_selector.*
import java.util.*

/**
 * 共通选择画面   1.0
 *
 * b_e      2020/12/03
 */
class CommonSelectorActivity : SBaseSelectorActivity() {
    private val options: CommonSelectorOptions by Extra(OPTIONS)

    override fun initView() {
        //设置标题栏
        toolbar.title = options.titleName
        toolbar.setNavigation(R.drawable.web_toolbar_back) {
            finishResult(Activity.RESULT_CANCELED)
        }

        //  设置是否支持检索
        isSearchVisible = options.hasSearch

        super.initView()

        // 设置Empty控件
        if (options.emptyView != 0)
            setEmptyView(LayoutInflater.from(activity).inflate(options.emptyView, null))

        // 初始化列表数据
        options.listGenerator?.let {
            generateList(it).getList(options.listCriteria) { data ->
                adapter.clear()
                adapter.addItems(data)

                (0 until lv_list.adapter.count).forEach { index ->
                    adapter.setCheck(
                        index, options.checkedValue.contains(adapter.getItem(index).value)
                    )
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     *
     * 由于使用了Extra，这里的懒加载很必要
     */
    override val adapter by lazy {
        SelectorListViewAdapter<CommonSelectorBiz.Item>(
            when (options.choiceMode) {
                ListView.CHOICE_MODE_SINGLE -> R.layout.list_item_common_selector_radio
                ListView.CHOICE_MODE_MULTIPLE, ListView.CHOICE_MODE_MULTIPLE_MODAL -> R.layout.list_item_common_selector_checkbox
                else -> R.layout.list_item_common_selector_radio
            }
        ) { viewHolder, _, item, _ ->
            viewHolder.setText(R.id.tv_title, item.title)
            if (item.subTitle.isNotEmpty())
                viewHolder.setText(R.id.tv_subTitle, item.subTitle)
        }.apply {
            filter { item, prefix ->
                item.title.indexOf(prefix.toString().toLowerCase(Locale.getDefault())) > -1
            }
        }
    }

    /**
     * 设置选择模式
     */
    override val choiceMode: Int get() = options.choiceMode

    /**
     * 确定按钮事件
     */
    override fun onSubmitClick(checkedItemIds: List<Int>) {
        finishResultOK(RESULT_DATA to checkedItemIds.map { adapter.getItem(it) }.toList())
    }

    /**
     * 实例化业务对象
     */
    private fun generateList(clazz: Class<out CommonSelectorBiz>): CommonSelectorBiz =
        if (clazz.constructors.count { it.toString().endsWith("(android.content.Context)") } > 0)
            clazz.getDeclaredConstructor(Context::class.java).run {
                isAccessible = true
                newInstance(activity)
            }
        else clazz.getDeclaredConstructor().run {
            isAccessible = true
            newInstance()
        }

    companion object {
        const val OPTIONS = "COMMON_SELECTOR_OPTION"
        const val RESULT_DATA = "RESULT_DATA"
    }
}