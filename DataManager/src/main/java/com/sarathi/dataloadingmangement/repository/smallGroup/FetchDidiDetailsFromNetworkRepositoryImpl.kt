package com.sarathi.dataloadingmangement.repository.smallGroup

import android.util.Log
import com.nudge.core.SUCCESS
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.response.BeneficiaryApiResponse
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class FetchDidiDetailsFromNetworkRepositoryImpl @Inject constructor(
    private val corePrefRepo: CoreSharedPrefs,
    private val dataLoadingApiService: DataLoadingApiService,
    private val subjectEntityDao: SubjectEntityDao
) : FetchDidiDetailsFromNetworkRepository {

    private val TAG = FetchDidiDetailsFromNetworkRepositoryImpl::class.java.simpleName

    override suspend fun fetchDidiDetailsFromNetwork() {
        try {
            val userId = corePrefRepo.getUserName().toInt()
            val response = dataLoadingApiService.getDidisFromNetwork(userId = userId)

            if (response.status.equals(SUCCESS)) {

                response.data?.let {

                    saveDidiDetailsToDb(it)

                } ?: throw NullPointerException("Data is null")

            }
        } catch (ex: Exception) {
            Log.e(TAG, "fetchDidiDetailsFromNetwork -> exception: ${ex.message}", ex)
        }

    }

    override suspend fun saveDidiDetailsToDb(beneficiaryApiResponse: BeneficiaryApiResponse) {
        val uniqueUserId = corePrefRepo.getUniqueUserIdentifier()

        subjectEntityDao.deleteSubjectsForUsers(uniqueUserId)

        val subjectList = ArrayList<SubjectEntity>()
        beneficiaryApiResponse.didiList.forEach {
            subjectList.add(
                SubjectEntity.getSubjectEntityFromResponse(
                    it,
                    uniqueUserId
                )
            )
        }

        subjectEntityDao.addAllSubjects(subjectList)

    }


}