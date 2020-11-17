package com.riverside.skeleton.kotlin.widgettest.anko

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import org.jetbrains.anko.*

class ListItemCheckableLinearLayout<T> : AnkoComponent<T> {
    override fun createView(ui: AnkoContext<T>): View =
        ui.ctx.checkableLinearLayout {
            lparams(matchParent, wrapContent)
            orientation = LinearLayout.HORIZONTAL
            checkedChild = true

            radioButton {
                isClickable = false
                isDuplicateParentStateEnabled = true
                isFocusable = false
                isFocusableInTouchMode = false
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.CENTER
            }

            textView {
                id = TV_TEXT
                isClickable = false
            }.lparams(matchParent, wrapContent) {
                gravity = Gravity.CENTER
            }
        }

    companion object ID {
        const val TV_TEXT = 101
    }
}