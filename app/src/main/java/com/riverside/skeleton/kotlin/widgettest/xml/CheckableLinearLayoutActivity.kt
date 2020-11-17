package com.riverside.skeleton.kotlin.widgettest.xml

import android.widget.ListView
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import kotlinx.android.synthetic.main.activity_checkable_linearlayout.*


class CheckableLinearLayoutActivity : SBaseActivity() {
    override fun setLayoutID() = R.layout.activity_checkable_linearlayout

    override fun initView() {
        val datas = listOf("aa", "bb", "cc", "dd")

        val adapter = ListViewAdapter(
            R.layout.list_item_checkable_linearlayout, datas
        ) { viewHolder, position, item ->
            viewHolder.setText(R.id.tv_text, item)
        }
        lv_list.choiceMode = ListView.CHOICE_MODE_SINGLE
        lv_list.adapter = adapter
    }
}