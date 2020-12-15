package com.riverside.skeleton.kotlin.util.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat

/**
 * GPS帮助类   1.0
 *
 * b_e  2020/12/15
 */
class GpsHelper(private val context: Context) {
    private var locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    var isRealTime = false

    var location: Location? = null

    private val provider
        get() = locationManager.getBestProvider(Criteria().apply {
            //查询精度：高
            accuracy = Criteria.ACCURACY_FINE
            //是否查询海拨：是
            isAltitudeRequired = true
            //是否查询方位角: 是
            isBearingRequired = true
            //是否允许付费：是
            isCostAllowed = true
            //电量要求：低
            powerRequirement = Criteria.POWER_LOW
        }, true)

    private val gpsStatusCallback = GpsStatus.Listener {
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val gnssStatus = object : GnssStatus.Callback() {
    }

    /**
     * 位置监听
     */
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            location?.let { this@GpsHelper.location = it }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            if (status == LocationProvider.AVAILABLE)
                isRealTime = true
            else if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE)
                isRealTime = false
        }

        @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
        override fun onProviderEnabled(provider: String?) {
            if (hasPermission) {
                location = locationManager.getLastKnownLocation(provider)
            }
        }

        override fun onProviderDisabled(provider: String?) {
            location = null
        }
    }

    private val hasPermission
        get() =
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

    /**
     * 打开GPS
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun openLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.registerGnssStatusCallback(gnssStatus)
        } else {
            locationManager.addGpsStatusListener(gpsStatusCallback)
        }

        locationManager.getLastKnownLocation(provider)
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 2000L, 10F, locationListener
        )
    }

    /**
     * 关闭GPS
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun closeLocaion() {
        locationManager.removeUpdates(locationListener)
    }
}