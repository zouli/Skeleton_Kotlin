package com.riverside.skeleton.kotlin.widget.selector

import java.io.Serializable

/**
 * 共通选择业务接口     1.0
 *
 * b_e      2020/12/03
 */
interface CommonSelectorBiz {
    /**
     *  取得列表数据
     *  通过callback设置列表
     */
    fun getList(criteria: Map<String, Any>, callback: (data: List<Item>) -> Unit)

    /**
     * 返回数据
     */
    data class Item(
        val title: String, val value: String, val subTitle: String = "", val subValue: String = ""
    ) : Serializable
}