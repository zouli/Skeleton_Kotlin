package com.riverside.skeleton.kotlin.util.preference

import android.content.Context
import com.riverside.skeleton.kotlin.base.application.SBaseApplication
import com.riverside.skeleton.kotlin.util.packageinfo.MetadataInfo
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Preference处理类 1.1
 *
 * b_e                           2019/09/29
 * 1.1  可以使用变量名为关键字      2020/12/08
 */
class Preference<T>(private val name: String = "", private val default: T) :
    ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        PreferenceHelper.findPreference(name.ifEmpty { property.name }, default)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        PreferenceHelper.putPreference(name.ifEmpty { property.name }, value)
}

object PreferenceHelper {
    val context: Context = SBaseApplication.instance
    private val app_home: String by MetadataInfo("APP_HOME", "")

    private val prefs by lazy {
        context.getSharedPreferences(app_home, Context.MODE_PRIVATE)
    }

    fun delete(vararg key: String) {
        if (key.isEmpty()) {
            prefs.edit().clear().apply()
        } else {
            key.forEach {
                prefs.edit().remove(it).apply()
            }
        }
    }

    internal fun <U> findPreference(name: String, default: U): U = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("未找到对应的项目类型")
        }
        res as U
    }

    internal fun <U> putPreference(name: String, value: U) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("未找到对应的项目类型")
        }.apply()
    }
}