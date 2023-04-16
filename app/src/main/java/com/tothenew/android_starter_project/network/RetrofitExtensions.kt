package com.tothenew.android_starter_project.network


import com.tothenew.android_starter_project.base.BaseResponseModel
import com.tothenew.android_starter_project.network.handler.ResponseHandler
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import retrofit2.Call
import retrofit2.Callback

suspend fun Call<BaseResponseModel>.execute(responseHandler: ResponseHandler<BaseResponseModel>) =
    suspendCoroutine<retrofit2.Response<BaseResponseModel>> { continuation ->

        enqueue(object : Callback<BaseResponseModel> {

            override fun onFailure(call: Call<BaseResponseModel>, throwable: Throwable) {
                responseHandler.onFailure(call = call, throwable = throwable)
                continuation.resumeWithException(exception = throwable)
            }

            override fun onResponse(call: Call<BaseResponseModel>, response: retrofit2.Response<BaseResponseModel>) {
                responseHandler.onResponse(call = call, response = response)
                continuation.resume(value = response)
            }
        })
    }
