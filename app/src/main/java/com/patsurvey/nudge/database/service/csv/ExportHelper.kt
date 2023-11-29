package com.patsurvey.nudge.database.service.csv

import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.service.csv.adapter.AnswerTableCSV
import com.patsurvey.nudge.database.service.csv.adapter.DidiTableCSV
import com.patsurvey.nudge.database.service.csv.adapter.NumericAnswerTableCSV
import com.patsurvey.nudge.database.service.csv.adapter.TolaTableCSV
import com.patsurvey.nudge.database.service.csv.adapter.toCsv
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ANSWER_TABLE
import com.patsurvey.nudge.utils.DIDI_TABLE
import com.patsurvey.nudge.utils.NUMERIC_TABLE_NAME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.TOLA_TABLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExportHelper @Inject constructor(
    private val apiService: ApiService,
    private val prefRepo: PrefRepo,
    private val didiDao: DidiDao,
    private val tolaDao: TolaDao,
    private val answerDao: AnswerDao,
    private val numericAnswerDao: NumericAnswerDao
) {

    fun exportAllData() {
        exportTolaTableToCsv()
        exportDidiTableToCsv()
        exportAnswerTableToCsv()
        exportNumericAnswerTableToCsv()
    }

    fun exportDidiTableToCsv() {
        CoroutineScope(Dispatchers.IO).launch {
            // get all didi detail from repository
            val didiEntity = didiDao.getAllDidis()

            // call export function from Export serivce
            ExportService.export<DidiTableCSV>(
                type = Exports.CSV(CsvConfig(prefix = "$DIDI_TABLE-${prefRepo.getMobileNumber()}")), // apply config + type of export
                content = didiEntity.toCsv() // send transformed data of exportable type
            ).catch { error ->
                // handle error here
                NudgeLogger.e("SettingViewModel", "exportDidiTableToCsv error", error)
            }.collect { path ->
                // do anything on success

                NudgeLogger.d("SettingViewModel", "exportDidiTableToCsv: $path")
            }
        }
    }

    fun exportTolaTableToCsv() {
        CoroutineScope(Dispatchers.IO).launch {
            // get all didi detail from repository
            val tolaEntity = tolaDao.getAllTolas()

            // call export function from Export serivce
            ExportService.export<TolaTableCSV>(
                type = Exports.CSV(CsvConfig(prefix = "$TOLA_TABLE-${prefRepo.getMobileNumber()}")), // apply config + type of export
                content = tolaEntity.toCsv() // send transformed data of exportable type
            ).catch { error ->
                // handle error here
                NudgeLogger.e("SettingViewModel", "exportTolaTableToCsv error", error)
            }.collect { path ->
                // do anything on success

                NudgeLogger.d("SettingViewModel", "exportTolaTableToCsv: $path")
            }
        }
    }

    fun exportAnswerTableToCsv() {
        CoroutineScope(Dispatchers.IO).launch {
            // get all didi detail from repository
            val answerEntity = answerDao.getAllAnswer()

            // call export function from Export serivce
            ExportService.export<AnswerTableCSV>(
                type = Exports.CSV(CsvConfig(prefix = "$ANSWER_TABLE-${prefRepo.getMobileNumber()}")), // apply config + type of export
                content = answerEntity.toCsv() // send transformed data of exportable type
            ).catch { error ->
                // handle error here
                NudgeLogger.e("SettingViewModel", "exportAnswerTableToCsv error", error)
            }.collect { path ->
                // do anything on success

                NudgeLogger.d("SettingViewModel", "exportAnswerTableToCsv: $path")
            }
        }
    }

    fun exportNumericAnswerTableToCsv() {
        CoroutineScope(Dispatchers.IO).launch {
            // get all didi detail from repository
            val numericAnswerEntity = numericAnswerDao.getAllNumericAnswers()

            // call export function from Export serivce
            ExportService.export<NumericAnswerTableCSV>(
                type = Exports.CSV(CsvConfig(prefix = "$NUMERIC_TABLE_NAME-${prefRepo.getMobileNumber()}")), // apply config + type of export
                content = numericAnswerEntity.toCsv() // send transformed data of exportable type
            ).catch { error ->
                // handle error here
                NudgeLogger.e("SettingViewModel", "exportNumericAnswerTableToCsv error", error)
            }.collect { path ->
                // do anything on success

                NudgeLogger.d("SettingViewModel", "exportNumericAnswerTableToCsv: $path")
            }
        }
    }

}