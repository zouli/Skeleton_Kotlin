package com.riverside.skeleton.kotlin.base.upgrade

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.riverside.skeleton.kotlin.base.activity.ActivityStackManager
import com.riverside.skeleton.kotlin.base.application.SBaseApplication
import com.riverside.skeleton.kotlin.util.file.getFileUri

/**
 * 自动升级监听   1.0
 * b_e  2019/09/25
 */
class UpgradeReceiver : BroadcastReceiver() {
    lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context

        if (intent.action == DOWNLOAD_UPGRADE_OVER) {
            intent.extras?.let {
                doUpgrade(it.getString(PATH_EXTRA) ?: "")
            }
        }
    }

    /**
     * 接受升级消息
     */
    private fun doUpgrade(path: String) {
        val intent = Intent()
        // 设置启动新程序
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // 启动ACTION_VIEW
        intent.action = Intent.ACTION_VIEW
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        // 设置启动功能的路径
        intent.setDataAndType(context.getFileUri(path), "application/vnd.android.package-archive")

        SBaseApplication.instance.startActivity(intent)
        ActivityStackManager.finishAllActivity()
    }

    companion object {
        val DOWNLOAD_UPGRADE_OVER = "${SBaseApplication.instance.packageName}.upgrade_over"
        val PATH_EXTRA = "path"
    }
}