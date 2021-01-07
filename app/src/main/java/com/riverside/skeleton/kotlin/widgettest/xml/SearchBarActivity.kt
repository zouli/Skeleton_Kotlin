package com.riverside.skeleton.kotlin.widgettest.xml

import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.notice.toast
import kotlinx.android.synthetic.main.activity_search_bar.*

class SearchBarActivity : SBaseActivity() {
    override val layoutId = R.layout.activity_search_bar

    override fun initView() {
        title = "SearchBar"

        sb_search.setOnCancelListener {
            "取消".toast(activity)
        }

        sb_search.setOnSearchListener {
            it.toast(activity)
        }
    }
}