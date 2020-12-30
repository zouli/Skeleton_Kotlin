package com.riverside.skeleton.kotlin.widgettest

import androidx.core.graphics.toColorInt
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.picasso.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_picasso.*

class PicassoActivity : SBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_picasso

    override fun initView() {
        title = "Picasso"

        val url =
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3127477182,1327372793&fm=15&gp=0.jpg"

        Picasso.get()
            .read(url)
            .into(iv_image)

        Picasso.get()
            .read(url)
//            .resizeT(250, 250)
//            .shadow(10F, "#999999".toColorInt(), -3F, -8F)
            .transform(RoundTransformation())
            .defaultImage(R.drawable.image_grid_add_image)
            .into(iv_image1)
    }
}