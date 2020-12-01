package com.riverside.skeleton.kotlin.widgettest.anko

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import com.imnjh.imagepicker.SImagePicker
import com.imnjh.imagepicker.activity.PhotoPickerActivity
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.notice.snackbar
import com.riverside.skeleton.kotlin.widget.containers.ImageGridView
import com.riverside.skeleton.kotlin.widget.gallery.FullScreenActivity
import com.riverside.skeleton.kotlin.widget.gallery.pager.NumberPagerView
import org.jetbrains.anko.*

class ImageGridViewActivity : SBaseActivity() {
    companion object {
        private const val REQUEST_CODE_IMAGE = 100
    }

    private lateinit var igv_image: ImageGridView
    private lateinit var igv_image1: ImageGridView

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initView() {
        verticalLayout {
            lparams(matchParent, matchParent)

            alignCenterToolbar {
                title = "ImageGridView"
                setNavigation(R.drawable.web_toolbar_back) {
                    finish()
                }
            }

            scrollView {
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                verticalLayout {
                    igv_image = imageGridView {
                        background = ColorDrawable(0xFFF3F3F3.toInt())
                        columnCount = 4
                        addButtonIcon = resources.getDrawable(R.drawable.image_grid_add_image)
                        dividerSize = 2.dip
                    }.lparams(matchParent, wrapContent)

                    igv_image1 = imageGridView {
                        columnCount = 3
                        dividerSize = 2.dip
                        isReadOnly = true
                        isSmartColumnCount = true
                    }.lparams(matchParent, wrapContent)
                }.lparams(matchParent, wrapContent)
            }.lparams(matchParent, matchParent)
        }

        initIGV()
        initIGV1()
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
            "http://pic1.win4000.com/pic/8/a8/99a938e209.jpg",
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce5db257c.jpg",
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce5a8951e.jpg",
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce6037bd4.jpg",
            "http://pic1.win4000.com/wallpaper/2017-11-15/5a0bce5b56b9f.jpg",
            "http://a.hiphotos.baidu.com/zhidao/pic/item/810a19d8bc3eb13513c00107ad1ea8d3fd1f4402.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/4610b912c8fcc3ce863f8b519c45d688d53f20d0.jpg",
            "http://03imgmini.eastday.com/mobile/20190411/2019041105_3395409e15bf4cc1aee6fe8d28d529b2_9130_wmk.jpg",
            "http://07imgmini.eastday.com/mobile/20190409/20190409144819_7839f4c2c0ba95a355ae63c59e842219_1.jpeg"
        )

        igv_image1.setOnImageClickListener { v, position, url ->
            startActivity<FullScreenActivity>(
                FullScreenActivity.IMAGE_URL to igv_image1.imageList,
                FullScreenActivity.DEFAULT_INDEX to position,
                FullScreenActivity.PAGER_VIEW to NumberPagerView::class.java.name
            )
        }

        igv_image1.imageList = list
    }
}