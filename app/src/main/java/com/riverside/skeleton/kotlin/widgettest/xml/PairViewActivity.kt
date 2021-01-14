package com.riverside.skeleton.kotlin.widgettest.xml

import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.dip
import kotlinx.android.synthetic.main.activity_pair_view.*

class PairViewActivity : SBaseActivity() {
    override val layoutId = R.layout.activity_pair_view

    override fun initView() {
        title = "PairView"

        pv_2.createSecondImage(resources.getDrawable(R.mipmap.ic_launcher), 16.dip)
        pv_2.createFirstText("aa", R.style.PairViewFirstText)
    }
}