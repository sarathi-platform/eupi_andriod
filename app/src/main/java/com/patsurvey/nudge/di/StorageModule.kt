package com.patsurvey.nudge.di

import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.data.prefs.SharedPrefs
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    abstract fun providesPreferences(
        sharedPref:SharedPrefs
    ):PrefRepo
}