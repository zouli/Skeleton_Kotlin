package com.riverside.skeleton.kotlin.base.utils.collectinfo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * 信息源接口    1.0
 * b_e  2019/06/20
 */
interface InfoSource {
    fun info(context: Context): JSONObject
}

/**
 * 取得App的版本信息   1.0
 */
object AppVersionInfo : InfoSource {
    override fun info(context: Context): JSONObject = JSONObject().also { json ->
        val pi = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        json["versionCode"] = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) pi.versionCode else pi.longVersionCode
        json["versionName"] = pi.versionName
    }
}

/**
 * 取得显示信息   1.0
 */
object DisplayInfo : InfoSource {
    override fun info(context: Context): JSONObject = JSON.parseObject(
        context.resources.displayMetrics.toString().replace("DisplayMetrics", "").replace("=", ":")
    )
}

/**
 * 取得OS相关信息 1.0
 */
object OSInfo : InfoSource {
    override fun info(context: Context): JSONObject = Build::class.java.declaredFields.propertiesJson
}

/**
 * 取得OS版本信息 1.0
 */
object OSVersionInfo : InfoSource {
    override fun info(context: Context): JSONObject = Build.VERSION::class.java.declaredFields.propertiesJson
}

/**
 * 取得话机信息   1.0
 */
object TelephonyInfo : InfoSource {
    @SuppressLint("HardwareIds")
    override fun info(context: Context): JSONObject = JSONObject().also { json ->
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                json["PhoneCount"] = tm.phoneCount
            }
            json["DeviceSoftwareVersion"] = tm.deviceSoftwareVersion
            json["Line1Number"] = tm.line1Number
            json["SimSerialNumber"] = tm.simSerialNumber
            json["DeviceId"] = tm.deviceId
            json["PhoneType"] = tm.phoneType
        }
    }
}

/**
 * 将Field数组转换为JSONObject
 */
val Array<Field>.propertiesJson
    get() = JSONObject().also { json ->
        this.forEach { c ->
            // 设置信息为可见
            c.isAccessible = true
            // 取变量的值
            val value = c.get(null)
            // 保存相关信息
            json[c.name] =
                if (value.javaClass.isArray)
                    JSONArray.toJSON(value)
                else
                    value
        }
    }

/**
 * 取得制定信息源的信息
 */
fun KClass<out InfoSource>.getInfo(context: Context): JSONObject {
    val constructor = this.java.getDeclaredConstructor()
    constructor.isAccessible = true
    val infoSource: InfoSource = constructor.newInstance() as InfoSource
    return infoSource.info(context)
}

val <T : Any> T.propertiesJson
    get() = JSONObject().also { json ->
        this::class.memberProperties.forEach {
            json[it.name] = it.getUnsafed(this)
        }
    }

fun <T, R> KProperty1<T, R>.getUnsafed(receiver: Any): R {
    return get(receiver as T)
}