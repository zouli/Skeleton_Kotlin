package com.riverside.skeleton.kotlin.widgettest.anko

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.net.rest.CommonRestService
import com.riverside.skeleton.kotlin.net.rest.checkResult
import com.riverside.skeleton.kotlin.net.rest.utils.next
import com.riverside.skeleton.kotlin.net.rest.utils.retrofit
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.converter.sp
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import com.riverside.skeleton.kotlin.widget.containers.RefreshListView
import org.jetbrains.anko.*

class RefreshListViewActivity : SBaseActivity() {
    lateinit var rlv: RefreshListView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        verticalLayout {
            lparams(matchParent, matchParent)

            val tv_empty = textView("Empty List") {
                gravity = Gravity.CENTER
            }.lparams(matchParent, matchParent)

            rlv = refreshListView {
                direction = SwipyRefreshLayoutDirection.BOTH
                divider = ColorDrawable(0xff00ff00.toInt())
                dividerHeight = 4.dip
            }.lparams(matchParent, matchParent)

            rlv.setEmptyView(tv_empty)
        }

        val adapter = ListViewAdapter<String> { position, item ->
            ListItemRefreshListView<RefreshListViewActivity>().createView(
                AnkoContext.create(
                    this@RefreshListViewActivity, RefreshListViewActivity()
                )
            ).apply {
                findViewById<TextView>(ListItemRefreshListView.TV_TITLE).text = item
            }
        }

        rlv.setAdapter(adapter)
        rlv.doClear { adapter.clear() }
        rlv.bindList {
            retrofit<CommonRestService>().getList(mapOf())
                .next { checkResult() }
                .subscribe({
                    adapter.addItems(it)
                }, {}, {
                    rlv.isRefreshing = false
                })
        }
        rlv.setOnItemClickListener { parent, view, position, id ->
            adapter.getItem(position).toast(this@RefreshListViewActivity)
        }
    }

}