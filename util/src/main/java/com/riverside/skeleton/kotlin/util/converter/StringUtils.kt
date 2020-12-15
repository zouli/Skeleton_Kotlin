package com.riverside.skeleton.kotlin.util.converter

/**
 * 字符串工具  1.0
 *
 * b_e  2020/12/15
 */

val String.phoneMark
    get() = (if (this.isEmpty()) "" else this.toCharArray().mapIndexed { index, char ->
        if (index in (3..6)) '*' else char
    }.joinToString(""))
