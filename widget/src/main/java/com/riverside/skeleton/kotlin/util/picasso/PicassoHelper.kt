package com.riverside.skeleton.kotlin.util.picasso

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.riverside.skeleton.kotlin.util.file.exists
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import java.io.File
import java.lang.Exception

/**
 * Picasso帮助类   1.0
 *
 * b_e      2020/12/05
 */

fun Picasso.read(url: String): RequestCreator = if (url.exists()) load(File(url)) else load(url)

fun RequestCreator.resize(width: Int): RequestCreator = this.resize(width, width)

fun RequestCreator.resizeDimen(widthResId: Int): RequestCreator =
    this.resizeDimen(widthResId, widthResId)

fun RequestCreator.defaultImage(@DrawableRes imageIdRes: Int): RequestCreator =
    this.placeholder(imageIdRes).apply { this.error(imageIdRes) }

fun RequestCreator.into(imageView: ImageView, success: () -> Unit) =
    this.into(imageView, success, {})

fun RequestCreator.into(imageView: ImageView, success: () -> Unit, error: (e: Exception) -> Unit) =
    this.into(imageView, object : Callback {
        override fun onSuccess() {
            success()
        }

        override fun onError(e: Exception) {
            error(e)
        }
    })

fun RequestCreator.resizeT(width: Int) = resizeT(width, width)

fun RequestCreator.resizeT(width: Int, height: Int): RequestCreator =
    this.apply { this.transform(ResizeTransformation(width, height)) }

fun RequestCreator.shadow(
    radius: Float, color: Int, left: Float = 0F, top: Float = 0F
): RequestCreator =
    this.apply {
        this.transform(ShadowTransformation(radius, color, left, top))
    }