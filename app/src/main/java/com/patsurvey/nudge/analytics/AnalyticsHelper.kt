package com.patsurvey.nudge.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log.e
import android.util.Log.i
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonSyntaxException
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.network.ApiServicesHelper
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.API_FAILED_EXCEPTION
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.ERROR_CODE_JSON_SYNTAX_ERROR
import com.patsurvey.nudge.utils.HTTP_EXCEPTION
import com.patsurvey.nudge.utils.IO_EXCEPTION
import com.patsurvey.nudge.utils.JSON_SYNTAX_EXCEPTION
import com.patsurvey.nudge.utils.PREF_MOBILE_NUMBER
import com.patsurvey.nudge.utils.RESPONSE_CODE_NETWORK_ERROR
import com.patsurvey.nudge.utils.RESPONSE_CODE_TIMEOUT
import com.patsurvey.nudge.utils.SOCKET_TIMEOUT_EXCEPTION
import com.patsurvey.nudge.utils.UNKNOWN_EXCEPTION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object AnalyticsHelper {

    private var firebaseAnalytics: FirebaseAnalytics? = null
//    private var coroutineScope: CoroutineScope? = null
    private var appContext: Context? = null
    var mPrefRepo: PrefRepo? = null
    var mApiService: ApiService? = null

    fun init(context: Context, prefRepo: PrefRepo, apiService: ApiService) {
        appContext = context
        mPrefRepo = prefRepo
        mApiService = apiService
        if (firebaseAnalytics == null)
            firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        firebaseAnalytics?.setUserProperty(EventParams.USER_NAME.eventParam, mPrefRepo?.getMobileNumber())
//        coroutineScope = scope
    }

    fun cleanup() {
        try {
            firebaseAnalytics = null
            appContext = null
            mPrefRepo = null
            mApiService = null
//            coroutineScope = null
        } catch (ex: Exception) {
            e("AnalyticsHelper", "cleanup", ex)
        }
    }

    fun logEvent(events: Events, paramsMap: Map<EventParams, Any>? = null) {
        val params = Bundle()
        val paramsForLogs: MutableMap<String, Any> = mutableMapOf()
        if (paramsMap != null) {
            for ((key, value) in paramsMap) {
                when (value) {
                    is Int -> params.putLong(key.eventParam, value.toLong())
                    is Long -> params.putLong(key.eventParam, value)
                    is String -> params.putString(key.eventParam, value)
                    is Double -> params.putDouble(key.eventParam, value)
                    else -> params.putString(key.eventParam, value.toString())
                }
            }
        }
        params.putString(EventParams.USER_NAME.eventParam, mPrefRepo?.getPref(PREF_MOBILE_NUMBER, "") ?: "")
        paramsForLogs[EventParams.USER_NAME.eventParam] = mPrefRepo?.getPref(PREF_MOBILE_NUMBER, "") ?: ""

        params.putString(EventParams.SDK_INT.eventParam, EventValues.SDK_INT_VALUE.eventValue)
        paramsForLogs[EventParams.SDK_INT.eventParam] = EventValues.SDK_INT_VALUE.eventValue

        params.putString(EventParams.BUILD_VERSION_NAME.eventParam, EventValues.BUILD_VERSION_NAME.eventValue)
        paramsForLogs[EventParams.BUILD_VERSION_NAME.eventParam] = EventValues.BUILD_VERSION_NAME.eventValue

        params.putString(EventParams.BUILD_DEVICE.eventParam, EventValues.DEVICE.eventValue)
        paramsForLogs[EventParams.BUILD_DEVICE.eventParam] = EventValues.DEVICE.eventValue

        params.putString(EventParams.BUILD_MANUFACTURER.eventParam, EventValues.MANUFACTURER.eventValue)
        paramsForLogs[EventParams.BUILD_MANUFACTURER.eventParam] =  EventValues.MANUFACTURER.eventValue

        params.putString(EventParams.BUILD_MODEL.eventParam, EventValues.MODEL.eventValue)
        paramsForLogs[EventParams.BUILD_MODEL.eventParam] =  EventValues.MODEL.eventValue

        params.putString(EventParams.BUILD_BRAND.eventParam, EventValues.BRAND.eventValue)
        paramsForLogs[EventParams.BUILD_BRAND.eventParam] =  EventValues.BRAND.eventValue

        firebaseAnalytics?.logEvent(events.eventName, params)
        CoroutineScope(Dispatchers.IO).launch {
            mApiService?.addLogs("${events.eventName}: $params ")
        }
        i("AnalyticsHelper", "logEvent- ${events.eventName} -> $params")
    }

    fun logServiceFailedEvent(exception: Exception, apiType: ApiType) {
        val eventName = Events.API_FAILED.eventName
        val params = Bundle()
        val paramsForLogs: MutableMap<String, Any> = mutableMapOf()


        params.putString(EventParams.EXCEPTION.eventParam, getExceptionName(exception = exception))
        paramsForLogs[EventParams.EXCEPTION.eventParam] = getExceptionName(exception = exception)

        params.putInt(EventParams.ERRORCODE.eventParam, getErrorCode(exception))
        paramsForLogs[EventParams.ERRORCODE.eventParam] = getErrorCode(exception = exception)

        params.putString(EventParams.SERVICE_CALL_TYPE.eventParam, apiType.name)
        paramsForLogs[EventParams.ERRORCODE.eventParam] = getErrorCode(exception = exception)

        params.putString(EventParams.API_PATH.eventParam, ApiServicesHelper.getApiSubPath(apiType))
        paramsForLogs[EventParams.API_PATH.eventParam] = ApiServicesHelper.getApiSubPath(apiType)

        params.putString(EventParams.USER_NAME.eventParam, mPrefRepo?.getPref(PREF_MOBILE_NUMBER, "") ?: "")
        paramsForLogs[EventParams.USER_NAME.eventParam] = mPrefRepo?.getPref(PREF_MOBILE_NUMBER, "") ?: ""

        params.putString(EventParams.SDK_INT.eventParam, EventValues.SDK_INT_VALUE.eventValue)
        paramsForLogs[EventParams.SDK_INT.eventParam] = EventValues.SDK_INT_VALUE.eventValue

        params.putString(EventParams.BUILD_VERSION_NAME.eventParam, EventValues.BUILD_VERSION_NAME.eventValue)
        paramsForLogs[EventParams.BUILD_VERSION_NAME.eventParam] = EventValues.BUILD_VERSION_NAME.eventValue

        params.putString(EventParams.BUILD_DEVICE.eventParam, EventValues.DEVICE.eventValue)
        paramsForLogs[EventParams.BUILD_DEVICE.eventParam] = EventValues.DEVICE.eventValue

        params.putString(EventParams.BUILD_MANUFACTURER.eventParam, EventValues.MANUFACTURER.eventValue)
        paramsForLogs[EventParams.BUILD_MANUFACTURER.eventParam] =  EventValues.MANUFACTURER.eventValue

        params.putString(EventParams.BUILD_MODEL.eventParam, EventValues.MODEL.eventValue)
        paramsForLogs[EventParams.BUILD_MODEL.eventParam] =  EventValues.MODEL.eventValue

        params.putString(EventParams.BUILD_BRAND.eventParam, EventValues.BRAND.eventValue)
        paramsForLogs[EventParams.BUILD_BRAND.eventParam] =  EventValues.BRAND.eventValue

        firebaseAnalytics?.logEvent(eventName, params)
        CoroutineScope(Dispatchers.IO).launch {
            var paramsValue: String = ""
            for ((key, value) in paramsForLogs) {
                paramsValue = "$paramsValue $key: ${value.toString()},"
            }
            mApiService?.addLogs("${eventName}-> $paramsValue")
        }
        i("AnalyticsHelper", "logServiceFailedEvent- ${eventName} -> $params")

    }

    fun logLocationEvents(events: Events, paramsMap: Map<EventParams, Any>? = null) {
        val params = Bundle()
        val paramsForLogs: MutableMap<String, Any> = mutableMapOf()
        if (paramsMap != null) {
            for ((key, value) in paramsMap) {
                when (value) {
                    is Int -> params.putLong(key.eventParam, value.toLong())
                    is Long -> params.putLong(key.eventParam, value)
                    is String -> params.putString(key.eventParam, value)
                    is Double -> params.putDouble(key.eventParam, value)
                    else -> params.putString(key.eventParam, value.toString())
                }
                paramsForLogs[key.eventParam] = value
            }
        }

        params.putString(EventParams.USER_NAME.eventParam, mPrefRepo?.getPref(PREF_MOBILE_NUMBER, "") ?: "")
        paramsForLogs[EventParams.USER_NAME.eventParam] = mPrefRepo?.getPref(PREF_MOBILE_NUMBER, "") ?: ""

        params.putString(EventParams.SDK_INT.eventParam, EventValues.SDK_INT_VALUE.eventValue)
        paramsForLogs[EventParams.SDK_INT.eventParam] = EventValues.SDK_INT_VALUE.eventValue

        params.putString(EventParams.BUILD_VERSION_NAME.eventParam, EventValues.BUILD_VERSION_NAME.eventValue)
        paramsForLogs[EventParams.BUILD_VERSION_NAME.eventParam] = EventValues.BUILD_VERSION_NAME.eventValue

        params.putString(EventParams.BUILD_DEVICE.eventParam, EventValues.DEVICE.eventValue)
        paramsForLogs[EventParams.BUILD_DEVICE.eventParam] = EventValues.DEVICE.eventValue

        params.putString(EventParams.BUILD_MANUFACTURER.eventParam, EventValues.MANUFACTURER.eventValue)
        paramsForLogs[EventParams.BUILD_MANUFACTURER.eventParam] =  EventValues.MANUFACTURER.eventValue

        params.putString(EventParams.BUILD_MODEL.eventParam, EventValues.MODEL.eventValue)
        paramsForLogs[EventParams.BUILD_MODEL.eventParam] =  EventValues.MODEL.eventValue

        params.putString(EventParams.BUILD_BRAND.eventParam, EventValues.BRAND.eventValue)
        paramsForLogs[EventParams.BUILD_BRAND.eventParam] =  EventValues.BRAND.eventValue


        firebaseAnalytics?.logEvent(events.eventName, params)
        CoroutineScope(Dispatchers.IO).launch {
            var paramsValue: String = ""
            for ((key, value) in paramsForLogs) {
                paramsValue = "$paramsValue $key: ${value.toString()},"
            }
            mApiService?.addLogs("${events.eventName}-> $paramsValue")
        }
        i("AnalyticsHelper", "logLocationEvents- ${events.eventName} -> $params")
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