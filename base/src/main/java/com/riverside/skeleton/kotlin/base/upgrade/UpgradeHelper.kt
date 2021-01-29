package com.riverside.skeleton.kotlin.base.upgrade

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.base.application.SBaseApplication
import com.riverside.skeleton.kotlin.base.fragment.resultRequest
import com.riverside.skeleton.kotlin.base.utils.permission.requestPermissions

/**
 * 自动升级服务   1.0
 * b_e  2019/09/26
 */
class UpgradeHelper(val activity: FragmentActivity) {
    private val intent = Intent(activity, UpgradeService().javaClass)
    private lateinit var title: String
    private lateinit var message: String
    private lateinit var downloadUrl: String

    /**
     * 开始升级
     */
    fun startUpgrade(title: String, message: String, downloadUrl: String) {
        this.title = title
        this.message = message
        this.downloadUrl = downloadUrl
        checkPermission()
    }

    /**
     * 验证安装权限
     */
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (SBaseApplication.instance.packageManager.canRequestPackageInstalls()) {
                doUpgrade()
            } else {
                //跳转至“安装未知应用”权限界面，引导用户开启权限
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${activity.packageName}")
                )

                activity.resultRequest(intent) { _, _ ->
                    checkPermission()
                }
            }
        } else {
            doUpgrade()
        }
    }

    /**
     * 开始下载APK
     */
    private fun doUpgrade() {
        (activity as SBaseActivity).requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            onGranted {
                intent.action = UpgradeService.ACTION_DOWNLOAD_URL
                intent.putExtra(UpgradeService.TITLE_EXTRA, title)
                intent.putExtra(UpgradeService.MESSAGE_EXTRA, message)
                intent.putExtra(UpgradeService.DOWNLOAD_URL_EXTRA, downloadUrl)
                activity.startService(intent)
            }
        }
    }

    /**
     * 停止升级
     */
    fun stopUpgrade() {
        activity.stopService(intent)
    }
}