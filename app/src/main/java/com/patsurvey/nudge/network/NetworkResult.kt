package com.patsurvey.nudge.network

data class NetworkResult<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> Success(data: T): NetworkResult<T> {
            return NetworkResult(NetworkResult.Status.SUCCESS, data, null)
        }

        fun <T> Error(message: String, data: T? = null): NetworkResult<T> {
            return NetworkResult(NetworkResult.Status.ERROR, data, message)
        }

        fun <T> Loading(data: T? = null): NetworkResult<T> {
            return NetworkResult(NetworkResult.Status.LOADING, data, null)
        }
    }

}
