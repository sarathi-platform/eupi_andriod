package com.nrlm.baselinesurvey.model.datamodel

import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nudge.core.datamodel.BaseLineQnATableCSV
import com.nudge.core.datamodel.HamletQnATableCSV


suspend fun List<SaveAnswerEventDto>.toCSVSave(
    sectionList: List<SectionEntity>,
    didiDetailList: List<SurveyeeEntity>,
    optionItemDao: OptionItemDao,
    questionEntityDao: QuestionEntityDao,
    uniqueUserIdentifier: String
): List<BaseLineQnATableCSV> = map {
    val csvList = ArrayList<BaseLineQnATableCSV>()
    var response = ""
    this.forEach {
        val didiName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.didiName
        val dadaName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.dadaName
        val houseNo = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.houseNo
        val villageName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.villageName
        val cohoretName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.cohortName

        it.question.options.forEach { option ->
            if (it.question.questionType.equals(
                    QuestionType.SingleSelectDropdown.name,
                    ignoreCase = true
                )
            ) {

                val optionId = listOf(option.optionId)
                val selectedValueOption = optionItemDao.getOptions(
                    DEFAULT_LANGUAGE_ID,
                    optionId,
                    it.question.questionId,
                    it.sectionId,
                    it.surveyId
                )
                val optionItemId = option.selectedValueWithIds
                val selectedValue = selectedValueOption.flatMap { it.values!! }
                var matchingValues: List<ValuesDto> = emptyList()
                if (!optionItemId.isNullOrEmpty()) {
                    matchingValues = optionItemId.flatMap { optionItem ->
                        // For each option item, find values that match a certain condition, here it's just an example.
                        // You need to replace 'id' with the actual property from ValuesDto that should match optionItem.optionId.
                        selectedValue.filter { valuesDto ->
                            optionItem.id == valuesDto.id
                        }
                    }
                } else {
                    matchingValues = selectedValueOption.flatMap { optionItem ->
                        // For each option item, find values that match a certain condition, here it's just an example.
                        // You need to replace 'id' with the actual property from ValuesDto that should match optionItem.optionId.
                        selectedValue.filter { valuesDto ->
                            valuesDto.id == optionItem.id
                        }
                    }
                }
                val selectedResponse = matchingValues.map { it.value }

                response = selectedResponse.joinToString("\n")
            } else if (it.question.questionType.toLowerCase()
                    .contains(QuestionType.Input.name.toLowerCase()) ||
                it.question.questionType.toLowerCase()
                    .contains(QuestionType.YrsMonthPicker.name.toLowerCase())
            ) {
                val selectedValues =
                    option.selectedValue //it.question.options.map { mIt -> mIt.selectedValue }
                if (selectedValues != null) {
                    response = selectedValues
                }
            } else {
                val optionId = listOf(option.optionId)
                val selectedValue = optionItemDao.getOptions(
                    DEFAULT_LANGUAGE_ID,
                    optionId,
                    it.question.questionId,
                    it.sectionId,
                    it.surveyId
                )
                val selectedResponse = selectedValue.map { it.display }
                response = selectedResponse.joinToString("\n")


            }
            csvList.add(
                BaseLineQnATableCSV(
                    id = it.question.questionId.toString(),
                    question = it.question.questionDesc,
                    subQuestion = option.optionDesc,
                    response = response,
                    subjectId = it.subjectId,
                    section = sectionList.find { sectionEntity -> sectionEntity.surveyId == it.surveyId && sectionEntity.sectionId == it.sectionId }?.sectionName,
                    didiName = didiName,
                    dadaName = dadaName,
                    house = houseNo,
                    cohoretName = cohoretName,
                    villageName = villageName,
                    surveyId = it.surveyId,
                    sectionId = it.sectionId,
                    orderId = questionEntityDao.getOrderId(
                        userid = uniqueUserIdentifier,
                        surveyId = it.surveyId,
                        sectionId = it.sectionId,
                        questionId = it.question.questionId
                    )
                )
            )
        }
    }
    return csvList
}

fun List<SaveAnswerEventForFormQuestionDto>.toCsv(
    sectionList: List<SectionEntity>,
    didiDetailList: List<SurveyeeEntity>,
    optionItemDao: OptionItemDao,
    questionEntityDao: QuestionEntityDao,
    uniqueUserIdentifier: String
): List<BaseLineQnATableCSV> {
    val csvList = ArrayList<BaseLineQnATableCSV>()
    this.forEach {
        val didiName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.didiName
        val dadaName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.dadaName
        val houseNo = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.houseNo
        val villageName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.villageName
        val cohoretName = didiDetailList.find { mIt -> mIt.didiId == it.subjectId }?.cohortName
        var response = ""
        it.question.options.forEach { o ->
            o.forEach { option ->
                var selectedResponse = listOf<String>()

                if (!option.selectedValueWithIds.isNullOrEmpty()) {
                    val optionId = listOf(option.optionId)
                    val selectedValueOption = optionItemDao.getOptions(
                        DEFAULT_LANGUAGE_ID,
                        optionId,
                        it.question.questionId,
                        it.sectionId,
                        it.surveyId
                    )
                    if (!selectedValueOption.isNullOrEmpty()) {
                        val selectedValue = selectedValueOption.first().values?.filter {
                            option.selectedValueWithIds.contains(it.id)
                        }

                        selectedResponse = selectedValue?.map { it.value } ?: listOf()

//                            response = selectedResponse?.joinToString("\n") ?: ""
                    } else {
                        selectedResponse = option.selectedValueWithIds.map { it.value }
                        //                          response = selectedResponse?.joinToString("\n") ?: ""
                    }
                } else {
                    selectedResponse = listOf(option.selectedValue!!)


                }

                selectedResponse.forEach { response ->
                    csvList.add(
                        BaseLineQnATableCSV(
                            id = it.question.questionId.toString(),
                            question = it.question.questionDesc,
                            subQuestion = option.optionDesc,
                            subjectId = it.subjectId,
                            response = response,
                            section = sectionList.find { sectionEntity -> sectionEntity.surveyId == it.surveyId && sectionEntity.sectionId == it.sectionId }?.sectionName,
                            didiName = didiName,
                            dadaName = dadaName,
                            house = houseNo,
                            cohoretName = cohoretName,
                            villageName = villageName,
                            surveyId = it.surveyId,
                            sectionId = it.sectionId,
                            orderId = questionEntityDao.getOrderId(
                                userid = uniqueUserIdentifier,
                                surveyId = it.surveyId,
                                sectionId = it.sectionId,
                                questionId = it.question.questionId
                            )
                        )
                    )
                }
            }
        }
    }
    return csvList
}

fun List<BaseLineQnATableCSV>.toCsvR(): List<BaseLineQnATableCSV> = map {
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
        orderId = it.orderId
    )
}

fun List<BaseLineQnATableCSV>.toCsv(): List<HamletQnATableCSV> = map {
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





