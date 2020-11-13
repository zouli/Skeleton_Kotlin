package com.riverside.skeleton.kotlin.util.converter

/**
 * 类型转换工具  1.0
 * b_e  2019/05/07
 */
fun Double.toString(x: Int = -1, trimZero: Boolean = true) =
    String.format("%${if (x > 0) ".$x" else ""}f", this).run {
        if (trimZero) {
            replace(Regex("0+?$"), "").replace(Regex("[.]$"), "")
        } else {
            this
        }
    }

fun Float.toString(x: Int = -1, trimZero: Boolean = true) = this.toDouble().toString(x, trimZero)

fun <T> parseType(parseFun: () -> T, default: T): T =
    try {
        parseFun()
    } catch (_: NumberFormatException) {
        default
    }

fun String.toInt(default: Int) = parseType({ this.replace(",", "").toInt() }, default)

fun String.toDouble(default: Double) = parseType({ this.replace(",", "").toDouble() }, default)

fun String.toFloat(default: Float) = parseType({ this.replace(",", "").toFloat() }, default)

fun String.toLong(default: Long) = parseType({ this.replace(",", "").toLong() }, default)

fun String.toMap() =
    if (this.length < 3)
        emptyMap()
    else
        this.substring(1 until this.length - 1)
            .split(",")
            .map { it.trim().split("=") }
            .filter { it.size > 1 }
            .map { (key, value) -> key to value }
            .toMap()