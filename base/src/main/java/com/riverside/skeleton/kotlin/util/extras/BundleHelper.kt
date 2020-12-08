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
 * FragmentArgument处理类     1.1
 *
 * b_e                           2020/11/29
 * 1.1  可以使用变量名为关键字      2020/12/08
 * 1.1  自动生成默认值             2020/12/08
 */
class FragmentArgument<T>(private val name: String = "", private val default: T? = null) :
    ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        try {
            val value = default ?: property.returnType.default as T
            return BundleHelper.findValue(name.ifEmpty { property.name }, value)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("无法生成默认值，请设置default!")
        }
    }
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
        getKeyNames(name).firstOrNull { bundle.containsKey(name) }?.let { keyName ->
            when (default) {
                is Boolean -> bundle.getBoolean(keyName, default)
                is Byte -> bundle.getByte(keyName, default)
                is Char -> bundle.getChar(keyName, default)
                is Short -> bundle.getShort(keyName, default)
                is Int -> bundle.getInt(keyName, default)
                is Long -> bundle.getLong(keyName, default)
                is Float -> bundle.getFloat(keyName, default)
                is Double -> bundle.getDouble(keyName, default)
                is String -> bundle.getString(keyName, default)
                is CharSequence -> bundle.getCharSequence(keyName, default)
                is Serializable -> bundle.getSerializable(keyName) ?: default
                is Parcelable -> bundle.getParcelable(keyName) ?: default
                is Bundle -> bundle.getBundle(keyName) ?: default
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
                    when (default) {
                        is IBinder -> bundle.getBinder(keyName) ?: default
                        else -> default
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    when (default) {
                        is Size -> bundle.getSize(keyName) ?: default
                        is SizeF -> bundle.getSizeF(keyName) ?: default
                        else -> default
                    }
                }
                is Array<*> -> when {
                    default.isArrayOf<Boolean>() -> bundle.getBooleanArray(keyName)
                    default.isArrayOf<Byte>() -> bundle.getByteArray(keyName)
                    default.isArrayOf<Short>() -> bundle.getShortArray(keyName)
                    default.isArrayOf<Char>() -> bundle.getCharArray(keyName)
                    default.isArrayOf<Int>() -> bundle.getIntArray(keyName)
                    default.isArrayOf<Long>() -> bundle.getLongArray(keyName)
                    default.isArrayOf<Float>() -> bundle.getFloatArray(keyName)
                    default.isArrayOf<Double>() -> bundle.getDoubleArray(keyName)
                    default.isArrayOf<String>() -> bundle.getStringArray(keyName)
                    default.isArrayOf<CharSequence>() -> bundle.getCharSequenceArray(keyName)
                    default.isArrayOf<Parcelable>() -> bundle.getParcelableArray(keyName)
                    else -> default
                }
                else -> default
            } as T
        } ?: default
}