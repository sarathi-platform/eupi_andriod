package com.patsurvey.nudge.base

import com.patsurvey.nudge.network.interfaces.FailureAPICallback
import com.patsurvey.nudge.network.interfaces.SuccessAPICallback
import com.patsurvey.nudge.network.handler.ResponseHandler
import androidx.lifecycle.MutableLiveData
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorDataModel
import kotlinx.coroutines.*
import retrofit2.Call
import javax.inject.Inject

open class BaseRepository {

    // job
    private var repoJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + repoJob)

    @Inject
    lateinit var apiInterface: ApiService

    /**
     * generic async api call for retrofit
     *
     * @return live data
     */
    fun callApi(
        call: Call<*>,
        response: MutableLiveData<ApiResponseModel>? = null,
        onSuccess: ((BaseResponseModel) -> Boolean)? = null,
        onFailure: ((Int) -> Boolean)? = null
    ): MutableLiveData<ApiResponseModel> {

        val onResponseLiveData = response ?: MutableLiveData()

        val responseHandler = ResponseHandler(
            object :
                SuccessAPICallback<BaseResponseModel> {
                override fun onResponse(t: BaseResponseModel) {
                    uiScope.launch {
                        withContext(Dispatchers.IO) {
                            val handled = onSuccess?.invoke(t)
                            if (handled == null || !handled) {
                                onResponseLiveData.postValue(
                                    ApiResponseModel(t, null, null, null)
                                )
                            }
                        }
                    }
                }
            },
            object : FailureAPICallback {
                override fun onFailure(
                    code: Int,
                    reqUrl: String,
                    throwable: Throwable?,
                    errorMessage: String?,
                    httpCode: Int,
                    mError: ErrorDataModel?
                ) {
                    val handled = onFailure?.invoke(code)
                    if (handled == null || !handled) {
                        onResponseLiveData.value =
                            ApiResponseModel(
                                null,
                                code,
                                reqUrl,
                                throwable,
                                errorMessage,
                                httpCode = httpCode,
                                mError = mError
                            )
                    }
                }
            }
        )


                try {
                    @Suppress("UNCHECKED_CAST")
                    (call as Call<BaseResponseModel>).enqueue(responseHandler)
                } catch (ignored: Exception) {
                    // ignored here because failure will be handled by ResponseHandler.FailureAPICallback
                }


        return onResponseLiveData
    }
}