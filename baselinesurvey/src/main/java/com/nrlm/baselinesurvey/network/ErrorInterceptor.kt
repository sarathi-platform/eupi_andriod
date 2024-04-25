package com.nrlm.baselinesurvey.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class ErrorInterceptor @Inject constructor(): Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        try {


        val originalRequest = chain.request()
        val request = originalRequest.newBuilder().build()
        val response = chain.proceed(request)
            // Inspect status codes of unsuccessful responses
            if (response.code >= 300) {
            val apiException = ApiException()
            apiException.setStatusCode(statusCode = response.code)
            apiException.setHeaders(headers = response.headers)
            apiException.setRequestUrl(url = response.request.url.toString())
            throw apiException
        }
            return response

        } catch (exception: Exception) {

            throw exception
        }

    }
}
