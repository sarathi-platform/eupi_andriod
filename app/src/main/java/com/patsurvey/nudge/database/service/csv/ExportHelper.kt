package com.patsurvey.nudge.database.service.csv

import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.analytics.AnalyticsHelper
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.service.csv.adapter.AnswerTableCSV
import com.patsurvey.nudge.database.service.csv.adapter.DataDumpRequestParts
import com.patsurvey.nudge.database.service.csv.adapter.DataDumpTableName
import com.patsurvey.nudge.database.service.csv.adapter.DidiTableCSV
import com.patsurvey.nudge.database.service.csv.adapter.NumericAnswerTableCSV
import com.patsurvey.nudge.database.service.csv.adapter.TolaTableCSV
import com.patsurvey.nudge.database.service.csv.adapter.toCsv
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ANSWER_TABLE
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.COMMON_ERROR_MSG
import com.patsurvey.nudge.utils.DIDI_TABLE
import com.patsurvey.nudge.utils.NUMERIC_TABLE_NAME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.RESPONSE_CODE_500
import com.patsurvey.nudge.utils.RESPONSE_CODE_BAD_GATEWAY
import com.patsurvey.nudge.utils.RESPONSE_CODE_CONFLICT
import com.patsurvey.nudge.utils.RESPONSE_CODE_DEACTIVATED
import com.patsurvey.nudge.utils.RESPONSE_CODE_NETWORK_ERROR
import com.patsurvey.nudge.utils.RESPONSE_CODE_NOT_FOUND
import com.patsurvey.nudge.utils.RESPONSE_CODE_NO_DATA
import com.patsurvey.nudge.utils.RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE
import com.patsurvey.nudge.utils.RESPONSE_CODE_TIMEOUT
import com.patsurvey.nudge.utils.RESPONSE_CODE_UNAUTHORIZED
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.TIMEOUT_ERROR_MSG
import com.patsurvey.nudge.utils.TOLA_TABLE
import com.patsurvey.nudge.utils.UNAUTHORISED_MESSAGE
import com.patsurvey.nudge.utils.UNREACHABLE_ERROR_MSG
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class ExportHelper @Inject constructor(
    private val apiService: ApiService,
    private val prefRepo: PrefRepo,
    private val didiDao: DidiDao,
    private val tolaDao: TolaDao,
    private val answerDao: AnswerDao,
    private val numericAnswerDao: NumericAnswerDao
) {

    private val TAG = ExportHelper::class.java.simpleName

    suspend fun exportAllData() {
        exportTolaTableToCsv()
        exportDidiTableToCsv()
        exportAnswerTableToCsv()
        exportNumericAnswerTableToCsv()
    }

    suspend fun exportDidiTableToCsv() {
        val didiEntity = didiDao.getAllDidis()

        ExportService.export<DidiTableCSV>(
            type = Exports.CSV(CsvConfig(prefix = "$DIDI_TABLE-${prefRepo.getMobileNumber()}")),
            content = didiEntity.toCsv()
        ).catch { error ->
            // handle error here
            NudgeLogger.e(TAG, "exportDidiTableToCsv error", error)
        }.collect { path ->
            NudgeLogger.d(TAG, "exportDidiTableToCsv: $path")
            try {
                val dataDumpRequestParts = DataDumpRequestParts.getDataDumpRequestParts(
                    path,
                    DataDumpTableName.DATA_DUMP_DIDI_TABLE,
                    (prefRepo.getMobileNumber() ?: -1).toString(),
                    if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                )
                val uploadDidiTableDumpResponse = apiService.uploadDataDump(
                    dataDump = dataDumpRequestParts.dataDump,
                    villageId = dataDumpRequestParts.villageId,
                    userType = dataDumpRequestParts.userType
                )
                if (uploadDidiTableDumpResponse.status.equals(SUCCESS)) {
                    NudgeLogger.d(
                        TAG,
                        "exportDidiTableToCsv uploadDidiTableDumpRequest success response => ${uploadDidiTableDumpResponse.json()} "
                    )
                } else {
                    NudgeLogger.d(
                        TAG,
                        "exportDidiTableToCsv uploadDidiTableDumpRequest success response => ${uploadDidiTableDumpResponse.json()} "
                    )
                }
            } catch (ex: Exception) {
                NudgeLogger.e(TAG, "exportDidiTableToCsv exception", ex)
                onCatchError(ex, ApiType.UPDLOAD_DATA_DUMP_API)
            }
        }
    }

    suspend fun exportTolaTableToCsv() {
        val tolaEntity = tolaDao.getAllTolas()

        ExportService.export<TolaTableCSV>(
            type = Exports.CSV(CsvConfig(prefix = "$TOLA_TABLE-${prefRepo.getMobileNumber()}")),
            content = tolaEntity.toCsv()
        ).catch { error ->
            // handle error here
            NudgeLogger.e(TAG, "exportTolaTableToCsv error", error)
        }.collect { path ->
            NudgeLogger.d(TAG, "exportTolaTableToCsv: $path")
            try {
                val dataDumpRequestParts = DataDumpRequestParts.getDataDumpRequestParts(
                    path,
                    DataDumpTableName.DATA_DUMP_TOLA_TABLE,
                    (prefRepo.getMobileNumber() ?: -1).toString(),
                    if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                )
                val uploadTolaTableDumpResponse = apiService.uploadDataDump(
                    dataDump = dataDumpRequestParts.dataDump,
                    villageId = dataDumpRequestParts.villageId,
                    userType = dataDumpRequestParts.userType
                )
                if (uploadTolaTableDumpResponse.status.equals(SUCCESS)) {
                    NudgeLogger.d(
                        TAG,
                        "exportTolaTableToCsv uploadTolaTableDumpResponse success response => ${uploadTolaTableDumpResponse.json()} "
                    )
                } else {
                    NudgeLogger.d(
                        TAG,
                        "exportTolaTableToCsv uploadTolaTableDumpResponse success response => ${uploadTolaTableDumpResponse.json()} "
                    )
                }
            } catch (ex: Exception) {
                NudgeLogger.e(TAG, "exportTolaTableToCsv exception", ex)
                onCatchError(ex, ApiType.UPDLOAD_DATA_DUMP_API)
            }
        }
    }

    suspend fun exportAnswerTableToCsv() {
        val answerEntity = answerDao.getAllAnswer()

        ExportService.export<AnswerTableCSV>(
            type = Exports.CSV(CsvConfig(prefix = "$ANSWER_TABLE-${prefRepo.getMobileNumber()}")),
            content = answerEntity.toCsv()
        ).catch { error ->
            // handle error here
            NudgeLogger.e(TAG, "exportAnswerTableToCsv error", error)
        }.collect { path ->
            NudgeLogger.d(TAG, "exportAnswerTableToCsv: $path")
            try {
                val dataDumpRequestParts = DataDumpRequestParts.getDataDumpRequestParts(
                    path,
                    DataDumpTableName.DATA_DUMP_ANSWERS_TABLE,
                    (prefRepo.getMobileNumber() ?: -1).toString(),
                    if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                )
                val uploadAnswerTableDumpResponse = apiService.uploadDataDump(
                    dataDump = dataDumpRequestParts.dataDump,
                    villageId = dataDumpRequestParts.villageId,
                    userType = dataDumpRequestParts.userType
                )
                if (uploadAnswerTableDumpResponse.status.equals(SUCCESS)) {
                    NudgeLogger.d(
                        TAG,
                        "exportAnswerTableToCsv uploadAnswerTableDumpResponse success response => ${uploadAnswerTableDumpResponse.json()} "
                    )
                } else {
                    NudgeLogger.d(
                        TAG,
                        "exportAnswerTableToCsv uploadAnswerTableDumpResponse success response => ${uploadAnswerTableDumpResponse.json()} "
                    )
                }
            } catch (ex: Exception) {
                NudgeLogger.e(TAG, "exportAnswerTableToCsv exception", ex)
                onCatchError(ex, ApiType.UPDLOAD_DATA_DUMP_API)
            }
        }
    }

    suspend fun exportNumericAnswerTableToCsv() {

        val numericAnswerEntity = numericAnswerDao.getAllNumericAnswers()

        ExportService.export<NumericAnswerTableCSV>(
            type = Exports.CSV(CsvConfig(prefix = "$NUMERIC_TABLE_NAME-${prefRepo.getMobileNumber()}")),
            content = numericAnswerEntity.toCsv()
        ).catch { error ->
            // handle error here
            NudgeLogger.e(TAG, "exportNumericAnswerTableToCsv error", error)
        }.collect { path ->
            NudgeLogger.d(TAG, "exportNumericAnswerTableToCsv: $path")
            try {
                val dataDumpRequestParts = DataDumpRequestParts.getDataDumpRequestParts(
                    path,
                    DataDumpTableName.DATA_DUMP_NUMERIC_ANSWERS_TABLE,
                    (prefRepo.getMobileNumber() ?: -1).toString(),
                    if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                )
                val uploadNumericAnswerTableDumpResponse = apiService.uploadDataDump(
                    dataDump = dataDumpRequestParts.dataDump,
                    villageId = dataDumpRequestParts.villageId,
                    userType = dataDumpRequestParts.userType
                )
                if (uploadNumericAnswerTableDumpResponse.status.equals(SUCCESS)) {
                    NudgeLogger.d(
                        TAG,
                        "exportNumericAnswerTableToCsv exportNumericAnswerTableToCsv success response => ${uploadNumericAnswerTableDumpResponse.json()} "
                    )
                } else {
                    NudgeLogger.d(
                        TAG,
                        "exportNumericAnswerTableToCsv exportNumericAnswerTableToCsv success response => ${uploadNumericAnswerTableDumpResponse.json()} "
                    )
                }
            } catch (ex: Exception) {
                NudgeLogger.e(TAG, "exportNumericAnswerTableToCsv exception", ex)
                onCatchError(ex, ApiType.UPDLOAD_DATA_DUMP_API)
            }
        }
    }

    open fun onCatchError(e: Exception, api: ApiType) {
        NudgeLogger.d("BaseViewModel", "onCatchError: message: ${e.message}, api: ${api.name}")
        AnalyticsHelper.logServiceFailedEvent(exception = e, apiType = api)
        when (e) {
            is HttpException -> {
                NudgeLogger.d(
                    "BaseViewModel",
                    "onCatchError code: ${
                        e.response()?.code() ?: 0
                    }, message: ${e.message()}, api: ${api.name}"
                )
            }

            is SocketTimeoutException -> {
                NudgeLogger.d(
                    "BaseViewModel",
                    "onCatchError SocketTimeoutException message: ${e.message}, api: ${api.name}"
                )
            }

            is IOException -> {
                NudgeLogger.d(
                    "BaseViewModel",
                    "onCatchError IOException message: ${e.message}, api: ${api.name}"
                )
            }

            is JsonSyntaxException -> {
                NudgeLogger.d(
                    "BaseViewModel",
                    "onCatchError JsonSyntaxException message: ${e.message}, api: ${api.name}"
                )
            }

            is ApiResponseFailException -> {
                NudgeLogger.d(
                    "BaseViewModel",
                    "onCatchError ApiResponseFailException message: ${e.message}, api: ${api.name}"
                )
            }

            else -> NudgeLogger.d(
                "BaseViewModel",
                "onCatchError exception: ${e.javaClass} message: ${e.message}, api: ${api.name}"
            )
        }
    }

}