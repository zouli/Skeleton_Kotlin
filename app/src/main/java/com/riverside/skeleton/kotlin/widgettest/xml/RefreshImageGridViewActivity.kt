package com.riverside.skeleton.kotlin.widgettest.xml

import android.os.Build
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.widget.adapter.ListViewAdapter
import com.riverside.skeleton.kotlin.widget.containers.ImageGridView
import kotlinx.android.synthetic.main.activity_imagegridview_list.*

class RefreshImageGridViewActivity : SBaseActivity() {
    override val layoutId = R.layout.activity_imagegridview_list

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        title = "ImageGridView List"

        val imageList = mutableListOf(
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce6037bd4.jpg",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201509%2F14%2F20150914171329_cj8VT.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1611756970&t=3eb88135bd96a5f99fa14f0ae5b46a83",
//            "https://b-ssl.duitang.com/uploads/item/201509/14/20150914171329_cj8VT.jpeg",
            "http://pic1.win4000.com/pic/8/a8/99a938e209.jpg",
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce5db257c.jpg",
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce5a8951e.jpg",
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce5b56b9f.jpg",
            "http://a.hiphotos.baidu.com/zhidao/pic/item/810a19d8bc3eb13513c00107ad1ea8d3fd1f4402.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/4610b912c8fcc3ce863f8b519c45d688d53f20d0.jpg",
//            "http://03imgmini.eastday.com/mobile/20190411/2019041105_3395409e15bf4cc1aee6fe8d28d529b2_9130_wmk.jpg",
            "http://07imgmini.eastday.com/mobile/20190409/20190409144819_7839f4c2c0ba95a355ae63c59e842219_1.jpeg"
        )

        val adapter =
            ListViewAdapter<String>(R.layout.list_item_imagegridview) { viewHolder, position, item ->
//                viewHolder.setText(R.id.tv_title, item)
                val igvImage = viewHolder.findViewById<ImageGridView>(R.id.igv_list)
                igvImage.imageList = imageList.take(position).toMutableList()
//                if (viewHolder.itemView.getTag(R.id.item_height) != null) {
//                    viewHolder.itemView.layoutParams.height =
//                        viewHolder.itemView.getTag(R.id.item_height) as Int
//                }
            }
//        rlv_list.adapter = adapter
        rlv_list.setAdapter(adapter)
        rlv_list.doClear { adapter.clearAndNotify() }
        rlv_list.bindList {
            (0 until 10).forEach { i ->
                adapter.addItem("aaAaa".repeat(i * 20 + 1))
            }
            rlv_list.isRefreshing = false
        }
        rlv_list.getListView().addHeaderView(TextView(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            text = "BBBBBBBBBBBBBBBBBBBBBBBBB"
        })

        btn_delete.setOnClickListener {
            adapter.remove(3)
        }
    }
}