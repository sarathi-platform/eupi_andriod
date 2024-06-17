package com.sarathi.dataloadingmangement.domain.use_case

import android.net.Uri
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.repository.FormRepositoryImpl
import javax.inject.Inject

class FormUseCase @Inject constructor(
    private val repository: FormRepositoryImpl,
    private val downloaderManager: DownloaderManager
) {

    suspend fun saveFormEData(
        subjectId: Int,
        taskId: Int,
        surveyId: Int,
        missionId: Int,
        activityId: Int,
        referenceId: String,
        subjectType: String
    ) {
        return repository.saveFromToDB(
            subjectId = subjectId,
            taskId = taskId,
            surveyId = surveyId,
            missionId = missionId,
            activityId = activityId,
            referenceId = referenceId,
            subjectType = subjectType
        )
    }

    suspend fun deleteFormE(
        referenceId: String,
        taskId: Int
    ): Int {
        return repository.deleteForm(
            taskId = taskId,
            referenceId = referenceId
        )
    }

    suspend fun getFormSummaryData(): List<FormEntity> {
        return repository.getFormSummaryData()
    }

    fun getFilePathUri(filePath: String): Uri? {
        return downloaderManager.getFilePathUri(filePath)
    }


}
