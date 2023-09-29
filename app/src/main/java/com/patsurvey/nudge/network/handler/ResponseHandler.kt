package com.patsurvey.nudge.network.handler

import com.patsurvey.nudge.network.interfaces.FailureAPICallback
import com.patsurvey.nudge.network.interfaces.SuccessAPICallback
import com.patsurvey.nudge.base.BaseResponseModel
import com.patsurvey.nudge.network.BaseNetworkConstants
import retrofit2.Call
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class ResponseHandler<T : BaseResponseModel> : retrofit2.Callback<T> {

    private var mSuccessAPICallback: SuccessAPICallback<T>? = null
    private var mFailureAPICallback: FailureAPICallback? = null
    private var mCall: Call<T>? = null

    constructor(successAPICallback: SuccessAPICallback<T>) {
        mSuccessAPICallback = successAPICallback
    }

    constructor(successAPICallback: SuccessAPICallback<T>, failureAPICallback: FailureAPICallback) {
        mSuccessAPICallback = successAPICallback
        mFailureAPICallback = failureAPICallback
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.code() == 200 && mSuccessAPICallback != null) {
            response.body()?.let(mSuccessAPICallback!!::onResponse)
        } else {
            val reqUrl = response.raw().request.url.toString()
            val method = response.raw().request.method
            response.errorBody()
                ?.let {
                    ServerErrorHandler.handleErrorResponse(
                        errorBody = it.string(),
                        failureAPICallback = mFailureAPICallback,
                        httpCode = response.code(),
                        reqUrl = reqUrl,
                        method = method
                    )
                } ?: run {
                mFailureAPICallback?.onFailure(
                    code = BaseNetworkConstants.CODE_SERVER_ERROR,
                    reqUrl = reqUrl,
                    throwable = null,
                    httpCode = response.code()
                )
            }
        }
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
        if (throwable is UnknownHostException ||
            throwable is ConnectException ||
            throwable is SocketTimeoutException ||
            throwable is SSLException
        ) {
            mFailureAPICallback?.onFailure(
                code = BaseNetworkConstants.CODE_NO_NETWORK,
                reqUrl = call.request().url.toString(),
                throwable = throwable
            )
        } else if (!call.isCanceled) {
            mFailureAPICallback?.onFailure(
                code = BaseNetworkConstants.CODE_SERVER_ERROR,
                reqUrl = call.request().url.toString(),
                throwable = throwable
            )
        }
    }
}
