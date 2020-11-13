package com.riverside.skeleton.kotlin.util.file

import java.io.File
import java.text.DecimalFormat

/**
 * 文件大小工具    1.0
 * b_e               2019/05/22
 * 添加2种计算方式     2020/11/12
 */
val File.size
    get() = if (this.isDirectory) {
        getTreeSize(this)
    } else FileSize(this.length())

private fun getTreeSize(files: File): FileSize = files.listFiles()
    .fold(FileSize(0)) { size, file -> if (file.isDirectory) size + getTreeSize(file) else size + file.length() }

class FileSize constructor(size: Long) {
    companion object {
        const val FLAG_SI_UNITS = 1 shl 2
        const val FLAG_IEC_UNITS = 1 shl 3

        var units = FLAG_SI_UNITS
    }

    private val df = DecimalFormat("0.00")
    private val unit = if ((units and FLAG_IEC_UNITS) != 0) 1024 else 1000

    val b = size * 1.0
    val kb = b / unit
    val mb = kb / unit
    val gb = mb / unit

    val bString = "${df.format(b)}B"
    val kbString = "${df.format(kb)}KB"
    val mbString = "${df.format(mb)}MB"
    val gbString = "${df.format(gb)}GB"

    override fun toString() = when {
        b < unit -> bString
        b < unit * unit -> kbString
        b < unit * unit * unit -> mbString
        else -> gbString
    }

    operator fun plus(addend: FileSize) = FileSize(b.toLong() + addend.b.toLong())
    operator fun plus(length: Long) = FileSize(b.toLong() + length)
}