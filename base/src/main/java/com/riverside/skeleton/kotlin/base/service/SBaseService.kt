package com.riverside.skeleton.kotlin.base.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.riverside.skeleton.kotlin.util.extras.ExtrasHelper

/**
 * Service基类    1.0
 * b_e      2020/11/26
 */
abstract class SBaseService : Service() {
    val mBinder = SBaseBinder(this)

    override fun onBind(intent: Intent?): IBinder? {
        intent?.let { ExtrasHelper.intent = it }
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { ExtrasHelper.intent = it }
        onCall(flags, startId)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onRebind(intent: Intent?) {
        intent?.let { ExtrasHelper.intent = it }
        super.onRebind(intent)
    }

    abstract fun onCall(flags: Int, startId: Int)
}

/**
 * Binder实现
 */
class SBaseBinder(service: SBaseService) : Binder() {
    val service = service
}

/**
 * ServiceConnection实现
 */
class SBaseServiceConnection<T : Service> : ServiceConnection {
    private lateinit var onConnected: (name: ComponentName, service: T) -> Unit
    private lateinit var onDisconnected: (name: ComponentName) -> Unit

    fun setOnConnected(block: (name: ComponentName, service: T) -> Unit) {
        onConnected = block
    }

    fun setOnDisconnected(block: (name: ComponentName) -> Unit) {
        onDisconnected = block
    }

    override fun onServiceDisconnected(name: ComponentName) {
        if (::onDisconnected.isInitialized) onDisconnected(name)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        if (::onConnected.isInitialized)
            onConnected(name, (service as SBaseBinder).service as T)
    }
}