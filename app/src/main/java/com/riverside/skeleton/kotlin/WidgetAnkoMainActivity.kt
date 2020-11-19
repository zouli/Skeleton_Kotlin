package com.riverside.skeleton.kotlin

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.widgettest.anko.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class WidgetAnkoMainActivity : SBaseActivity() {
    override fun initView() {
        verticalLayout {
            lparams(matchParent, matchParent)
            button("captcha") {
                onClick {
                    startActivity<CaptchaActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("CheckableLinearLayout") {
                onClick {
                    startActivity<CheckableLinearLayoutActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("RecyclerView") {
                onClick {
                    startActivity<RecyclerViewActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("Refresh ListView") {
                onClick {
                    startActivity<RefreshListViewActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("Refresh GridView") {
                onClick {
                    startActivity<RefreshGridViewActivity>()
                }
            }.lparams(matchParent, wrapContent)
        }
    }
}