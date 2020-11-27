package com.riverside.skeleton.kotlin.widget.gallery.imageloader

import android.widget.ImageView
import com.riverside.skeleton.kotlin.util.file.exists
import com.squareup.picasso.Picasso
import java.io.File

/**
 * 图片加载器    1.0
 * b_e      2020/11/23
 */
interface ImageLoader {
    fun loadImage(imageView: ImageView, url: String, width: Int, height: Int)

    fun loadImage(imageView: ImageView, url: String)
}

/**
 * 图片加载器Picasso版
 */
class PicassoImageLoader : ImageLoader {
    override fun loadImage(imageView: ImageView, url: String, width: Int, height: Int) {
        Picasso.get().run {
            if (url.exists()) this.load(File(url)) else this.load(url)
        }.let {
            if (width > -1 && height > -1) {
                it.resize(width, height).centerCrop()
            } else it
        }.into(imageView)
    }

    override fun loadImage(imageView: ImageView, url: String) {
        loadImage(imageView, url, -1, -1)
    }
}