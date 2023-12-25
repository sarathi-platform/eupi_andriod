package com.nrlm.baselinesurvey.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.nrlm.baselinesurvey.R
import java.util.function.Consumer

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


                } catch (ex: Exception) {
                    BaselineLogger.e("LocationUtil", "getLocation", ex)
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
                Toast.makeText(
                    context,
                    context.getString(R.string.location_permission_not_granted_message),
                    Toast.LENGTH_LONG
                ).show()
            }
            showPermissionDialog = true
            return null
        }
        return null;
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getLocation(
        context: Activity,
        gpsConsumer: Consumer<Location>,
        networkConsumer: Consumer<Location>
    ) {
        BaselineLogger.d("LocationUtil", "getLocation(Build.VERSION_CODES.R): called")
        var locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            BaselineLogger.d(
                "LocationUtil",
                "getLocation(Build.VERSION_CODES.R): permission granted"
            )
            val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            BaselineLogger.d(
                "LocationUtil",
                "getLocation(Build.VERSION_CODES.R): hasGps: $hasGps, hasNetwork: $hasNetwork"
            )

            if (hasGps || hasNetwork) {
                if (hasGps) {
                    BaselineLogger.d(
                        "LocationUtil",
                        "getLocation(Build.VERSION_CODES.R): hasGps: locationManager.getCurrentLocation called"
                    )
                    locationManager.getCurrentLocation(
                        LocationManager.GPS_PROVIDER,
                        null,
                        context.mainExecutor,
                        gpsConsumer
                    )
                }
                if (hasNetwork) {
                    BaselineLogger.d(
                        "LocationUtil",
                        "getLocation(Build.VERSION_CODES.R): hasNetwork: locationManager.getCurrentLocation called"
                    )
                    locationManager.getCurrentLocation(
                        LocationManager.NETWORK_PROVIDER,
                        null,
                        context.mainExecutor,
                        networkConsumer
                    )
                }
            } else {
                BaselineLogger.d(
                    "LocationUtil",
                    "getLocation(Build.VERSION_CODES.R): hasGps: false || hasNetwork: false"
                )

//                Toast.makeText(context, "Location not enabled.", Toast.LENGTH_SHORT).show()
//                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
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
            BaselineLogger.d(
                "LocationUtil",
                "getLocation(Build.VERSION_CODES.R): permission not granted, permission requested again"
            )
            ActivityCompat.requestPermissions(
                context,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        } else {
            BaselineLogger.d(
                "LocationUtil",
                "getLocation(Build.VERSION_CODES.R): permission denied, custom permission prompt displayed"
            )
            context.runOnUiThread {
                Toast.makeText(context, "Location Permission Not Granted", Toast.LENGTH_LONG).show()
            }
            showPermissionDialog = true
        }
    }

    fun getLocation(
        context: Activity,
        gpsLocationListener: LocationListener,
        networkLocationListener: LocationListener
    ) {
        BaselineLogger.d("LocationUtil", "getLocation: called")
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            BaselineLogger.d("LocationUtil", "getLocation: permission granted")
            val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            BaselineLogger.d(
                "LocationUtil",
                "getLocation: hasGps: $hasGps, hasNetwork: $hasNetwork"
            )

            if (hasGps || hasNetwork) {
                if (hasGps) {
                    BaselineLogger.d(
                        "LocationUtil",
                        "getLocation: hasGps: locationManager.getCurrentLocation called"
                    )
                    locationManager.requestSingleUpdate(
                        LocationManager.GPS_PROVIDER,
                        gpsLocationListener,
                        Looper.getMainLooper()
                    )
                }
                if (hasNetwork) {
                    BaselineLogger.d(
                        "LocationUtil",
                        "getLocation: hasNetwork: locationManager.getCurrentLocation called"
                    )
                    locationManager.requestSingleUpdate(
                        LocationManager.NETWORK_PROVIDER,
                        networkLocationListener,
                        Looper.getMainLooper()
                    )
                }

            } else {
                BaselineLogger.d("LocationUtil", "getLocation: hasGps || hasNetwork: false")
//                Toast.makeText(context, "Location not enabled.", Toast.LENGTH_SHORT).show()
//                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
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
            BaselineLogger.d(
                "LocationUtil",
                "getLocation: permission not granted, permission requested again"
            )
            ActivityCompat.requestPermissions(
                context,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        } else {
            BaselineLogger.d(
                "LocationUtil",
                "getLocation: permission denied, custom permission prompt displayed"
            )
            context.runOnUiThread {
                Toast.makeText(context, "Location Permission Not Granted", Toast.LENGTH_LONG).show()
            }
            showPermissionDialog = true
        }
    }


    private fun isLocationEnabled(context: Activity, mLocationManager: LocationManager): Boolean {
        val locationEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mLocationManager.isLocationEnabled
        } else {
            val mode = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
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

    var location: LocationCoordinates = LocationCoordinates(0.0, 0.0)

    fun setLocation(context: Activity) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            var locationByGps: Location? = null
            var locationByNetwork: Location? = null
            val gpsConsumer = Consumer<Location> { gpsLocation ->
                BaselineLogger.d("LocationUtil", "setLocation -> gpsConsumer: called")
                if (gpsLocation != null) {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> gpsConsumer: gpsLocation != null => gpsLocation: $gpsLocation"
                    )
                    locationByGps = gpsLocation
                    location = LocationCoordinates(
                        locationByGps?.latitude ?: 0.0,
                        locationByGps?.longitude ?: 0.0
                    )
                } else {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> gpsConsumer: gpsLocation == null"
                    )
                }
            }
            val networkConsumer = Consumer<Location> { networkLocation ->
                BaselineLogger.d("LocationUtils", "setLocation -> networkLocation: called")
                if (networkLocation != null) {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> networkConsumer: gpsLocation != null => gpsLocation: $networkLocation"
                    )

                    locationByNetwork = networkLocation
                    location = LocationCoordinates(
                        locationByNetwork?.latitude ?: 0.0,
                        locationByNetwork?.longitude ?: 0.0
                    )
                } else {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> gpsConsumer: gpsLocation == null"
                    )
                }

            }
            getLocation(context, gpsConsumer, networkConsumer)
        } else {
            var locationByGps: Location? = null
            var locationByNetwork: Location? = null
            BaselineLogger.d("LocationUtils", "setLocation -> gpsLocationListener called")
            val gpsLocationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(gpsLocation: Location) {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> gpsLocationListener onLocationChanged: location => $location"
                    )
                    locationByGps = gpsLocation
                    location = LocationCoordinates(
                        locationByGps?.latitude ?: 0.0,
                        locationByGps?.longitude ?: 0.0
                    )
                }

                override fun onStatusChanged(
                    provider: String,
                    status: Int,
                    extras: Bundle
                ) {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> gpsLocationListener onStatusChanged: provider => $provider status: $status"
                    )
                }

                override fun onProviderEnabled(provider: String) {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> gpsLocationListener onProviderEnabled: provider => $provider"
                    )
                }

                override fun onProviderDisabled(provider: String) {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> gpsLocationListener onProviderDisabled: provider => $provider"
                    )
                }
            }
            BaselineLogger.d("LocationUtils", "setLocation -> networkLocationListener called")
            val networkLocationListener: LocationListener = object :
                LocationListener {
                override fun onLocationChanged(networkLocation: Location) {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> networkLocationListener onLocationChanged: location => $location"
                    )
                    locationByNetwork = networkLocation
                    location = LocationCoordinates(
                        locationByNetwork?.latitude ?: 0.0,
                        locationByNetwork?.longitude ?: 0.0
                    )
                }

                override fun onStatusChanged(
                    provider: String,
                    status: Int,
                    extras: Bundle
                ) {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> networkLocationListener onStatusChanged: provider => $provider status: $status"
                    )
                }

                override fun onProviderEnabled(provider: String) {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> networkLocationListener onProviderEnabled: provider => $provider"
                    )
                }

                override fun onProviderDisabled(provider: String) {
                    BaselineLogger.d(
                        "LocationUtils",
                        "setLocation -> networkLocationListener onProviderEnabled: provider => $provider"
                    )
                }
            }
            getLocation(
                context,
                gpsLocationListener,
                networkLocationListener
            )
        }
    }

}