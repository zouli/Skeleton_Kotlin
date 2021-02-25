package com.riverside.skeleton.kotlin.widget.web

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.extras.Extra
import com.riverside.skeleton.kotlin.util.resource.ContextHolder
import com.riverside.skeleton.kotlin.widget.R
import kotlinx.android.synthetic.main.activity_web_browser.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 共通Web画面      1.0
 * b_e      2020/11/28
 */
@SuppressLint("SetJavaScriptEnabled")
class WebBrowserActivity : SBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_web_browser

    private val wvContent: WebView by lazy {
        WebView(ContextHolder.applicationContext).apply {
            this.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            ll_web.addView(this)

            with(this.settings) {
                // 启用支持javascript
                setSupportZoom(true)
                loadWithOverviewMode = true
                useWideViewPort = true
                defaultTextEncodingName = "UTF-8"
                loadsImagesAutomatically = true

                //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_NO_CACHE

                //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
                //然后 复写 WebChromeClient的onCreateWindow方法
                setSupportMultipleWindows(false)
                javaScriptCanOpenWindowsAutomatically = true

                //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置
                domStorageEnabled = true
                databaseEnabled = true
                setAppCacheEnabled(true)
                setAppCachePath(ContextHolder.applicationContext.cacheDir.absolutePath)
            }
        }
    }

    //访问地址
    private val location: String by Extra()

    //标题名
    private val titleName: String by Extra()

    override fun initView() {
        acToolbar.title = titleName
        acToolbar.setNavigation(R.drawable.web_toolbar_back) {
            actionKey(KeyEvent.KEYCODE_BACK)
        }

        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        wvContent.webViewClient = nativeWebViewClient
        wvContent.webChromeClient = nativeWebChromeClient
        wvContent.loadUrl(location)
    }

    private fun actionKey(keyCode: Int) =
        GlobalScope.launch {
            try {
                Instrumentation().sendKeyDownUpSync(keyCode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && wvContent.canGoBack()) {
            wvContent.goBack() // 返回前一个页面
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()

        with(wvContent) {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            clearHistory()

            (parent as ViewGroup).removeView(this)
            destroy()
        }
    }

    /**
     * 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
     */
    private val nativeWebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (titleName.isEmpty()) acToolbar.title = view.title
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
            request.url?.let { url -> handleUri(view, url) } ?: false

        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
            handleUri(view, Uri.parse(url))

        private fun handleUri(view: WebView, uri: Uri) = uri.scheme?.let {
            if (it.startsWith("http")) {
                return false
            } else if (it.startsWith("tel") || it.startsWith("mallto") || it.startsWith("geo")) {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            true
        } ?: false
    }

    private val nativeWebChromeClient = object : WebChromeClient() {
        //=========HTML5定位==========================================================
        //需要先加入权限
        //<uses-permission android:name="android.permission.INTERNET"/>
        //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        override fun onGeolocationPermissionsShowPrompt(
            origin: String, callback: GeolocationPermissions.Callback
        ) {
            //注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            callback.invoke(origin, false, true)
            super.onGeolocationPermissionsShowPrompt(origin, callback)
        }

        //=========多窗口的问题==========================================================
        override fun onCreateWindow(
            view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message
        ): Boolean {
            resultMsg.let {
                (it.obj as WebView.WebViewTransport).apply { this.webView = wvContent }
                it.sendToTarget()
            }
            return true
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) =
            if (newProgress == 100)
                pb_loading.visibility = View.INVISIBLE
            else {
                pb_loading.visibility = View.VISIBLE
                pb_loading.progress = newProgress
            }

        override fun onJsAlert(
            view: WebView, url: String, message: String, result: JsResult
        ): Boolean {
            AlertDialog.Builder(activity).setTitle(acToolbar.title)
                .setMessage(message)
                .setPositiveButton("确定") { _, _ ->
                    result.confirm()
                }
                .setCancelable(false)
                .create().show()
            return true
        }

        override fun onJsConfirm(
            view: WebView, url: String, message: String, result: JsResult
        ): Boolean {
            AlertDialog.Builder(activity).setTitle(acToolbar.title)
                .setMessage(message)
                .setPositiveButton("确定") { _, _ ->
                    result.confirm()
                }
                .setNegativeButton("取消") { _, _ ->
                    result.cancel()
                }
                .setCancelable(false)
                .create().show()
            return true
        }
    }

    companion object {
        const val LOCATION = "location"
        const val TITLE_NAME = "title_name"
    }
}