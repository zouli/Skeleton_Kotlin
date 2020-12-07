package com.riverside.skeleton.kotlin.basetest

import android.Manifest
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.base.utils.collectinfo.TelephonyInfo
import com.riverside.skeleton.kotlin.base.utils.permission.requestPermissions
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.notice.toast
import kotlinx.android.synthetic.main.activity_permissions.*

class PermissionsActivity : SBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_permissions

    override fun initView() {
        title = "Permissions"

        SLog.w(TelephonyInfo.info(activity).toJSONString())

        btn_es.setOnClickListener {
            requestPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ) {
                onGranted { perms ->
                    "同意$perms".toast(activity)
                }
                onDenied { perms ->
                    "拒绝$perms".toast(activity)
                }
            }
//                EasyPermissions.requestPermissions(
//                    this, "请开启存储权限", 1,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
        }
    }
}