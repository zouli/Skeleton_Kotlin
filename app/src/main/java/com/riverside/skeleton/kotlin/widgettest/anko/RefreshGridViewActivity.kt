package com.riverside.skeleton.kotlin.widgettest.anko

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.net.rest.CommonRestService
import com.riverside.skeleton.kotlin.net.rest.checkResult
import com.riverside.skeleton.kotlin.net.rest.utils.next
import com.riverside.skeleton.kotlin.net.rest.utils.retrofit
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import com.riverside.skeleton.kotlin.widget.containers.refreshview.RefreshGridView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class RefreshGridViewActivity : SBaseActivity() {
    lateinit var rgv: RefreshGridView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        title = "Refresh GridView"

        verticalLayout {
            val tv_empty = textView("Empty List") {
                gravity = Gravity.CENTER
            }.lparams(matchParent, matchParent)

            rgv = refreshGridView {
                direction = SwipyRefreshLayoutDirection.BOTTOM
                horizontalSpacing = 5.dip
                verticalSpacing = 4.dip
                numColumns = 3
                background = ColorDrawable(0xff0000ff.toInt())
            }.lparams(matchParent, matchParent)

            rgv.setEmptyView(tv_empty)
        }

        val adapter = ListViewAdapter<String> { position, item ->
            ListItemRefreshListView<RefreshGridViewActivity>().createView(
                AnkoContext.create(
                    this@RefreshGridViewActivity, RefreshGridViewActivity()
                )
            ).apply {
                findViewById<TextView>(ListItemRefreshListView.TV_TITLE).text = item
            }
        }

        rgv.setAdapter(adapter)
        rgv.doClear { adapter.clear() }
        rgv.bindList {
            retrofit<CommonRestService>().getList(mapOf())
                .next { checkResult() }
                .subscribe({
                    adapter.addItems(it)
                }, {}, {
                    rgv.isRefreshing = false
                })
        }
        rgv.setOnItemClickListener { parent, view, position, id ->
            adapter.getItem(position).toast(this@RefreshGridViewActivity)
        }
    }
}