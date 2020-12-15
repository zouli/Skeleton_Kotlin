package com.riverside.skeleton.kotlin.util.resource

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * 标识工具    1.1
 *
 * b_e  2019/05/13
 * 1.1  根据字符串生成UUID   2020/12/15
 */

val uuid get() = UUID.randomUUID().toString()

val String.uuid
    get() = UUID(
        this.hashCode().toLong(), "Riverside_Skeleton".hashCode().toLong()
    ).toString()

val String.md5: String
    get() =
        try {
            MessageDigest.getInstance("MD5").digest(this.toByteArray())
                .joinToString(separator = "") {
                    Integer.toHexString(it.toInt() and 0xFF)
                        .run { if (this.length < 2) "0$this" else this }
                }
        } catch (_: NoSuchAlgorithmException) {
            ""
        }