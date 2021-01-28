package com.riverside.skeleton.kotlin.widgettest.anko

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.image.tintDrawable
import com.riverside.skeleton.kotlin.util.resource.getDrawableById
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onItemSelectedListener

class CheckableLinearLayoutActivity : SBaseActivity() {
    lateinit var lv_list: ListView

    override fun initView() {
        title = "Checkable LinearLayout"

        verticalLayout {
            lparams(matchParent, matchParent)

            lv_list = listView {
                descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false

                onItemSelectedListener { }
            }.lparams(matchParent, wrapContent)
        }

        initData()
    }

    private fun initData() {
        val data = listOf("aa", "bb", "cc", "dd")

        val adapter =
            ListViewAdapter(data) { position: Int, item: String ->
                ListItemCheckableLinearLayout<CheckableLinearLayoutActivity>().createView(
                    AnkoContext.create(
                        this@CheckableLinearLayoutActivity,
                        CheckableLinearLayoutActivity()
                    )
                ).apply {
                    findViewById<TextView>(ListItemCheckableLinearLayout.TV_TEXT).text = item
                    findViewById<ImageView>(ListItemCheckableLinearLayout.IV_IMAGE).image =
                        getDrawableById(R.mipmap.ic_launcher)!!.tintDrawable(R.color.checkable_image_selector)
                }
            }
        lv_list.choiceMode = ListView.CHOICE_MODE_SINGLE
        lv_list.adapter = adapter

        adapter.addItem("eee")
    }

}