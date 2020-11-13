package com.riverside.skeleton.kotlin.util.resource

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo

object ContextHolder {
    var applicationContext: Context = Application()
        @SuppressLint("PrivateApi")
        get() =
            (Class.forName("android.app.ActivityThread").getMethod("currentApplication")
                .invoke(null)
                ?: Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(
                    null
                )) as Application

    val isDebug
        get() =
            applicationContext.applicationInfo?.let {
                (it.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            } ?: false
}