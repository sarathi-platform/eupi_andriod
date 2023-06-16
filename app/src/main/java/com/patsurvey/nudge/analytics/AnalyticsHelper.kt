package com.patsurvey.nudge.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log.e
import android.util.Log.i
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonSyntaxException
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.utils.API_FAILED_EXCEPTION
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.ERROR_CODE_JSON_SYNTAX_ERROR
import com.patsurvey.nudge.utils.HTTP_EXCEPTION
import com.patsurvey.nudge.utils.IO_EXCEPTION
import com.patsurvey.nudge.utils.JSON_SYNTAX_EXCEPTION
import com.patsurvey.nudge.utils.PREF_KEY_USER_NAME
import com.patsurvey.nudge.utils.RESPONSE_CODE_NETWORK_ERROR
import com.patsurvey.nudge.utils.RESPONSE_CODE_TIMEOUT
import com.patsurvey.nudge.utils.SOCKET_TIMEOUT_EXCEPTION
import com.patsurvey.nudge.utils.UNKNOWN_EXCEPTION
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object AnalyticsHelper {

    private var firebaseAnalytics: FirebaseAnalytics? = null
//    private var coroutineScope: CoroutineScope? = null
    private var appContext: Context? = null
    private var mPrefRepo: PrefRepo? = null

    fun init(context: Context, prefRepo: PrefRepo) {
        appContext = context
        mPrefRepo = prefRepo
        if (firebaseAnalytics == null) firebaseAnalytics = FirebaseAnalytics.getInstance(context)
//        coroutineScope = scope
    }

    fun cleanup() {
        try {
            firebaseAnalytics = null
            appContext = null
            mPrefRepo = null
//            coroutineScope = null
        } catch (ex: Exception) {
            e("AnalyticsHelper", "cleanup", ex)
        }
    }

    fun logServiceFailedEvent(exception: Exception, apiType: ApiType) {
        val eventName = Events.API_FAILED.eventName
        val params = Bundle()
        params.putString(EventParams.EXCEPTION.eventParam, getExceptionName(exception = exception))
        params.putInt(EventParams.ERRORCODE.eventParam, getErrorCode(exception))
        params.putString(EventParams.SERVICECALL.eventParam, apiType.name)
        params.putString(EventParams.USER_NAME.eventParam, mPrefRepo?.getPref(PREF_KEY_USER_NAME, "") ?: "")
        params.putString(EventParams.SDK_INT.eventParam, EventValues.SDK_INT_VALUE.eventValue)
        params.putString(EventParams.BUILD_VERSION_NAME.eventParam, EventValues.BUILD_VERSION_NAME.eventValue)

        firebaseAnalytics?.logEvent(eventName, params)
        i("AnalyticsHelper", "logEvent- ${eventName} -> $params")

    }

    private fun getErrorCode(exception: Exception): Int {
        return when (exception) {
            is HttpException -> {
                exception.response()?.code() ?: 0
            }
            is SocketTimeoutException -> RESPONSE_CODE_TIMEOUT
            is IOException -> RESPONSE_CODE_NETWORK_ERROR
            is JsonSyntaxException -> ERROR_CODE_JSON_SYNTAX_ERROR
            else -> -1
        }
    }

    private fun getExceptionName(exception: Exception): String{
        return when (exception) {
            is HttpException -> HTTP_EXCEPTION
            is SocketTimeoutException -> SOCKET_TIMEOUT_EXCEPTION
            is IOException -> IO_EXCEPTION
            is JsonSyntaxException -> JSON_SYNTAX_EXCEPTION
            is ApiResponseFailException -> API_FAILED_EXCEPTION
            else -> UNKNOWN_EXCEPTION
        }
    }

}