package com.nudge.core.model

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.nudge.core.BLANK_STRING
import com.nudge.core.json

object CoreAppDetails {
     @SuppressLint("StaticFieldLeak")
     var mApplicationDetails :ApplicationDetails ?=null

    fun setApplicationDetails(applicationDetails: ApplicationDetails){
        mApplicationDetails=applicationDetails
    }

    fun getApplicationDetails(): ApplicationDetails? {
        return mApplicationDetails
    }
     data class ApplicationDetails(
         val packageName:String,
         val applicationID:String,
         val activity: Activity,
     )
 }
