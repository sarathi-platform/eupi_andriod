package com.patsurvey.nudge.data.prefs


interface PrefRepo {
   fun getAppLanguage():String?
   fun saveAppLanguage(code: String?)

   fun isPermissionGranted():Boolean?
   fun savePermissionGranted(isGranted: Boolean?)

   fun saveSelectedVillage(id: Int)

   fun getSelectedVillage(): Int?

   fun getLoginStatus(): Boolean

   fun getAccessToken(): String?
   fun saveAccessToken(token: String)

   fun setOnlineStatus(isOnline: Boolean)

   fun getOnlinceStatus(): Boolean

   fun saveMobileNumber(mobileNumber: String)

   fun getMobileNumber(): String?

   fun savePref(key: String, value: String)

   fun savePref(key: String, value: Int)

   fun savePref(key: String, value: Boolean)

   fun savePref(key: String, value: Long)

   fun savePref(key: String, value: Float)

   fun getPref(key: String, defaultValue: Int): Int

   fun getPref(key: String, defaultValue: String): String?

   fun getPref(key: String, defaultValue: Boolean): Boolean

   fun getPref(key: String, defaultValue: Long): Long

   fun getPref(key: String, defaultValue: Float): Float

}