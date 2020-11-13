package com.riverside.skeleton.kotlin.base.utils

import android.Manifest
import com.alibaba.fastjson.JSON
import com.riverside.skeleton.kotlin.base.application.SBaseApplication
import com.riverside.skeleton.kotlin.base.utils.collectinfo.CollectInfoHelper
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.converter.toString
import com.riverside.skeleton.kotlin.util.file.mkdirs
import com.riverside.skeleton.kotlin.util.file.unaryPlus
import com.riverside.skeleton.kotlin.util.packageinfo.hasPermission
import com.zxy.recovery.callback.RecoveryCallback
import java.io.FileOutputStream
import java.util.*

/**
 * 全局异常捕捉回调    1.1
 * b_e 2019/09/23
 */
class CrashCallback : RecoveryCallback {
    override fun stackTrace(stackTrace: String) {
        if (SBaseApplication.isDebug) SLog.e(stackTrace)

        //收集设备参数信息
        val infos = CollectInfoHelper.collectInfo
        //保存日志文件
        saveCrashInfo2File(infos, stackTrace)
    }

    override fun cause(cause: String) {
        if (SBaseApplication.isDebug) SLog.e(cause)
    }

    override fun throwable(throwable: Throwable) {
        if (SBaseApplication.isDebug) SLog.e(throwable)
    }

    override fun exception(
        throwExceptionType: String,
        throwClassName: String,
        throwMethodName: String,
        throwLineNumber: Int
    ) {
        if (SBaseApplication.isDebug) {
            SLog.e("exceptionClassName:$throwExceptionType")
            SLog.e("throwClassName:$throwClassName")
            SLog.e("throwMethodName:$throwMethodName")
            SLog.e("throwLineNumber:$throwLineNumber")
        }
    }

    /**
     * 保存错误信息到文件中
     */
    private fun saveCrashInfo2File(description: String, ex: String) {
        //将其他信息转换为json对象
        val infos = JSON.parseObject(description).also { it["cause"] = ex }

        //取得日志文件名
        val fileName =
            "crash-${Date().toString("yyyy-MM-dd-HH-mm-ss")}-${System.currentTimeMillis()}.log"

        //取得日志文件保存路径
        var path =
            if (SBaseApplication.instance.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                SBaseApplication.instance.mkdirs(+"crash")
            else
                SBaseApplication.instance.cacheDir.path

        //生成日志文件
        val fos = FileOutputStream("$path${+fileName}")
        fos.write(infos.toString().toByteArray())
        fos.close()
    }
}