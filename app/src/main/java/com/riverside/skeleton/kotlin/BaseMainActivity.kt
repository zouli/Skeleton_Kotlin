package com.riverside.skeleton.kotlin

import android.os.Bundle
import android.view.KeyEvent
import android.widget.CheckBox
import android.widget.LinearLayout
import com.riverside.skeleton.kotlin.base.activity.ActivityStackManager
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.base.activity.TransferListener
import com.riverside.skeleton.kotlin.base.activity.activityStaticMonitor
import com.riverside.skeleton.kotlin.base.upgrade.UpgradeHelper
import com.riverside.skeleton.kotlin.base.utils.BackButtonHelper
import com.riverside.skeleton.kotlin.basetest.*
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.extras.startActivity
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.util.preference.Preference
import com.riverside.skeleton.kotlin.util.preference.PreferenceHelper
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class BaseMainActivity : SBaseActivity() {
    lateinit var cb_all: CheckBox
    var a: Int by Preference(default = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityStaticMonitor.addTransferListener(object : TransferListener {
            override fun onBecameForeground() {
                SLog.d("程序进入前台")
            }

            override fun onBecameBackground() {
                SLog.d("程序进入后台")
            }
        })

        SLog.w("SP:a=$a")
        a = 9
        SLog.w("SP:a=$a")
        PreferenceHelper.delete()
    }

    override fun initView() {
        title = "Base"

        scrollView {
            lparams(matchParent, matchParent)

            verticalLayout {
                lparams(matchParent, wrapContent)
                button("eventbus") {
                    onClick {
                        startActivity<RxBusActivity>()
                    }
                }.lparams(matchParent, wrapContent)

                button("validate anko") {
                    onClick {
                        startActivity<ValidateAnkoActivity>()
                    }
                }.lparams(matchParent, wrapContent)

                button("validate xml") {
                    onClick {
                        startActivity<ValidateXmlActivity>()
                    }
                }.lparams(matchParent, wrapContent)

                button("multiple finish") {
                    onClick {
                        ActivityStackManager.startMultipleFinish()
                        startActivity<MultipleFinishActivity>()
                    }
                }.lparams(matchParent, wrapContent)

                linearLayout {
                    orientation = LinearLayout.HORIZONTAL

                    button("to login") {
                        onClick {
                            ActivityStackManager.toLoginActivity(cb_all.isChecked)
                        }
                    }.lparams(0, wrapContent, 1.0F)

                    cb_all = checkBox("close all").lparams(wrapContent, wrapContent)
                }.lparams(matchParent, wrapContent)

                button("Crash") {
                    onClick {
                        throw Exception("aaaaaa")
                    }
                }

                button("upgrade") {
                    onClick {
                        val upgradeHelper = UpgradeHelper(activity)
                        upgradeHelper.startUpgrade(
                            "Test",
                            "Test Upgrade",
                            "http://www.915guo.com.cn/EXAM/upload/MedicalExam_CSTP.apk"
                        )
                    }
                }

                button("Extras") {
                    onClick {
                        startActivity<ExtrasActivity>()
                    }
                }

                button("Permissions") {
                    onClick {
                        startActivity<PermissionsActivity>()
                    }
                }

                button("Dialog") {
                    onClick {
                        startActivity<DialogActivity>()
                    }
                }
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return BackButtonHelper.onKeyUp(keyCode, event) {
            when (it) {
                1 -> "再按一次返回键，退出APP".toast(this)
                2 -> finish()
            }
        } || super.onKeyUp(keyCode, event)
    }
}