package com.riverside.skeleton.kotlin.util.resource

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * 标识工具    1.2
 *
 * b_e  2019/05/13
 * 1.1  根据字符串生成UUID 2020/12/15
 * 1.2  生成16位hash   2021/01/28
 */

val uuid: UUID get() = UUID.randomUUID()

val String.uuid: UUID
    get() = UUID(this.hashCode().toLong(), "Riverside_Skeleton".hashCode().toLong())

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

fun Any.hashCode16() = this.hashCode().let { (it xor (it ushr 16)) and 0xFFFF }