package com.riverside.skeleton.kotlin.base.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.*
import androidx.fragment.app.DialogFragment
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.extras.BundleHelper
import com.riverside.skeleton.kotlin.util.extras.IntentsHelper
import com.riverside.skeleton.kotlin.util.resource.hashCode16

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
        arguments?.let { BundleHelper.bundle[hashCode()] = it }
        return setView(container) ?: if (layoutId != 0) inflater.inflate(
            layoutId, container, false
        ) else null
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
    val callbacks = SparseArray<ResultCallback>()

    inline fun <reified T : Activity> startActivityForResult(
        vararg params: Pair<String, Any?>,
        noinline callback: ResultCallback
    ) {
        callbacks.put(callback.hashCode16(), callback)
        IntentsHelper.startActivityForResult(this, T::class.java, params, callback.hashCode16())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbacks[requestCode]?.let {
            it(resultCode, data)
            callbacks.remove(requestCode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BundleHelper.bundle.remove(hashCode())
    }
}