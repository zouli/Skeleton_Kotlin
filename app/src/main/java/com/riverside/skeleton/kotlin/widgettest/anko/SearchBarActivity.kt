package com.riverside.skeleton.kotlin.widgettest.anko

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class SearchBarActivity : SBaseActivity() {
    override fun initView() {
        title = "SearchBar"

        verticalLayout {
            lparams(matchParent, matchParent)
            searchBar {
                setEditTextMarginStart(8.dip)
                isFocusable = false
            }.lparams(matchParent, wrapContent)
        }
    }
}