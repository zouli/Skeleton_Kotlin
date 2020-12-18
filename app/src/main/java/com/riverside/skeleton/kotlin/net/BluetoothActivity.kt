package com.riverside.skeleton.kotlin.net

import android.annotation.SuppressLint
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.bluetooth.BluetoothHelper
import com.riverside.skeleton.kotlin.util.bluetooth.BluetoothLeHelper
import com.riverside.skeleton.kotlin.util.dialog.AlertBuilder
import com.riverside.skeleton.kotlin.util.dialog.alert
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.util.resource.uuid
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class BluetoothActivity : SBaseActivity() {
    private lateinit var bluetoothHelper: BluetoothHelper
    private lateinit var le: BluetoothLeHelper
    private val uuid = "bluetooth_test".uuid

    @SuppressLint("MissingPermission", "NewApi")
    override fun initView() {
        title = "Bluetooth"
        bluetoothHelper = BluetoothHelper(activity)
        bluetoothHelper.addChangedListener { status, data ->
            SLog.w(status)
        }
        le = BluetoothLeHelper(activity)

        verticalLayout {
            lparams(matchParent, matchParent)

            button("On") {
                onClick {
                    bluetoothHelper.turnOn()
                }
            }.lparams(matchParent, wrapContent)

            button("Off") {
                onClick {
                    bluetoothHelper.turnOff()
                }
            }.lparams(matchParent, wrapContent)

            textView("Normal").lparams(matchParent, wrapContent)

            button("Discoverable") {
                onClick {
                    bluetoothHelper.discoverable()
                }
            }.lparams(matchParent, wrapContent)

            button("Accept") {
                onClick {
                    bluetoothHelper.accept("bluetoothTest", uuid) {
                        it.toast(activity)
                    }
                }
            }.lparams(matchParent, wrapContent)

            button("Connect") {
                onClick {
                    alert {
                        items(bluetoothHelper.pairedDevices) { _, item, _ ->
                            item.name.toast(activity)
                            bluetoothHelper.connect(uuid, item) {
                                it.toast(activity)
                            }
                        }
                        cancelButton { _, _ ->
                            bluetoothHelper.discovery {
                                alert {
                                    message = it.name ?: it.address
                                    okButton { dialog, which ->
//                                        bluetoothHelper.cancelDiscovery()
                                        bluetoothHelper.connect(uuid, it) {
                                            it.toast(activity)
                                        }
                                    }
                                    cancelButton { dialog, which ->
                                    }
                                }.showResult() == AlertBuilder.Result.YES
                            }
                        }
                        isCancelable = false
                    }.show()
                }
            }.lparams(matchParent, wrapContent)

            button("Send") {
                onClick {
                    bluetoothHelper.send(bluetoothHelper.myName.toByteArray())
                }
            }.lparams(matchParent, wrapContent)

            button("Close") {
                onClick {
                    bluetoothHelper.close()
                }
            }.lparams(matchParent, wrapContent)

            textView("BLE").lparams(matchParent, wrapContent)

            button("LE Discovery") {
                onClick {

                    le.discovery {
                        it.address.toast(activity)
//                        it.connectGatt(activity, true, object : BluetoothGattCallback() {
//
//                        })
                        false
                    }
                }
            }.lparams(matchParent, wrapContent)

            button("LE Cancel Discovery") {
                onClick {
                    le.cancelDiscovery()
                }
            }.lparams(matchParent, wrapContent)

        }
    }
}