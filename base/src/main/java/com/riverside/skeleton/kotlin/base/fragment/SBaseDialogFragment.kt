package com.riverside.skeleton.kotlin.base.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.extras.BundleHelper
import com.riverside.skeleton.kotlin.util.extras.IntentsHelper

/**
 * DialogFragment基类 1.0
 * <p>
 * b_e      2020/11/29
 */
abstract class SBaseDialogFragment : DialogFragment(), ISBaseFragment {
    override val sBaseActivity: SBaseActivity get() = activity as SBaseActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        menuId.takeIf { it != 0 }?.let { setHasOptionsMenu(true) }
        arguments?.let { BundleHelper.bundle = it }
        return setView(container) ?: inflater.inflate(layoutId, container)
    }

    override val layoutId: Int get() = 0

    override fun setView(container: ViewGroup?): View? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView()
    }

    abstract override fun initView()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuId.takeIf { it != 0 }?.let {
            inflater.inflate(it, menu)
            super.onCreateOptionsMenu(menu, inflater)
        }
    }

    override val menuId: Int get() = 0

    /**
     * 封装ForResult
     */
    var callbackIndex = mutableListOf<String>()
    var callbackList = mutableMapOf<Int, (resultCode: Int, intent: Intent?) -> Unit>()

    inline fun <reified T : Activity> startActivityForResult(
        vararg params: Pair<String, Any?>,
        noinline callback: (resultCode: Int, intent: Intent?) -> Unit
    ) {
        var index = callbackIndex.indexOf(T::class.java.toString())
        if (index < 0) {
            callbackIndex.add(T::class.java.toString())
            index = callbackIndex.size - 1
            callbackList[index] = callback
        }
        IntentsHelper.startActivityForResult(this, T::class.java, params, index)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackList[requestCode]?.let { it(resultCode, data) }
    }
}