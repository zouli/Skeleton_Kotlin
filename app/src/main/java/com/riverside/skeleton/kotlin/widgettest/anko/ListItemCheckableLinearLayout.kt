package com.riverside.skeleton.kotlin.widgettest.anko

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.util.converter.dip
import org.jetbrains.anko.*

class ListItemCheckableLinearLayout<T> : AnkoComponent<T> {
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun createView(ui: AnkoContext<T>): View =
        ui.ctx.checkableLinearLayout {
            lparams(matchParent, wrapContent)
            orientation = LinearLayout.HORIZONTAL
            childCheckable = true
            background = ColorDrawable(0xFFFFFFFF.toInt())

            radioButton {
                isClickable = false
                isDuplicateParentStateEnabled = true
                isFocusable = false
                isFocusableInTouchMode = false
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.CENTER
            }

            imageView {
                id = IV_IMAGE
                adjustViewBounds = true
            }.lparams(16.dip, 16.dip) {
                gravity = Gravity.CENTER
            }

            textView {
                id = TV_TEXT
                isClickable = false
            }.lparams(matchParent, wrapContent) {
                gravity = Gravity.CENTER
                leftMargin = 4.dip
            }
        }

    companion object ID {
        const val TV_TEXT = 101
        const val IV_IMAGE = 102
    }
}