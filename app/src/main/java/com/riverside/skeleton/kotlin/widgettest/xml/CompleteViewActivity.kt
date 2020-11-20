package com.riverside.skeleton.kotlin.widgettest.xml

import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import kotlinx.android.synthetic.main.activity_complete_view.*

class CompleteViewActivity : SBaseActivity() {
    override fun setLayoutID() = R.layout.activity_complete_view

    override fun initView() {
        val datas = mutableListOf("aa", "bb", "cc", "dd")
        datas.add("ee")
        datas.add("ff")
        datas.add("gg")
        datas.add("hh")
        datas.add("ii")

        val adapterList = ListViewAdapter(
            R.layout.list_item_refresh_listview, datas
        ) { viewHolder, position, item ->
            viewHolder.setText(R.id.tv_title, item)
        }

        clv_list.adapter = adapterList
        adapterList.notifyDataSetChanged()

        val adapterGrid = ListViewAdapter(
            R.layout.list_item_refresh_listview, datas
        ) { viewHolder, position, item ->
            viewHolder.setText(R.id.tv_title, item)
        }

        cgv.adapter = adapterGrid
    }

}