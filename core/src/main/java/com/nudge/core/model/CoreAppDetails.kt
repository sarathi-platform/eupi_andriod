package com.nudge.core.model

import android.annotation.SuppressLint
import android.app.Activity

object CoreAppDetails {
     @SuppressLint("StaticFieldLeak")
     var mApplicationDetails :ApplicationDetails ?=null

    fun setApplicationDetails(applicationDetails: ApplicationDetails){
        mApplicationDetails=applicationDetails
    }

    fun getApplicationDetails(): ApplicationDetails? {
        return mApplicationDetails
    }

    fun getContext() = mApplicationDetails?.activity?.applicationContext!!

     data class ApplicationDetails(
         val packageName:String,
         val applicationID:String,
         val activity: Activity,
     )
 }
