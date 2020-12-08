package com.riverside.skeleton.kotlin.util.extras

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance

/**
 * 根据类型取得默认值
 */
val KType.default: Any
    get() = when (this.classifier) {
        Int::class -> 0
        Long::class -> 0L
        CharSequence::class -> ""
        String::class -> ""
        Float::class -> 0F
        Double::class -> 0.0
        Char::class -> Char.MIN_VALUE
        Short::class -> 0.toShort()
        Boolean::class -> false
        IntArray::class ->
            if (this.arguments.isNotEmpty()) arrayOf<Int>() else IntArray(0)
        LongArray::class ->
            if (this.arguments.isNotEmpty()) arrayOf<Long>() else LongArray(0)
        FloatArray::class ->
            if (this.arguments.isNotEmpty()) arrayOf<Float>() else FloatArray(0)
        DoubleArray::class ->
            if (this.arguments.isNotEmpty()) arrayOf<Double>() else DoubleArray(0)
        CharArray::class ->
            if (this.arguments.isNotEmpty()) arrayOf<Char>() else CharArray(0)
        ShortArray::class ->
            if (this.arguments.isNotEmpty()) arrayOf<Short>() else ShortArray(0)
        BooleanArray::class ->
            if (this.arguments.isNotEmpty()) arrayOf<Boolean>() else BooleanArray(0)
        List::class -> mutableListOf(this.arguments[0].type?.default).apply { clear() }
        Array<String>::class -> arrayOf<String>()
        else -> (this.classifier as KClass<*>).createInstance()
    }

/**
 * 生成可能的关键字
 */
fun getKeyNames(name: String): MutableList<String> = mutableListOf(name).apply {
    val keyName = name.toCharArray().joinToString("") {
        if (it in 'A'..'Z') "_${it + 32}" else it.toString()
    }

    if (!this.contains(keyName)) this.add(keyName)
}