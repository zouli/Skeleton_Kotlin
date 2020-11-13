package com.riverside.skeleton.kotlin.util.file

import android.text.TextUtils
import java.io.File
import java.net.MalformedURLException
import java.net.URL

/**
 * 文件工具    1.0
 * b_e  2019/05/22
 */
class FileUtils {
    companion object {
        const val SEPARATOR = "/"
    }
}

operator fun String.unaryPlus() =
    if (this.startsWith(FileUtils.SEPARATOR)) this else FileUtils.SEPARATOR + this

val String.file get() = File(this)

fun String.exists() = if (TextUtils.isEmpty(this)) false else this.file.exists()

fun String.mkdirs(): String = (!this.exists() && this.file.mkdirs()).let { this }

val String.filename get() = this.file.name

val String.filenameWithUrl
    get() = try {
        URL(this).file
    } catch (_: MalformedURLException) {
        ""
    }

infix fun String.rename(dest: String) = this.exists() && this.file.renameTo(dest.file)

fun String.delete(recursively: Boolean = false) =
    this.exists() && if (recursively) this.file.delete() else this.file.deleteRecursively()

infix fun String.copyTo(dest: String) = this.copyTo(dest, true)

fun String.copyTo(dest: String, overwrite: Boolean = true) =
    this.file.copyTo(dest.file, overwrite)