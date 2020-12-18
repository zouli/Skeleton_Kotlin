package com.riverside.skeleton.kotlin.util.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.riverside.skeleton.kotlin.util.notice.toast

/**
 * 蓝牙帮助类(BLE)   1.0
 *
 * b_e  2020/12/16
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BluetoothLeHelper @RequiresPermission(Manifest.permission.BLUETOOTH) constructor(context: Context) :
    BaseBluetoothHelper(context) {
    /**
     * 当前设备是否支持 Bluetooth_LE
     */
    override val isSupported: Boolean =
        if (BluetoothAdapter.getDefaultAdapter() == null ||
            !context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        ) {
            "设备不支持蓝牙低功耗(BLE)".toast(context)
            false
        } else {
            if (!isEnabled) openBluetooth()
            true
        }

    /**
     * 开始搜索设备
     */
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun discovery(block: (device: BluetoothDevice) -> Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adapter.bluetoothLeScanner.startScan(scanCallback)
        } else {
            adapter.startLeScan(leScanResult)
        }
        foundCallback = block
        changeStatus(CONNECTING, ByteArray(0))
        return true
    }

    private val scanCallback =
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP) object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                if (isFoundCallbackInit && foundCallback(result.device))
                    cancelDiscovery()
            }
        }

    private val leScanResult =
        BluetoothAdapter.LeScanCallback @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN) { device, _, _ ->
            if (isFoundCallbackInit && foundCallback(device))
                cancelDiscovery()
        }

    /**
     * 停止搜索设备
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    override fun cancelDiscovery(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adapter.bluetoothLeScanner.stopScan(scanCallback)
        } else
            adapter.stopLeScan(leScanResult)
        return true
    }

    init {
        changeStatus(DISCONNECTED, ByteArray(0))
    }
}