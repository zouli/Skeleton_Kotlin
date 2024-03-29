package com.riverside.skeleton.kotlin.widgettest.xml

import androidx.recyclerview.widget.LinearLayoutManager
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.image.tintDrawable
import com.riverside.skeleton.kotlin.util.recyclerview.SpacesItemDecoration
import com.riverside.skeleton.kotlin.util.resource.getDrawableById
import com.riverside.skeleton.kotlin.widget.adapter.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_recycler_view.*

class RecyclerViewActivity : SBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_recycler_view

    override fun initView() {
        title = "RecyclerAdapter"

        val data = mutableListOf("aaaaaaaaaa", "bb", "cccccc", "ddddd")
        data.add("eeeeeee")
        data.add("f")
        data.add("gg")

        val adapter = RecyclerAdapter(
            R.layout.list_item_checkable_linearlayout, data
        ) { viewHolder, position, item ->
            viewHolder.setText(R.id.tv_text, item)
            viewHolder.setImageDrawable(
                R.id.iv_image,
                getDrawableById(R.mipmap.ic_launcher)!!.tintDrawable(R.color.checkable_image_selector)
            )
        }

        rv_list.layoutManager = LinearLayoutManager(this@RecyclerViewActivity)
        rv_list.addItemDecoration(SpacesItemDecoration(1.dip))
        rv_list.adapter = adapter
    }
}