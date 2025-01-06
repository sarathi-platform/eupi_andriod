package com.nudge.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nudge.core.model.EventLimitAlertUiModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class NotificationHandler @Inject constructor(@ApplicationContext private val context: Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)


    fun createSoftAlertNotification(softAlertUiModel: EventLimitAlertUiModel) {

        val notification =
            NotificationCompat.Builder(context, SOFT_ALERT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(softAlertUiModel.alertTitle)
                .setContentText(softAlertUiModel.alertMessage)
                .setSmallIcon(softAlertUiModel.alertIcon)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = SOFT_ALERT_NOTIFICATION_CHANNEL_ID
            val channel = NotificationChannel(
                channelId,
                "Soft Event Limit Alert channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            notification.setChannelId(SOFT_ALERT_NOTIFICATION_CHANNEL_ID)
        }



        if (checkIfSoftAlertNotificationIsAlreadyPresent()) {
            notificationManager.cancel(SOFT_ALERT_NOTIFICATION_TAG, SOFT_ALERT_NOTIFICATION_ID)
        }

        notificationManager.notify(
            SOFT_ALERT_NOTIFICATION_TAG,
            SOFT_ALERT_NOTIFICATION_ID,
            notification.build()
        )

    }

    private fun checkIfSoftAlertNotificationIsAlreadyPresent(): Boolean {
        val activeNotification = notificationManager.activeNotifications
        return activeNotification.any { it.id == SOFT_ALERT_NOTIFICATION_ID || it.tag == SOFT_ALERT_NOTIFICATION_TAG }
    }


}

const val SOFT_ALERT_NOTIFICATION_CHANNEL_ID = "soft_alert_notification_channel_id"
const val SOFT_ALERT_NOTIFICATION_TAG = "soft_alert_notification_tag"
const val SOFT_ALERT_NOTIFICATION_ID = 1