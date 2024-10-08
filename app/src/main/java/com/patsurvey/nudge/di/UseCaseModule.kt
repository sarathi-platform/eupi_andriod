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
import com.patsurvey.nudge.activities.ui.progress.domain.repository.ChangeUserRepository
import com.patsurvey.nudge.activities.ui.progress.domain.repository.ChangeUserRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.repository.FetchCasteListRepository
import com.patsurvey.nudge.activities.ui.progress.domain.repository.FetchCasteListRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.repository.FetchPatQuestionRepository
import com.patsurvey.nudge.activities.ui.progress.domain.repository.FetchPatQuestionRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.ChangeUserUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.FetchCasteListUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.FetchCrpDataUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.FetchPatQuestionUseCase
import com.patsurvey.nudge.data.prefs.PrefRepo
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
import com.sarathi.dataloadingmangement.domain.use_case.FetchUserDetailUseCase
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
        fetchUserDetailUseCase: FetchUserDetailUseCase,
        fetchPatQuestionUseCase: FetchPatQuestionUseCase,
        fetchCasteListUseCase: FetchCasteListUseCase
    ): FetchCrpDataUseCase {
        return FetchCrpDataUseCase(
            fetchUserDetailUseCase = fetchUserDetailUseCase,
            fetchPatQuestionUseCase = fetchPatQuestionUseCase,
            fetchCasteListUseCase = fetchCasteListUseCase
        )
    }

    @Provides
    @Singleton
    fun provideFetchPatQuestionUseCase(
        fetchPatQuestionRepository: FetchPatQuestionRepository
    ): FetchPatQuestionUseCase {
        return FetchPatQuestionUseCase(fetchPatQuestionRepository = fetchPatQuestionRepository)
    }

    @Provides
    @Singleton
    fun provideFetchPatQuestionRepository(
        languageListDao: LanguageListDao,
        questionListDao: QuestionListDao,
        coreSharedPrefs: CoreSharedPrefs
    ): FetchPatQuestionRepository {
        return FetchPatQuestionRepositoryImpl(languageListDao, questionListDao, coreSharedPrefs)
    }

    @Provides
    @Singleton
    fun provideFetchCasteListUseCase(
        fetchCasteListRepository: FetchCasteListRepository
    ): FetchCasteListUseCase {

        return FetchCasteListUseCase(fetchCasteListRepository = fetchCasteListRepository)

    }

    @Provides
    @Singleton
    fun provideFetchCasteListRepository(
        languageListDao: LanguageListDao,
        casteListDao: CasteListDao,
        coreSharedPrefs: CoreSharedPrefs
    ): FetchCasteListRepository {
        return FetchCasteListRepositoryImpl(
            languageListDao = languageListDao,
            casteListDao = casteListDao,
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

}