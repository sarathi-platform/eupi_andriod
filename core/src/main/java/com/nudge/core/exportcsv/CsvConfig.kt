package com.nudge.core.exportcsv

import java.text.DateFormat
import java.util.Locale

data class CsvConfig(
    private val prefix: String,
    private val suffix: String = DateFormat
        .getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT,Locale.ENGLISH)
        .format(System.currentTimeMillis())
        .toString()
        .replace(",","")
        .replace(" ", "_"),

    val fileName: String = "$prefix-$suffix.csv",
    val hostPath: String
)
