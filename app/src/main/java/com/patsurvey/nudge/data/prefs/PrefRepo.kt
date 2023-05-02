package com.patsurvey.nudge.data.prefs


interface PrefRepo {
   fun getAppLanguage():String?
   fun saveAppLanguage(code: String?)

   fun isPermissionGranted():Boolean?
   fun savePermissionGranted(isGranted: Boolean?)

   fun saveSelectedVillage(id: Int)

   fun getSelectedVillage(): Int?
}