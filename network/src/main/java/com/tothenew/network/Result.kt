package com.tothenew.network

sealed class Result<RES> constructor(private val value: Any?, var isOutput: Boolean = false) {
    val isSuccess = value !is Error && value !is Unit
    val isFailure = value is Error

    data class Success<RES>(val data: RES, var message: String?, val code: Int?) :
        Result<RES>(data, true)

    data class Failure<RES>(val exception: Error, var message: String?, val code: Int?) :
        Result<RES>(exception, true)

    data class LOAD<RES>(val nothing: Nothing? = null) : Result<RES>(nothing, false)
}


/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific error should extend [FeatureError] class.
 */
open class Error {
    /**
     * Case when
     * Network failure
     * NETWORK_FAILURE
     * */
    object NetworkConnection : Error()

    object ServerError : Error()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureError : Error()

    object APIError : FeatureError()

    object Unauthorized : FeatureError()
}
