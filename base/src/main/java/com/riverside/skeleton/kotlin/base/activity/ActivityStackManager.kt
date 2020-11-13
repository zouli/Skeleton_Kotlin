package com.riverside.skeleton.kotlin.base.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import java.util.*

/**
 * Activity生存周期管理类 1.2
 * <p>
 * b_e  2019/08/29
 */
object ActivityStackManager {
    var activityList: MutableList<Activity> = Collections.synchronizedList(LinkedList<Activity>())
    var multipleFinishFlag = -1

    /**
     * 加入列表
     */
    fun addActivity(activity: Activity) {
        activityList.add(activity)
    }

    /**
     * 当前Activity
     */
    val currentActivity: Activity?
        get() =
            if (activityList.isNotEmpty())
                activityList[activityList.size - 1]
            else null

    /**
     * 列表中Activity数量
     */
    val countActivity get() = activityList.size

    /**
     * 开启多重关闭模式，执行多重关闭(doMultipleFinish)后会显示当前画面
     */
    fun startMultipleFinish() {
        if (activityList.isNotEmpty())
            for (i in activityList.size - 1 downTo multipleFinishFlag + 1) {
                //防止有activity正在关闭中
                if (activityList[i].isFinishing) continue
                multipleFinishFlag = i
                break
            }
    }

    /**
     * 执行多重关闭
     */
    fun doMultipleFinish() {
        if (activityList.isNotEmpty() && multipleFinishFlag != -1) {
            for (i in activityList.size - 1 downTo multipleFinishFlag + 1)
                finishActivity(activityList[i])
        }
    }

    /**
     * 从列表中删除Activity，并关闭Activity
     */
    fun finishActivity(activity: Activity) {
        if (activityList.isNotEmpty()) {
            activityList.remove(activity)
            activity.finish()
        }
    }

    /**
     * 关闭所有Activity
     */
    fun finishAllActivity() {
        for (activity in activityList) activity.finish()
        activityList.clear()
    }

    /**
     * 跳转到Login窗体
     * <p>
     * 使用前需要在AndroidManifest.xml的
     * LoginActivity的<intent-filter></intent-filter>中
     * 添加<action android:name="${applicationId}.LOGIN_ACTIVITY" />
     */
    fun toLoginActivity(closeAll: Boolean) {
        currentActivity?.let { activity ->
            val app = activity.application

            val intent = Intent()
            intent.action = app.packageName + ".LOGIN_ACTIVITY"
            app.packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA)
                .forEach { resolveInfo ->
                    if (activity.componentName.className != resolveInfo.activityInfo.name
                        || activity.isFinishing
                    ) {
                        if (closeAll) finishAllActivity()

                        val intentLogin = Intent()
                        intentLogin.setClassName(
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name
                        )
                        if (closeAll) {
                            intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            app.startActivity(intentLogin)
                        } else activity.startActivity(intentLogin)

                        return
                    }
                }
        }
    }
}