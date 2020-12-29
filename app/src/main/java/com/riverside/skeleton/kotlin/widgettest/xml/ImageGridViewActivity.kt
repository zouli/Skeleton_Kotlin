package com.riverside.skeleton.kotlin.widgettest.xml

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.graphics.toColorInt
import com.imnjh.imagepicker.SImagePicker
import com.imnjh.imagepicker.activity.PhotoPickerActivity
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.extras.startActivity
import com.riverside.skeleton.kotlin.util.notice.snackbar
import com.riverside.skeleton.kotlin.util.picasso.read
import com.riverside.skeleton.kotlin.util.picasso.shadow
import com.riverside.skeleton.kotlin.widget.containers.ImageGridView
import com.riverside.skeleton.kotlin.widget.gallery.FullScreenActivity
import com.riverside.skeleton.kotlin.widget.gallery.imageloader.ImageLoader
import com.riverside.skeleton.kotlin.widget.gallery.pager.NumberPagerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_imagegridview.*
import java.util.*

class ImageGridViewActivity : SBaseActivity() {
    companion object {
        private const val REQUEST_CODE_IMAGE = 100
    }

    override val layoutId: Int get() = R.layout.activity_imagegridview

    private var imageSize = 6

    override fun initView() {
        acToolbar.title = "ImageGridView"

        spinner.adapter = ArrayAdapter<Int>(
            this, android.R.layout.simple_spinner_item, arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        )
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                imageSize = position + 1
                initIGV1()

            }

        }
        spinner.setSelection(2)

        initIGV()
    }

    fun initIGV() {
        igv_image.setAddImageClickListener {
            SImagePicker.from(activity)
                .maxCount(9)
                .rowCount(3)
                .pickMode(SImagePicker.MODE_IMAGE)
                .showCamera(true)
                .setSelected(igv_image.imageList as ArrayList<String>)
                .forResult(REQUEST_CODE_IMAGE)
        }

        igv_image.setOnImageClickListener { v, position, url ->
            startActivity<FullScreenActivity>(
                FullScreenActivity.IMAGE_URL to igv_image.imageList,
                FullScreenActivity.DEFAULT_INDEX to position,
                FullScreenActivity.PAGER_VIEW to NumberPagerView::class.java.name
            )
        }

        igv_image.setDeleteImageClickListener { position, url ->
            "是否删除图片".snackbar(activity)
                .action("嗯") {
                    igv_image.removeImage(position)
                }
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE) {
            data?.let {
                igv_image.imageList =
                    it.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT_SELECTION)
            }
        }
    }

    fun initIGV1() {
        val list = mutableListOf(
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce6037bd4.jpg",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201509%2F14%2F20150914171329_cj8VT.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1611756970&t=3eb88135bd96a5f99fa14f0ae5b46a83",
            "http://pic1.win4000.com/pic/8/a8/99a938e209.jpg",
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce5db257c.jpg",
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce5a8951e.jpg",
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce5b56b9f.jpg",
            "http://a.hiphotos.baidu.com/zhidao/pic/item/810a19d8bc3eb13513c00107ad1ea8d3fd1f4402.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/4610b912c8fcc3ce863f8b519c45d688d53f20d0.jpg",
//            "http://03imgmini.eastday.com/mobile/20190411/2019041105_3395409e15bf4cc1aee6fe8d28d529b2_9130_wmk.jpg",
            "http://07imgmini.eastday.com/mobile/20190409/20190409144819_7839f4c2c0ba95a355ae63c59e842219_1.jpeg"
        )

        val pLoader: ImageLoader = object : ImageLoader {
            override fun loadImage(imageView: ImageView, url: String, width: Int, height: Int) {
                Picasso.get().read(url).let {
                    if (width > -1 && height > -1) {
                        it.resize(width, height).centerCrop()
                    } else it
                }.apply { shadow(6F, "#999999".toColorInt(), -2F, -5F) }.into(imageView)
            }

            override fun loadImage(imageView: ImageView, url: String) {
                loadImage(imageView, url, -1, -1)
            }
        }

        val sLoader: ImageGridView.SmartColumnLoader = object : ImageGridView.SmartColumnLoader {
            override fun getColumnCount(size: Int) = when (size) {
                1 -> 1
                2, 4 -> 2
                3, 5 -> -3
                6 -> -4
                else -> 3
            }

            override fun getCoordinate(
                position: Int, columnCount: Int, smartColumnCount: Int
            ): Triple<Int, Int, Int> {
                var spec = 1
                var row = position / columnCount
                var col = position % columnCount
                if (smartColumnCount < 0) {
                    spec = if (position < 2) columnCount / 2 else 1
                    row = when {
                        position < 2 -> 0
                        else -> ((position - 2) / columnCount) + 2
                    }
                    col = when {
                        position < 2 -> columnCount / 2 * position
                        else -> (position - 2) % columnCount
                    }
                }

                SLog.w("columnCount$columnCount,position:$position,row:$row,col:$col,spec:$spec")
                return Triple(row, col, spec)
            }

        }

//        igv_image1.smartColumnLoader = sLoader
        igv_image1.imageLoader = pLoader

        igv_image1.setOnImageClickListener { v, position, url ->
            startActivity<FullScreenActivity>(
                FullScreenActivity.IMAGE_URL to igv_image1.imageList,
                FullScreenActivity.DEFAULT_INDEX to position,
                FullScreenActivity.PAGER_VIEW to NumberPagerView::class.java.name
            )
        }

        igv_image1.imageList = list.take(imageSize).toMutableList()
    }
}