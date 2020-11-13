package com.riverside.skeleton.kotlin.base.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.multidex.MultiDexApplication
import com.riverside.skeleton.kotlin.base.activity.ActivityStaticMonitor
import com.riverside.skeleton.kotlin.base.utils.CrashCallback
import com.riverside.skeleton.kotlin.base.utils.collectinfo.*
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.slog.SLogLevel
import com.riverside.skeleton.kotlin.slog.printer.SLogPrinter
import com.riverside.skeleton.kotlin.util.resource.ContextHolder
import com.zxy.recovery.core.Recovery
import java.lang.reflect.Method

/**
 * Application基类    1.0
 * <p>
 * b_e 2019/09/23
 */
open class SBaseApplication : MultiDexApplication() {
    //Module的Application列表
    lateinit var moduleApplications: Array<Class<out Application>>
    private val moduleApplicationList = mutableListOf<Application>()

    override fun onCreate() {
        super.onCreate()
        instance = this

        //设置收集信息
        with(CollectInfoHelper) {
            application = instance
            with(infoSourceClassList) {
                add(AppVersionInfo::class)
                add(DisplayInfo::class)
                add(OSInfo::class)
                add(OSVersionInfo::class)
                add(TelephonyInfo::class)
            }
        }

        if (isDebug) {
            //开启Android的严格模式
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build()
            )
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
        } else {
            SLog.addPrinter(SLogPrinter(SLogLevel.LEVEL_ERROR))
        }

        //启动防灾模式
        Recovery.getInstance()
            .debug(isDebug)
            .recoverInBackground(true)
            .recoverStack(isDebug)
            .callback(CrashCallback())
            .silent(true, Recovery.SilentMode.RESTART)
            .init(instance)

        //启动Activity状态监控
        ActivityStaticMonitor.init(instance)

        moduleApplicationList.forEach {
            it.onCreate()
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        //附加其他module的Application
        base?.let {
            if (this::moduleApplications.isInitialized) {
                moduleApplicationList.clear()

                moduleApplications.forEach {
                    val moduleApplication = it.newInstance()
                    moduleApplicationList.add(moduleApplication as Application)

                    val method: Method =
                        Application::class.java.getDeclaredMethod("attach", Context::class.java)
                    method.isAccessible = true
                    method.invoke(moduleApplication, baseContext)
                }
            }
        }
    }

    companion object {
        lateinit var instance: SBaseApplication

        val isDebug get() = ContextHolder.isDebug
    }
}