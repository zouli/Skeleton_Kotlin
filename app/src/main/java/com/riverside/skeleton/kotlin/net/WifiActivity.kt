package com.riverside.skeleton.kotlin.net

import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.base.utils.collectinfo.WifiInfo
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.util.wifi.WifiHelper
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class WifiActivity : SBaseActivity() {
    override fun initView() {
        title = "Wifi"

        SLog.w(WifiInfo.info(activity).toJSONString())

        val wifiHelper = WifiHelper(activity)
        wifiHelper.onStateChanged {
            SLog.w(it)
        }

        verticalLayout {
            lparams(matchParent, matchParent)

            button("Open Wifi") {
                onClick {
                    wifiHelper.openWifi()
                    wifiHelper.wifiState.toString().toast(activity)
                }
            }.lparams(matchParent, wrapContent)

            button("Close Wifi") {
                onClick {
                    wifiHelper.closeWifi()
                    wifiHelper.wifiState.toString().toast(activity)
                }
            }.lparams(matchParent, wrapContent)

            button("Configurations") {
                onClick {
                    SLog.w(wifiHelper.configurations)
                }
            }.lparams(matchParent, wrapContent)

            button("Scan Result") {
                onClick {
                    wifiHelper.scan {
                        SLog.w(it)
                    }
                }
            }.lparams(matchParent, wrapContent)

            button("Add Wifi") {
                onClick {
                    wifiHelper.addWifi(
                        "lianshengshixun",
                        "lssx123456",
                        WifiHelper.WifiCipherType.WPA
                    )
                    wifiHelper.switchWifi("lianshengshixun")
                }
            }.lparams(matchParent, wrapContent)

            button("remove Wifi") {
                onClick {
                    wifiHelper.disconnectWifi("lianshengshixun")
                    wifiHelper.switchWifi("联胜_5G")
                }
            }.lparams(matchParent, wrapContent)

            button("Open Wifi Ap") {
                onClick {
                    wifiHelper.openWifiAp("HUAWEI Mate 8", "123456")
                    wifiHelper.wifiApState.toString().toast(activity)
                }
            }.lparams(matchParent, wrapContent)

            button("Close Wifi Ap") {
                onClick {
                    wifiHelper.closeWifiAp()
                    wifiHelper.wifiApState.toString().toast(activity)
                }
            }.lparams(matchParent, wrapContent)
        }
    }
}