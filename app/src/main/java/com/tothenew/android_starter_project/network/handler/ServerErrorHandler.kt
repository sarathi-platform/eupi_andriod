package com.tothenew.android_starter_project.network.handler

import com.google.gson.GsonBuilder
import com.tothenew.android_starter_project.network.BaseNetworkConstants
import com.tothenew.android_starter_project.network.interfaces.FailureAPICallback
import com.tothenew.android_starter_project.network.model.ErrorDataModel
import java.lang.ref.WeakReference

object ServerErrorHandler {

    /**
     * Handles error response from Retrofit
     *
     * @param errorBody
     * @param failureAPICallback
     * @param httpCode
     * @param reqUrl
     */
    fun handleErrorResponse(
        errorBody: String,
        failureAPICallback: FailureAPICallback?,
        httpCode: Int,
        reqUrl: String,
        method: String
    ) {
        var mError: ErrorDataModel? = null
        try {
            mError = GsonBuilder().create().fromJson(errorBody, ErrorDataModel::class.java)
        } catch (e: Exception) {
//            Logger.printStackTrace(e)
        }

//        Logger.e("errorResponse", mError?.message.toString())

            failureAPICallback?.onFailure(
                code = mError?.errorCode ?: BaseNetworkConstants.CODE_INVALID,
                reqUrl = reqUrl,
                throwable = null,
                errorMessage = mError?.message,
                httpCode = httpCode,
                mError = mError
            )
        }
    }

    /**
     * Handles error responses from Apollo
     *
     * @param errorBody
     * @param failureAPICallback
     * @param httpCode
     * @param reqUrl
     */
    fun handleApolloErrorResponse(
        errorBody: String,
        failureAPICallback: FailureAPICallback?,
        httpCode: Int,
        reqUrl: String
    ) {
        var mError: ErrorDataModel? = null

        try {
            mError = GsonBuilder().create().fromJson(errorBody, ErrorDataModel::class.java)
        } catch (e: Exception) {
//            e.message?.let { it1 -> Logger.e("Exception", it1) }
        }

      //  Logger.e("errorResponse", mError?.message.toString())

            failureAPICallback?.onFailure(
                code = mError?.errorCode ?: BaseNetworkConstants.CODE_INVALID,
                reqUrl = reqUrl,
                throwable = null,
                errorMessage = mError?.message,
                httpCode = httpCode
            )


}
