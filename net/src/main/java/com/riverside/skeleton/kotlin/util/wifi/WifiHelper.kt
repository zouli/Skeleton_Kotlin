package com.riverside.skeleton.kotlin.util.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build

/**
 * Wifi帮助类  1.0
 *
 * b_e  2020/12/14
 */
class WifiHelper(private val context: Context) {
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val wifiLock by lazy { wifiManager.createWifiLock("WifiHelper") }

    /**
     * 当前Wifi状态
     *
     * @see #WIFI_STATE_DISABLED
     * @see #WIFI_STATE_DISABLING
     * @see #WIFI_STATE_ENABLED
     * @see #WIFI_STATE_ENABLING
     * @see #WIFI_STATE_UNKNOWN
     */
    val wifiState get() = wifiManager.wifiState

    /**
     * 取得配置好的网络
     */
    val configurations: MutableList<WifiConfiguration> get() = wifiManager.configuredNetworks

    private var scanResults: MutableList<ScanResult> = mutableListOf()

    private lateinit var scanResultCallback: (scanResults: MutableList<ScanResult>) -> Unit
    private lateinit var stateChangedCallback: (intent: Intent) -> Unit

    /**
     * 打开Wifi
     */
    fun openWifi() {
        if (!wifiManager.isWifiEnabled) wifiManager.isWifiEnabled = true
        context.registerReceiver(wifiReceiver, IntentFilter().let {
            it.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            it.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            it.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION)
            it.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
            it.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            it.addAction(WifiManager.RSSI_CHANGED_ACTION)
            it.addAction(WIFI_AP_STATE_CHANGED_ACTION)
            it
        })
    }

    /**
     * 关闭Wifi
     */
    fun closeWifi() {
        if (wifiManager.isWifiEnabled) wifiManager.isWifiEnabled = false
    }

    /**
     * 锁定WifiLock
     */
    fun acquireWifi() {
        wifiLock.acquire()
    }

    /**
     * 解锁WifiLock
     */
    fun releaseWifi() {
        if (wifiLock.isHeld) wifiLock.release()
    }

    /**
     * 添加Wifi
     */
    fun addWifi(configuration: WifiConfiguration) {
        wifiManager.enableNetwork(wifiManager.addNetwork(configuration), true)
    }

    fun addWifi(SSID: String, password: String, type: WifiCipherType) {
        addWifi(createConfiguration(SSID, password, type))
    }

    /**
     * 断开Wifi
     */
    fun disconnectWifi(netWorkId: Int) {
        wifiManager.disableNetwork(netWorkId)
        wifiManager.disconnect()
    }

    fun disconnectWifi(SSID: String) {
        getConfiguration(SSID)?.let { disconnectWifi(it.networkId) }
    }

    /**
     * 进行扫描
     */
    fun scan(block: (scanResults: MutableList<ScanResult>) -> Unit = {}) {
        scanResultCallback = block

        context.registerReceiver(wifiReceiver, IntentFilter().let {
            it.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            it
        })

        val success = wifiManager.startScan()
        if (!success) {
            scanResultCallback(scanResults)
        }
    }

    /**
     * 切换Wifi
     */
    fun switchWifi(index: Int) {
        if (index >= configurations.size) return
        wifiManager.enableNetwork(configurations[index].networkId, true)
    }

    fun switchWifi(SSID: String) {
        getConfiguration(SSID)?.run { wifiManager.enableNetwork(networkId, true) }
    }

    /**
     * 取得指定的WifiConfiguration
     */
    fun getConfiguration(SSID: String) =
        configurations.firstOrNull { it.SSID == "\"$SSID\"" }

    /**
     * 创建WifiConfiguration
     */
    private fun createConfiguration(
        SSID: String, password: String, type: WifiCipherType
    ): WifiConfiguration {
        getConfiguration(SSID)?.let { wifiManager.removeNetwork(it.networkId) }

        return WifiConfiguration().apply {
            this.allowedAuthAlgorithms.clear()
            this.allowedGroupCiphers.clear()
            this.allowedKeyManagement.clear()
            this.allowedPairwiseCiphers.clear()
            this.allowedProtocols.clear()
            this.SSID = "\"$SSID\""
            when (type) {
                WifiCipherType.NO_PASS -> {
                    this.wepKeys[0] = ""
                    this.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    this.wepTxKeyIndex = 0
                }
                WifiCipherType.WEP -> {
                    this.hiddenSSID = true
                    this.wepKeys[0] = "\"$password\""
                    this.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                    this.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                    this.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                    this.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                    this.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                    this.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    this.wepTxKeyIndex = 0
                }
                WifiCipherType.WPA -> {
                    this.hiddenSSID = true
                    this.preSharedKey = "\"$password\""
                    this.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                    this.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                    this.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                    this.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                    this.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                    this.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                    this.status = WifiConfiguration.Status.ENABLED
                }
            }
        }
    }

    /**
     * Wifi是否可以连接
     */
    fun hasWifi(SSID: String) =
        scanResults.firstOrNull { it.SSID == "\"$SSID\"" }?.let { true } ?: false

    /**
     * Wifi变更事件
     */
    fun onStateChanged(block: (intent: Intent) -> Unit) {
        stateChangedCallback = block
    }

    /**
     * 接收Wifi变更信息
     */
    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                    // 取得扫描结果
                    if (intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false))
                        scanResults = wifiManager.scanResults
                    if (::scanResultCallback.isInitialized) scanResultCallback(scanResults)
                }
                "android.net.wifi.WIFI_AP_STATE_CHANGED" -> {
                    wifiApState = intent.getIntExtra("wifi_state", 0)
                }
                else -> {
                    if (::stateChangedCallback.isInitialized) stateChangedCallback(intent)
                }
            }
        }
    }

    lateinit var apReservation: WifiManager.LocalOnlyHotspotReservation

    /**
     * 打开Ap
     */
    fun openWifiAp(SSID: String, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wifiManager.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
                override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation?) {
                    super.onStarted(reservation)
                    reservation?.let { this@WifiHelper.apReservation = it }
                }
            }, null)
        } else {
            closeWifi()
            setWifiApEnabled(createConfiguration(SSID, password, WifiCipherType.WPA), true)
        }
    }

    /**
     * 关闭Ap
     */
    fun closeWifiAp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (::apReservation.isInitialized) apReservation.close()
        } else {
            setWifiApEnabled(null, false)
        }
    }

    private fun setWifiApEnabled(apConnection: WifiConfiguration?, isEnabled: Boolean): Boolean {
        val method = wifiManager.javaClass.getMethod(
            "setWifiApEnabled", WifiConfiguration::class.java, java.lang.Boolean.TYPE
        )
        return method.invoke(wifiManager, apConnection, isEnabled) as Boolean
    }

    /**
     * AP的状态
     *
     * #WIFI_AP_STATE_DISABLING = 10
     * #WIFI_AP_STATE_DISABLED = 11
     * #WIFI_AP_STATE_ENABLING = 12
     * #WIFI_AP_STATE_ENABLED = 13
     * #WIFI_AP_STATE_FAILED = 14
     */
    var wifiApState = 0

    enum class WifiCipherType {
        NO_PASS, WEP, WPA
    }

    companion object {
        const val WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED"
    }
}