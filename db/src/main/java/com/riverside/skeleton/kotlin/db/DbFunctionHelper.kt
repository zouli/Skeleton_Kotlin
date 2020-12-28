package com.riverside.skeleton.kotlin.db

/**
 * 数据库函数帮助类 1.0
 *
 * b_e  2020/12/28
 */
fun SField.avg(): SField = this.apply { function("AVG") }
fun SField.count(): SField = this.apply { function("COUNT") }
fun SField.groupConcat(separator: String = ","): SField =
    this.apply { function("GROUP_CONCAT", "'$separator'") }

fun SField.max(): SField = this.apply { function("MAX") }
fun SField.min(): SField = this.apply { function("MIN") }
fun SField.sum(): SField = this.apply { function("SUM") }
fun SField.total(): SField = this.apply { function("TOTAL") }