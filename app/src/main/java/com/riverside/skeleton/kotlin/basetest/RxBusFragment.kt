package com.riverside.skeleton.kotlin.basetest

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.riverside.skeleton.kotlin.base.eventbus.EventBus
import com.riverside.skeleton.kotlin.base.eventbus.EventConsumer
import com.riverside.skeleton.kotlin.base.fragment.SBaseFragment
import com.riverside.skeleton.kotlin.util.extras.FragmentArgument
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class RxBusFragment : SBaseFragment() {
    lateinit var tv_title: TextView

    //    lateinit var bus: Disposable
    lateinit var bus: EventConsumer<String>
    lateinit var busS: EventConsumer<String>

    private val value: Int by FragmentArgument(default = -1)

    override fun setView(container: ViewGroup?): View? =
        UI {
            verticalLayout {
                lparams(matchParent, matchParent)
                tv_title = textView {
                    gravity = Gravity.CENTER
                }.lparams(matchParent, wrapContent)
            }
        }.view

    override fun initView() {
        tv_title.text = value.toString()

//        bus = RxBus.register<String> {
//            tv_title.text = it
//        }
        bus = EventBus.register {
            tv_title.text = it
        }

        busS = EventBus.registerSticky {
            tv_title.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        RxBus.unRegister(bus)
        EventBus.unregister(bus)
        EventBus.unregister(busS)
    }
}