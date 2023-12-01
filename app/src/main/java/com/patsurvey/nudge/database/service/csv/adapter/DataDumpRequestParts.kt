package com.patsurvey.nudge.database.service.csv.adapter

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.uriFromFile
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


data class DataDumpRequestParts(
    @Part val dataDump: MultipartBody.Part,
    @Part("villageId") val  villageId: RequestBody,
    @Part("flowType") val  userType: RequestBody
) {

    companion object {

        private val TAG = DataDumpRequestParts::class.java.simpleName

        fun getDataDumpRequestParts(context: Context, dataDumpFilePath: String, tableName: DataDumpTableName, mobileNumber: String, userType: String): DataDumpRequestParts {
            val dataDumpList = arrayListOf<MultipartBody.Part>()
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