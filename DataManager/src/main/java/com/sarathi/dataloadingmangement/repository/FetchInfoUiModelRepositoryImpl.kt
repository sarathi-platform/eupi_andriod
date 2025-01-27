package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageDao
import com.sarathi.dataloadingmangement.data.dao.MissionLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.model.uiModel.ActivityInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel
import javax.inject.Inject

class FetchInfoUiModelRepositoryImpl @Inject constructor(
    private val sharedPrefs: CoreSharedPrefs,
    private val missionLanguageAttributeDao: MissionLanguageAttributeDao,
    private val activityLanguageDao: ActivityLanguageDao,
    private val livelihoodDao: LivelihoodDao
) : FetchInfoUiModelRepository {

    override suspend fun fetchMissionInfo(missionId: Int): MissionInfoUIModel? {
        val missionUIInfoModel = missionLanguageAttributeDao.fetchMissionInfo(
            missionId = missionId,
            userId = sharedPrefs.getUniqueUserIdentifier(),
            languageCode = sharedPrefs.getSelectedLanguageCode()
        )
        val livelihoodName =
            findLivelihoodSubtitle(missionUIInfoModel?.livelihoodType)
        return missionUIInfoModel?.copy(livelihoodName = livelihoodName)
    }

    override suspend fun fetchActivityInfo(missionId: Int, activityId: Int): ActivityInfoUIModel? {
        val activityUIInfoModel = activityLanguageDao.fetchActivityInfo(
            missionId = missionId,
            activityId = activityId,
            userId = sharedPrefs.getUniqueUserIdentifier(),
            languageCode = sharedPrefs.getSelectedLanguageCode()
        )
        val livelihoodName =
            findLivelihoodSubtitle(activityUIInfoModel?.livelihoodType)
        return activityUIInfoModel?.copy(livelihoodName = livelihoodName)
    }

    private fun findLivelihoodSubtitle(livelihoodType: String?): String? {
        val livelihood = livelihoodDao.getLivelihoodList(
            userId = sharedPrefs.getUniqueUserIdentifier(),
            languageCode = sharedPrefs.getSelectedLanguageCode()
        )
        val livelihoodName =
            livelihood.find { it.type.equals(livelihoodType, true) }?.name
                ?: livelihoodType
        return livelihoodName
    }
}