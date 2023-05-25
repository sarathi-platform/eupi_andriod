package com.patsurvey.nudge.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DownloadCompleteReceiver: BroadcastReceiver() {

    private val TAG = DownloadCompleteReceiver::class.java.simpleName

    private lateinit var downloadManager: DownloadManager

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (intent.action == "android. intent. action. DOWNLOAD_COMPLETE") {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id != -1L) {
                     Log.d(TAG, "Download with Download Id: $id finished")
                }
            }
        }
    }
}