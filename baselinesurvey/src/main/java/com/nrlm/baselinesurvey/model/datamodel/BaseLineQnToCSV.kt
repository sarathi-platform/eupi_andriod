package com.nrlm.baselinesurvey.model.datamodel
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nudge.core.datamodel.BaseLineQnATableCSV
import com.nudge.core.datamodel.HamletQnATableCSV


fun List<SaveAnswerEventDto>.toCSVSave(sectionList: List<SectionEntity>, didiDetailList: List<SurveyeeEntity>) : List<BaseLineQnATableCSV> = map {
    val selectedValues = it.question.options.map { mIt -> mIt.selectedValue }
    val response = selectedValues.joinToString("\n")
    val didiName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.didiName
    val dadaName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.dadaName
    val houseNo = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.houseNo
    val villageName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.villageName
    val cohoretName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.cohortName
    val sub = it.question.options.map { mIt -> mIt.optionDesc }
    val subject = sub.joinToString("\n")
    BaseLineQnATableCSV(
        id = it.question.questionId.toString(),
        question = it.question.questionDesc,
        subQuestion = subject,
        response = response,
        subjectId = it.subjectId,
        section = sectionList.find { sectionEntity -> sectionEntity.surveyId == it.surveyId && sectionEntity.sectionId == it.sectionId }?.sectionName,
        didiName = didiName,
        dadaName = dadaName,
        house = houseNo,
        cohoretName = cohoretName,
        villageName = villageName,
        surveyId = it.surveyId,
        sectionId = it.sectionId
        )
}



fun List<SaveAnswerEventForFormQuestionDto>.toCsv(sectionList: List<SectionEntity>, didiDetailList: List<SurveyeeEntity>) : List<BaseLineQnATableCSV> {
    val csvList = ArrayList<BaseLineQnATableCSV>()
    this.forEach {
        val didiName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.didiName
        val dadaName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.dadaName
        val houseNo = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.houseNo
        val villageName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.villageName
        val cohoretName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.cohortName
        it.question.options.forEach { o ->
            o.forEach { option->
                csvList.add(
                BaseLineQnATableCSV(
                    id = it.question.questionId.toString(),
                    question = it.question.questionDesc,
                    subQuestion = option.optionDesc,
                    subjectId = it.subjectId,
                    response = option.selectedValue,
                    section = sectionList.find { sectionEntity -> sectionEntity.surveyId == it.surveyId && sectionEntity.sectionId == it.sectionId }?.sectionName,
                    didiName = didiName,
                    dadaName = dadaName,
                    house = houseNo,
                    cohoretName = cohoretName,
                    villageName = villageName,
                    surveyId = it.surveyId,
                    sectionId = it.sectionId
                ))
            }
        }
    }
    return csvList
}

fun List<BaseLineQnATableCSV>.toCsvR() : List<BaseLineQnATableCSV> = map {
    BaseLineQnATableCSV(
        id = it.id,
        question = it.question,
        subQuestion = it.subQuestion,
        response = it.response,
        subjectId = it.subjectId,
        section = it.section,
        didiName = it.didiName,
        dadaName = it.dadaName,
        house = it.house,
        cohoretName = it.cohoretName,
        villageName = it.villageName,
        surveyId = it.surveyId,
    )
}

fun List<BaseLineQnATableCSV>.toCsv() : List<HamletQnATableCSV> = map {
    HamletQnATableCSV(
        id = it.id,
        question = it.question,
        subQuestion = it.subQuestion,
        response = it.response,
        subjectId = it.subjectId,
        section = it.section,
        cohoretName = it.didiName, /*CohoretName is Didi Name*/
        villageName = it.villageName,
        surveyId = it.surveyId,
    )
}





