package com.riverside.skeleton.kotlin.util.resource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream

/**
 * Assets工具    1.0
 * b_e  2019/05/13
 */
fun Context.getAssetFile(filename: String): InputStream = this.resources.assets.open(filename)

fun Context.getAssetString(filename: String): String =
    this.getAssetFile(filename).bufferedReader().useLines { it.joinToString(separator = "") }

fun Context.getAssetBitmap(filename: String): Bitmap =
    this.getAssetFile(filename).let { BitmapFactory.decodeStream(it) }

fun Context.getAssetFileList(path: String): Array<String> = this.resources.assets.list(path) ?: arrayOf()