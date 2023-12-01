package com.patsurvey.nudge.database.service.csv

import android.content.Context
import android.os.Build
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storageMetadata
import com.google.gson.JsonSyntaxException
import com.patsurvey.nudge.BuildConfig
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
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ANSWER_TABLE
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.DIDI_TABLE
import com.patsurvey.nudge.utils.NUMERIC_TABLE_NAME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.TOLA_TABLE
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.json
import com.patsurvey.nudge.utils.uriFromFile
import kotlinx.coroutines.flow.catch
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

    private val storageRef = Firebase.storage.reference
    private val folderName = if (BuildConfig.DEBUG) "debug" else "prod"

   val tablePaths = mutableListOf<String>()
    suspend fun exportAllData(context: Context) {
        exportTolaTableToCsv(context)
        exportDidiTableToCsv(context)
        exportAnswerTableToCsv(context)
        exportNumericAnswerTableToCsv(context)
    }

    suspend fun exportDidiTableToCsv(context: Context) {
        val didiEntity = didiDao.getDidiTableDump()

        ExportService.export<DidiTableCSV>(
            type = Exports.CSV(CsvConfig(prefix = "$DIDI_TABLE-${prefRepo.getMobileNumber()}")),
            content = didiEntity.toCsv()
        ).catch { error ->
            // handle error here
            NudgeLogger.e(TAG, "exportDidiTableToCsv error", error)
        }.collect { path ->
            NudgeLogger.d(TAG, "exportDidiTableToCsv: path => $path")

            try {
                    val file = uriFromFile(context, File(path))
                    val didiTableDumpRef = storageRef.child("$folderName/${file.lastPathSegment}")
                    val didiTableDumpUploadTask = didiTableDumpRef
                        .putFile(file)

                didiTableDumpUploadTask
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                                NudgeLogger.d(
                                    TAG,
                                    "exportDidiTableToCsv addOnCompleteListener didiTableDumpUploadTask success"
                                )

                            } else {
                                NudgeLogger.d(
                                    TAG,
                                    "exportDidiTableToCsv addOnCompleteListener failure"
                                )
                            }
                        }
                        .addOnFailureListener {
                            NudgeLogger.d(
                                TAG,
                                "exportDidiTableToCsv addOnCompleteListener failure"
                            )
                        }



            } catch (ex: Exception) {
                onCatchError(ex, ApiType.UPDLOAD_DATA_DUMP_API)
            }

            try {
                val dataDumpRequestParts = DataDumpRequestParts.getDataDumpRequestParts(
                    context,
                    path,
                    DataDumpTableName.DATA_DUMP_DIDI_TABLE,
                    (prefRepo.getMobileNumber() ?: -1).toString(),
                    if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                )
                NudgeLogger.d(TAG, "DataDumpRequestParts: dataDump = ${dataDumpRequestParts.dataDump.body.json()},\n" +
                        "                    villageId = ${dataDumpRequestParts.villageId},\n" +
                        "                    userType = ${dataDumpRequestParts.userType}")
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

    suspend fun exportTolaTableToCsv(context: Context) {
        val tolaEntity = tolaDao.getTolaTableDump()

        ExportService.export<TolaTableCSV>(
            type = Exports.CSV(CsvConfig(prefix = "$TOLA_TABLE-${prefRepo.getMobileNumber()}")),
            content = tolaEntity.toCsv()
        ).catch { error ->
            // handle error here
            NudgeLogger.e(TAG, "exportTolaTableToCsv error", error)
        }.collect { path ->
            NudgeLogger.d(TAG, "exportTolaTableToCsv: path => $path")
            try {
                val file = uriFromFile(context, File(path))
                val tolaTableDumpRef = storageRef.child("$folderName/${file.lastPathSegment}")
                val tolaTableDumpUploadTask = tolaTableDumpRef
                    .putFile(file)

                tolaTableDumpUploadTask
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            NudgeLogger.d(
                                TAG,
                                "exportDidiTableToCsv addOnCompleteListener tolaTableDumpUploadTask success"
                            )

                        } else {
                            NudgeLogger.d(
                                TAG,
                                "exportDidiTableToCsv addOnCompleteListener failure"
                            )
                        }
                    }
                    .addOnFailureListener {
                        NudgeLogger.d(
                            TAG,
                            "exportDidiTableToCsv addOnCompleteListener failure"
                        )
                    }

            } catch (ex: Exception) {
                onCatchError(ex, ApiType.UPDLOAD_DATA_DUMP_API)
            }
            try {
                val dataDumpRequestParts = DataDumpRequestParts.getDataDumpRequestParts(
                    context,
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

    suspend fun exportAnswerTableToCsv(context: Context) {
        val answerEntity = answerDao.getAllAnswer()

        ExportService.export<AnswerTableCSV>(
            type = Exports.CSV(CsvConfig(prefix = "$ANSWER_TABLE-${prefRepo.getMobileNumber()}")),
            content = answerEntity.toCsv()
        ).catch { error ->
            // handle error here
            NudgeLogger.e(TAG, "exportAnswerTableToCsv error", error)
        }.collect { path ->
            NudgeLogger.d(TAG, "exportAnswerTableToCsv: path => $path")

            try {
                val file = uriFromFile(context, File(path))
                val answerTableDumpRef = storageRef.child("$folderName/${file.lastPathSegment}")
                val answerTableDumpUploadTask = answerTableDumpRef
                    .putFile(file)

                answerTableDumpUploadTask
                    .addOnCompleteListener { it ->
                        if (it.isSuccessful) {
                             NudgeLogger.d(
                                TAG,
                                "exportDidiTableToCsv addOnCompleteListener answerTableDumpUploadTask success"
                            )

                        } else {
                            NudgeLogger.d(
                                TAG,
                                "exportDidiTableToCsv addOnCompleteListener failure"
                            )
                        }
                    }
                    .addOnFailureListener {
                        NudgeLogger.d(
                            TAG,
                            "exportDidiTableToCsv addOnCompleteListener failure"
                        )
                    }

            } catch (ex: Exception) {
                onCatchError(ex, ApiType.UPDLOAD_DATA_DUMP_API)
            }

            try {
                val dataDumpRequestParts = DataDumpRequestParts.getDataDumpRequestParts(
                    context,
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

    suspend fun exportNumericAnswerTableToCsv(context: Context) {

        val numericAnswerEntity = numericAnswerDao.getAllNumericAnswers()

        ExportService.export<NumericAnswerTableCSV>(
            type = Exports.CSV(CsvConfig(prefix = "$NUMERIC_TABLE_NAME-${prefRepo.getMobileNumber()}")),
            content = numericAnswerEntity.toCsv()
        ).catch { error ->
            // handle error here
            NudgeLogger.e(TAG, "exportNumericAnswerTableToCsv error", error)
        }.collect { path ->
            NudgeLogger.d(TAG, "exportNumericAnswerTableToCsv: path => $path")
            try {
                val file = uriFromFile(context, File(path))
                val numericAnswerTableDumpRef = storageRef.child("$folderName/${file.lastPathSegment}")
                val numericAnswerTableDumpUploadTask = numericAnswerTableDumpRef
                    .putFile(file)

                numericAnswerTableDumpUploadTask
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            NudgeLogger.d(
                                TAG,
                                "exportDidiTableToCsv addOnCompleteListener numericAnswerTableDumpUploadTask success"
                            )

                        } else {
                            NudgeLogger.d(
                                TAG,
                                "exportDidiTableToCsv addOnCompleteListener failure"
                            )
                        }
                    }
                    .addOnFailureListener {
                        NudgeLogger.d(
                            TAG,
                            "exportDidiTableToCsv addOnCompleteListener failure"
                        )
                    }

            } catch (ex: Exception) {
                onCatchError(ex, ApiType.UPDLOAD_DATA_DUMP_API)
            }
            try {
                val dataDumpRequestParts = DataDumpRequestParts.getDataDumpRequestParts(
                    context,
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

    private suspend fun uploadFileToFirebase(context: Context, filePaths: List<String>) {
        try {
            filePaths.forEach { path ->
                val file = uriFromFile(context, File(path))
                val didiTableDumpRef = storageRef.child("$folderName/${file.lastPathSegment}")
                val uploadTask = didiTableDumpRef
                    .putFile(file)

                uploadTask
                    .addOnSuccessListener { it ->
                        if (it.task.isSuccessful) {
                            NudgeLogger.d(
                                TAG,
                                "exportDidiTableToCsv addOnCompleteListener success response => path: ${didiTableDumpRef.downloadUrl.result.path}, " +
                                        "\n name = ${didiTableDumpRef.downloadUrl.result.lastPathSegment}"
                            )

                        } else {
                            NudgeLogger.d(
                                TAG,
                                "exportDidiTableToCsv addOnCompleteListener failure"
                            )
                        }
                    }
                    .addOnFailureListener {
                        NudgeLogger.d(
                            TAG,
                            "exportDidiTableToCsv addOnCompleteListener failure"
                        )
                    }

                /*val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    NudgeLogger.d(
                        TAG,
                        "exportDidiTableToCsv continueWithTask downloadUrl = ${didiTableDumpRef.downloadUrl}"
                    )
                    didiTableDumpRef.downloadUrl
                }.addOnCompleteListener { task ->

                }*/
            }

        } catch (ex: Exception) {
            onCatchError(ex, ApiType.UPDLOAD_DATA_DUMP_API)
        }
    }

}