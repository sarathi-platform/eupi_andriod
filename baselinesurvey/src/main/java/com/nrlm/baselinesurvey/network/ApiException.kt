package com.nrlm.baselinesurvey.network

import okhttp3.Headers
import java.io.IOException

class ApiException : IOException() {

    private var statusCode = 0
    private var headers: Headers? = null
    private var requestUrl: String? = null

    fun getHeaders(): Headers? {
        return headers
    }

    fun setHeaders(headers: Headers) {
        this.headers = headers
    }

    fun getStatusCode(): Int {
        return statusCode
    }

    fun setStatusCode(statusCode: Int) {

        this.statusCode = statusCode
    }

    fun setRequestUrl(url: String) {
        requestUrl = url
    }

    fun getRequestUrl(): String? {
        return requestUrl
    }
}