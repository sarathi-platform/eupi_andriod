package com.patsurvey.nudge.di

import com.patsurvey.nudge.activities.ui.splash.ConfigRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcScorePercentageDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.service.csv.ExportHelper
import com.patsurvey.nudge.network.interfaces.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ExportModule {

    @Singleton
    @Provides
    fun provideExportHelper(
        apiService: ApiService, didiDao: DidiDao, tolaDao: TolaDao, answerDao: AnswerDao, numericAnswerDao: NumericAnswerDao
    ): ExportHelper {
        return ExportHelper(apiService, didiDao, tolaDao, answerDao, numericAnswerDao)
    }

}