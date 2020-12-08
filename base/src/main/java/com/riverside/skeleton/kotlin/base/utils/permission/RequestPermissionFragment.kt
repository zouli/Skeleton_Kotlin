package com.riverside.skeleton.kotlin.base.utils.permission

import android.annotation.SuppressLint
import com.riverside.skeleton.kotlin.base.fragment.SBaseFragment
import com.riverside.skeleton.kotlin.util.extras.FragmentArgument
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

/**
 * 权限结果回调Fragment   1.0
 *
 * b_e      2020/12/07
 */
class RequestPermissionFragment : SBaseFragment(), EasyPermissions.PermissionCallbacks {
    private val permissions: Array<String> by FragmentArgument()
    private lateinit var onPermissionCallback: PermissionCallback

    @SuppressLint("RestrictedApi")
    override fun initView() {
        // 直接调用权限申请，绕过EasyPermission的判断
        val request: PermissionRequest =
            PermissionRequest
                .Builder(this, PERMISSION_REQUEST_CODE, *permissions)
                .build()
        request.helper.directRequestPermissions(PERMISSION_REQUEST_CODE, *permissions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (::onPermissionCallback.isInitialized) onPermissionCallback.onDenied(perms)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (::onPermissionCallback.isInitialized) onPermissionCallback.onGranted(perms)
        }
    }

    /**
     * 设置权限结果回调
     */
    fun setOnPermissionCallback(callback: PermissionCallback.() -> Unit) {
        onPermissionCallback = PermissionCallback()
        onPermissionCallback.callback()
    }

    /**
     * 权限结果回调
     */
    class PermissionCallback {
        private lateinit var denied: (perms: MutableList<String>) -> Unit
        private lateinit var granted: (perms: MutableList<String>) -> Unit

        fun onDenied(listener: (perms: MutableList<String>) -> Unit) {
            denied = listener
        }

        fun onDenied(perms: MutableList<String>) {
            if (::denied.isInitialized) denied(perms)
        }

        fun onGranted(listener: (perms: MutableList<String>) -> Unit) {
            granted = listener
        }

        fun onGranted(perms: MutableList<String>) {
            if (::granted.isInitialized) granted(perms)
        }
    }


    companion object {
        const val PERMISSIONS = "permissions"
        const val PERMISSION_REQUEST_CODE = 81
    }
}