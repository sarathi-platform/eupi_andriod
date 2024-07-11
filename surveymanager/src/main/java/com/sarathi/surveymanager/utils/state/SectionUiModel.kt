package com.sarathi.surveymanager.utils.state

import androidx.room.TypeConverters
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.converters.ContentListConverter
import com.sarathi.dataloadingmangement.model.survey.response.ContentList

data class SectionUiModel(
    val id: Int,
    var userId: String? = BLANK_STRING,
    val sectionId: Int = 0,
    val missionId: Int,
    val surveyId: Int,
    val sectionName: String = BLANK_STRING,
    val sectionOrder: Int,
    val sectionDetails: String = BLANK_STRING,
    val sectionIcon: String = BLANK_STRING,
    val questionSize: Int = 0,
    val sectionStatus: String,
    val taskId: Int,
    @TypeConverters(ContentListConverter::class)
    val contentEntities: List<ContentList>
)
