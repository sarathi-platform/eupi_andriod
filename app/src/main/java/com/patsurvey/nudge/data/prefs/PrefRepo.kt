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
}