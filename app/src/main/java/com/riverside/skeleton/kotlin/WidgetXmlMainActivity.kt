package com.riverside.skeleton.kotlin

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.extras.startActivity
import com.riverside.skeleton.kotlin.widgettest.xml.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class WidgetXmlMainActivity : SBaseActivity() {
    override fun initView() {
        title = "Widget XML"

        scrollView {
            verticalLayout {
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

                button("SearchBar") {
                    onClick {
                        startActivity<SearchBarActivity>()
                    }
                }

                button("PairView") {
                    onClick {
                        startActivity<PairViewActivity>()
                    }
                }.lparams(matchParent, wrapContent)

                button("ImageGridViewList") {
                    onClick {
                        startActivity<RefreshImageGridViewActivity>()
                    }
                }.lparams(matchParent, wrapContent)

                button("AddressSelectorView") {
                    onClick {
                        startActivity<AddressSelectorViewActivity>()
                    }
                }
            }.lparams(matchParent, matchParent)
        }
    }
}