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
            }.lparams(matchParent, wrapContent)

            button("widget anko") {
                onClick {
                    startActivity<WidgetAnkoMainActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("widget xml") {
                onClick {
                    startActivity<WidgetXmlMainActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("widget UI") {
                onClick {
                    startActivity<WidgetUIMainActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("db") {
                onClick {
                    startActivity<DbMainActivity>()
                }
            }.lparams(matchParent, wrapContent)
        }

        SLog.w(AppVersionInfo.info(this@MainActivity).getString("versionName"))
    }
}