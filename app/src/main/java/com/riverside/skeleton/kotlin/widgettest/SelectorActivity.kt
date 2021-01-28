package com.riverside.skeleton.kotlin.widgettest

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.util.extras.finishResult
import com.riverside.skeleton.kotlin.util.image.tintDrawable
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.util.resource.getDrawableById
import com.riverside.skeleton.kotlin.widget.adapter.SelectorListViewAdapter
import com.riverside.skeleton.kotlin.widget.selector.SBaseSelectorActivity
import org.jetbrains.anko.textColor

class SelectorActivity : SBaseSelectorActivity() {
    override fun initView() {
        toolbar.title = "Selector"
        toolbar.setNavigation(R.drawable.web_toolbar_back) {
            finishResult(Activity.RESULT_CANCELED)
        }

        super.initView()

        val data = listOf("aba", "bcb", "cdc", "ded")
        adapter.addItems(data)
        adapter.addItem("bcb")
        adapter.addItem("eae")

        val emptyText = TextView(activity).apply {
            text = "Empty Text"
            textSize = 18F
            textColor = "#999999".toColorInt()
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }

        setEmptyView(emptyText)
    }

    override val choiceMode = ListView.CHOICE_MODE_MULTIPLE

    override fun onSubmitClick(checkedItemIds: List<Int>) {
        checkedItemIds.toString().toast(activity)
        finish()
    }

    @SuppressLint("DefaultLocale")
    override val adapter = SelectorListViewAdapter<String>(
        R.layout.list_item_checkable_linearlayout
    ) { viewHolder, _, item, _ ->
        viewHolder.setText(R.id.tv_text, item)
        viewHolder.setImageDrawable(
            R.id.iv_image,
            getDrawableById(R.mipmap.ic_launcher)!!.tintDrawable(R.color.checkable_image_selector)
        )
    }.apply {
        filter { item, prefix ->
            item.indexOf(prefix.toString().toLowerCase()) > -1
        }
    }
}