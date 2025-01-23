package com.nudge.core.di


import android.content.Context
import com.nudge.core.database.dao.translation.TranslationConfigDao
import com.nudge.core.helper.TranslationHelper
import com.nudge.core.preference.CoreSharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class TranslationHelperModule {
    @Provides
    fun provideTranslationHelper(
        translationConfigDao: TranslationConfigDao, coreSharedPrefs: CoreSharedPrefs,
        @ApplicationContext context: Context
    ): TranslationHelper {
        return TranslationHelper(
            translationConfigDao = translationConfigDao,
            coreSharedPrefs = coreSharedPrefs,
            context = context
        )
    }

    @Singleton
    @Provides
    fun provideCoreSharedPrefs(@ApplicationContext context: Context): CoreSharedPrefs {
        return CoreSharedPrefs(context)
    }
}