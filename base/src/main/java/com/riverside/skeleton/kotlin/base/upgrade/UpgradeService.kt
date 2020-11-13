package com.riverside.skeleton.kotlin.base.upgrade

import android.Manifest
import android.app.DownloadManager
import android.app.IntentService
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import com.riverside.skeleton.kotlin.base.R
import com.riverside.skeleton.kotlin.base.activity.ActivityStackManager
import com.riverside.skeleton.kotlin.base.application.SBaseApplication
import com.riverside.skeleton.kotlin.util.file.*
import com.riverside.skeleton.kotlin.util.packageinfo.hasPermission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * 自动升级服务   1.0
 * b_e  2019/09/25
 */
class UpgradeService : IntentService(UpgradeService::class.simpleName) {
    /**
     * 引用系统下载服务
     */
    lateinit var downloadManager: DownloadManager
    // 下载窗口标题
    private lateinit var title: String
    // 下载窗口信息
    private lateinit var message: String
    // 下载地址
    private lateinit var downloadUrl: String
    // 文件下载ID，用于和系统下载服务通信
    private var lastDownloadId: Long = 0
    // 文件下载监控服务
    private lateinit var downloadObserver: DownloadChangeObserver
    // 下载窗口
    private lateinit var downloadDialog: ProgressDialog

    override fun onCreate() {
        downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent) {
        if (intent.action == ACTION_DOWNLOAD_URL) {
            intent.extras?.let {
                downloadUrl(
                    it.getString(TITLE_EXTRA) ?: "",
                    it.getString(MESSAGE_EXTRA) ?: "",
                    it.getString(DOWNLOAD_URL_EXTRA) ?: ""
                )
            }
        }
    }

    private fun downloadUrl(title: String, message: String, downloadUrl: String) {
        this.title = title
        this.message = message
        this.downloadUrl = downloadUrl

        //显示下载窗口
        showDialog()
        //启动下载服务
        startDownload()
    }

