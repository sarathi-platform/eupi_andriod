package com.nudge.core.di


import com.nudge.core.database.dao.translation.TranslationConfigDao
import com.nudge.core.helper.TranslationHelper
import com.nudge.core.preference.CoreSharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class TranslationHelperModule {
    @Singleton
    @Provides
    fun provideTranslationHelper(
        translationConfigDao: TranslationConfigDao, coreSharedPrefs: CoreSharedPrefs
    ): TranslationHelper {
        return TranslationHelper(
            translationConfigDao = translationConfigDao,
            coreSharedPrefs = coreSharedPrefs
        )
    }
}