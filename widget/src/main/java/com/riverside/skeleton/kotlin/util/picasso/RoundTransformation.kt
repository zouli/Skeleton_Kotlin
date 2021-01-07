package com.riverside.skeleton.kotlin.util.picasso

import android.graphics.*
import android.os.Build
import androidx.annotation.RequiresApi
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

/**
 * 圆角图片变换   1.0
 *
 * b_e  2021/01/06
 */
class RoundCornerTransformation(private val radius: Float) : Transformation {
    override fun key() = "roundCorner"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun transform(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val bitmap = Bitmap.createBitmap(width, height, source.config)
        val paint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
        }
        val paintImage = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        }
        Canvas(bitmap).apply {
            drawRoundRect(0F, 0F, width.toFloat(), height.toFloat(), radius, radius, paint)
            drawBitmap(source, 0F, 0F, paintImage)
        }
        source.recycle()
        return bitmap
    }
}