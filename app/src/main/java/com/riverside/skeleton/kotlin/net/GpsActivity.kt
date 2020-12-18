package com.riverside.skeleton.kotlin.net

import android.Manifest
import android.annotation.SuppressLint
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.base.utils.permission.hasPermissions
import com.riverside.skeleton.kotlin.util.gps.GpsHelper
import com.riverside.skeleton.kotlin.util.notice.toast
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class GpsActivity : SBaseActivity() {
    private lateinit var gps: GpsHelper

    @SuppressLint("MissingPermission")
    override fun initView() {
        title = "Gps"
        gps = GpsHelper(activity)

        verticalLayout {
            lparams(matchParent, matchParent)

            button("Open Gps") {
                onClick {
                    if (hasPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        gps.openLocation()
                    }
                }
            }.lparams(matchParent, wrapContent)

            button("Get Gps") {
                onClick {
                    gps.location.toString().toast(activity)
                }
            }.lparams(matchParent, wrapContent)

            button("Close Gps") {
                onClick {
                    if (hasPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        gps.closeLocaion()
                    }
                }
            }.lparams(matchParent, wrapContent)
        }
    }
}