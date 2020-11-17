package com.riverside.skeleton.kotlin.widgettest.anko

import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onItemSelectedListener

class CheckableLinearLayoutActivity : SBaseActivity() {
    lateinit var lv_list: ListView

    override fun initView() {
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
        val datas = listOf("aa", "bb", "cc", "dd")

        val adapter =
            ListViewAdapter(
                datas
            ) { position: Int, item: String ->
                ListItemCheckableLinearLayout<CheckableLinearLayoutActivity>().createView(
                    AnkoContext.create(
                        this@CheckableLinearLayoutActivity,
                        CheckableLinearLayoutActivity()
                    )
                ).apply {
                    findViewById<TextView>(ListItemCheckableLinearLayout.TV_TEXT).text = item
                }
            }
        lv_list.choiceMode = ListView.CHOICE_MODE_SINGLE
        lv_list.adapter = adapter

        adapter.addItem("eee")
    }

}