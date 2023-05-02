package com.patsurvey.nudge.base

import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.network.NetworkResult
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.repository.ConfigRepositoryLocal
import kotlinx.coroutines.*
import retrofit2.Response
import javax.inject.Inject

open class BaseRepository{

    // job
    private var repoJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + repoJob)

    @Inject
    lateinit var apiInterface: ApiService

    suspend fun <T> safeApiCall(apiCall:suspend ()->Response<T>):NetworkResult<T>{
        try {
            val response=apiCall()
            if(response.isSuccessful){
                val body=response.body()
                body?.let {
                    return NetworkResult.Success(it)
                }
            }
            return error("${response.code()} ${response.message()}")
        }catch (e:Exception){
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error("Api call failed $errorMessage")
}