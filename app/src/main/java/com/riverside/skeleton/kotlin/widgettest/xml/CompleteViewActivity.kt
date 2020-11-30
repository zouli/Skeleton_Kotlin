package com.riverside.skeleton.kotlin.widgettest.xml

import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.view.strongRequestTouch
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import kotlinx.android.synthetic.main.activity_complete_view.*

class CompleteViewActivity : SBaseActivity() {
    override fun setLayoutID() = R.layout.activity_complete_view

    override fun initView() {
        title = "Complete View"

        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
        et_edit.setText((1..500).map { allowedChars.random() }.joinToString(""))
        et_edit.strongRequestTouch()

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