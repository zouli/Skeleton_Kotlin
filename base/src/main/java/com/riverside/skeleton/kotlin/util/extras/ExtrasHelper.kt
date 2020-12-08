package com.riverside.skeleton.kotlin.util.extras

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Extra处理类     1.1
 *
 * b_e                           2020/11/26
 * 1.1  可以使用变量名为关键字      2020/12/08
 * 1.1  自动生成默认值             2020/12/08
 */
class Extra<T>(private val name: String = "", private val default: T? = null) :
    ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        try {
            val value = default ?: property.returnType.default as T
            return ExtrasHelper.findExtra(name.ifEmpty { property.name }, value)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("无法生成默认值，请设置default!")
        }
    }
}

object ExtrasHelper {
    lateinit var intent: Intent

    internal fun <T> putExtra(intent: Intent, name: String, value: T) {
        when (value) {
            is Int -> intent.putExtra(name, value)
            is Long -> intent.putExtra(name, value)
            is CharSequence -> intent.putExtra(name, value)
            is String -> intent.putExtra(name, value)
            is Float -> intent.putExtra(name, value)
            is Double -> intent.putExtra(name, value)
            is Char -> intent.putExtra(name, value)
            is Short -> intent.putExtra(name, value)
            is Boolean -> intent.putExtra(name, value)
            is Serializable -> intent.putExtra(name, value)
            is Bundle -> intent.putExtra(name, value)
            is Parcelable -> intent.putExtra(name, value)
            is Array<*> -> when {
                value.isArrayOf<Int>() -> intent.putExtra(name, value)
                value.isArrayOf<Long>() -> intent.putExtra(name, value)
                value.isArrayOf<Float>() -> intent.putExtra(name, value)
                value.isArrayOf<Double>() -> intent.putExtra(name, value)
                value.isArrayOf<Char>() -> intent.putExtra(name, value)
                value.isArrayOf<Short>() -> intent.putExtra(name, value)
                value.isArrayOf<Boolean>() -> intent.putExtra(name, value)
                value.isArrayOf<CharSequence>() -> intent.putExtra(name, value)
                value.isArrayOf<String>() -> intent.putExtra(name, value)
                value.isArrayOf<Parcelable>() -> intent.putExtra(name, value)
                value.isArrayOf<Byte>() -> intent.putExtra(name, value)
            }
            is Byte -> intent.putExtra(name, value)
        }
    }

    internal fun <T> findExtra(name: String, default: T): T =
        getKeyNames(name).firstOrNull { intent.hasExtra(name) }?.let { keyName ->
            when (default) {
                is Int -> intent.getIntExtra(keyName, default)
                is Long -> intent.getLongExtra(keyName, default)
                is CharSequence -> intent.getCharSequenceExtra(keyName) ?: default
                is String -> intent.getStringExtra(keyName) ?: default
                is Float -> intent.getFloatExtra(keyName, default)
                is Double -> intent.getDoubleExtra(keyName, default)
                is Char -> intent.getCharExtra(keyName, default)
                is Short -> intent.getShortExtra(keyName, default)
                is Boolean -> intent.getBooleanExtra(keyName, default)
                is Serializable -> intent.getSerializableExtra(keyName) ?: default
                is Bundle -> intent.getBundleExtra(keyName) ?: default
                is Parcelable -> intent.getParcelableExtra(keyName) ?: default
                is Array<*> -> when {
                    default.isArrayOf<Int>() -> intent.getIntArrayExtra(keyName)
                    default.isArrayOf<Long>() -> intent.getLongArrayExtra(keyName)
                    default.isArrayOf<Float>() -> intent.getFloatArrayExtra(keyName)
                    default.isArrayOf<Double>() -> intent.getDoubleArrayExtra(keyName)
                    default.isArrayOf<Char>() -> intent.getCharArrayExtra(keyName)
                    default.isArrayOf<Short>() -> intent.getShortArrayExtra(keyName)
                    default.isArrayOf<Boolean>() -> intent.getBooleanArrayExtra(keyName)
                    default.isArrayOf<CharSequence>() -> intent.getCharSequenceArrayExtra(keyName)
                    default.isArrayOf<String>() -> intent.getStringArrayExtra(keyName)
                    default.isArrayOf<Parcelable>() -> intent.getParcelableArrayExtra(keyName)
                    default.isArrayOf<Byte>() -> intent.getByteArrayExtra(keyName)
                    else -> default
                }
                is Byte -> intent.getByteExtra(keyName, default)
                else -> default
            } as T
        } ?: default
}