package com.riverside.skeleton.kotlin.basetest

import android.app.Activity
import android.content.Context
import android.view.View
import com.riverside.skeleton.kotlin.base.fragment.SBaseFragment
import com.riverside.skeleton.kotlin.base.service.SBaseServiceConnection
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.extras.*
import com.riverside.skeleton.kotlin.util.notice.toast
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

class ExtrasFragment : SBaseFragment() {
    var i = 1
    private var conn = SBaseServiceConnection<TestService>()

    override fun setView(): View? {
        return UI {
            verticalLayout {
                lparams(matchParent, matchParent)

                textView("Fragment").lparams(matchParent, wrapContent)

                button("start activity") {
                    onClick {
                        this@ExtrasFragment.startActivity<ExtrasActivity>("flag" to 1)
                    }
                }.lparams(matchParent, wrapContent)

                button("start activity for result") {
                    onClick {
                        this@ExtrasFragment.startActivityForResult<ExtrasActivity>("flag" to 2) { resultCode, intent ->
                            if (resultCode == Activity.RESULT_OK) {
                                intent?.getStringExtra("value")
                                    ?.let { "Fragment $it".toast(sBaseActivity) }
                            }
                        }
                    }
                }.lparams(matchParent, wrapContent)

                button("start service") {
                    onClick {
                        this@ExtrasFragment.startService<TestService>("flag" to i++)
                    }
                }.lparams(matchParent, wrapContent)

                button("stop service") {
                    onClick {
                        this@ExtrasFragment.stopService<TestService>("flag" to i++)
                    }
                }.lparams(matchParent, wrapContent)

                button("bind service") {
                    onClick {
                        conn = bindingService<TestService>(
                            Context.BIND_AUTO_CREATE,
                            "flag" to i++,
                            onConnected = { name, service ->
                                SLog.w(service.getIndex())
                            },
                            onDisconnected = { name -> })
                    }
                }.lparams(matchParent, wrapContent)

                button("unbind service") {
                    onClick {
                        unbindService(conn)
                    }
                }.lparams(matchParent, wrapContent)
            }
        }.view
    }

    override fun initView() {
    }
}