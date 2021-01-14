package com.riverside.skeleton.kotlin.widgettest.xml

import android.os.Build
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.view.strongRequestTouch
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import com.riverside.skeleton.kotlin.widget.containers.CompleteListView
import kotlinx.android.synthetic.main.activity_complete_view.*

class CompleteViewActivity : SBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_complete_view

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initView() {
        title = "Complete View"

        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
        et_edit.setText((1..500).map { allowedChars.random() }.joinToString(""))
        et_edit.strongRequestTouch()

        val data = mutableListOf("aa", "bb", "cc", "dd")
        data.add("ee")
        data.add("ff")
        data.add("gg")
        data.add("hh")
        data.add("ii")

        val adapterList = ListViewAdapter(
            R.layout.list_item_checkable_linearlayout, data
        ) { viewHolder, position, item ->
            viewHolder.setText(R.id.tv_text, item)
        }

        clv_list.adapter = adapterList
        clv_list.choiceMode = CompleteListView.CHOICE_MODE_SINGLE
        clv_list.setItemChecked(3, true)
        adapterList.notifyDataSetChanged()

        clv_list.setOnItemClickListener { view, position ->
            SLog.w(clv_list.getCheckedItemPosition())
        }

        val adapterGrid = ListViewAdapter(
            R.layout.list_item_refresh_listview, data
        ) { viewHolder, position, item ->
            viewHolder.setText(R.id.tv_title, item)
        }

        cgv.adapter = adapterGrid
    }

}