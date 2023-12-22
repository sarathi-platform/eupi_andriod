package com.patsurvey.nudge.database.service.csv

import androidx.annotation.WorkerThread
import com.opencsv.CSVWriter
import com.opencsv.bean.MappingStrategy
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.patsurvey.nudge.utils.BLANK_STRING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileWriter

object ExportService {

    fun <T : Exportable> export(type: Exports, content: List<T>) : Flow<String> =
        when (type) {
            is Exports.CSV -> writeToCSV<T>(type.csvConfig, content)
        }


    @WorkerThread
    private fun <T : Exportable> writeToCSV(csvConfig: CsvConfig, content: List<T>) =
        flow<String> {

            var csvFilePath = BLANK_STRING
            with(csvConfig) {
                hostPath.ifEmpty { throw IllegalStateException("Wrong Path") }

                val hostDirectory = File(hostPath)
                if (!hostDirectory.exists()){
                    hostDirectory.mkdir() // Create Dir if not exists
                }

                // Create csv file
                val csvFile = File("${hostDirectory.path}/$fileName")
                val csvWriter = CSVWriter(FileWriter(csvFile))

                // Write to csvFile
                StatefulBeanToCsvBuilder<T>(csvWriter)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(true)
                    .build()
                    .write(content)

                csvWriter.close()
                csvFilePath = csvFile.absolutePath

            }
            // emit Success
            emit(csvFilePath)

        }

}