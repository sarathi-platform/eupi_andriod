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
import com.patsurvey.nudge.analytics.AnalyticsHelper
import com.patsurvey.nudge.analytics.EventParams
import com.patsurvey.nudge.analytics.Events

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


            if (isLocationEnabled(context, mLocationManager)) {
                val criteria = getCriteria(
                    if (batteryLevel > 30)
                        Criteria.ACCURACY_FINE
                    else
                        Criteria.ACCURACY_COARSE
                )
                try {
                    val locationProvider = mLocationManager.getBestProvider(
                        criteria, true
                    )

                    val location =
                        locationProvider?.let { mLocationManager.getLastKnownLocation(it) }

                    Log.d(
                        "LocationUtil",
                        "locationProvider: $locationProvider, location: lat-${location?.latitude}, long-${location?.longitude}"
                    )


                    return if (location != null) {
                        AnalyticsHelper.logLocationEvents(
                            Events.LOCATION_FETCHED,
                            mapOf(
                                EventParams.LOCATION_CRITERIA to "$criteria",
                                EventParams.LOCATION_PROVIDER to locationProvider!!,
                            )
                        )
                        LocationCoordinates(location.latitude, location.longitude)
                    } else {
                        context.runOnUiThread {
                            Toast.makeText(context, "Location not Available", Toast.LENGTH_LONG)
                                .show()
                        }
                        AnalyticsHelper.logLocationEvents(
                            Events.LOCATION_FETCH_FAILED,
                            mapOf(
                                EventParams.LOCATION_CRITERIA to "$criteria",
                                EventParams.LOCATION_PROVIDER to locationProvider!!,
                            )
                        )
                        LocationCoordinates(0.0, 0.0)
                    }
                } catch (ex: Exception) {
                    AnalyticsHelper.logLocationEvents(
                        Events.LOCATION_FETCH_FAILED,
                        mapOf(
                            EventParams.LOCATION_CRITERIA to "$criteria",
                            EventParams.EXCEPTION to ex.message!!,
                        )
                    )
                    return null
                }
            } else {
                context.runOnUiThread {
                    Toast.makeText(context, "Location not Enabled", Toast.LENGTH_LONG).show()
                }
                return null
            }

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            && ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            return null
        } else {
            context.runOnUiThread {
                Toast.makeText(context, "Location Permission Not Granted", Toast.LENGTH_LONG).show()
            }
            AnalyticsHelper.logLocationEvents(
                Events.LOCATION_PERMISSION_GRANTED,
                mapOf(
                    EventParams.PERMISSION_GRANTED to (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED)
                )
            )
            showPermissionDialog = true
            return null
        }
    }


    private fun isLocationEnabled(context: Activity, mLocationManager: LocationManager): Boolean {
        val locationEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            AnalyticsHelper.logLocationEvents(
                Events.LOCATION_ENABLED,
                mapOf(
                    EventParams.LOCATION_ENABLED to (mLocationManager.isLocationEnabled)
                )
            )
            mLocationManager.isLocationEnabled
        } else {
            val mode = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            AnalyticsHelper.logLocationEvents(
                Events.LOCATION_ENABLED,
                mapOf(
                    EventParams.LOCATION_ENABLED to (mode != Settings.Secure.LOCATION_MODE_OFF),
                    EventParams.LOCATION_MODE to getLocationSettingModeFromInt(mode),
                    EventParams.LOCATION_MODE_INT to mode
                )
            )
            (mode != Settings.Secure.LOCATION_MODE_OFF)
        }
        return locationEnabled
    }

    private fun getLocationSettingModeFromInt(mode: Int): String {
        return when (mode) {
            0 -> "LOCATION_MODE_OFF"
            1 -> "LOCATION_MODE_SENSORS_ONLY"
            2 -> "LOCATION_MODE_BATTERY_SAVING"
            3 -> "LOCATION_MODE_HIGH_ACCURACY"
            else -> "LOCATION_MODE_OFF"
        }
    }

    private fun getCriteria(accuracy: Int): Criteria {

        val criteria = Criteria()
        criteria.accuracy = accuracy
        criteria.isAltitudeRequired = false;
        criteria.isBearingRequired = false;
        criteria.isSpeedRequired = false;
        criteria.powerRequirement = Criteria.POWER_MEDIUM
        Log.d("getCriteria: ", "$criteria")

        return criteria
    }

    var showPermissionDialog = false

}