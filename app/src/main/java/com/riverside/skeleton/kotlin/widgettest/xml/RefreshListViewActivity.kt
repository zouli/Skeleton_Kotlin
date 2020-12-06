package com.riverside.skeleton.kotlin.widgettest.xml

import android.os.Build
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.net.rest.CommonRestService
import com.riverside.skeleton.kotlin.net.rest.checkResult
import com.riverside.skeleton.kotlin.net.rest.utils.next
import com.riverside.skeleton.kotlin.net.rest.utils.retrofit
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import kotlinx.android.synthetic.main.activity_refresh_listview.*

class RefreshListViewActivity : SBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_refresh_listview

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        title = "Refresh ListView"

        val adapter =
            ListViewAdapter<String>(R.layout.list_item_refresh_listview) { viewHolder, position, item ->
                viewHolder.setText(R.id.tv_title, item)
            }
        rlv.setEmptyView(tv_empty)
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