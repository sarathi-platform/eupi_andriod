package com.patsurvey.nudge.utils

import android.Manifest
import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.location.LocationProvider
import android.os.BatteryManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.utils.PermissionUtil.PREF_ACCESS_COARSE_LOCATION_PERMISSION
import com.patsurvey.nudge.utils.PermissionUtil.PREF_ACCESS_FINE_LOCATION_PERMISSION

object LocationUtil {

    fun getLocation(context: Activity): LocationCoordinates? {

        val mLocationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val batteryManager =
            context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val locationProvider = mLocationManager.getBestProvider(
                getCriteria(
                    if (batteryLevel > 30)
                        Criteria.ACCURACY_FINE
                    else
                        Criteria.ACCURACY_COARSE
                ), true
            )
            val location = locationProvider?.let { mLocationManager.getLastKnownLocation(it) }
            Log.d(
                "LocationUtil",
                "locationProvider: $locationProvider, location: lat-${location?.latitude}, long-${location?.longitude}"
            )

            return LocationCoordinates(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                && ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                val locationProvider =
                    mLocationManager.getBestProvider(getCriteria(Criteria.ACCURACY_FINE), true)
                val location = locationProvider?.let { mLocationManager.getLastKnownLocation(it) }
                Log.d(
                    "LocationUtil",
                    "location: lat-${location?.latitude}, long-${location?.longitude}"
                )
                return LocationCoordinates(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
            } else {
                Toast.makeText(context, "Location Permission Not Granted", Toast.LENGTH_LONG).show()
                return null
            }
        }

    }

    private fun getCriteria(accuracy: Int): Criteria {

        val criteria = Criteria()
        criteria.accuracy = accuracy
        criteria.isAltitudeRequired = false;
        criteria.isBearingRequired = false;
        criteria.isSpeedRequired = false;
        criteria.isCostAllowed = true;
        criteria.powerRequirement = Criteria.POWER_MEDIUM

        return criteria
    }

}