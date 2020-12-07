package com.riverside.skeleton.kotlin.util.packageinfo

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.riverside.skeleton.kotlin.util.resource.ContextHolder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * PackageInfo处理类   1.2
 * b_e  2019/09/29
 */
class MetadataInfo<T>(private val name: String, private val default: T) :
    ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        PackageInfoHelper.getMetadata(name, default)
}

object PackageInfoHelper {
    val context: Context = ContextHolder.applicationContext

    private val metadata by lazy {
        context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    }

    /**
     * 取得对应的MetaData数据的值
     */
    internal fun <T> getMetadata(key: String, default: T): T = with(metadata) {
        val res: Any = when (default) {
            is String -> metaData?.getString(key, default) ?: default
            is Long -> metaData?.getLong(key, default) ?: default
            is Int -> metaData?.getInt(key, default) ?: default
            else -> throw IllegalArgumentException("The data can not to read")
        }
        res as T
    }
}

/**
 * 检查手机上是否安装了指定的软件
 */
fun Context.isAvailable(packageName: String): Boolean =
    this.packageManager.getInstalledPackages(0)
        .find { it.packageName == packageName } != null