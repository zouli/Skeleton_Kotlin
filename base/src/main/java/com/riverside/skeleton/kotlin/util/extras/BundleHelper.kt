package com.riverside.skeleton.kotlin.util.extras

import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * FragmentArgument处理类     1.0
 * b_e      2020/11/29
 */
class FragmentArgument<T>(private val name: String, private val default: T) :
    ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        BundleHelper.findValue(name, default)
}

object BundleHelper {
    lateinit var bundle: Bundle

    internal fun <T> putValue(bundle: Bundle, name: String, value: T) {
        when (value) {
            is Boolean -> bundle.putBoolean(name, value)
            is Byte -> bundle.putByte(name, value)
            is Char -> bundle.putChar(name, value)
            is Short -> bundle.putShort(name, value)
            is Int -> bundle.putInt(name, value)
            is Long -> bundle.putLong(name, value)
            is Float -> bundle.putFloat(name, value)
            is Double -> bundle.putDouble(name, value)
            is String -> bundle.putString(name, value)
            is CharSequence -> bundle.putCharSequence(name, value)
            is Serializable -> bundle.putSerializable(name, value)
            is Parcelable -> bundle.putParcelable(name, value)
            is Bundle -> bundle.putBundle(name, value)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
                when (value) {
                    is IBinder -> bundle.putBinder(name, value)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                when (value) {
                    is Size -> bundle.putSize(name, value)
                    is SizeF -> bundle.putSizeF(name, value)
                }
            }
            is Array<*> -> when {
                value.isArrayOf<Boolean>() -> bundle.putBooleanArray(name, value as BooleanArray)
                value.isArrayOf<Byte>() -> bundle.putByteArray(name, value as ByteArray)
                value.isArrayOf<Short>() -> bundle.putShortArray(name, value as ShortArray)
                value.isArrayOf<Char>() -> bundle.putCharArray(name, value as CharArray)
                value.isArrayOf<Int>() -> bundle.putIntArray(name, value as IntArray)
                value.isArrayOf<Long>() -> bundle.putLongArray(name, value as LongArray)
                value.isArrayOf<Float>() -> bundle.putFloatArray(name, value as FloatArray)
                value.isArrayOf<Double>() -> bundle.putDoubleArray(name, value as DoubleArray)
                value.isArrayOf<String>() -> bundle.putStringArray(name, value as Array<String>)
                value.isArrayOf<CharSequence>() -> bundle.putCharSequenceArray(
                    name, value as Array<CharSequence>
                )
                value.isArrayOf<Parcelable>() -> bundle.putParcelableArray(
                    name, value as Array<Parcelable>
                )
            }
        }
    }

    internal fun <T> findValue(name: String, default: T): T =
        if (bundle.containsKey(name)) {
            when (default) {
                is Boolean -> bundle.getBoolean(name, default)
                is Byte -> bundle.getByte(name, default)
                is Char -> bundle.getChar(name, default)
                is Short -> bundle.getShort(name, default)
                is Int -> bundle.getInt(name, default)
                is Long -> bundle.getLong(name, default)
                is Float -> bundle.getFloat(name, default)
                is Double -> bundle.getDouble(name, default)
                is String -> bundle.getString(name, default)
                is CharSequence -> bundle.getCharSequence(name, default)
                is Serializable -> bundle.getSerializable(name) ?: default
                is Parcelable -> bundle.getParcelable(name) ?: default
                is Bundle -> bundle.getBundle(name) ?: default
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
                    when (default) {
                        is IBinder -> bundle.getBinder(name) ?: default
                        else -> default
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    when (default) {
                        is Size -> bundle.getSize(name) ?: default
                        is SizeF -> bundle.getSizeF(name) ?: default
                        else -> default
                    }
                }
                is Array<*> -> when {
                    default.isArrayOf<Boolean>() -> bundle.getBooleanArray(name)
                    default.isArrayOf<Byte>() -> bundle.getByteArray(name)
                    default.isArrayOf<Short>() -> bundle.getShortArray(name)
                    default.isArrayOf<Char>() -> bundle.getCharArray(name)
                    default.isArrayOf<Int>() -> bundle.getIntArray(name)
                    default.isArrayOf<Long>() -> bundle.getLongArray(name)
                    default.isArrayOf<Float>() -> bundle.getFloatArray(name)
                    default.isArrayOf<Double>() -> bundle.getDoubleArray(name)
                    default.isArrayOf<String>() -> bundle.getStringArray(name)
                    default.isArrayOf<CharSequence>() -> bundle.getCharSequenceArray(name)
                    default.isArrayOf<Parcelable>() -> bundle.getParcelableArray(name)
                    else -> default
                }
                else -> default
            } as T
        } else default
}