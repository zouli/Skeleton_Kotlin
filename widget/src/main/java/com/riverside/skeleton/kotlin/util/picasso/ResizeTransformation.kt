package com.riverside.skeleton.kotlin.util.picasso

import android.graphics.Bitmap
import com.squareup.picasso.Transformation

/**
 * 图片大小变换   1.0
 *
 * b_e      2020/12/04
 */
class ResizeTransformation(val width: Int, val height: Int) : Transformation {
    override fun key() = "width:$width height:$height"

    override fun transform(source: Bitmap): Bitmap =
        Bitmap.createScaledBitmap(source, width, height, false).apply {
            if (this != source) source.recycle()
        }
}