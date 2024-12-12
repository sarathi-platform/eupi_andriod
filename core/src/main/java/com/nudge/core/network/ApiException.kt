package com.nudge.core.network

sealed class ApiException(message: String) : Exception(message) {

    class HttpError(val statusCode: String, message: String) :
        ApiException("HTTP Error $statusCode: $message")

    class TimeoutException(message: String = "API request timed out") : ApiException(message)

    class HostNotFoundException(message: String = "No Internet connection found") :
        ApiException(message)

    class NullResponse(message: String = "Received null response with status code 200") :
        ApiException(message)

    class EmptyResponse(message: String = "Received empty response with status code 200") :
        ApiException(message)
}