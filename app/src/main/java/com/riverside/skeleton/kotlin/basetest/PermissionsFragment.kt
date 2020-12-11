package com.riverside.skeleton.kotlin.basetest

import android.Manifest
import android.view.View
import android.view.ViewGroup
import com.riverside.skeleton.kotlin.base.fragment.SBaseFragment
import com.riverside.skeleton.kotlin.base.utils.permission.requestPermissions
import com.riverside.skeleton.kotlin.util.notice.toast
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

class PermissionsFragment : SBaseFragment() {
    override fun setView(container: ViewGroup?): View? {
        return UI {
            verticalLayout {
                lparams(matchParent, matchParent)

                textView("Fragment").lparams(matchParent, wrapContent)

                button("No Message") {
                    onClick {
                        requestPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ) {
                            onGranted { perms ->
                                "f同意$perms".toast(sBaseActivity)
                            }
                            onDenied { perms ->
                                "f拒绝$perms".toast(sBaseActivity)
                            }
                        }
                    }
                }

                button("Message") {
                    onClick {
                        requestPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            title = "Test", rationale = "请开启存储与相机权限"
                        ) {
                            onGranted { perms ->
                                "f同意$perms".toast(sBaseActivity)
                            }
                            onDenied { perms ->
                                "f拒绝$perms".toast(sBaseActivity)
                            }
                        }
                    }
                }
            }
        }.view
    }

    override fun initView() {
    }
}