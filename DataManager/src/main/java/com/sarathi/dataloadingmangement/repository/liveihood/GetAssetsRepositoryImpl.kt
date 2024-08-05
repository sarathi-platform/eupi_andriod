package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.model.uiModel.ActivityFormUIModel
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodDropDownUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import javax.inject.Inject


class GetAssetsRepositoryImpl @Inject constructor(
    val livelihoodDao: LivelihoodDao,
    val coreSharedPrefs: CoreSharedPrefs
) : AssetsRepository {
    //    override suspend fun getActivity(missionId: Int): List<ActivityUiModel> {
//        return activityDao.getActivities(
//            coreSharedPrefs.getUniqueUserIdentifier(),
//            coreSharedPrefs.getAppLanguage(),
//            missionId
//        )
//    }
    override suspend fun getAssets(userId: String, languageCode: String):List<LivelihoodDropDownUiModel>{
     return livelihoodDao.getAssetsData(userId,languageCode)
    }


}
