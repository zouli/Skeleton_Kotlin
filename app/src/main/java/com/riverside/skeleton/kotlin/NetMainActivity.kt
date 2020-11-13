package com.riverside.skeleton.kotlin

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.net.rest.CommonRestService
import com.riverside.skeleton.kotlin.net.rest.ObservableHelper2
import com.riverside.skeleton.kotlin.net.rest.utils.ObservableHelper
import com.riverside.skeleton.kotlin.net.rest.utils.RestSubscriber
import com.riverside.skeleton.kotlin.net.rest.utils.RetrofitBindHelper
import com.riverside.skeleton.kotlin.slog.SLog
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class NetMainActivity : SBaseActivity() {
    override fun initView() {
        verticalLayout {
            lparams(matchParent, matchParent)

            button("login") {
                onClick {
                    RetrofitBindHelper.getInstance().doBind(CommonRestService::class.java)
                        .login()
                        .compose(ObservableHelper.startReading())
                        .compose(ObservableHelper2.checkResult())
                        .compose(ObservableHelper.toMainThread())
                        .subscribe(object : RestSubscriber<String>() {
                            override fun onError(t: Throwable) {
                                SLog.e("onError", t)
                            }
                        })
                }
            }.lparams(matchParent, wrapContent)

            button("logout") {
                onClick {
                    RetrofitBindHelper.getInstance().doBind(CommonRestService::class.java)
                        .logout()
                        .compose(ObservableHelper.startReading())
                        .compose(ObservableHelper2.checkResult())
                        .compose(ObservableHelper.toMainThread())
                        .subscribe(object : RestSubscriber<String>() {

                        })
                }
            }.lparams(matchParent, wrapContent)

            button("session_timeout") {
                onClick {
                    RetrofitBindHelper.getInstance().doBind(CommonRestService::class.java)
                        .sessionTimeout()
                        .compose(ObservableHelper.startReading())
                        .compose(ObservableHelper2.checkResult())
                        .compose(ObservableHelper.checkSessionTimeout())
                        .compose(ObservableHelper.toMainThread())
                        .subscribe(object : RestSubscriber<String>() {

                        })
                }
            }.lparams(matchParent, wrapContent)

            button("get_list") {
                onClick {
                    RetrofitBindHelper.getInstance().doBind(CommonRestService::class.java)
                        .getList(HashMap())
                        .compose(ObservableHelper.startReading())
                        .compose(ObservableHelper2.checkResult())
                        .compose(ObservableHelper.forEach())
                        .compose(ObservableHelper.toMainThread())
                        .subscribe(object : RestSubscriber<String>() {
                            override fun onNext(t: String) {
                                SLog.w(t)
                            }
                        })
                }
            }.lparams(matchParent, wrapContent)

            button("retry") {
                onClick {
                    RetrofitBindHelper.getInstance().doBind(CommonRestService::class.java)
                        .retry()
                        .compose(ObservableHelper.startReading())
                        .compose(ObservableHelper2.checkResult())
                        .compose(ObservableHelper.toMainThread())
                        .subscribe(object : RestSubscriber<String>() {

                        })
                }
            }.lparams(matchParent, wrapContent)
        }
    }
}