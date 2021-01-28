package com.riverside.skeleton.kotlin.widgettest.anko

import android.os.Build
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.resource.getDrawableById
import org.jetbrains.anko.*

class PairViewActivity : SBaseActivity() {
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initView() {
        title = "PairView"

        verticalLayout {
            lparams(matchParent, matchParent)

            linearLayout {
                orientation = LinearLayout.HORIZONTAL

                pairView {
                    orientation = LinearLayout.VERTICAL
                    background = getDrawableById(R.drawable.imagegrid_delete_image)
                    padding = 32.dip
                    createFirstText("AA", R.style.PairViewFirstText)
                    createSecondText("BB", R.style.PairViewSecondText)
                }.lparams(wrapContent, wrapContent, 1.0f)

                pairView {
                    orientation = LinearLayout.VERTICAL
                    padding = 32.dip
                    createSecondImage(getDrawableById(R.mipmap.ic_launcher)!!, 16.dip)
                    createFirstText("aa", R.style.PairViewFirstText)
                }.lparams(wrapContent, wrapContent, 1.0f)

                pairView {
                    orientation = LinearLayout.VERTICAL
                    padding = 32.dip
                    createFirstImage(
                        getDrawableById(R.mipmap.ic_launcher)!!,
                        48.dip, R.style.PairViewFirstText
                    )
                    createSecondText("BB", R.style.PairViewSecondText)
                }.lparams(wrapContent, wrapContent, 1.0f)
            }.lparams(matchParent, wrapContent)
        }
    }
}