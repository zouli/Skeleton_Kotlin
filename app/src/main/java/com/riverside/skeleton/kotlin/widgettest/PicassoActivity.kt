package com.riverside.skeleton.kotlin.widgettest

import androidx.core.graphics.toColorInt
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.picasso.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_picasso.*

class PicassoActivity : SBaseActivity() {
    override fun setLayoutID() = R.layout.activity_picasso

    override fun initView() {
        title = "Picasso"

        val url =
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1607081087578&di=a0447d549b1fa7670657670fe9947a25&imgtype=0&src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F201610%2F06%2F20161006190647_M3akn.jpeg"

        Picasso.get()
            .read(url)
            .into(iv_image)

        Picasso.get()
            .read(url)
            .resizeT(446, 250)
            .shadow(10F, "#999999".toColorInt(), -3F, -8F)
            .defaultImage(R.drawable.image_grid_add_image)
            .into(iv_image1)
    }
}