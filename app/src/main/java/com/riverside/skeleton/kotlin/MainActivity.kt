package com.riverside.skeleton.kotlin

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.base.utils.collectinfo.AppVersionInfo
import com.riverside.skeleton.kotlin.slog.SLog
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : SBaseActivity() {
    override fun initView() {
        verticalLayout {
            lparams(matchParent, matchParent)

            button("util") {
                onClick {
                    startActivity<UtilMainActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("base") {
                onClick {
//                    startApp(sdcardBasePath + +"Notifications/Rain.mp3")
//                    SLog.w(CollectInfoHelper.collectInfo)
                    startActivity<BaseMainActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("net") {
                onClick {
                    startActivity<NetMainActivity>()
                }
            }
        }

        SLog.w(AppVersionInfo.info(this@MainActivity).getString("versionName"))
    }
}