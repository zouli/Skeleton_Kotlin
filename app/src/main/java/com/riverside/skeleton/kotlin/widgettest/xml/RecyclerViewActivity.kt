package com.riverside.skeleton.kotlin.widgettest.xml

import androidx.recyclerview.widget.LinearLayoutManager
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.widget.adapter.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_recycler_view.*

class RecyclerViewActivity : SBaseActivity() {
    override fun setLayoutID() = R.layout.activity_recycler_view

    override fun initView() {
        val datas = listOf("aa", "bb", "cc", "dd")

        val adapter = RecyclerAdapter(
            R.layout.list_item_checkable_linearlayout, datas
        ) { viewHolder, position, item ->
            viewHolder.setText(R.id.tv_text, item)
        }

        rv_list.layoutManager = LinearLayoutManager(this@RecyclerViewActivity)
        rv_list.adapter = adapter
    }
}