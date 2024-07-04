package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_EMAIL
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_IDENTITY_NUMBER
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_PROFILE_IMAGE
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_ROLE_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_TYPE_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_USER_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_STATE_ID
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.mat.response.TaskResponse
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.network.response.UserDetailsResponse
import javax.inject.Inject

class IDataLoadingScreenRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs,
    val dataLoadingApiService: DataLoadingApiService
) : IDataLoadingScreenRepository {
    override suspend fun fetchMissionDataFromServer(): ApiResponseModel<List<ProgrameResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveMissionToDB(missions: List<MissionResponse>, programmeId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun saveMissionsActivityToDB(
        activities: List<ActivityResponse>,
        missionId: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun saveMissionsActivityTaskToDB(
        missionId: Int,
        activityId: Int,
        subject: String,
        activities: List<TaskResponse>
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchContentsFromServer(contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveContentToDB(contents: List<Content>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteContentFromDB() {
        TODO("Not yet implemented")
    }

    override suspend fun getContentData(): List<Content> {
        TODO("Not yet implemented")
    }

    override suspend fun saveProgrammeToDb(programme: ProgrameResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetailsFromNetwork(languageId: String): ApiResponseModel<UserDetailsResponse> {
        return dataLoadingApiService.userAndVillageListAPI(languageId)
    }

    override fun saveUserDetails(userDetails: UserDetailsResponse) {
        coreSharedPrefs.savePref(PREF_KEY_USER_NAME, userDetails.username ?: "")
        coreSharedPrefs.savePref(PREF_KEY_NAME, userDetails.name ?: "")
        coreSharedPrefs.savePref(PREF_KEY_EMAIL, userDetails.email ?: "")
        coreSharedPrefs.savePref(PREF_KEY_IDENTITY_NUMBER, userDetails.identityNumber ?: "")
        coreSharedPrefs.savePref(PREF_KEY_PROFILE_IMAGE, userDetails.profileImage ?: "")
        coreSharedPrefs.savePref(PREF_KEY_ROLE_NAME, userDetails.roleName ?: "")
        coreSharedPrefs.savePref(PREF_KEY_TYPE_NAME, userDetails.typeName ?: "")
        coreSharedPrefs.savePref(PREF_STATE_ID, userDetails.referenceId.first().stateId ?: -1)
    }
}