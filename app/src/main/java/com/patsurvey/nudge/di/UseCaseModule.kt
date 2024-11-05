package com.patsurvey.nudge.di


import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.EventsWriterRepository
import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.UpdateMissionStatusUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.UpdateActivityStatusUseCase
import com.nudge.core.analytics.AnalyticsManager
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.dao.RequestStatusDao
import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.syncmanager.database.SyncManagerDatabase
import com.nudge.syncmanager.domain.repository.SyncApiRepository
import com.nudge.syncmanager.domain.repository.SyncApiRepositoryImpl
import com.nudge.syncmanager.domain.repository.SyncBlobRepository
import com.nudge.syncmanager.domain.repository.SyncBlobRepositoryImpl
import com.nudge.syncmanager.domain.repository.SyncRepository
import com.nudge.syncmanager.domain.repository.SyncRepositoryImpl
import com.nudge.syncmanager.domain.usecase.AddUpdateEventUseCase
import com.nudge.syncmanager.domain.usecase.BlobUploadUseCase
import com.nudge.syncmanager.domain.usecase.FetchEventsFromDBUseCase
import com.nudge.syncmanager.domain.usecase.GetUserDetailsSyncRepoUseCase
import com.nudge.syncmanager.domain.usecase.SyncAPIUseCase
import com.nudge.syncmanager.domain.usecase.SyncAnalyticsEventUseCase
import com.nudge.syncmanager.domain.usecase.SyncManagerUseCase
import com.nudge.syncmanager.imageupload.BlobImageUploader
import com.nudge.syncmanager.network.SyncApiService
import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepository
import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepositoryImpl
import com.patsurvey.nudge.activities.backup.domain.repository.ReopenActivityEventHelperRepository
import com.patsurvey.nudge.activities.backup.domain.repository.ReopenActivityEventHelperRepositoryImpl
import com.patsurvey.nudge.activities.backup.domain.use_case.ClearLocalDBExportUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.ExportImportUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.GetExportOptionListUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.GetUserDetailsExportUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.ReopenActivityEventHelperUseCase
import com.patsurvey.nudge.activities.backup.domain.use_case.ReopenActivityUseCase
import com.patsurvey.nudge.activities.domain.repository.impls.CheckEventLimitThresholdRepositoryImpl
import com.patsurvey.nudge.activities.domain.repository.interfaces.CheckEventLimitThresholdRepository
import com.patsurvey.nudge.activities.domain.useCase.CheckEventLimitThresholdUseCase
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
import com.patsurvey.nudge.activities.sync.history.domain.repository.SyncHistoryRepository
import com.patsurvey.nudge.activities.sync.history.domain.repository.SyncHistoryRepositoryImpl
import com.patsurvey.nudge.activities.sync.history.domain.use_case.GetSyncHistoryUseCase
import com.patsurvey.nudge.activities.sync.history.domain.use_case.SyncHistoryUseCase
import com.patsurvey.nudge.activities.sync.home.domain.repository.SyncHomeRepository
import com.patsurvey.nudge.activities.sync.home.domain.repository.SyncHomeRepositoryImpl
import com.patsurvey.nudge.activities.sync.home.domain.use_case.FetchLastSyncDateForNetwork
import com.patsurvey.nudge.activities.sync.home.domain.use_case.GetSyncEventsUseCase
import com.patsurvey.nudge.activities.sync.home.domain.use_case.GetUserDetailsSyncUseCase
import com.patsurvey.nudge.activities.sync.home.domain.use_case.SyncEventDetailUseCase
import com.patsurvey.nudge.activities.ui.progress.domain.repository.impls.SelectionVillageRepositoryImpl
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.SelectionVillageRepository
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.SelectionVillageUseCase
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.data.prefs.SharedPrefs
import com.patsurvey.nudge.database.NudgeDatabase
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.database.service.csv.ExportHelper
import com.patsurvey.nudge.network.interfaces.ApiService
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.domain.use_case.DeleteAllGrantDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
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
        getSummaryFileRepository: GetSummaryFileRepository,
        syncHomeRepository: SyncHomeRepository
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
            clearSelectionDBExportUseCase = ClearSelectionDBExportUseCase(repository),
            getSyncEventsUseCase = GetSyncEventsUseCase(syncHomeRepository)

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
    fun provideSyncHomeRepository(
        corePrefRepo: CorePrefRepo,
        eventsDao: EventsDao,
        syncApiService: SyncApiService
    ): SyncHomeRepository {
        return SyncHomeRepositoryImpl(
            corePrefRepo = corePrefRepo,
            eventsDao = eventsDao,
            syncApiService = syncApiService
        )
    }

    @Provides
    @Singleton
    fun provideSyncHomeUseCase(
        repository: SyncHomeRepository,
        eventsWriterRepository: EventsWriterRepository,
        syncRepository: SyncRepository,
        syncAPiRepository: SyncApiRepository,
        syncAnalyticsEventUseCase: SyncAnalyticsEventUseCase
    ): SyncEventDetailUseCase {
        return SyncEventDetailUseCase(
            getUserDetailsSyncUseCase = GetUserDetailsSyncUseCase(repository),
            getSyncEventsUseCase = GetSyncEventsUseCase(repository),
            eventsWriterUseCase = EventsWriterUserCase(eventsWriterRepository),
            fetchLastSyncDateForNetwork = FetchLastSyncDateForNetwork(repository),
            syncAPIUseCase = SyncAPIUseCase(syncRepository, syncAPiRepository),
            syncAnalyticsEventUseCase = syncAnalyticsEventUseCase
        )
    }

    @Provides
    @Singleton
    fun provideSyncHistoryRepository(
        prefRepo: PrefRepo,
        eventsDao: EventsDao,
        eventStatusDao: EventStatusDao
    ):SyncHistoryRepository{
        return SyncHistoryRepositoryImpl(
            prefRepo = prefRepo,
            eventsDao = eventsDao,
            eventStatusDao = eventStatusDao
        )
    }

    @Provides
    @Singleton
    fun provideSyncHistoryUseCase(
        repository: SyncHistoryRepository
    ):SyncHistoryUseCase{
        return SyncHistoryUseCase(
           getSyncHistoryUseCase = GetSyncHistoryUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideSyncRepository(
        corePrefRepo: CorePrefRepo,
        requestStatusDao: RequestStatusDao,
        eventStatusDao: EventStatusDao,
        imageStatusDao: ImageStatusDao,
        apiService: SyncApiService,
        eventsDao: EventsDao,
        blobImageUploader: BlobImageUploader
    ): SyncRepository {
        return SyncRepositoryImpl(
            corePrefRepo = corePrefRepo,
            requestStatusDao = requestStatusDao,
            eventStatusDao = eventStatusDao,
            imageStatusDao = imageStatusDao,
            apiService = apiService,
            eventDao = eventsDao,
            imageUploader = blobImageUploader
        )
    }

    @Provides
    @Singleton
    fun provideSyncApiRepository(
        apiService: SyncApiService,
        eventStatusDao: EventStatusDao,
        corePrefRepo: CorePrefRepo,
        imageStatusDao: ImageStatusDao,
    ): SyncApiRepository {
        return SyncApiRepositoryImpl(
            apiService = apiService,
            imageStatusDao = imageStatusDao,
            eventStatusDao = eventStatusDao,
            corePrefRepo = corePrefRepo
        )
    }

    @Provides
    @Singleton
    fun provideSyncManagerUseCase(
        repository: SyncRepository,
        syncAPiRepository: SyncApiRepository,
        syncBlobRepository: SyncBlobRepository,
        analyticsManager: AnalyticsManager,
        fetchAppConfigFromCacheOrDbUsecase: FetchAppConfigFromCacheOrDbUsecase
    ): SyncManagerUseCase {
        return SyncManagerUseCase(
            addUpdateEventUseCase = AddUpdateEventUseCase(repository),
            syncAPIUseCase = SyncAPIUseCase(repository, syncAPiRepository),
            getUserDetailsSyncUseCase = GetUserDetailsSyncRepoUseCase(repository),
            fetchEventsFromDBUseCase = FetchEventsFromDBUseCase(repository),
            syncBlobUploadUseCase = BlobUploadUseCase(syncBlobRepository),
            syncAnalyticsEventUseCase = SyncAnalyticsEventUseCase(
                analyticsManager = analyticsManager
            ),
            fetchAppConfigFromCacheOrDbUsecase = fetchAppConfigFromCacheOrDbUsecase
        )
    }

    @Provides
    @Singleton
    fun provideSyncAnalyticsEventUseCase(
        analyticsManager: AnalyticsManager,
    ): SyncAnalyticsEventUseCase {
        return SyncAnalyticsEventUseCase(analyticsManager)
    }

    @Provides
    @Singleton
    fun provideSyncBlobRepository(
        apiService: SyncApiService,
        eventStatusDao: EventStatusDao,
        corePrefRepo: CorePrefRepo,
        imageStatusDao: ImageStatusDao,
        blobImageUploader: BlobImageUploader,
        eventsDao: EventsDao
    ): SyncBlobRepository {
        return SyncBlobRepositoryImpl(
            eventStatusDao = eventStatusDao,
            imageStatusDao = imageStatusDao,
            corePrefRepo = corePrefRepo,
            apiService = apiService,
            blobImageUploader = blobImageUploader,
            eventsDao = eventsDao
        )
    }



    @Provides
    @Singleton
    fun provideReopenActivityEventHelperUseCase(
        reopenActivityEventHelperRepository: ReopenActivityEventHelperRepository
    ): ReopenActivityEventHelperUseCase {
        return ReopenActivityEventHelperUseCase(reopenActivityEventHelperRepository)
    }

    @Provides
    @Singleton
    fun provideReopenActivityEventHelperRepository(
        coreSharedPrefs: CoreSharedPrefs,
        prefRepo: PrefBSRepo,
        activityDao: ActivityDao,
        missionActivityDao: MissionActivityDao
    ): ReopenActivityEventHelperRepository {
        return ReopenActivityEventHelperRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            prefBSRepo = prefRepo,
            activityDao = activityDao,
            missionActivityDao = missionActivityDao
        )
    }

    @Provides
    @Singleton
    fun provideReopenActivityUseCase(
        updateMissionActivityTaskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
        updateMissionStatusUseCase: UpdateMissionStatusUseCase, // For Baseline
        updateActivityStatusUseCase: UpdateActivityStatusUseCase, // For Baseline
        matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
        reopenActivityEventHelperUseCase: ReopenActivityEventHelperUseCase,
        eventWriterHelperImpl: EventWriterHelperImpl,
    ): ReopenActivityUseCase {

        return ReopenActivityUseCase(
            updateMissionActivityTaskStatusUseCase = updateMissionActivityTaskStatusUseCase,
            updateMissionStatusUseCase = updateMissionStatusUseCase,
            updateActivityStatusUseCase = updateActivityStatusUseCase,
            matStatusEventWriterUseCase = matStatusEventWriterUseCase,
            reopenActivityEventHelperUseCase = reopenActivityEventHelperUseCase,
            eventWriterHelperImpl = eventWriterHelperImpl
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
    fun providesFetchEventsFromDBUseCase(repository: SyncRepository): FetchEventsFromDBUseCase {
        return FetchEventsFromDBUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesCheckEventLimitThresholdUseCase(
        checkEventLimitThresholdRepository: CheckEventLimitThresholdRepository,
        fetchEventsFromDBUseCase: FetchEventsFromDBUseCase
    ): CheckEventLimitThresholdUseCase {
        return CheckEventLimitThresholdUseCase(
            checkEventLimitThresholdRepository,
            fetchEventsFromDBUseCase
        )
    }

    @Provides
    @Singleton
    fun providesCheckEventLimitThresholdRepository(
        coreSharedPrefs: CoreSharedPrefs,
        eventsDao: EventsDao,
        eventStatusDao: EventStatusDao
    ): CheckEventLimitThresholdRepository {
        return CheckEventLimitThresholdRepositoryImpl(
            coreSharedPrefs = coreSharedPrefs,
            eventsDao = eventsDao,
            eventStatusDao = eventStatusDao
        )
    }

}