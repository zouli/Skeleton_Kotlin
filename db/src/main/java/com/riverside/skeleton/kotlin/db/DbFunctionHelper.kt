package com.riverside.skeleton.kotlin.db

fun SField.avg(): SField = this.apply { function("AVG") }
fun SField.count(): SField = this.apply { function("COUNT") }
fun SField.groupConcat(separator: String = ","): SField =
    this.apply { function("GROUP_CONCAT", "'$separator'") }

fun SField.max(): SField = this.apply { function("MAX") }
fun SField.min(): SField = this.apply { function("MIN") }
fun SField.sum(): SField = this.apply { function("SUM") }
fun SField.total(): SField = this.apply { function("TOTAL") }