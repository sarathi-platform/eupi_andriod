package com.nrlm.baselinesurvey.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.GzipSource
import okio.buffer
import java.io.IOException


class GzipInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest: Request.Builder = chain.request().newBuilder()
        newRequest.addHeader("Accept-Encoding", "gzip")
        val response: Response = chain.proceed(newRequest.build())
        return if (isGzipped(response)) {
            unzip(response)
        } else {
            response
        }
    }

    @Throws(IOException::class)
    private fun unzip(response: Response): Response {
        if (response.body == null) {
            return response
        }
        val gzipSource = GzipSource(response.body!!.source())
        val bodyString = gzipSource.buffer().readUtf8()
        val responseBody: ResponseBody =
            ResponseBody.create(response.body!!.contentType(), bodyString)
        val strippedHeaders = response.headers.newBuilder()
            .removeAll("Content-Encoding")
            .removeAll("Content-Length")
            .build()
        return response.newBuilder()
            .headers(strippedHeaders)
            .body(responseBody)
            .message(response.message)
            .build()
    }

    private fun isGzipped(response: Response): Boolean {
        return response.header("Content-Encoding") != null && response.header("Content-Encoding") == "gzip"
    }
}