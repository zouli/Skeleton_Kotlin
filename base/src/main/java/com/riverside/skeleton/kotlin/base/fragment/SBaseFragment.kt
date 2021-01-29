package com.riverside.skeleton.kotlin.base.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.*
import androidx.fragment.app.Fragment
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.extras.BundleHelper
import com.riverside.skeleton.kotlin.util.extras.IntentsHelper
import com.riverside.skeleton.kotlin.util.resource.hashCode16

/**
 * Fragment基类 1.0
 * <p>
 * b_e                      2019/09/23
 * 封装ForResult            2020/11/26
 * 添加ISBaseFragment接口   2020/11/29
 */
abstract class SBaseFragment : Fragment(), ISBaseFragment {
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
        noinline callback: (resultCode: Int, intent: Intent?) -> Unit
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

interface ISBaseFragment {
    val layoutId: Int
    fun setView(container: ViewGroup?): View?
    fun initView()
    val menuId: Int
    val sBaseActivity: SBaseActivity
}