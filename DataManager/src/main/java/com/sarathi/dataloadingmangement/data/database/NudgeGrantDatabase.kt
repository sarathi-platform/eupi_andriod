package com.sarathi.dataloadingmangement.data.database


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sarathi.dataloadingmangement.data.converters.ConditionsDtoConvertor
import com.sarathi.dataloadingmangement.data.converters.ContentListConverter
import com.sarathi.dataloadingmangement.data.converters.ContentMapConverter
import com.sarathi.dataloadingmangement.data.converters.IntConverter
import com.sarathi.dataloadingmangement.data.converters.OptionQuestionConverter
import com.sarathi.dataloadingmangement.data.converters.QuestionsOptionsConverter
import com.sarathi.dataloadingmangement.data.converters.StringConverter

import com.sarathi.dataloadingmangement.data.converters.ValuesDtoConverter
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.ActivityLanguageDao
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.LanguageDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.MissionLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.ProgrammeDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TaskAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigLanguageAttributesEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityLanguageAttributesEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.AttributeValueReferenceEntity
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.data.entities.ContentConfigEntity
import com.sarathi.dataloadingmangement.data.entities.LanguageEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.data.entities.MissionLanguageEntity
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity
import com.sarathi.dataloadingmangement.data.entities.ProgrammeEntity
import com.sarathi.dataloadingmangement.data.entities.QuestionEntity
import com.sarathi.dataloadingmangement.data.entities.SectionEntity
import com.sarathi.dataloadingmangement.data.entities.SubjectAttributeEntity
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyLanguageAttributeEntity
import com.sarathi.dataloadingmangement.data.entities.TaskAttributesEntity
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.dataloadingmangement.data.entities.smallGroup.SmallGroupDidiMappingEntity

const val NUDGE_GRANT_DATABASE_VERSION = 1

@Database(
    entities = [
        MissionEntity::class,
        ActivityEntity::class,
        ActivityTaskEntity::class,
        ActivityConfigEntity::class,
        ActivityLanguageAttributesEntity::class,
        ActivityConfigLanguageAttributesEntity::class,
        AttributeValueReferenceEntity::class,
        MissionLanguageEntity::class,
        SubjectAttributeEntity::class,
        TaskAttributesEntity::class,
        UiConfigEntity::class,
        ContentConfigEntity::class,
        Content::class,
        SurveyEntity::class,
        SectionEntity::class,
        QuestionEntity::class,
        OptionItemEntity::class,
        ProgrammeEntity::class,
        SurveyAnswerEntity::class,
        SurveyLanguageAttributeEntity::class,
        SubjectEntity::class,
        SmallGroupDidiMappingEntity::class
    ],
    version = NUDGE_GRANT_DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    IntConverter::class,
    QuestionsOptionsConverter::class,
    OptionQuestionConverter::class,
    StringConverter::class,
    ConditionsDtoConvertor::class,
    ContentListConverter::class,
    ContentMapConverter::class,
    ValuesDtoConverter::class,
)
abstract class NudgeGrantDatabase : RoomDatabase() {

    abstract fun missionDao(): MissionDao
    abstract fun activityDao(): ActivityDao
    abstract fun taskDao(): TaskDao
    abstract fun contentDao(): ContentDao
    abstract fun languageDao(): LanguageDao
    abstract fun activityConfigDao(): ActivityConfigDao
    abstract fun activityLanguageAttributeDao(): ActivityLanguageAttributeDao
    abstract fun activityLanguageDao(): ActivityLanguageDao
    abstract fun attributeValueReferenceDao(): AttributeValueReferenceDao
    abstract fun contentConfigDao(): ContentConfigDao
    abstract fun missionLanguageAttributeDao(): MissionLanguageAttributeDao
    abstract fun subjectAttributeDao(): SubjectAttributeDao
    abstract fun taskAttributeDao(): TaskAttributeDao
    abstract fun uiConfigDao(): UiConfigDao
    abstract fun surveyEntityDao(): SurveyEntityDao

    abstract fun sectionEntityDao(): SectionEntityDao

    abstract fun questionEntityDao(): QuestionEntityDao
    abstract fun optionItemDao(): OptionItemDao
    abstract fun programmeDao(): ProgrammeDao
    abstract fun surveyAnswersDao(): SurveyAnswersDao
    abstract fun surveyLanguageAttributeDao(): SurveyLanguageAttributeDao


    abstract fun subjectEntityDao(): SubjectEntityDao

    abstract fun smallGroupDidiMappingDao(): SmallGroupDidiMappingDao

    class NudgeDatabaseCallback : Callback()

}