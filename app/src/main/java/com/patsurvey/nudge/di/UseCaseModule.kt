package com.patsurvey.nudge.di


import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepository
import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepositoryImpl
import com.patsurvey.nudge.activities.backup.domain.use_case.ClearLocalDBExportUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.ExportImportUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.GetExportOptionListUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.GetUserDetailsExportUseCase
import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository
import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepositoryImpl
import com.patsurvey.nudge.activities.settings.domain.use_case.ExportHandlerSettingUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.GetAllPoorDidiForVillageUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.GetSettingOptionListUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.GetUserDetailsUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.LogoutUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.SaveLanguageScreenOpenFromUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.SettingBSUserCase
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.service.csv.ExportHelper
import com.patsurvey.nudge.network.interfaces.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideSettingBSScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService,
        didiDao: DidiDao,
        stepsListDao: StepsListDao,
        exportHelper:ExportHelper
    ): SettingBSRepository {
        return SettingBSRepositoryImpl(prefRepo, apiService,didiDao,stepsListDao, exportHelper)
    }

    @Provides
    @Singleton
    fun providesSettingScreenUseCase(
        repository: SettingBSRepository
    ): SettingBSUserCase {
        return SettingBSUserCase(
            getSettingOptionListUseCase = GetSettingOptionListUseCase(repository),
            logoutUseCase = LogoutUseCase(repository),
            saveLanguageScreenOpenFromUseCase = SaveLanguageScreenOpenFromUseCase(repository),
            getAllPoorDidiForVillageUseCase = GetAllPoorDidiForVillageUseCase(repository),
            exportHandlerSettingUseCase = ExportHandlerSettingUseCase(repository),
            getUserDetailsUseCase = GetUserDetailsUseCase(repository)

        )
    }

    @Provides
    @Singleton
    fun providesExportImportScreenRepository(
        nudgeBaselineDatabase: NudgeBaselineDatabase,
        prefRepo: PrefBSRepo
    ):ExportImportRepository{
        return ExportImportRepositoryImpl(
            nudgeBaselineDatabase = nudgeBaselineDatabase,
            prefBSRepo = prefRepo
        )
    }

    @Provides
    @Singleton
    fun providesExportImportUseCase(
        repository: ExportImportRepository
    ): ExportImportUseCase {
        return ExportImportUseCase(
            clearLocalDBExportUseCase = ClearLocalDBExportUseCase(repository),
            getExportOptionListUseCase = GetExportOptionListUseCase(repository),
            getUserDetailsExportUseCase = GetUserDetailsExportUseCase(repository)
        )
    }
}