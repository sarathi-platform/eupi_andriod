package com.nrlm.baselinesurvey.model.datamodel

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.response.ContentList

data class SectionListItem(

    val sectionId: Int = 0,

    val surveyId: Int = 0,

    val sectionName: String = NO_SECTION,

    val sectionOrder: Int = 1,

    val sectionDetails: String = BLANK_STRING,

    val sectionIcon: String = BLANK_STRING,

    val questionSize: Int = 0,

//    val sectionIcon: Int = 0,

    val contentData: ContentEntity? = null,

    val questionList: List<QuestionEntity> = listOf(),

    val questionAnswerMapping: Map<Int, List<OptionItemEntity>> = mapOf(),
    val questionContentMapping: Map<Int, List<ContentList>> = mapOf(),

    val languageId: Int,
    val optionsItemMap: Map<Int, List<OptionItemEntity>> = mapOf()

)
