package com.sarathi.dataloadingmangement.repository.livelihood

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity

interface FetchDidiDetailsFromDbRepository {

    suspend fun getSubjectListForUser(userId: String): List<SubjectEntity>

    suspend fun getSubjectListForSmallGroup(userId: String, smallGroupId: Int): List<SubjectEntity>

    suspend fun getSubjectListCount(userId: String): Int

    fun getUniqueUserId(): String

}