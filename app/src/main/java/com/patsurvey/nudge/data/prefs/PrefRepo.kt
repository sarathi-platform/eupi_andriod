package com.patsurvey.nudge.data.prefs


interface PrefRepo {
   fun getAppLanguage():String?
   fun saveAppLanguage(code: String?)

   fun isPermissionGranted():Boolean?
   fun savePermissionGranted(isGranted: Boolean?)

   fun getLoginStatus(): Boolean

   fun getAccessToken(): String?
   fun saveAccessToken(token: String)

}