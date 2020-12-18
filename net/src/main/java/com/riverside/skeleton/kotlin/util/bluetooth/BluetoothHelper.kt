package com.riverside.skeleton.kotlin.util.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.riverside.skeleton.kotlin.util.packageinfo.PackageInfoHelper.context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*

/**
 * 蓝牙帮助类    1.0
 *
 * b_e  2020/12/16
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BluetoothHelper @RequiresPermission(Manifest.permission.BLUETOOTH) constructor(context: Context) :
    BaseBluetoothHelper(context) {
    private lateinit var connectedThread: ConnectedThread
    private lateinit var receiveListener: (message: String) -> Unit

    /**
     * 已配对设备
     */
    val pairedDevices
        @RequiresPermission(Manifest.permission.BLUETOOTH)
        get() = adapter.bondedDevices?.toMutableList() ?: mutableListOf()

    /**
     * 开始搜索设备
     */
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun discovery(block: (device: BluetoothDevice) -> Boolean) =
        adapter.startDiscovery().apply {
            foundCallback = block
            changeStatus(CONNECTING, ByteArray(0))
        }

    /**
     * 开启允许被发现
     */
    fun discoverable() {
        context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        })
    }

    /**
     * 停止搜索设备
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    override fun cancelDiscovery() = adapter.cancelDiscovery()

    private val bluetoothReceiver = object : BroadcastReceiver() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
        override fun onReceive(context: Context, intent: Intent) {
            // 搜索到没有配对的设备
            if (intent.action == BluetoothDevice.ACTION_FOUND) {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (isFoundCallbackInit && foundCallback(device))
                    cancelDiscovery()
            }
        }
    }

    /**
     * 接受连接
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    fun accept(name: String, uuid: UUID, block: (message: String) -> Unit) {
        discoverable()

        val serviceSocket = adapter.listenUsingRfcommWithServiceRecord(name, uuid)
        var socket: BluetoothSocket? = null
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    changeStatus(LISTENING, ByteArray(0))
                    socket = serviceSocket.accept()
                } catch (e: IOException) {
                } finally {
                    socket?.apply {
                        serviceSocket.close()
                        connectedThread = ConnectedThread(this)
                        receiveListener = block
                    }
                    break
                }
            }
        }
    }

    /**
     * 连接
     */
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN])
    fun connect(uuid: UUID, device: BluetoothDevice, block: (message: String) -> Unit) {
        cancelDiscovery()

        val socket = device.createRfcommSocketToServiceRecord(uuid)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socket.connect()
                connectedThread = ConnectedThread(socket)
                receiveListener = block
                changeStatus(CONNECTED, ByteArray(0))
            } catch (e: IOException) {
                socket.close()
            }
        }
    }

    /**
     * 发送信息
     */
    fun send(msg: ByteArray) {
        if (::connectedThread.isInitialized) connectedThread.write(msg)
    }

    init {
        if (!isSupported || !isEnabled) openBluetooth()
        context.registerReceiver(bluetoothReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        addChangedListener { status, data ->
            when (status) {
                READ_MESSAGE ->
                    if (::receiveListener.isInitialized) receiveListener(String(data))
            }
        }

        changeStatus(DISCONNECTED, ByteArray(0))
    }

    /**
     * 关闭
     */
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN])
    override fun close() {
        super.close()
        changeStatus(DISCONNECTED, ByteArray(0))
        if (::connectedThread.isInitialized) connectedThread.cancel()

        try {
            context.unregisterReceiver(bluetoothReceiver)
        } catch (e: Exception) {
        }
    }

    /**
     * 连接线程
     */
    private inner class ConnectedThread(private val socket: BluetoothSocket) {
        private val inputStream: InputStream = socket.inputStream
        private val outputStream: OutputStream = socket.outputStream
        val buffer = ByteArray(4 * 1024)

        init {
            GlobalScope.launch(Dispatchers.IO) {
                var bufferSize: Int
                while (true) {
                    bufferSize = try {
                        inputStream.read(buffer)
                    } catch (e: IOException) {
                        break
                    }
                    changeStatus(READ_MESSAGE, buffer.take(bufferSize).toByteArray())
                }
            }
        }

        fun write(bytes: ByteArray) {
            try {
                outputStream.write(bytes)
            } catch (e: IOException) {
            }
            changeStatus(WRITE_MESSAGE, ByteArray(0))
        }

        fun cancel() {
            try {
                inputStream.close()
                outputStream.close()
                socket.close()
            } catch (e: IOException) {
            }
        }
    }
}