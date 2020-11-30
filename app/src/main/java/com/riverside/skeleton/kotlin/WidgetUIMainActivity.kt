package com.riverside.skeleton.kotlin

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.DateUtils
import com.riverside.skeleton.kotlin.util.converter.hourOfDay
import com.riverside.skeleton.kotlin.util.converter.minute
import com.riverside.skeleton.kotlin.util.converter.toString
import com.riverside.skeleton.kotlin.util.extras.startActivity
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.widget.datepicker.DatePickerFragment
import com.riverside.skeleton.kotlin.widget.web.WebBrowserActivity
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import java.util.*

class WidgetUIMainActivity :SBaseActivity() {
    override fun initView() {
        title = "Widget UI"

        verticalLayout {
            lparams(matchParent, matchParent)

            button("Web Browser") {
                onClick {
                    startActivity<WebBrowserActivity>(
                        WebBrowserActivity.LOCATION to "https://www.baidu.com"
                    )
                }
            }.lparams(matchParent, wrapContent)

            button("Date Picker") {
                onClick {
                    DatePickerFragment.Creator(supportFragmentManager)
                        .setDate(Calendar.getInstance().apply { set(1989, 4, 15, 5, 21) })
                        .showDate()
                        .showTime()
                        .setOnCancelled { "取消".toast(activity) }
                        .setOnDateTimeRecurrenceSet { selectedDate, hourOfDay, minute, recurrenceOption, recurrenceRule ->
                            selectedDate.firstDate.apply {
                                this.hourOfDay = hourOfDay
                                this.minute = minute
                            }.toString(DateUtils.DATE_FORMAT_PATTERN2)
                                .toast(activity)
                        }
                        .show()
                }
            }.lparams(matchParent, wrapContent)
        }
    }
}