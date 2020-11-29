package com.riverside.skeleton.kotlin

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.DateUtils
import com.riverside.skeleton.kotlin.util.converter.toString
import com.riverside.skeleton.kotlin.util.extras.startActivity
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.widget.datepicker.DatePickerFragment
import com.riverside.skeleton.kotlin.widget.web.WebBrowserActivity
import com.riverside.skeleton.kotlin.widgettest.xml.*
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import java.util.*

class WidgetXmlMainActivity : SBaseActivity() {
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

            button("Web Browser") {
                onClick {
                    startActivity<WebBrowserActivity>(
                        WebBrowserActivity.LOCATION to "https://www.baidu.com"
                    )
                }
            }.lparams(matchParent, wrapContent)

            button("Date Picker") {
                onClick {
                    DatePickerFragment.Create(supportFragmentManager)
                        .setDate(Calendar.getInstance().apply { set(1989, 4, 15, 5, 21) })
                        .showDate()
                        .showTime()
                        .setOnCancelled { "取消".toast(activity) }
                        .setOnDateTimeRecurrenceSet { selectedDate, hourOfDay, minute, recurrenceOption, recurrenceRule ->
                            selectedDate.firstDate.toString(DateUtils.DATE_FORMAT_PATTERN2)
                                .toast(activity)
                        }
                        .show()
                }
            }.lparams(matchParent, wrapContent)
        }
    }
}