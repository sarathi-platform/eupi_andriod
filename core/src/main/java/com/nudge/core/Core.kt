package com.nudge.core

import android.app.Activity
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Core {

    private var db: FirebaseFirestore? = null
    private var mApplicationContext:Activity ?=null
    fun init(context: Context) {
        initializeFirebaseDb()
        setContext(context)
    }

    companion object {
        private var mContext: Context? = null
        fun getContext(): Context? {
            return mContext
        }

        fun setContext(context: Context) {
            mContext = context
        }
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