package com.nudge.core

import android.app.Activity
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

class Core {

    private var db: FirebaseFirestore? = null
    private var mApplicationContext:Activity ?=null
    fun init(context: Context) {
        initializeFirebaseDb()
        setContext(context)
        initMixPanelAnalytics()
    }

    companion object {
        private var mContext: Context? = null
        private var mixpanelAPI: MixpanelAPI? = null

        fun getContext(): Context? {
            return mContext
        }

        fun setContext(context: Context) {
            mContext = context
        }
        fun setUserForMixPanel(mobileNo: String, name: String, userType: String) {
            mixpanelAPI?.identify(mobileNo, true);

// Identify must be called before properties are set
            mixpanelAPI?.getPeople()?.set("name", name);
            mixpanelAPI?.getPeople()?.set("userType", userType);
        }

        fun trackEvent(map: Map<String, String>, eventName: String) {
            mixpanelAPI?.track(eventName, JSONObject(map))

        }

    }

    private fun initMixPanelAnalytics() {
        mixpanelAPI = MixpanelAPI.getInstance(mContext, "618e2ccf90d6fcc20c66280888c938cd", true);

    }


    private fun initializeFirebaseDb() {
        db = Firebase.firestore
    }

    fun getFirebaseDb(): FirebaseFirestore? {
        if (db == null) {
            initializeFirebaseDb()
            return db
        }
        return db

    }

    fun setMainActivityContext(context: Activity){
        mApplicationContext=context
    }
    fun getMainActivityContext(): Activity? {
        return mApplicationContext
    }




}