package com.riverside.skeleton.kotlin.basetest

import android.view.Gravity
import android.widget.TextView
import com.riverside.skeleton.kotlin.base.fragment.SBaseFragment
import com.riverside.skeleton.kotlin.base.rxbus.RxBus
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class RxBusFragment : SBaseFragment() {
    lateinit var tv_title: TextView
    lateinit var bus: Disposable

    override fun setView() =
        UI {
            verticalLayout {
                lparams(matchParent, matchParent)
                tv_title = textView {
                    gravity = Gravity.CENTER
                }.lparams(matchParent, wrapContent)
            }
        }.view

    override fun initView() {
        bus = RxBus.register<String>(next = {
            tv_title.text = it
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RxBus.unRegister(bus)
    }
}