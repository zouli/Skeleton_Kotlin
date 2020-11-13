package com.riverside.skeleton.kotlin.base.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity

/**
 * Fragment基类 1.0
 * <p>
 * b_e  2019/09/23
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
}