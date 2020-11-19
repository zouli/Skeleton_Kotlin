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
import kotlinx.android.synthetic.main.activity_refresh_gridview.*

class RefreshGridViewActivity : SBaseActivity() {
    override fun setLayoutID(): Int = R.layout.activity_refresh_gridview

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        val adapter =
            ListViewAdapter<String>(R.layout.list_item_refresh_listview) { viewHolder, position, item ->
                viewHolder.setText(R.id.tv_title, item)
            }
        rgv.setEmptyView(tv_empty)
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