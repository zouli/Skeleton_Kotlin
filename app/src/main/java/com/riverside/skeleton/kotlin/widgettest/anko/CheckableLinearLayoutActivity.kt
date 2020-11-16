package com.riverside.skeleton.kotlin.widgettest.anko

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
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
                with(lv_list.context) {
                    checkableLinearLayout {
                        lparams(matchParent, wrapContent)
                        orientation = LinearLayout.HORIZONTAL
                        checkedChild = true

                        radioButton {
                            isClickable = false
                            isDuplicateParentStateEnabled = true
                            isFocusable = false
                            isFocusableInTouchMode = false
                        }.lparams(wrapContent, wrapContent) {
                            gravity = Gravity.CENTER
                        }

                        val tv_text = textView {
                            isClickable = false
                        }.lparams(matchParent, wrapContent) {
                            gravity = Gravity.CENTER
                        }

                        tv_text.text = item
                    }
                }
            }
        lv_list.choiceMode = ListView.CHOICE_MODE_SINGLE
        lv_list.adapter = adapter

        adapter.addItem("eee")
    }

}