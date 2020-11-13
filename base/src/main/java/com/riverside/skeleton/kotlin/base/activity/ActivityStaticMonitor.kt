package com.riverside.skeleton.kotlin.base.activity

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

/**
 * Activity状态监控 1.1
 * <p>
 * b_e  2019/08/30
 */
class ActivityStaticMonitor private constructor() : Application.ActivityLifecycleCallbacks {
    //当前在前台显示的Activity数量
    private var activityActive: Int = 0

    //前后台转换Listener列表
    private var transferListenerList: ArrayList<TransferListener>? = null

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
        activityActive++
        if (activityActive == 1) {
            transferListenerList?.let {
                //抄袭的android的处理方式，应该是防止多线程数据混乱
                val tmpListeners = transferListenerList?.clone() as ArrayList<TransferListener>
                tmpListeners.forEach {
                    it.onBecameForeground()
                }
            }
        }
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.let { ActivityStackManager.finishActivity(it) }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
        activityActive--
        if (activityActive == 0) {
            transferListenerList?.let {
                //抄袭的android的处理方式，应该是防止多线程数据混乱
                val tmpListeners = transferListenerList?.clone() as ArrayList<TransferListener>
                tmpListeners.forEach {
                    it.onBecameBackground()
                }
            }
        }
    }

    /**
     * 添加前后台转换监听
     */
    fun addTransferListener(listener: TransferListener) {
        if (transferListenerList == null) {
            transferListenerList = ArrayList()
        }
        transferListenerList?.add(listener)
    }

    /**
     * 删除前后台转换监听
     */
    fun removeTransferListener(listener: TransferListener) {
        if (transferListenerList == null) {
            return
        }
        transferListenerList?.remove(listener)
        if (transferListenerList?.size == 0) {
            transferListenerList = null
        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activity?.let { ActivityStackManager.addActivity(it) }
    }

    companion object {
        lateinit var instance: ActivityStaticMonitor

        fun init(application: Application): ActivityStaticMonitor =
            if (this::instance.isInitialized) instance else {
                synchronized(ActivityStaticMonitor::class.java) {
                    instance = ActivityStaticMonitor()
                    instance
                }
            }.also { application.registerActivityLifecycleCallbacks(it) }
    }
}

/**
 * 前后台转换监听接口
 */
interface TransferListener {
    /**
     * 程序进入前台
     */
    fun onBecameForeground()

    /**
     * 程序进入后台
     */
    fun onBecameBackground()
}

val Application.activityStaticMonitor get() = ActivityStaticMonitor.init(this)
val Context.activityStaticMonitor
    get() = this.applicationContext.let {
        if (it is Application) ActivityStaticMonitor.init(it)
        else throw IllegalStateException("ActivityStaticCallbacks初始化时无法获得Application对象")
    }