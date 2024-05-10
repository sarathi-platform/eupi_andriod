package com.nudge.core.exportcsv

sealed class Exports {
    data class CSV(val csvConfig: CsvConfig) : Exports()
}