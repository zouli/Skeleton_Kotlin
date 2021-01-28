package com.riverside.skeleton.kotlin.widgettest.xml

import android.widget.ListView
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.image.tintDrawable
import com.riverside.skeleton.kotlin.util.resource.getDrawableById
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import kotlinx.android.synthetic.main.activity_checkable_linearlayout.*


class CheckableLinearLayoutActivity : SBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_checkable_linearlayout

    override fun initView() {
        title = "Checkable LinearLayout"

        val data = listOf("aa", "bb", "cc", "dd")

        val adapter = ListViewAdapter(
            R.layout.list_item_checkable_linearlayout, data
        ) { viewHolder, position, item ->
            viewHolder.setText(R.id.tv_text, item)
            viewHolder.setImageDrawable(
                R.id.iv_image,
                getDrawableById(R.mipmap.ic_launcher)!!.tintDrawable(R.color.checkable_image_selector)
            )
        }
        lv_list.choiceMode = ListView.CHOICE_MODE_SINGLE
        lv_list.adapter = adapter
    }
}