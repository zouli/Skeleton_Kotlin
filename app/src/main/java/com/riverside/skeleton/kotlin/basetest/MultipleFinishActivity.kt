package com.riverside.skeleton.kotlin.basetest

import android.view.Gravity
import android.widget.TextView
import com.riverside.skeleton.kotlin.base.activity.ActivityStackManager
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.extras.startActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MultipleFinishActivity : SBaseActivity() {
    lateinit var tv_title: TextView

    override fun initView() {
        title = "Multiple Finish"

        verticalLayout {
            lparams(matchParent, matchParent)

            button("open") {
                onClick {
                    startActivity<MultipleFinishActivity>()
                }
            }.lparams(matchParent, wrapContent)

            button("all close") {
                onClick {
                    ActivityStackManager.doMultipleFinish()
                }
            }.lparams(matchParent, wrapContent)

            tv_title = textView {
                gravity = Gravity.CENTER
            }.lparams(matchParent, matchParent)
        }

        tv_title.text = ActivityStackManager.countActivity.toString()
    }
}