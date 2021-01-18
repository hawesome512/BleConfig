package com.hawesome.bleconfig.model

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.location.Location
import android.location.LocationManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hawesome.bleconfig.R
import kotlinx.android.synthetic.main.activity_main.*

/*
* 士林工厂位置
* */
object XSLocation {

    fun checkInRegion(context: Activity, block: () -> Unit) {
        getLastKnownLocation(context)?.let {
            //纬度
            val lat = it.latitude
            //经度
            val long = it.longitude
            if (lat > 24.590 && lat < 24.610 && long > 118.090 && long < 118.110) {
                block()
                return
            }
        }
        AlertDialog.Builder(context)
            .setTitle(R.string.factory_alert_title)
            .setMessage(R.string.factory_alert_content)
            .setCancelable(false)
            .setPositiveButton(R.string.confirm) { dialogInterface: DialogInterface, i: Int ->
                context.finish()
            }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(context: Context): Location? {
        //获取地理位置管理器
        val mLocationManager =
            context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }

}