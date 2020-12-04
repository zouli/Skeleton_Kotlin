package com.riverside.skeleton.kotlin.widget.gallery.imageloader

import android.widget.ImageView
import com.riverside.skeleton.kotlin.util.picasso.read
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator

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
    private lateinit var funs: RequestCreator.(width: Int, height: Int) -> Unit

    fun function(block: RequestCreator.(width: Int, height: Int) -> Unit) {
        funs = block
    }

    override fun loadImage(imageView: ImageView, url: String, width: Int, height: Int) {
        Picasso.get().read(url).let {
            if (width > -1 && height > -1) {
                it.resize(width, height).centerCrop()
            } else it
        }.apply { if (::funs.isInitialized) funs(width, height) }.into(imageView)
    }

    override fun loadImage(imageView: ImageView, url: String) {
        loadImage(imageView, url, -1, -1)
    }
}