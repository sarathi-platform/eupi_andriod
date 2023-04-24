package com.patsurvey.nudge.utils

import com.google.accompanist.permissions.ExperimentalPermissionsApi

object PermissionUtil {

    const val PREF_ACCESS_FINE_LOCATION_PERMISSION = "PREF_ACCESS_FINE_LOCATION_PERMISSION"
    const val PREF_ACCESS_COARSE_LOCATION_PERMISSION = "PREF_ACCESS_COARSE_LOCATION_PERMISSION"
    const val PREF_CAMERA_PERMISSION = "PREF_CAMERA_PERMISSION"


    @OptIn(ExperimentalPermissionsApi::class)
    fun setPermissionGranted(permission: String, isGranted: Boolean) {
        NugdePrefs.setPref(permission, isGranted)
    }

    fun isPermissionGranted(permission: String): Boolean {
        return NugdePrefs.setPref(permission, false)
    }

}