package com.patsurvey.nudge.di


import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.EventsWriterRepository
import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.syncmanager.database.SyncManagerDatabase
import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepository
import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepositoryImpl
import com.patsurvey.nudge.activities.backup.domain.use_case.ClearLocalDBExportUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.ExportImportUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.GetExportOptionListUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.GetUserDetailsExportUseCase
import com.patsurvey.nudge.activities.settings.domain.repository.GetSummaryFileRepository
import com.patsurvey.nudge.activities.settings.domain.repository.GetSummaryFileRepositoryImpl
import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository
import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepositoryImpl
import com.patsurvey.nudge.activities.settings.domain.use_case.ClearSelectionDBExportUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.ExportHandlerSettingUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.GetAllPoorDidiForVillageUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.GetCasteUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.GetSettingOptionListUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.GetSummaryFileUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.GetUserDetailsUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.LogoutUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.SaveLanguageScreenOpenFromUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.SettingBSUserCase
import com.patsurvey.nudge.activities.ui.progress.domain.repository.impls.ChangeUserRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.repository.impls.FetchCasteListRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.repository.impls.FetchPatQuestionRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.repository.impls.FetchSelectionUserDataRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.repository.impls.LanguageListRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.repository.impls.PreferenceProviderRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.repository.impls.SelectionVillageRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.ChangeUserRepository
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchCasteListRepository
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchPatQuestionRepository
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchSelectionUserDataRepository
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.LanguageListRepository
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.PreferenceProviderRepository
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.SelectionVillageRepository
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.ChangeUserUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.FetchCasteListUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.FetchCrpDataUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.FetchPatQuestionUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.LanguageListUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.PreferenceProviderUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.SelectionVillageUseCase
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.data.prefs.SharedPrefs
import com.patsurvey.nudge.database.NudgeDatabase
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.LastSelectedTolaDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.UserDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.database.service.csv.ExportHelper
import com.patsurvey.nudge.network.interfaces.ApiService
import com.sarathi.dataloadingmangement.domain.use_case.DeleteAllGrantDataUseCase
import com.sarathi.dataloadingmangement.repository.UserPropertiesRepository
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
        casteListDao: CasteListDao,
        exportHelper:ExportHelper,
        nudgeDatabase: NudgeDatabase
    ): SettingBSRepository {
        return SettingBSRepositoryImpl(
            prefRepo = prefRepo,
            apiService = apiService,
            didiDao = didiDao,
            stepsListDao = stepsListDao,
            exportHelper = exportHelper,
            casteListDao = casteListDao,
            nudgeDatabase = nudgeDatabase
        )
    }

    @Provides
    @Singleton
    fun providesSettingScreenUseCase(
        repository: SettingBSRepository,
        getSummaryFileRepository: GetSummaryFileRepository
    ): SettingBSUserCase {
        return SettingBSUserCase(
            getSettingOptionListUseCase = GetSettingOptionListUseCase(repository),
            logoutUseCase = LogoutUseCase(repository),
            saveLanguageScreenOpenFromUseCase = SaveLanguageScreenOpenFromUseCase(repository),
            getAllPoorDidiForVillageUseCase = GetAllPoorDidiForVillageUseCase(repository),
            exportHandlerSettingUseCase = ExportHandlerSettingUseCase(repository),
            getUserDetailsUseCase = GetUserDetailsUseCase(repository),
            getSummaryFileUseCase = GetSummaryFileUseCase(getSummaryFileRepository),
            getCasteUseCase = GetCasteUseCase(repository),
            clearSelectionDBExportUseCase = ClearSelectionDBExportUseCase(repository)

        )
    }

    @Provides
    @Singleton
    fun providesExportImportScreenRepository(
        nudgeBaselineDatabase: NudgeBaselineDatabase,
        prefRepo: PrefBSRepo,
        nudgeDatabase: NudgeDatabase,
        coreSharedPrefs: CoreSharedPrefs,
        syncManagerDatabase: SyncManagerDatabase
    ):ExportImportRepository{
        return ExportImportRepositoryImpl(
            nudgeBaselineDatabase = nudgeBaselineDatabase,
            prefBSRepo = prefRepo,
            nudgeDatabase = nudgeDatabase,
            coreSharedPrefs = coreSharedPrefs,
            syncManagerDatabase = syncManagerDatabase
        )
    }

    @Provides
    @Singleton
    fun providesExportImportUseCase(
        repository: ExportImportRepository,
        eventsWriterRepository: EventsWriterRepository,
        deleteAllDataUsecase: DeleteAllGrantDataUseCase
    ): ExportImportUseCase {
        return ExportImportUseCase(
            clearLocalDBExportUseCase = ClearLocalDBExportUseCase(repository, deleteAllDataUsecase),
            getExportOptionListUseCase = GetExportOptionListUseCase(repository),
            getUserDetailsExportUseCase = GetUserDetailsExportUseCase(repository),
            eventsWriterUseCase = EventsWriterUserCase(eventsWriterRepository)

        )
    }

    @Provides
    @Singleton
    fun provideGetSummaryFileRepository(
        activityTaskDao: ActivityTaskDao,
        missionActivityDao: MissionActivityDao
    ): GetSummaryFileRepository {
        return GetSummaryFileRepositoryImpl(activityTaskDao, missionActivityDao)
    }

    @Provides
    @Singleton
    fun provideFetchCrpDataUseCase(
        fetchSelectionUserDataRepository: FetchSelectionUserDataRepository,
        fetchPatQuestionUseCase: FetchPatQuestionUseCase,
        fetchCasteListUseCase: FetchCasteListUseCase,
        userPropertiesRepository: UserPropertiesRepository
    ): FetchCrpDataUseCase {
        return FetchCrpDataUseCase(
            fetchSelectionUserDataRepository = fetchSelectionUserDataRepository,
            fetchPatQuestionUseCase = fetchPatQuestionUseCase,
            fetchCasteListUseCase = fetchCasteListUseCase,
            userPropertiesRepository = userPropertiesRepository
        )
    }

    @Provides
    @Singleton
    fun provideFetchPatQuestionUseCase(
        fetchPatQuestionRepository: FetchPatQuestionRepository,
        languageListUseCase: LanguageListUseCase
    ): FetchPatQuestionUseCase {
        return FetchPatQuestionUseCase(
            fetchPatQuestionRepository = fetchPatQuestionRepository,
            languageListUseCase = languageListUseCase
        )
    }

    @Provides
    @Singleton
    fun provideFetchPatQuestionRepository(
        languageListDao: LanguageListDao,
        questionListDao: QuestionListDao,
        apiService: ApiService,
        coreSharedPrefs: CoreSharedPrefs
    ): FetchPatQuestionRepository {
        return FetchPatQuestionRepositoryImpl(
            languageListDao,
            questionListDao,
            apiService,
            coreSharedPrefs
        )
    }

    @Provides
    @Singleton
    fun provideFetchCasteListUseCase(
        fetchCasteListRepository: FetchCasteListRepository,
        languageListUseCase: LanguageListUseCase
    ): FetchCasteListUseCase {

        return FetchCasteListUseCase(
            fetchCasteListRepository = fetchCasteListRepository,
            languageListUseCase = languageListUseCase
        )

    }

    @Provides
    @Singleton
    fun provideFetchCasteListRepository(
        casteListDao: CasteListDao,
        apiService: ApiService,
        coreSharedPrefs: CoreSharedPrefs
    ): FetchCasteListRepository {
        return FetchCasteListRepositoryImpl(
            casteListDao = casteListDao,
            apiService = apiService,
            corePrefRepo = coreSharedPrefs
        )
    }

    @Provides
    @Singleton
    fun provideChangeUserUseCase(
        changeUserRepository: ChangeUserRepository
    ): ChangeUserUseCase {
        return ChangeUserUseCase(changeUserRepository)
    }

    @Provides
    @Singleton
    fun provideChangeUserRepository(
        selectionSharedPrefs: SharedPrefs,
        coreSharedPrefs: CoreSharedPrefs,
        casteListDao: CasteListDao,
        didiDao: DidiDao,
        stepsListDao: StepsListDao,
        tolaDao: TolaDao,
        lastSelectedTolaDao: LastSelectedTolaDao,
        numericAnswerDao: NumericAnswerDao,
        answerDao: AnswerDao,
        questionListDao: QuestionListDao,
        userDao: UserDao,
        villageListDao: VillageListDao,
        bpcSummaryDao: BpcSummaryDao,
        poorDidiListDao: PoorDidiListDao,
        syncManagerDatabase: SyncManagerDatabase,
    ): ChangeUserRepository {
        return ChangeUserRepositoryImpl(
            selectionSharedPrefs = selectionSharedPrefs,
            coreSharedPrefs = coreSharedPrefs,
            casteListDao = casteListDao,
            didiDao = didiDao,
            stepsListDao = stepsListDao,
            tolaDao = tolaDao,
            lastSelectedTolaDao = lastSelectedTolaDao,
            numericAnswerDao = numericAnswerDao,
            answerDao = answerDao,
            questionListDao = questionListDao,
            userDao = userDao,
            villageListDao = villageListDao,
            bpcSummaryDao = bpcSummaryDao,
            poorDidiListDao = poorDidiListDao,
            syncManagerDatabase = syncManagerDatabase
        )
    }

    @Provides
    @Singleton
    fun providesSelectionVillageUseCase(
        selectionVillageRepository: SelectionVillageRepository
    ): SelectionVillageUseCase {
        return SelectionVillageUseCase(
            selectionVillageRepository
        )
    }

    @Provides
    @Singleton
    fun providesSelectionVillageRepository(
        selectionSharedPrefs: SharedPrefs,
        coreSharedPrefs: CoreSharedPrefs,
        villageListDao: VillageListDao
    ): SelectionVillageRepository {
        return SelectionVillageRepositoryImpl(
            selectionSharedPrefs = selectionSharedPrefs,
            coreSharedPrefs = coreSharedPrefs,
            villageListDao = villageListDao
        )
    }

    @Provides
    @Singleton
    fun providesPreferenceProviderUseCase(
        preferenceProviderRepository: PreferenceProviderRepository
    ): PreferenceProviderUseCase {
        return PreferenceProviderUseCase(preferenceProviderRepository)

    }

    @Provides
    @Singleton
    fun providesPreferenceProviderRepository(
        selectionSharedPrefs: SharedPrefs,
        coreSharedPrefs: CoreSharedPrefs
    ): PreferenceProviderRepository {
        return PreferenceProviderRepositoryImpl(
            selectionSharedPrefs = selectionSharedPrefs,
            coreSharedPrefs = coreSharedPrefs
        )
    }

    @Provides
    @Singleton
    fun providesFetchSelectionUserDataRepository(
        sharedPrefs: CoreSharedPrefs,
        apiService: ApiService,
        languageListDao: LanguageListDao,
        villageListDao: VillageListDao
    ): FetchSelectionUserDataRepository {
        return FetchSelectionUserDataRepositoryImpl(
            sharedPrefs = sharedPrefs,
            apiService = apiService,
            languageDao = languageListDao,
            villageListDao = villageListDao
        )
    }

    @Provides
    @Singleton
    fun providesLanguageListUseCase(
        languageListRepository: LanguageListRepository
    ): LanguageListUseCase {
        return LanguageListUseCase(
            languageListRepository = languageListRepository
        )

    }

    @Provides
    @Singleton
    fun providesLanguageListRepository(
        languageListDao: LanguageListDao
    ): LanguageListRepository {
        return LanguageListRepositoryImpl(languageListDao)

    }
}