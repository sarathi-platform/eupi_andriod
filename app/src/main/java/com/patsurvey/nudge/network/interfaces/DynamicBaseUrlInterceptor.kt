package com.patsurvey.nudge.network.interfaces

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class DynamicBaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newBaseUrl = ApiBaseUrlManager.baseUrl
        val newHttpUrl: HttpUrl = newBaseUrl.toHttpUrl()
        val updatedUrl = originalRequest.url.newBuilder()
            .scheme(newHttpUrl.scheme)
            .host(newHttpUrl.host)
            .port(newHttpUrl.port)
            .build()

        val updatedRequest = originalRequest.newBuilder().url(updatedUrl).build()

        return chain.proceed(updatedRequest)
    }
}