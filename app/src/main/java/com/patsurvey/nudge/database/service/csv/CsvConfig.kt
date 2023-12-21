package com.patsurvey.nudge.database.service.csv

import android.os.Environment
import com.patsurvey.nudge.utils.NudgeCore
import java.text.DateFormat

data class CsvConfig(
    private val prefix: String,
    private val suffix: String = DateFormat
        .getDateTimeInstance()
        .format(System.currentTimeMillis())
        .toString()
        .replace(",","")
        .replace(" ", "_"),

    val fileName: String = "$prefix-$suffix.csv",
    val hostPath: String = NudgeCore.getAppContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath ?:  ""
)
