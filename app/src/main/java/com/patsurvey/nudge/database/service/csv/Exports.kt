package com.patsurvey.nudge.database.service.csv

sealed class Exports {
    data class CSV(val csvConfig: CsvConfig) : Exports()
}