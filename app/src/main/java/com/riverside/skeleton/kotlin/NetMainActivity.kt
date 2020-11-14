package com.riverside.skeleton.kotlin

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.net.rest.CommonRestService
import com.riverside.skeleton.kotlin.net.rest.checkResult
import com.riverside.skeleton.kotlin.net.rest.utils.*
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
                    retrofit(CommonRestService::class)
                        .login()
                        .next { checkResult() }
                        .subscribe({}, { t -> SLog.e("onError", t) })
                }
            }.lparams(matchParent, wrapContent)

            button("logout") {
                onClick {
                    retrofit(CommonRestService::class)
                        .logout()
                        .next { checkResult() }
                        .subscribe { }
                }
            }.lparams(matchParent, wrapContent)

            button("session_timeout") {
                onClick {
                    retrofit(CommonRestService::class)
                        .sessionTimeout()
                        .next {
                            checkResult().checkSessionTimeout()
                        }
                        .subscribe { }
                }
            }.lparams(matchParent, wrapContent)

            button("get_list") {
                onClick {
                    retrofit(CommonRestService::class)
                        .getList(HashMap())
                        .next { checkResult().iterate() }
                        .subscribe { SLog.w(it) }
                }
            }.lparams(matchParent, wrapContent)

            button("retry") {
                onClick {
                    retrofit(CommonRestService::class)
                        .retry()
                        .next { checkResult() }
                        .subscribe { }
                }
            }.lparams(matchParent, wrapContent)
        }
    }
}