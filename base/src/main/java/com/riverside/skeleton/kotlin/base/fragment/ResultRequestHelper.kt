package com.riverside.skeleton.kotlin.base.fragment

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.riverside.skeleton.kotlin.util.resource.hashCode16

/**
 * Result请求帮助类  1.0
 *
 * b_e  2021/01/28
 */
class ResultRequestHelper(activity: FragmentActivity) {
    private val fragment = activity.let { act ->
        act.supportFragmentManager.let { fm ->
            fm.findFragmentByTag(OnResultEventFragment.TAG)
                ?: OnResultEventFragment().apply {
                    fm.beginTransaction()
                        .add(this, OnResultEventFragment.TAG)
                        .commitNowAllowingStateLoss()
                    fm.executePendingTransactions()
                }
        } as OnResultEventFragment
    }

    fun startForResult(intent: Intent, callback: ResultCallback) {
        fragment.startForResult(intent, callback)
    }
}

/**
 * Result接受Fragment
 */
class OnResultEventFragment : Fragment() {
    private val callbacks = SparseArray<ResultCallback>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun startForResult(intent: Intent, callback: ResultCallback) {
        callbacks.put(callback.hashCode16(), callback)
        startActivityForResult(intent, callback.hashCode16())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbacks[requestCode]?.let {
            it(resultCode, data)
            callbacks.remove(requestCode)
        }
    }

    companion object {
        const val TAG = "OnResultEventFragment"
    }
}

/**
 * Result回调事件
 */
typealias ResultCallback = (resultCode: Int, data: Intent?) -> Unit

fun FragmentActivity.resultRequest(intent: Intent, callback: ResultCallback) {
    ResultRequestHelper(this).startForResult(intent, callback)
}