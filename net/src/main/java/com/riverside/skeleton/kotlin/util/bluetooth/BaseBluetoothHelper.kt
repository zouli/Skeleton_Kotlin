package com.riverside.skeleton.kotlin.util.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.riverside.skeleton.kotlin.util.packageinfo.PackageInfoHelper
import java.io.Closeable

/**
 * 蓝牙帮助类Base    1.0
 *
 * b_e  2020/12/16
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
abstract class BaseBluetoothHelper(context: Context) : Closeable {
    private var bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    protected val adapter: BluetoothAdapter by lazy { bluetoothManager.adapter }

    lateinit var foundCallback: ((device: BluetoothDevice) -> Boolean)
    protected val isFoundCallbackInit get() = ::foundCallback.isInitialized

    /**
     * 设备是否蓝牙
     */
    open val isSupported: Boolean get() = BluetoothAdapter.getDefaultAdapter() != null

    /**
     * 取得设备名称
     */
    val myName: String = adapter.name

    /**
     * 取得设备地址
     */
    val address @SuppressLint("HardwareIds") @RequiresPermission(Manifest.permission.BLUETOOTH) get() = adapter.address

    /**
     * 设备的 bluetooth 是否已经开启
     */
    val isEnabled @RequiresPermission(Manifest.permission.BLUETOOTH) get() = adapter.isEnabled

    /**
     * 开启当前设备的 Bluetooth
     */
    fun openBluetooth() {
        PackageInfoHelper.context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        })
    }

    /**
     * 开启当前设备的 Bluetooth
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun turnOn() = adapter.enable()

    /**
     * 关闭当前设备的 Bluetooth
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun turnOff() = adapter.disable()

    /**
     * 开始搜索设备
     */
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION])
    abstract fun discovery(block: (device: BluetoothDevice) -> Boolean): Boolean

    /**
     * 停止搜索设备
     */
    abstract fun cancelDiscovery(): Boolean

    /**
     * 取得设备
     */
    fun getDevice(address: String): BluetoothDevice = adapter.getRemoteDevice(address)

    private val listenerList = mutableListOf<ChangedListener>()

    fun addChangedListener(block: ChangedListener) {
        if (!listenerList.contains(block)) listenerList.add(block)
    }

    fun removeChangedListener(block: ChangedListener) {
        if (listenerList.contains(block)) listenerList.remove(block)
    }

    val handler = Handler { msg ->
        listenerList.forEach {
            it(msg.what, (msg.obj as ByteArray))
        }
        false
    }

    /**
     * 变更状态
     */
    protected fun changeStatus(status: Int, data: ByteArray) {
        handler.obtainMessage(status, data).sendToTarget()
    }

    override fun close() {
        listenerList.clear()
    }

    companion object {
        const val DISCONNECTED = 0
        const val LISTENING = 1
        const val CONNECTING = 2
        const val CONNECTED = 3
        const val READ_MESSAGE = 4
        const val WRITE_MESSAGE = 5
    }
}

typealias ChangedListener = (status: Int, data: ByteArray) -> Unit