package com.riverside.skeleton.kotlin.widgettest.anko

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import com.riverside.skeleton.kotlin.widget.containers.CompleteGridView
import com.riverside.skeleton.kotlin.widget.containers.CompleteListView
import kotlinx.android.synthetic.main.activity_complete_view.*
import org.jetbrains.anko.*

class CompleteViewActivity : SBaseActivity() {
    private lateinit var clv_list: CompleteListView
    private lateinit var cgv: CompleteGridView

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initView() {
        scrollView {
            lparams(matchParent, matchParent)
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false

            verticalLayout {
                clv_list = completeListView {
                    orientation = LinearLayout.VERTICAL
                    divider = ColorDrawable(0xffffff00.toInt())
                    dividerHeight = 2.dip
                }.lparams(matchParent, wrapContent)

                cgv = completeGridView {
                    numColumns = 3
                }.lparams(matchParent, wrapContent)
            }.lparams(matchParent, wrapContent)
        }

        val datas = mutableListOf("aa", "bb", "cc", "dd")
        datas.add("ee")
        datas.add("ff")
        datas.add("gg")
        datas.add("hh")
        datas.add("ii")

        val adapterList = ListViewAdapter(datas) { position, item ->
            ListItemRefreshListView<CompleteViewActivity>().createView(
                AnkoContext.create(
                    this@CompleteViewActivity, CompleteViewActivity()
                )
            ).apply {
                findViewById<TextView>(ListItemRefreshListView.TV_TITLE).text = item
            }
        }

        clv_list.adapter = adapterList
        adapterList.notifyDataSetChanged()

        val adapterGrid = ListViewAdapter(datas) { position, item ->
            ListItemRefreshListView<CompleteViewActivity>().createView(
                AnkoContext.create(
                    this@CompleteViewActivity, CompleteViewActivity()
                )
            ).apply {
                findViewById<TextView>(ListItemRefreshListView.TV_TITLE).text = item
            }
        }

        cgv.adapter = adapterGrid
    }

}