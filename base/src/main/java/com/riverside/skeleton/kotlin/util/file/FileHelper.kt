package com.riverside.skeleton.kotlin.util.file

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.riverside.skeleton.kotlin.util.packageinfo.MetadataInfo
import java.io.File

/**
 * 文件帮助    1.3
 * b_e  2019/05/23
 */
val Context.basePath: String by MetadataInfo("APP_HOME", "")

val Context.sdcardBasePath
    get() =
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
            Environment.getExternalStorageDirectory().toString()
        else
            ""

val Context.cachePath get() = this.externalCacheDir?.path ?: ""

fun Context.getPath(path: String) = sdcardBasePath + getPathWithoutSD(path)

fun Context.getPathWithoutSD(path: String) = +basePath + +path.trim()

fun Context.mkdirs(path: String) = getPath(path).mkdirs()

fun Context.createNoMediaFile() =
    getPath(".nomedia").apply { if (this.exists()) File(this).apply { this.createNewFile() } }

fun Context.getFileUri(path: String): Uri =
    File(path).let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            FileProvider.getUriForFile(
                this@getFileUri,
                this@getFileUri.packageName + ".fileprovider",
                it
            )
        else
            Uri.fromFile(it)
    }