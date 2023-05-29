package com.patsurvey.nudge.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

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

            if (isLocationEnabled(context, mLocationManager)) {
                val location = locationProvider?.let { mLocationManager.getLastKnownLocation(it) }

                Log.d(
                    "LocationUtil",
                    "locationProvider: $locationProvider, location: lat-${location?.latitude}, long-${location?.longitude}"
                )

                return if (location != null)
                    LocationCoordinates(location.latitude, location.longitude)
                else {
                    context.runOnUiThread {
                        Toast.makeText(context, "Location not Available", Toast.LENGTH_LONG).show()
                    }
                    LocationCoordinates(0.0, 0.0)
                }
            } else {
                context.runOnUiThread {
                    Toast.makeText(context, "Location not Enabled", Toast.LENGTH_LONG).show()
                }
                return null
            }

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
                    mLocationManager.getBestProvider(
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
                    "location: lat-${location?.latitude}, long-${location?.longitude}"
                )
                return LocationCoordinates(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
            } else {
                context.runOnUiThread{
                    Toast.makeText(context, "Location Permission Not Granted", Toast.LENGTH_LONG).show()
                }
                return null
            }
        }

    }

    private fun isLocationEnabled(context: Activity, mLocationManager: LocationManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mLocationManager.isLocationEnabled
        } else {
            val mode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF)
            (mode != Settings.Secure.LOCATION_MODE_OFF)
        }

    }

    private fun getCriteria(accuracy: Int): Criteria {

        val criteria = Criteria()
        criteria.accuracy = accuracy
        criteria.isAltitudeRequired = false;
        criteria.isBearingRequired = false;
        criteria.isSpeedRequired = false;
        criteria.powerRequirement = Criteria.POWER_MEDIUM

        return criteria
    }

}