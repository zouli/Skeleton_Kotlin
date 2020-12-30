package com.riverside.skeleton.kotlin.util.picasso

import android.graphics.*
import com.squareup.picasso.Transformation

/**
 * 图片阴影变换   1.0
 *
 * b_e  2020/12/05
 */
class ShadowTransformation(
    private val radius: Float, val color: Int, private val left: Float, private val top: Float
) :
    Transformation {
    override fun key() = "shadow"

    override fun transform(source: Bitmap): Bitmap {
        val blurFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = color
            maskFilter = blurFilter
        }

        val offsetXY = IntArray(2)
        val shadowBitmap = source.extractAlpha(shadowPaint, offsetXY)
        val shadowImage32 = Bitmap.createBitmap(
            shadowBitmap.width, shadowBitmap.height, Bitmap.Config.ARGB_8888
        )
        Canvas(shadowImage32).apply {
            drawRect(
                radius, radius, shadowBitmap.width - radius, shadowBitmap.height - radius,
                shadowPaint
            )
            drawBitmap(source, -offsetXY[0] + left, -offsetXY[1] + top, null)
        }
        source.recycle()
        return shadowImage32
    }
}