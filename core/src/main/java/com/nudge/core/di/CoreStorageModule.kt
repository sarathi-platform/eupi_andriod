package com.nudge.core.di

import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.preference.CoreSharedPrefs
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    abstract fun providesPreferences(
        sharedPref: CoreSharedPrefs
    ): CorePrefRepo
}
