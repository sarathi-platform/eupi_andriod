package com.patsurvey.nudge.database.service.csv.adapter

import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import java.io.File

data class DataDumpRequestParts(
    @Part val dataDump: MultipartBody.Part,
    @Part("villageId") val  villageId: RequestBody,
    @Part("flowType") val  userType: RequestBody
) {

    companion object {
        fun getDataDumpRequestParts(dataDumpFilePath: String, tableName: DataDumpTableName, mobileNumber: String, userType: String): DataDumpRequestParts {
            val fileName = when (tableName) {
                DataDumpTableName.DATA_DUMP_DIDI_TABLE -> "dataDumpDidiTable"
                DataDumpTableName.DATA_DUMP_TOLA_TABLE -> "dataDumpTolaTable"
                DataDumpTableName.DATA_DUMP_ANSWERS_TABLE -> "dataDumpAnswersTable"
                DataDumpTableName.DATA_DUMP_NUMERIC_ANSWERS_TABLE -> "dataDumpNumericAnswersTable"
            }
            val dataDumpRequest = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                File(dataDumpFilePath)
            )
            val dataDumpRequestPart = MultipartBody.Part.createFormData(
                "$fileName-$mobileNumber",
                File(dataDumpFilePath).name,
                dataDumpRequest
            )
            val villageIdRequest = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                mobileNumber
            )
            val userTypeRequest = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                userType
            )
            return DataDumpRequestParts(dataDump = dataDumpRequestPart, villageId = villageIdRequest, userType = userTypeRequest)
        }
    }

}

enum class DataDumpTableName {
    DATA_DUMP_DIDI_TABLE,
    DATA_DUMP_TOLA_TABLE,
    DATA_DUMP_ANSWERS_TABLE,
    DATA_DUMP_NUMERIC_ANSWERS_TABLE
}