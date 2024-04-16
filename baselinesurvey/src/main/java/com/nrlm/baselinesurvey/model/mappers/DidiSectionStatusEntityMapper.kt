package com.nrlm.baselinesurvey.model.mappers

import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.model.response.SectionStatusResponseModel
import com.nrlm.baselinesurvey.utils.states.SectionStatus.Companion.getOrdinalFromSectionStatus

object DidiSectionStatusEntityMapper {

    fun getDidiSectionStatusEntity(sectionStatus: List<SectionStatusResponseModel>): List<DidiSectionProgressEntity> {

        return sectionStatus.map {

            DidiSectionProgressEntity(
                surveyId = it.surveyId ?: -1,
                sectionId = it.sectionId?.toInt() ?: -1,
                sectionStatus = getOrdinalFromSectionStatus(it.status ?: ""),
                didiId = it.didiId ?: -1,
                id = 0

            )
        }


    }
}