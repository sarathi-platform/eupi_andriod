package com.nrlm.baselinesurvey.model.response

import com.nrlm.baselinesurvey.model.request.SectionList

data class SavedSurveyAnswersResponse(
    val answers: List<Answers>,
    val baselineSurveyStatus: Int,
    val beneficiaryId: Int,
    val createdBy: Int,
    val createdDate: Long,
    val id: String,
    val localCreatedDate: Int,
    val localModifiedDate: Int,
    val modifiedBy: Int,
    val modifiedDate: Long,
    val sectionList: List<SectionList>,
    val shgFlag: Int,
    val stateId: Int,
    val status: Int,
    val surveyId: Int,
    val totalScore: Int,
    val userType: String,
    val villageId: Int
)