    /**
     * 显示下载窗口
     */
    private fun showDialog() {
        Observable.just("")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                downloadDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ProgressDialog(
                        ActivityStackManager.currentActivity,
                        R.style.Colored_Dialog_Alert
                    )
                } else {
                    ProgressDialog(ActivityStackManager.currentActivity)
                }
                downloadDialog.setTitle(title)
                if (!TextUtils.isEmpty(message)) {
                    downloadDialog.setMessage(message)
                }
                downloadDialog.setOnCancelListener { cancelDownload() }
                downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                downloadDialog.setCanceledOnTouchOutside(false)
                downloadDialog.setProgressNumberFormat("")
                downloadDialog.show()
            }
    }

    /**
     * 取消下载
     */
    private fun cancelDownload() {
        // 关闭窗口
        downloadDialog.dismiss()
        // 取消系统下载任务
        downloadManager.remove(lastDownloadId)
        // 结束程序
        ActivityStackManager.finishAllActivity()
    }

    /**
     * 设置进度条的进度
     *
     * @param progress 当前进度 1~100
     */
    private fun setProgress(progress: Int) {
        Observable.just("")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                downloadDialog.progress = progress
            }
    }

    /**
     * 启动系统下载服务
     */
    private fun startDownload() {
        val down = try {
            //下载设置
            DownloadManager.Request(Uri.parse(this.downloadUrl))
        } catch (e: IllegalArgumentException) {
            //发送错误消息
            val intent = Intent(DOWNLOAD_ERROR)
            intent.putExtra("error", e)
            sendBroadcast(intent)

            //关闭下载窗口
            downloadDialog.dismiss()
            return
        }

        //设置允许使用的网络类型，流量+wifi
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        //发出通知
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        //显示下载界面
        down.setVisibleInDownloadsUi(true)

        with(SBaseApplication.instance) {
            if (this.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //设置文件保存路径
                this.mkdirs("upgrade")
                val filepath = this.getPath("upgrade")
                val path = this.getPathWithoutSD("upgrade")
                //设置文件名
                val filename = "upgrade.apk"
                //设置备份文件名
                val filenameBak = "upgradebak.apk"

                //备份上一版安装程序
                (filepath + +filenameBak).delete()
                (filepath + +filename) rename (filepath + +filenameBak)

                //设置下载后文件存放的位置
                down.setDestinationInExternalPublicDir(path, filename)
            }
        }

        //将下载请求放入队列
        lastDownloadId = downloadManager.enqueue(down)

        //启动下载监控服务
        downloadObserver = DownloadChangeObserver(null)
        contentResolver.registerContentObserver(CONTENT_URI, true, downloadObserver)
    }

    /**
     * 下载完成
     */
    private fun downloadOver(filename: String) {
        Observable.just("")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //关闭下载窗口
                downloadDialog.dismiss()

                //取消系统下载任务
                //downloadManager.remove(lastDownloadId);

                // 发送文件下载完成消息
                val intent = Intent(UpgradeReceiver.DOWNLOAD_UPGRADE_OVER)
                intent.setPackage(packageName)
                intent.putExtra("path", filename)
                sendBroadcast(intent)
            }
    }

    /**
     * 查看当前下载状态
     */
    fun queryDownloadStatus(id: Long) {
        // 取得当前下载文件状态
        val query = DownloadManager.Query()
        query.setFilterById(id)
        // 查询下载状态
        val cursor = downloadManager.query(query)
        if (cursor != null && cursor.moveToFirst()) {
            // 当前下载状态ID
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            // 当前下载状态
            val status = cursor.getInt(columnIndex)
            // 当前状态理由ID
            val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
            // 当前状态理由
            val reason = cursor.getInt(columnReason)

            val filename =
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))?.let {
                        Uri.parse(it).path
                    }
                } else {
                    cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME).let {
                        cursor.getString(it)
                    }
                }

            // 文件大小ID
            val fileSizeIdx = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            // 文件大小
            val fileSize = cursor.getInt(fileSizeIdx)
            // 已下载的文件大小ID
            val bytesDLIdx = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            // 已下载的文件大小
            val bytesDL = cursor.getInt(bytesDLIdx)

            // TODO:下载的状态是不是都需要处理呢？
            when (status) {
                DownloadManager.STATUS_FAILED -> when (reason) {
                    DownloadManager.ERROR_CANNOT_RESUME -> {
                        // some possibly transient error occurred but we can't resume the download
                    }
                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> {
                        // no external storage device was found.
                        // Typically, this is because the SD card is not mounted
                    }
                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> {
                        // the requested destination file already exists
                        // (the download manager will not overwrite an existing file)
                    }
                    DownloadManager.ERROR_FILE_ERROR -> {
                        // a storage issue arises which doesn't fit under any other error code
                    }
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> {
                        // an error receiving or processing data occurred at the HTTP level
                    }
                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> {
                        //sd卡满了
                        // here was insufficient storage space.
                        // Typically, this is because the SD card is full
                    }
                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> {
                        // there were too many redirects
                    }
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> {
                        // an HTTP code was received that download manager can't handle
                    }
                    DownloadManager.ERROR_UNKNOWN -> {
                        // he download has completed with an error that doesn't fit
                        // under any other error code
                    }
                    // isNeedDownloadAgain = true;
                }
                DownloadManager.STATUS_PAUSED -> when (reason) {
                    DownloadManager.PAUSED_QUEUED_FOR_WIFI -> {
                        // the download exceeds a size limit for downloads over the
                        // mobile network and the download manager is waiting for a
                        // Wi-Fi connection to proceed
                    }
                    DownloadManager.PAUSED_UNKNOWN -> {
                        // the download is paused for some other reason
                    }
                    DownloadManager.PAUSED_WAITING_FOR_NETWORK -> {
                        // the download is waiting for network connectivity to proceed
                    }
                    DownloadManager.PAUSED_WAITING_TO_RETRY -> {
                        // the download is paused because some network error
                        // occurred and the download manager is waiting before
                        // retrying the request
                    }
                    // isNeedDownloadAgain = false;
                }
                DownloadManager.STATUS_PENDING -> {
                    // the download is waiting to start
                    // isNeedDownloadAgain = false;
                }
                DownloadManager.STATUS_RUNNING -> {
                    // the download is currently running
                    // isNeedDownloadAgain = false;
                    // 设置当前下载进度
                    val progress = (bytesDL.toFloat() / fileSize * 100).toInt()
                    setProgress(progress)
                }
                DownloadManager.STATUS_SUCCESSFUL ->
                    // the download has successfully completed
                    // isNeedDownloadAgain = false;

                    // 下载完成
                    filename?.let { downloadOver(it) }
            }
        }
    }

    /**
     * 系统下载服务监控类
     */
    inner class DownloadChangeObserver(handler: Handler?) : ContentObserver(handler) {
        /**
         * 状态监控
         */
        override fun onChange(selfChange: Boolean) {
            //检测当前下载状态
            queryDownloadStatus(lastDownloadId)
        }
    }

    companion object {
        /**
         * 系统下载服务上下文URI
         */
        val CONTENT_URI = Uri.parse("content://downloads/my_downloads")
        /**
         * 下载出现错误
         */
        val DOWNLOAD_ERROR = "${SBaseApplication.instance.packageName}.download_error"

        val ACTION_DOWNLOAD_URL = "DownloadUrl"
        val TITLE_EXTRA = "title"
        val MESSAGE_EXTRA = "message"
        val DOWNLOAD_URL_EXTRA = "downloadUrl"
    }
}