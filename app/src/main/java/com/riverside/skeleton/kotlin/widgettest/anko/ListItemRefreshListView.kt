package com.riverside.skeleton.kotlin.widgettest.anko

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import com.riverside.skeleton.kotlin.util.converter.dip
import org.jetbrains.anko.*

class ListItemRefreshListView<T> : AnkoComponent<T> {
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun createView(ui: AnkoContext<T>): View =
        ui.ctx.verticalLayout {
            lparams(matchParent, wrapContent)
            background = ColorDrawable(0xffffffff.toInt())

            textView {
                id = TV_TITLE
                gravity = Gravity.CENTER
                padding = 16.dip
                textColor = "#333333".toColorInt()
                textSize = 14F
            }.lparams(matchParent, wrapContent)
        }

    companion object ID {
        const val TV_TITLE = 102
    }
}