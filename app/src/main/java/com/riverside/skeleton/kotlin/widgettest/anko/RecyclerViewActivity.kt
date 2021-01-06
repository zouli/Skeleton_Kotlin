package com.riverside.skeleton.kotlin.widgettest.anko

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.image.tintDrawable
import com.riverside.skeleton.kotlin.util.recyclerview.FlowLayoutManager
import com.riverside.skeleton.kotlin.util.recyclerview.SpacesItemDecoration
import com.riverside.skeleton.kotlin.widget.adapter.RecyclerAdapter
import org.jetbrains.anko.*

class RecyclerViewActivity : SBaseActivity() {
    lateinit var rv_list: RecyclerView

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initView() {
        title = "RecyclerAdapter"

        verticalLayout {
            lparams(matchParent, matchParent)
            background = ColorDrawable(0xFFDDDDDD.toInt())

            rv_list = recyclerView {
            }.lparams(matchParent, wrapContent)
        }

        val data = mutableListOf("aaaaaaaaaaaaaa", "bbbbbbbb", "ccccccccccc", "ddddd")
        data.add("eeeeeeeeee")
        data.add("ff")
        data.add("ggggggg")

        val adapter = RecyclerAdapter(data, { viewGroup, viewType ->
            with(rv_list.context) {
                verticalLayout {
                    lparams(wrapContent, wrapContent)
                    background = ColorDrawable("#FFFFFF".toColorInt())
                    padding = 8.dip

                    textView {
                        id = 101
                    }.lparams(matchParent, wrapContent) {
                        gravity = Gravity.CENTER
                    }
                }
            }
        }, { viewHolder, position, item ->
            viewHolder.setText(101, item)
        })

        rv_list.layoutManager = FlowLayoutManager()
        rv_list.addItemDecoration(SpacesItemDecoration(1.dip))
        rv_list.adapter = adapter
        adapter.addItem("eee")
    }
}