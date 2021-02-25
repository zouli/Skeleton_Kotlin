package com.riverside.skeleton.kotlin.widgettest.xml

import android.os.Build
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.dialog.alert
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.widget.selector.AddressSelectItem
import com.riverside.skeleton.kotlin.widget.selector.AddressSelectorView
import kotlinx.android.synthetic.main.activity_address_selector_view.*

class AddressSelectorViewActivity : SBaseActivity() {
    override val layoutId = R.layout.activity_address_selector_view

    private lateinit var selectedPosition: List<AddressSelectItem>

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initView() {
        title = "AddressSelectorView"

        asv.setSelectedData(
            listOf(
                AddressSelectItem("1", "1省"),
                AddressSelectItem("13", "13市"),
                AddressSelectItem("132", "132市")
            )
        )

        asv.setOnGetListListener { position, parent ->
            SLog.w("position=$position")
            asv.setListData(parent?.let {
                (0..9).map {
                    AddressSelectItem("${parent.code}${it}", "${parent.code}${it}市", parent)
                }
            } ?: (0..9).map { AddressSelectItem("$it", "${it}省") })
        }

        asv.setOnSelectIsOver {
            it.toString().toast(this)
        }

        btn_alert.setOnClickListener {
            alert {
                val alert = this
                val view = LinearLayout(this@AddressSelectorViewActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    addView(AddressSelectorView(this@AddressSelectorViewActivity).apply {
                        layoutParams =
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                300.dip
                            )

                        if (::selectedPosition.isInitialized) this.setSelectedData(selectedPosition)

                        this.setOnGetListListener { position, parent ->
                            position.toString().toast(activity)
                            this.setListData(parent?.let {
                                (0..9).map {
                                    AddressSelectItem(
                                        "${parent.code}${it}", "${parent.code}${it}市", parent
                                    )
                                }
                            } ?: (0..9).map { AddressSelectItem("$it", "${it}省") })
                        }
                        this.setOnSelectIsOver {
                            SLog.w(it)
                            selectedPosition = it
                            alert.dismiss()
                        }
                    })
                }

                centerTitle = "所在地"
                customView = view
                cancelButton { dialog, which ->

                }
                showAtBottom()
            }.show()
        }
    }
}