package com.patsurvey.nudge.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.patsurvey.nudge.database.converters.BeneficiaryStepConverter
import com.patsurvey.nudge.database.converters.IntConverter
import com.patsurvey.nudge.database.converters.QuestionsOptionsConverter
import com.patsurvey.nudge.database.dao.*

@Database(entities = [VillageEntity::class, UserEntity::class, LanguageEntity::class, StepListEntity::class, CasteEntity::class,
    TolaEntity::class, DidiEntity::class, LastTolaSelectedEntity::class,QuestionEntity::class,SectionAnswerEntity::class], version = 1, exportSchema = false)
@TypeConverters(IntConverter::class, BeneficiaryStepConverter::class,QuestionsOptionsConverter::class)
abstract class NudgeDatabase: RoomDatabase()  {

    abstract fun villageListDao(): VillageListDao
    abstract fun userDao(): UserDao
    abstract fun languageListDao(): LanguageListDao
    abstract fun stepsListDao(): StepsListDao
    abstract fun tolaDao(): TolaDao
    abstract fun casteListDao(): CasteListDao
    abstract fun didiDao(): DidiDao
    abstract fun lastSelectedTola(): LastSelectedTolaDao
    abstract fun questionListDao(): QuestionListDao
    abstract fun answerDao(): AnswerDao

}