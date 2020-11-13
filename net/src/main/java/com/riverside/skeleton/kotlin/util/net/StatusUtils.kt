package com.riverside.skeleton.kotlin.util.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

/**
 * 网络状态工具类  1.0
 * b_e  2019/09/27
 */
/**
 * 取得ConnectivityManager
 */
val Context.connectivityManager: ConnectivityManager
    get() = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

/**
 * 取得当前网络
 */
val Context.activeNetworkInfo: NetworkInfo? get() = this.connectivityManager.activeNetworkInfo

/**
 * 检查网络是否可用
 */
val Context.isNetworkAvailable: Boolean get() = this.activeNetworkInfo?.isConnected ?: false

/**
 * 检查网络能力
 */
fun Context.hasNetCapability(type0: Int, type23: Int): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.connectivityManager.activeNetwork?.let {
            this.connectivityManager.getNetworkCapabilities(it)?.hasCapability(type23)
        } ?: false
    } else {
        this.activeNetworkInfo?.let { it.type == type0 } ?: false
    }

/**
 * 检查是否为Wifi
 */
val Context.isWifi: Boolean
    get() = this.hasNetCapability(ConnectivityManager.TYPE_WIFI, NetworkCapabilities.TRANSPORT_WIFI)

/**
 * 检查是否为移动网络
 */
val Context.isMobile: Boolean
    get() = this.hasNetCapability(
        ConnectivityManager.TYPE_MOBILE,
        NetworkCapabilities.TRANSPORT_CELLULAR
    )
