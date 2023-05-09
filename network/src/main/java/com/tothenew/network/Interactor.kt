package com.tothenew.network

import android.content.Context
import com.tothenew.network.utils.API_ERROR
import com.tothenew.network.utils.NETWORK_FAILURE
import com.tothenew.network.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

object Interactor {
    suspend fun <RES : Any> handleRequest(
        context: Context,
        requestFunc: suspend () -> RES
    ): Result<RES> {
        return if (NetworkUtil.isInternetAvailable(context)) {
            try {
                withContext(Dispatchers.Default) {
                    val data = requestFunc.invoke()

                    Result.Success<RES>(data, "", 0)
                }
            } catch (he: Throwable) {
                return if (he is HttpException) {
                    if (he.code() == 401) {
                        Result.Failure<RES>(Error.Unauthorized, he.message, he.code())
                    } else {
                        Result.Failure<RES>(Error.ServerError, he.message, he.code())
                    }
                } else {
                    Result.Failure<RES>(Error.ServerError, he.message, API_ERROR)
                }
            }
        } else {
            Result.Failure<RES>(Error.NetworkConnection, "Something went wrong", NETWORK_FAILURE)
        }
    }
}