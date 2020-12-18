package com.riverside.skeleton.kotlin

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.net.BluetoothActivity
import com.riverside.skeleton.kotlin.net.GpsActivity
import com.riverside.skeleton.kotlin.net.WifiActivity
import com.riverside.skeleton.kotlin.net.rest.CommonRestService
import com.riverside.skeleton.kotlin.net.rest.checkResult
import com.riverside.skeleton.kotlin.net.rest.utils.checkSessionTimeout
import com.riverside.skeleton.kotlin.net.rest.utils.iterate
import com.riverside.skeleton.kotlin.net.rest.utils.next
import com.riverside.skeleton.kotlin.net.rest.utils.retrofit
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.extras.startActivity
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class NetMainActivity : SBaseActivity() {

    override fun initView() {
        title = "Net"

        verticalLayout {
            lparams(matchParent, matchParent)

            button("login") {
                onClick {
                    retrofit<CommonRestService>()
                        .login()
                        .next { checkResult() }
                        .subscribe({}, { t -> SLog.e("onError", t) })
                }
            }.lparams(matchParent, wrapContent)

            button("logout") {
                onClick {
                    retrofit<CommonRestService>()
                        .logout()
                        .next { checkResult() }
                        .subscribe { }
                }
            }.lparams(matchParent, wrapContent)

            button("session_timeout") {
                onClick {
                    retrofit<CommonRestService>()
                        .sessionTimeout()
                        .next {
                            checkResult().checkSessionTimeout()
                        }
                        .subscribe { }
                }
            }.lparams(matchParent, wrapContent)

            button("get_list") {
                onClick {
                    retrofit<CommonRestService>()
                        .getList(mapOf())
                        .next { checkResult().iterate() }
                        .subscribe { SLog.w(it) }
                }
            }.lparams(matchParent, wrapContent)

            button("retry") {
                onClick {
                    retrofit<CommonRestService>()
                        .retry()
                        .next { checkResult() }
                        .subscribe { }
                }
            }.lparams(matchParent, wrapContent)

            button("Wifi") {
                onClick {
                    startActivity<WifiActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("Gps") {
                onClick {
                    startActivity<GpsActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("Bluetooth") {
                onClick {
                    startActivity<BluetoothActivity>()
                }
            }.lparams(matchParent, wrapContent)
        }
    }
}