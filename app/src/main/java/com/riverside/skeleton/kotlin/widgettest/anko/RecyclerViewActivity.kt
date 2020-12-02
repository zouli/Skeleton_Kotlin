package com.riverside.skeleton.kotlin.widgettest.anko

import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.image.tintDrawable
import com.riverside.skeleton.kotlin.util.view.SpacesItemDecoration
import com.riverside.skeleton.kotlin.widget.adapter.RecyclerAdapter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

class RecyclerViewActivity : SBaseActivity() {
    lateinit var rv_list: RecyclerView

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initView() {
        title = "RecyclerAdapter"

        verticalLayout {
            lparams(matchParent, matchParent)
            background = ColorDrawable(0xFFDDDDDD.toInt())

            rv_list = recyclerView {
            }.lparams(matchParent, matchParent)
        }

        val data = listOf("aa", "bb", "cc", "dd")

        val adapter = RecyclerAdapter(data, { viewGroup, viewType ->
            ListItemCheckableLinearLayout<RecyclerViewActivity>().createView(
                AnkoContext.create(
                    this@RecyclerViewActivity,
                    RecyclerViewActivity()
                )
            )
        }, { viewHolder, position, item ->
            viewHolder.setText(ListItemCheckableLinearLayout.TV_TEXT, item)
            viewHolder.setImageDrawable(
                ListItemCheckableLinearLayout.IV_IMAGE,
                resources.getDrawable(R.mipmap.ic_launcher)
                    .tintDrawable(R.color.checkable_image_selector)
            )
        })

        rv_list.layoutManager = LinearLayoutManager(this@RecyclerViewActivity)
        rv_list.addItemDecoration(SpacesItemDecoration(1.dip))
        rv_list.adapter = adapter
        adapter.addItem("eee")
    }
}