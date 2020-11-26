package com.riverside.skeleton.kotlin.base.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.extras.IntentsHelper

/**
 * Fragment基类 1.0
 * <p>
 * b_e            2019/09/23
 * 封装ForResult  2020/11/26
 */
abstract class SBaseFragment : Fragment() {
    lateinit var sBaseActivity: SBaseActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setMenuID().takeIf { it != 0 }?.let { setHasOptionsMenu(true) }
        return setView() ?: inflater.inflate(setLayoutID(), null)
    }

    open fun setLayoutID(): Int = 0

    open fun setView(): View? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sBaseActivity = activity as SBaseActivity
        initView()
    }

    abstract fun initView()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        setMenuID().takeIf { it != 0 }?.let {
            inflater.inflate(it, menu)
            super.onCreateOptionsMenu(menu, inflater)
        }
    }

    open fun setMenuID(): Int = 0

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