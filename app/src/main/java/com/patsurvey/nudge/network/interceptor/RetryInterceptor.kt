package com.patsurvey.nudge.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class RetryInterceptor(private val maxRetryCount: Int, private val retryDelayMillis: Long) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var tryCount = 0
        var response: Response
        var lastException: IOException? = null

        while (true) {
            try {
                response = chain.proceed(chain.request())
                if (response.isSuccessful) {
                    return response
                } else {
                    throw IOException("Request failed with status code: ${response.code}")
                }
            } catch (e: IOException) {
                lastException = e
                tryCount++
                if (tryCount >= maxRetryCount) {
                    throw e // Exceed retry limit, throw the exception
                }
                // Wait before retrying
                TimeUnit.MILLISECONDS.sleep(retryDelayMillis)
            }
        }
    }
}
