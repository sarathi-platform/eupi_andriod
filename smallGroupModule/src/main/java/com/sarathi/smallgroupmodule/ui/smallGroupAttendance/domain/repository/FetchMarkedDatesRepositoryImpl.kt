package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository

import com.nudge.core.enums.ValueTypes.Companion.convertToDataType
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import javax.inject.Inject

class FetchMarkedDatesRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val subjectEntityDao: SubjectEntityDao,
    private val subjectAttributeDao: SubjectAttributeDao,
    private val attributeValueReferenceDao: AttributeValueReferenceDao
) : FetchMarkedDatesRepository {

    override suspend fun fetchMarkedDates(): List<Long> {
        val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()
        val markedDatesList = ArrayList<Long>()
        attributeValueReferenceDao.getMarkedDatesList(userId = uniqueUserId).apply {
            for (markedDatesUiModel in this) {
                val convertedDate =
                    markedDatesUiModel.value.convertToDataType(markedDatesUiModel.valueType)

                if (convertedDate !is Long)
                    break

                markedDatesList.add(convertedDate)
            }
        }
        return markedDatesList
    }


}