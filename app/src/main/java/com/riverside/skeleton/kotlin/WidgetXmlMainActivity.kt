package com.riverside.skeleton.kotlin

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.widgettest.xml.CaptchaActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class WidgetXmlMainActivity : SBaseActivity() {
    override fun initView() {
        verticalLayout {
            lparams(matchParent, matchParent)
            button("captcha") {
                onClick {
                    startActivity<CaptchaActivity>()
                }
            }.lparams(matchParent, wrapContent)
        }

    }
}