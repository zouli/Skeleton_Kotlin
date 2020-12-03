package com.riverside.skeleton.kotlin.widgettest

import com.riverside.skeleton.kotlin.widget.selector.CommonSelectorBiz

class CommonSelectorTestBiz : CommonSelectorBiz {
    override fun getList(
        criteria: Map<String, Any>, callback: (data: List<CommonSelectorBiz.Item>) -> Unit
    ) {
        val data = mutableListOf(
            CommonSelectorBiz.Item("a", "1"),
            CommonSelectorBiz.Item("b", "2"),
            CommonSelectorBiz.Item("c", "3"),
            CommonSelectorBiz.Item("d", "4")
        )

        callback(data)
    }
}