package com.riverside.skeleton.kotlin.util.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.riverside.skeleton.kotlin.base.R

private val FILE_EXT = arrayOf(
    R.array.file_extension_audio,
    R.array.file_extension_excel,
    R.array.file_extension_image,
    R.array.file_extension_package,
    R.array.file_extension_pdf,
    R.array.file_extension_ppt,
    R.array.file_extension_text,
    R.array.file_extension_video,
    R.array.file_extension_web,
    R.array.file_extension_word
)

fun Context.startApp(url: String) =
    if (url.exists()) {
        getIntent(this, url)?.let {
            this@startApp.startActivity(it)
            true
        } ?: false
    } else
        false

private fun getIntent(context: Context, url: String): Intent? =
    createIntent(context, FILE_EXT.first {
        context.resources.getStringArray(it).filter { s -> url.endsWith(s) }.any()
    }, url)

private fun createIntent(context: Context, urlType: Int, url: String): Intent =
    Intent(Intent.ACTION_VIEW).run {
        val uri =
            if (urlType == R.array.file_extension_web)
                Uri.parse(url).buildUpon()
                    .encodedAuthority("com.android.htmlfileprovider")
                    .scheme("content").encodedPath(url).build()
            else
                context.getFileUri(url)

        val type = when (urlType) {
            R.array.file_extension_image -> "image/*"
            R.array.file_extension_web -> "text/html"
            R.array.file_extension_package -> "application/vnd.android.package-archive"
            R.array.file_extension_audio -> "audio/*"
            R.array.file_extension_video -> "video/*"
            R.array.file_extension_text -> "text/plain"
            R.array.file_extension_pdf -> "application/pdf"
            R.array.file_extension_word -> "application/msword"
            R.array.file_extension_excel -> "application/vnd.ms-excel"
            R.array.file_extension_ppt -> "application/vnd.ms-powerpoint"
            R.array.file_extension_chm -> "application/x-chm"
            else -> ""
        }

        when (urlType) {
            R.array.file_extension_audio,
            R.array.file_extension_video -> {
                this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                this.putExtra("oneshot", 0)
                this.putExtra("configchange", 0)
            }
            R.array.file_extension_package -> {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            R.array.file_extension_image,
            R.array.file_extension_text,
            R.array.file_extension_pdf,
            R.array.file_extension_word,
            R.array.file_extension_excel,
            R.array.file_extension_ppt,
            R.array.file_extension_chm -> {
                this.addCategory(Intent.CATEGORY_DEFAULT)
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        this.setDataAndType(uri, type)
    }
