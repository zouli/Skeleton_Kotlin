package com.riverside.skeleton.kotlin.util.picasso

import android.graphics.*
import com.squareup.picasso.Transformation

/**
 * 圆形图片变换   1.0
 *
 * b_e  2020/12/30
 */
class RoundTransformation : Transformation {
    override fun key() = "round"

    override fun transform(source: Bitmap): Bitmap {
        val size = source.width.coerceAtMost(source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }
        val bitmap = Bitmap.createBitmap(size, size, source.config)
        val paint = Paint().apply {
            shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            isAntiAlias = true
        }
        Canvas(bitmap).apply {
            drawCircle(size / 2f, size / 2f, size / 2f, paint)
        }
        squaredBitmap.recycle()
        return bitmap
    }
}