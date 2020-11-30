package com.riverside.skeleton.kotlin

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.extras.startActivity
import com.riverside.skeleton.kotlin.widgettest.anko.*
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class WidgetAnkoMainActivity : SBaseActivity() {
    override fun initView() {
        title = "Widget Anko"

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

            button("Complete View") {
                onClick {
                    startActivity<CompleteViewActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("ImageGridView") {
                onClick {
                    startActivity<ImageGridViewActivity>()
                }
            }.lparams(matchParent, wrapContent)
        }
    }
}