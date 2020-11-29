@file:Suppress("DEPRECATION")

package com.riverside.skeleton.kotlin.util.extras

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.base.fragment.ISBaseFragment
import com.riverside.skeleton.kotlin.base.service.SBaseService
import com.riverside.skeleton.kotlin.base.service.SBaseServiceConnection

/**
 * StartActivity封装  1.0
 * b_e      2020/11/26
 */
inline fun <reified T : SBaseActivity> Context.startActivity(vararg params: Pair<String, Any?>) =
    IntentsHelper.startActivity(this, T::class.java, params)

inline fun <reified T : SBaseActivity> Fragment.startActivity(vararg params: Pair<String, Any?>) =
    context?.let { IntentsHelper.startActivity(it, T::class.java, params) }

inline fun <reified T : SBaseActivity> Activity.startActivityForResult(
    requestCode: Int, vararg params: Pair<String, Any?>
) = IntentsHelper.startActivityForResult(this, T::class.java, params, requestCode)

inline fun <reified T : SBaseActivity> Fragment.startActivityForResult(
    requestCode: Int, vararg params: Pair<String, Any?>
) = IntentsHelper.startActivityForResult(activity as Activity, T::class.java, params, requestCode)

fun Activity.finishResult(requestCode: Int, vararg params: Pair<String, Any?>) {
    setResult(requestCode, IntentsHelper.createIntent(params))
    finish()
}

fun Activity.finishResultOK(vararg params: Pair<String, Any?>) =
    finishResult(Activity.RESULT_OK, *params)

inline fun <reified T : SBaseService> Context.startService(vararg params: Pair<String, Any?>) =
    IntentsHelper.startService(this, T::class.java, params)

inline fun <reified T : SBaseService> Fragment.startService(vararg params: Pair<String, Any?>) =
    context?.let { IntentsHelper.startService(it, T::class.java, params) }

inline fun <reified T : SBaseService> Context.stopService(vararg params: Pair<String, Any?>) =
    IntentsHelper.stopService(this, T::class.java, params)

inline fun <reified T : SBaseService> Fragment.stopService(vararg params: Pair<String, Any?>) =
    context?.let { IntentsHelper.stopService(it, T::class.java, params) }

inline fun <reified T : SBaseService> Context.bindingService(
    flag: Int, vararg params: Pair<String, Any?>,
    noinline onConnected: (name: ComponentName, service: T) -> Unit,
    noinline onDisconnected: (name: ComponentName) -> Unit
) = SBaseServiceConnection<T>().apply {
    IntentsHelper.bindService(this@bindingService, T::class.java, this, flag, params)
    this.setOnConnected(onConnected)
    this.setOnDisconnected(onDisconnected)
}

inline fun <reified T : SBaseService> Fragment.bindingService(
    flag: Int, vararg params: Pair<String, Any?>,
    noinline onConnected: (name: ComponentName, service: T) -> Unit,
    noinline onDisconnected: (name: ComponentName) -> Unit
) = SBaseServiceConnection<T>().apply {
    context?.let {
        IntentsHelper.bindService(it, T::class.java, this, flag, params)
        this.setOnConnected(onConnected)
        this.setOnDisconnected(onDisconnected)
    }
}

fun Fragment.unbindService(conn: ServiceConnection) =
    IntentsHelper.unbindService(activity as Activity, conn)

fun ISBaseFragment.setArguments(vararg params: Pair<String, Any?>): Fragment =
    (this as Fragment).apply {
        this.arguments = Bundle().apply {
            if (params.isNotEmpty()) params.forEach { (name, value) ->
                BundleHelper.putValue(this, name, value)
            }
        }
    }

object IntentsHelper {
    fun startActivity(
        context: Context, activity: Class<out Activity>, params: Array<out Pair<String, Any?>>
    ) {
        context.startActivity(createIntent(context, activity, params))
    }

    fun startActivityForResult(
        context: Activity, activity: Class<out Activity>,
        params: Array<out Pair<String, Any?>>, requestCode: Int
    ) = context.startActivityForResult(createIntent(context, activity, params), requestCode)

    fun startActivityForResult(
        fragment: Fragment, activity: Class<out Activity>,
        params: Array<out Pair<String, Any?>>, requestCode: Int
    ) = fragment.startActivityForResult(
        fragment.context?.let { createIntent(it, activity, params) }, requestCode
    )

    fun startService(
        context: Context, service: Class<out Service>, params: Array<out Pair<String, Any?>>
    ) = context.startService(createIntent(context, service, params))

    fun stopService(
        context: Context, service: Class<out Service>, params: Array<out Pair<String, Any?>>
    ) = context.stopService(createIntent(context, service, params))

    fun bindService(
        context: Context, service: Class<out Service>,
        conn: ServiceConnection, flag: Int, params: Array<out Pair<String, Any?>>
    ) = context.bindService(createIntent(context, service, params), conn, flag)

    fun unbindService(context: Context, conn: ServiceConnection) = context.unbindService(conn)

    private fun <T> createIntent(
        context: Context, clazz: Class<out T>, params: Array<out Pair<String, Any?>>
    ) = createIntent(params).apply {
        setClass(context, clazz)
    }

    fun createIntent(params: Array<out Pair<String, Any?>>) = Intent().apply {
        if (params.isNotEmpty()) params.forEach { (name, value) ->
            ExtrasHelper.putExtra(this, name, value)
        }
    }
}
