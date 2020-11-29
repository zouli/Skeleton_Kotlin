package com.riverside.skeleton.kotlin.util.extras

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Extra处理类     1.0
 * b_e      2020/11/26
 */
class Extra<T>(private val name: String, private val default: T) :
    ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        ExtrasHelper.findExtra(name, default)
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
        if (intent.hasExtra(name)) {
            when (default) {
                is Int -> intent.getIntExtra(name, default)
                is Long -> intent.getLongExtra(name, default)
                is CharSequence -> intent.getCharSequenceExtra(name) ?: default
                is String -> intent.getStringExtra(name) ?: default
                is Float -> intent.getFloatExtra(name, default)
                is Double -> intent.getDoubleExtra(name, default)
                is Char -> intent.getCharExtra(name, default)
                is Short -> intent.getShortExtra(name, default)
                is Boolean -> intent.getBooleanExtra(name, default)
                is Serializable -> intent.getSerializableExtra(name) ?: default
                is Bundle -> intent.getBundleExtra(name) ?: default
                is Parcelable -> intent.getParcelableExtra(name) ?: default
                is Array<*> -> when {
                    default.isArrayOf<Int>() -> intent.getIntArrayExtra(name)
                    default.isArrayOf<Long>() -> intent.getLongArrayExtra(name)
                    default.isArrayOf<Float>() -> intent.getFloatArrayExtra(name)
                    default.isArrayOf<Double>() -> intent.getDoubleArrayExtra(name)
                    default.isArrayOf<Char>() -> intent.getCharArrayExtra(name)
                    default.isArrayOf<Short>() -> intent.getShortArrayExtra(name)
                    default.isArrayOf<Boolean>() -> intent.getBooleanArrayExtra(name)
                    default.isArrayOf<CharSequence>() -> intent.getCharSequenceArrayExtra(name)
                    default.isArrayOf<String>() -> intent.getStringArrayExtra(name)
                    default.isArrayOf<Parcelable>() -> intent.getParcelableArrayExtra(name)
                    default.isArrayOf<Byte>() -> intent.getByteArrayExtra(name)
                    else -> default
                }
                is Byte -> intent.getByteExtra(name, default)
                else -> default
            } as T
        } else default
}