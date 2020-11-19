package com.riverside.skeleton.kotlin.widgettest.anko

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.net.rest.CommonRestService
import com.riverside.skeleton.kotlin.net.rest.checkResult
import com.riverside.skeleton.kotlin.net.rest.utils.next
import com.riverside.skeleton.kotlin.net.rest.utils.retrofit
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import com.riverside.skeleton.kotlin.widget.containers.RefreshGridView
import kotlinx.android.synthetic.main.list_item_refresh_listview.*
import org.jetbrains.anko.*

class RefreshGridViewActivity : SBaseActivity() {
    lateinit var rgv: RefreshGridView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
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
            with(rgv.context) {
                verticalLayout {
                    lparams(matchParent, wrapContent)
                    background = ColorDrawable(0xffffffff.toInt())

                    val tv_title = textView {
                        gravity = Gravity.CENTER
                        padding = 16.dip
                        textColor = "#333333".toColorInt()
                        textSize = 14F
                    }.lparams(matchParent, wrapContent)

                    tv_title.text = item
                }
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