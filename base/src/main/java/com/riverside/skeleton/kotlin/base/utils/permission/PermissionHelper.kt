package com.riverside.skeleton.kotlin.base.utils.permission

import android.content.Context
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.base.fragment.ISBaseFragment
import com.riverside.skeleton.kotlin.util.extras.setArguments
import com.riverside.skeleton.kotlin.util.looper.runOnUi
import pub.devrel.easypermissions.EasyPermissions

/**
 * 权限帮助类    1.0
 *
 * b_e      2020/12/07
 */

/**
 * 判断是否有权限
 */
fun Context.hasPermissions(vararg permissions: String): Boolean =
    EasyPermissions.hasPermissions(this, *permissions)

fun SBaseActivity.hasPermissions(vararg permissions: String): Boolean =
    (this as Context).hasPermissions(*permissions)

fun ISBaseFragment.hasPermissions(vararg permissions: String): Boolean =
    this.sBaseActivity.hasPermissions(*permissions)

/**
 * 请求权限
 */
fun SBaseActivity.requestPermissions(
    vararg permissions: String, rationale: String = "",
    callback: RequestPermissionFragment.PermissionCallback.() -> Unit
) {
    PermissionHelper(this).requestPermissions(
        *permissions, rationale = rationale, callback = callback
    )
}

fun ISBaseFragment.requestPermissions(
    vararg permissions: String, rationale: String = "",
    callback: RequestPermissionFragment.PermissionCallback.() -> Unit
) {
    PermissionHelper(this.sBaseActivity).requestPermissions(
        *permissions, rationale = rationale, callback = callback
    )
}

/**
 * 权限帮助类
 */
class PermissionHelper(val activity: SBaseActivity) {
    companion object {
        const val FRAGMENT_TAG = "SKELETON_REQUEST_PERMISSION_FRAGMENT"
    }

    fun requestPermissions(
        vararg permissions: String, rationale: String = "",
        callback: RequestPermissionFragment.PermissionCallback.() -> Unit
    ) {
        if (!activity.hasPermissions(*permissions)) {
            runOnUi {
                removeFragment()
                activity.supportFragmentManager.beginTransaction()
                    .add(
                        RequestPermissionFragment().apply {
                            setOnPermissionCallback(callback)
                        }.setArguments(RequestPermissionFragment.PERMISSIONS to permissions)
                        , FRAGMENT_TAG
                    )
                    .commitNowAllowingStateLoss()
            }
        } else {
            val permissionCallback = RequestPermissionFragment.PermissionCallback()
            permissionCallback.callback()
            permissionCallback.onGranted(permissions.toMutableList())
        }
    }

    private fun removeFragment() {
        activity.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            ?.also {
                activity.supportFragmentManager.beginTransaction().remove(it)
                    .commitNowAllowingStateLoss()
            }
    }
}