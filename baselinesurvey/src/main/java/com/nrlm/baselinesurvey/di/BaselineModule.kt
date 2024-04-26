package com.nrlm.baselinesurvey.di

import com.nrlm.baselinesurvey.activity.domain.repository.MainActivityRepository
import com.nrlm.baselinesurvey.activity.domain.repository.MainActivityRepositoryImpl
import com.nrlm.baselinesurvey.activity.domain.use_case.IsLoggedInUseCase
import com.nrlm.baselinesurvey.activity.domain.use_case.MainActivityUseCase
import com.nrlm.baselinesurvey.data.domain.EventWriterHelper
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.ContentDao
import com.nrlm.baselinesurvey.database.dao.DidiInfoDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.FormQuestionResponseDao
import com.nrlm.baselinesurvey.database.dao.InputTypeQuestionAnswerDao
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionAnswerEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.dao.VillageListDao
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.ui.auth.repository.LoginScreenRepository
import com.nrlm.baselinesurvey.ui.auth.repository.LoginScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.auth.repository.OtpVerificationRepository
import com.nrlm.baselinesurvey.ui.auth.repository.OtpVerificationRepositoryImpl
import com.nrlm.baselinesurvey.ui.auth.use_case.GenerateOtpUseCase
import com.nrlm.baselinesurvey.ui.auth.use_case.GetMobileNumberUseCase
import com.nrlm.baselinesurvey.ui.auth.use_case.LoginScreenUseCase
import com.nrlm.baselinesurvey.ui.auth.use_case.OtpVerificationUseCase
import com.nrlm.baselinesurvey.ui.auth.use_case.ResendOtpUseCase
import com.nrlm.baselinesurvey.ui.auth.use_case.SaveAccessTokenUseCase
import com.nrlm.baselinesurvey.ui.auth.use_case.SaveMobileNumberUseCase
import com.nrlm.baselinesurvey.ui.auth.use_case.ValidateOtpUseCase
import com.nrlm.baselinesurvey.ui.backup.domain.repository.ExportImportRepository
import com.nrlm.baselinesurvey.ui.backup.domain.repository.ExportImportRepositoryImpl
import com.nrlm.baselinesurvey.ui.backup.domain.use_case.ClearLocalDBExportUseCase
import com.nrlm.baselinesurvey.ui.backup.domain.use_case.ExportImportUseCase
import com.nrlm.baselinesurvey.ui.backup.domain.use_case.GetExportOptionListUseCase
import com.nrlm.baselinesurvey.ui.backup.domain.use_case.GetUserDetailsExportUseCase
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.CasteListRepository
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.CasteListRepositoryImpl
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.EventsWriterRepository
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.EventsWriterRepositoryImpl
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.SurveyStateRepository
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.SurveyStateRepositoryImpl
import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase
import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.GetCasteListUseCase
import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.UpdateSurveyStateUserCase
import com.nrlm.baselinesurvey.ui.form_response_summary_screen.domain.use_case.FormResponseSummaryScreenUseCase
import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepository
import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.language.domain.use_case.GetLanguageListFromDbUseCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.GetLanguageScreenOpenFromUserCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.GetSelectedVillageUseCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.GetVillageDetailUseCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.LanguageScreenUseCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.SaveSelectedLanguageUseCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.SaveSelectedVillageUseCase
import com.nrlm.baselinesurvey.ui.mission_screen.domain.repository.MissionScreenRepository
import com.nrlm.baselinesurvey.ui.mission_screen.domain.repository.MissionScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case.FetchMissionDataFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case.GetMissionListFromDbUseCase
import com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case.GetTaskDetailsFromDbUseCase
import com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case.MissionScreenUseCase
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository.MissionSummaryScreenRepository
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository.MissionSummaryScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.GetMissionActivitiesFromDBUseCase
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.GetPendingTaskCountLiveUseCase
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.MissionSummaryScreenUseCase
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.UpdateMisisonState
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.UpdateMissionStatusUseCase
import com.nrlm.baselinesurvey.ui.profile.domain.repository.ProfileBSRepository
import com.nrlm.baselinesurvey.ui.profile.domain.repository.ProfileBSRepositoryImpl
import com.nrlm.baselinesurvey.ui.profile.domain.use_case.GetIdentityNumberUseCase
import com.nrlm.baselinesurvey.ui.profile.domain.use_case.GetProfileMobileNumberUseCase
import com.nrlm.baselinesurvey.ui.profile.domain.use_case.GetUserEmailUseCase
import com.nrlm.baselinesurvey.ui.profile.domain.use_case.GetUserNameUseCase
import com.nrlm.baselinesurvey.ui.profile.domain.use_case.ProfileBSUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepository
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.DeleteFormQuestionOptionResponseUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.DeleteFormQuestionResponseUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.GetSectionAnswersUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.GetSectionUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.GetSectionsListUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.QuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.SaveSectionAnswerUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.UpdateSectionProgressUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository.FormQuestionResponseRepository
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository.FormQuestionResponseRepositoryImpl
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.FormQuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.GetFormQuestionResponseUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.SaveFormQuestionResponseUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.UpdateFormQuestionResponseUseCase
import com.nrlm.baselinesurvey.ui.search.use_case.GetSectionListForSurveyUseCase
import com.nrlm.baselinesurvey.ui.search.use_case.SearchScreenUseCase
import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepository
import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.GetSectionListUseCase
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.GetSectionProgressForDidiUseCase
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.GetSurvyeDetails
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.SectionListScreenUseCase
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.UpdateSubjectStatusUseCase
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.UpdateTaskStatusUseCase
import com.nrlm.baselinesurvey.ui.setting.domain.repository.SettingBSRepository
import com.nrlm.baselinesurvey.ui.setting.domain.repository.SettingBSRepositoryImpl
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.ClearLocalDBUseCase
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.GetUserDetailsUseCase
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.LogoutUseCase
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.SaveLanguageScreenOpenFromUseCase
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.SettingBSUserCase
import com.nrlm.baselinesurvey.ui.splash.domain.repository.SplashScreenRepository
import com.nrlm.baselinesurvey.ui.splash.domain.repository.SplashScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.splash.domain.use_case.FetchLanguageFromNetworkConfigUseCase
import com.nrlm.baselinesurvey.ui.splash.domain.use_case.LoggedInUseCase
import com.nrlm.baselinesurvey.ui.splash.domain.use_case.SaveLanguageConfigUseCase
import com.nrlm.baselinesurvey.ui.splash.domain.use_case.SaveLanguageOpenFromUseCase
import com.nrlm.baselinesurvey.ui.splash.domain.use_case.SaveQuestionImageUseCase
import com.nrlm.baselinesurvey.ui.splash.domain.use_case.SplashScreenUseCase
import com.nrlm.baselinesurvey.ui.start_screen.domain.repository.StartScreenRepository
import com.nrlm.baselinesurvey.ui.start_screen.domain.repository.StartScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.start_screen.domain.use_case.GetSurveyeeDetailsUserCase
import com.nrlm.baselinesurvey.ui.start_screen.domain.use_case.SaveSurveyeeImagePathUseCase
import com.nrlm.baselinesurvey.ui.start_screen.domain.use_case.StartSurveyScreenUserCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.SurveyeeListScreenRepository
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.SurveyeeListScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchCastesFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchContentDataFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchDataUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchSectionStatusFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchSurveyAnswerFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchSurveyFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchSurveyeeListFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchUserDetailFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.GetActivityStateFromDBUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.GetSurveyeeListUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.MoveSurveyeeToThisWeekUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.SurveyeeScreenUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.UpdateActivityStatusUseCase
import com.nudge.core.database.dao.ApiStatusDao
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BaselineModule {
    @Provides
    @Singleton
    fun provideSplashScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService,
        languageListDao: LanguageListDao,
        baselineDatabase: NudgeBaselineDatabase
    ): SplashScreenRepository {
        return SplashScreenRepositoryImpl(prefRepo, apiService, languageListDao, baselineDatabase)
    }

    @Provides
    @Singleton
    fun provideSplashUseCases(repository: SplashScreenRepository): SplashScreenUseCase {
        return SplashScreenUseCase(
            fetchLanguageConfigFromNetworkUseCase = FetchLanguageFromNetworkConfigUseCase(repository),
            saveLanguageConfigUseCase = SaveLanguageConfigUseCase(repository),
            saveQuestionImageUseCase = SaveQuestionImageUseCase(repository),
            loggedInUseCase = LoggedInUseCase(repository),
            saveLanguageOpenFromUseCase = SaveLanguageOpenFromUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideLanguageScreenUseCase(languageScreenRepository: LanguageScreenRepository): LanguageScreenUseCase {
        return LanguageScreenUseCase(
            getLanguageListFromDbUseCase = GetLanguageListFromDbUseCase(languageScreenRepository),
            getSelectedVillageUseCase = GetSelectedVillageUseCase(languageScreenRepository),
            saveSelectedVillageUseCase = SaveSelectedVillageUseCase(languageScreenRepository),
            getVillageDetailUseCase = GetVillageDetailUseCase(languageScreenRepository),
            saveSelectedLanguageUseCase = SaveSelectedLanguageUseCase(languageScreenRepository),
            getLanguageScreenOpenFromUserCase = GetLanguageScreenOpenFromUserCase(
                languageScreenRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideMissionScreenUseCase(
        missionScreenRepository: MissionScreenRepository,
        dataLoadingScreenRepository: DataLoadingScreenRepository
    ): MissionScreenUseCase {
        return MissionScreenUseCase(
            fetchMissionDataFromNetworkUseCase = FetchMissionDataFromNetworkUseCase(
                dataLoadingScreenRepository
            ),
            getMissionListFromDbUseCase = GetMissionListFromDbUseCase(missionScreenRepository),
            getTaskDetailsFromDbUseCase = GetTaskDetailsFromDbUseCase(missionScreenRepository)
        )
    }

    @Provides
    @Singleton
    fun provideMissionSummaryScreenUseCase(
        missionSummaryScreenRepository: MissionSummaryScreenRepository,
        eventsWriterRepository: EventsWriterRepository
    ): MissionSummaryScreenUseCase {
        return MissionSummaryScreenUseCase(
            getMissionActivitiesFromDBUseCase = GetMissionActivitiesFromDBUseCase(
                missionSummaryScreenRepository,
            ),
            updateMisisonState = UpdateMisisonState(missionSummaryScreenRepository),
            updateMissionStatusUseCase = UpdateMissionStatusUseCase(missionSummaryScreenRepository),
            getPendingTaskCountLiveUseCase = GetPendingTaskCountLiveUseCase(
                missionSummaryScreenRepository
            ),
            eventsWriterUserCase = EventsWriterUserCase(eventsWriterRepository)
        )
    }

    @Provides
    @Singleton
    fun provideLanguageScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService,
        languageListDao: LanguageListDao,
        villageListDao: VillageListDao
    ): LanguageScreenRepository {
        return LanguageScreenRepositoryImpl(prefRepo, languageListDao, villageListDao)
    }

    @Provides
    @Singleton
    fun provideLoginScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService
    ): LoginScreenRepository {
        return LoginScreenRepositoryImpl(prefRepo, apiService)
    }

    @Provides
    @Singleton
    fun provideLoginScreenUseCase(loginScreenRepository: LoginScreenRepository): LoginScreenUseCase {
        return LoginScreenUseCase(
            generateOtpUseCase = GenerateOtpUseCase(loginScreenRepository),
            saveMobileNumberUseCase = SaveMobileNumberUseCase(loginScreenRepository)
        )
    }

    @Provides
    @Singleton
    fun provideOtpVerificationRepository(
        prefRepo: PrefRepo,
        apiService: ApiService
    ): OtpVerificationRepository {
        return OtpVerificationRepositoryImpl(prefRepo, apiService)
    }

    @Provides
    @Singleton
    fun provideOtpVerificationUseCase(otpVerificationRepository: OtpVerificationRepository): OtpVerificationUseCase {
        return OtpVerificationUseCase(
            validateOtpUseCase = ValidateOtpUseCase(otpVerificationRepository),
            resendOtpUseCase = ResendOtpUseCase(otpVerificationRepository),
            saveAccessTokenUseCase = SaveAccessTokenUseCase(otpVerificationRepository),
            getMobileNumberUseCase = GetMobileNumberUseCase(otpVerificationRepository)
        )
    }

    @Provides
    @Singleton
    fun provideSurveyeeListScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService,
        surveyeeEntityDao: SurveyeeEntityDao,
        languageListDao: LanguageListDao,
        activityTaskDao: ActivityTaskDao,
        missionActivityDao: MissionActivityDao
    ): SurveyeeListScreenRepository {
        return SurveyeeListScreenRepositoryImpl(
            prefRepo,
            apiService,
            surveyeeEntityDao,
            languageListDao,
            activityTaskDao,
            missionActivityDao
        )
    }

    @Provides
    @Singleton
    fun provideSurveyeeScreenUseCase(
        surveyeeListScreenRepository: SurveyeeListScreenRepository,
        sectionListScreenRepository: SectionListScreenRepository,
        missionSummaryScreenRepository: MissionSummaryScreenRepository,
        eventsWriterRepository: EventsWriterRepository
    ): SurveyeeScreenUseCase {
        return SurveyeeScreenUseCase(
            getSurveyeeListUseCase = GetSurveyeeListUseCase(surveyeeListScreenRepository),
            moveSurveyeeToThisWeek = MoveSurveyeeToThisWeekUseCase(surveyeeListScreenRepository),
            getActivityStateFromDBUseCase = GetActivityStateFromDBUseCase(
                surveyeeListScreenRepository
            ),
            updateActivityStatusUseCase = UpdateActivityStatusUseCase(surveyeeListScreenRepository),
            eventsWriterUseCase = EventsWriterUserCase(eventsWriterRepository),
            updateSubjectStatusUseCase = UpdateSubjectStatusUseCase(sectionListScreenRepository),
            updateTaskStatusUseCase = UpdateTaskStatusUseCase(sectionListScreenRepository),
            getPendingTaskCountLiveUseCase = GetPendingTaskCountLiveUseCase(
                missionSummaryScreenRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideSectionListScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService,
        surveyEntityDao: SurveyEntityDao,
        sectionEntityDao: SectionEntityDao,
        questionEntityDao: QuestionEntityDao,
        didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
        optionItemDao: OptionItemDao,
        surveyeeEntityDao: SurveyeeEntityDao,
        contentDao: ContentDao,
        taskDao: ActivityTaskDao
    ): SectionListScreenRepository {
        return SectionListScreenRepositoryImpl(
            prefRepo,
            apiService,
            surveyEntityDao,
            sectionEntityDao,
            questionEntityDao,
            didiSectionProgressEntityDao,
            optionItemDao,
            surveyeeEntityDao,
            contentDao,
            taskDao
        )
    }

    @Provides
    @Singleton
    fun providesSectionListScreenUseCase(
        sectionListScreenRepository: SectionListScreenRepository,
        eventsWriterRepository: EventsWriterRepository
    ): SectionListScreenUseCase {
        return SectionListScreenUseCase(
            getSectionListUseCase = GetSectionListUseCase(sectionListScreenRepository),
            getSectionProgressForDidiUseCase = GetSectionProgressForDidiUseCase(
                sectionListScreenRepository
            ),
            getSurvyeDetails = GetSurvyeDetails(sectionListScreenRepository),
            updateSubjectStatusUseCase = UpdateSubjectStatusUseCase(sectionListScreenRepository),
            updateTaskStatusUseCase = UpdateTaskStatusUseCase(sectionListScreenRepository),
            eventsWriterUseCase = EventsWriterUserCase(eventsWriterRepository)
        )
    }

    @Provides
    @Singleton
    fun provideQuestionScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService,
        surveyeeEntityDao: SurveyeeEntityDao,
        surveyEntityDao: SurveyEntityDao,
        sectionEntityDao: SectionEntityDao,
        questionEntityDao: QuestionEntityDao,
        didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
        sectionAnswerEntityDao: SectionAnswerEntityDao,
        optionItemDao: OptionItemDao,
        formQuestionResponseDao: FormQuestionResponseDao,
        inputTypeQuestionAnswerDao: InputTypeQuestionAnswerDao,
        contentDao: ContentDao
    ): QuestionScreenRepository {
        return QuestionScreenRepositoryImpl(
            prefRepo = prefRepo,
            apiService = apiService,
            surveyeeEntityDao = surveyeeEntityDao,
            surveyEntityDao = surveyEntityDao,
            sectionEntityDao = sectionEntityDao,
            questionEntityDao = questionEntityDao,
            didiSectionProgressEntityDao = didiSectionProgressEntityDao,
            sectionAnswerEntityDao = sectionAnswerEntityDao,
            optionItemDao = optionItemDao,
            formQuestionResponseDao = formQuestionResponseDao,
            inputTypeQuestionAnswerDao = inputTypeQuestionAnswerDao,
            contentDao
        )
    }

    @Provides
    @Singleton
    fun providesQuestionScreenUseCase(
        questionScreenRepository: QuestionScreenRepository,
        formQuestionResponseRepository: FormQuestionResponseRepository,
        startScreenRepository: StartScreenRepository,
        missionSummaryScreenRepository: MissionSummaryScreenRepository,
        eventsWriterRepository: EventsWriterRepository
    ): QuestionScreenUseCase {
        return QuestionScreenUseCase(
            getSectionUseCase = GetSectionUseCase(questionScreenRepository),
            getSectionsListUseCase = GetSectionsListUseCase(questionScreenRepository),
            updateSectionProgressUseCase = UpdateSectionProgressUseCase(questionScreenRepository),
            saveSectionAnswerUseCase = SaveSectionAnswerUseCase(questionScreenRepository),
            getSectionAnswersUseCase = GetSectionAnswersUseCase(questionScreenRepository),
            getFormQuestionResponseUseCase = GetFormQuestionResponseUseCase(
                formQuestionResponseRepository
            ),
            saveFormQuestionResponseUseCase = SaveFormQuestionResponseUseCase(
                formQuestionResponseRepository
            ),
            updateFormQuestionResponseUseCase = UpdateFormQuestionResponseUseCase(
                formQuestionResponseRepository
            ),
            deleteFormQuestionResponseUseCase = DeleteFormQuestionResponseUseCase(
                formQuestionResponseRepository
            ),
            getSurveyeeDetailsUserCase = GetSurveyeeDetailsUserCase(startScreenRepository),
            getPendingTaskCountLiveUseCase = GetPendingTaskCountLiveUseCase(
                missionSummaryScreenRepository
            ),
            eventsWriterUseCase = EventsWriterUserCase(eventsWriterRepository)
        )
    }

    @Provides
    @Singleton
    fun provideMainActivityRepository(
        prefRepo: PrefRepo,
        apiService: ApiService
    ): MainActivityRepository {
        return MainActivityRepositoryImpl(prefRepo, apiService)
    }

    @Provides
    @Singleton
    fun provideMainActivityUseCase(
        repository: MainActivityRepository
    ): MainActivityUseCase {
        return MainActivityUseCase(
            isLoggedInUseCase = IsLoggedInUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideDataLoadingScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService,
        languageListDao: LanguageListDao,
        surveyeeEntityDao: SurveyeeEntityDao,
        surveyEntityDao: SurveyEntityDao,
        sectionEntityDao: SectionEntityDao,
        questionEntityDao: QuestionEntityDao,
        optionItemDao: OptionItemDao,
        missionEntityDao: MissionEntityDao,
        missionActivityDao: MissionActivityDao,
        activityTaskDao: ActivityTaskDao,
        contentDao: ContentDao,
        baselineDatabase: NudgeBaselineDatabase,
        didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
        apiStatusDao: ApiStatusDao

    ): DataLoadingScreenRepository {
        return DataLoadingScreenRepositoryImpl(
            prefRepo,
            apiService,
            languageListDao,
            surveyeeEntityDao,
            surveyEntityDao,
            sectionEntityDao,
            questionEntityDao,
            optionItemDao,
            missionEntityDao,
            missionActivityDao,
            activityTaskDao,
            contentDao,
            baselineDatabase,
            didiSectionProgressEntityDao,
            apiStatusDao
        )
    }

    @Provides
    @Singleton
    fun provideFetchDataUseCaseUseCase(
        repository: DataLoadingScreenRepository,
        splashScreenRepository: SplashScreenRepository
    ): FetchDataUseCase {
        return FetchDataUseCase(
            fetchSurveyeeListFromNetworkUseCase = FetchSurveyeeListFromNetworkUseCase(repository),
            fetchUserDetailFromNetworkUseCase = FetchUserDetailFromNetworkUseCase(repository),
            fetchSurveyFromNetworkUseCase = FetchSurveyFromNetworkUseCase(repository),
            fetchMissionDataFromNetworkUseCase = FetchMissionDataFromNetworkUseCase(repository),
            fetchCastesFromNetworkUseCase = FetchCastesFromNetworkUseCase(repository),
            fetchContentnDataFromNetworkUseCase = FetchContentDataFromNetworkUseCase(repository),
            fetchSectionStatusFromNetworkUseCase = FetchSectionStatusFromNetworkUseCase(repository),
            fetchSurveyAnswerFromNetworkUseCase = FetchSurveyAnswerFromNetworkUseCase(repository),
            loggedInUseCase = LoggedInUseCase(splashScreenRepository)
        )
    }

    @Provides
    @Singleton
    fun provideStartSurveyScreenRepository(
        prefRepo: PrefRepo,
        surveyeeEntityDao: SurveyeeEntityDao,
        didiInfoDao: DidiInfoDao
    ): StartScreenRepository {
        return StartScreenRepositoryImpl(prefRepo, surveyeeEntityDao, didiInfoDao)
    }

    @Provides
    @Singleton
    fun provideStartSurveyScreenUseCase(
        repository: StartScreenRepository,
        surveyStateRepository: SurveyStateRepository,
        casteListRepository: CasteListRepository,
        questionScreenRepository: QuestionScreenRepository,
        eventsWriterRepository: EventsWriterRepository
    ): StartSurveyScreenUserCase {
        return StartSurveyScreenUserCase(
            getSurveyeeDetailsUserCase = GetSurveyeeDetailsUserCase(repository),
            saveSurveyeeImagePathUseCase = SaveSurveyeeImagePathUseCase(repository),
            updateSurveyStateUseCase = UpdateSurveyStateUserCase(surveyStateRepository),
            getCasteListUseCase = GetCasteListUseCase(casteListRepository),
            getSectionUseCase = GetSectionUseCase(questionScreenRepository),
            eventsWriterUseCase = EventsWriterUserCase(eventsWriterRepository)
        )
    }

    @Provides
    @Singleton
    fun provideSurveyStateRepository(
        prefRepo: PrefRepo,
        surveyeeEntityDao: SurveyeeEntityDao,
        didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
        didiInfoDao: DidiInfoDao
    ): SurveyStateRepository {
        return SurveyStateRepositoryImpl(
            prefRepo,
            surveyeeEntityDao,
            didiSectionProgressEntityDao,
            didiInfoDao
        )
    }

    @Provides
    @Singleton
    fun provideFormQuestionResponseRepository(
        questionEntityDao: QuestionEntityDao,
        optionItemDao: OptionItemDao,
        formQuestionResponseDao: FormQuestionResponseDao,
        prefRepo: PrefRepo,
        contentDao: ContentDao
    ): FormQuestionResponseRepository {
        return FormQuestionResponseRepositoryImpl(
            questionEntityDao = questionEntityDao,
            optionItemDao = optionItemDao,
            formQuestionResponseDao = formQuestionResponseDao,
            prefRepo = prefRepo,
            contentDao = contentDao
        )
    }

    @Provides
    @Singleton
    fun providesQuestionTypeScreenUseCase(
        formQuestionResponse: FormQuestionResponseRepository,
        eventsWriterRepository: EventsWriterRepository
    ): FormQuestionScreenUseCase {
        return FormQuestionScreenUseCase(
            getFormQuestionResponseUseCase = GetFormQuestionResponseUseCase(
                formQuestionResponse
            ),
            saveFormQuestionResponseUseCase = SaveFormQuestionResponseUseCase(formQuestionResponse),
            updateFormQuestionResponseUseCase = UpdateFormQuestionResponseUseCase(
                formQuestionResponse
            ),
            deleteFormQuestionOptionResponseUseCase = DeleteFormQuestionOptionResponseUseCase(
                formQuestionResponse
            ),
            deleteFormQuestionResponseUseCase = DeleteFormQuestionResponseUseCase(
                formQuestionResponse
            ),
            eventsWriterUserCase = EventsWriterUserCase(eventsWriterRepository)
        )
    }

    @Provides
    @Singleton
    fun provideMissionRepository(
        missionEntityDao: MissionEntityDao,
        missionActivityDao: MissionActivityDao,
        taskDao: ActivityTaskDao,
        prefRepo: PrefRepo
    ): MissionScreenRepository {
        return MissionScreenRepositoryImpl(missionEntityDao, missionActivityDao, taskDao, prefRepo)
    }

    @Provides
    @Singleton
    fun provideMissionSummaryRepository(
        missionActivityDao: MissionActivityDao,
        taskDao: ActivityTaskDao,
        surveyeeEntityDao: SurveyeeEntityDao,
        missionEntityDao: MissionEntityDao,
        prefRepo: PrefRepo
    ): MissionSummaryScreenRepository {
        return MissionSummaryScreenRepositoryImpl(
            missionActivityDao,
            taskDao,
            surveyeeEntityDao,
            missionEntityDao,
            prefRepo
        )
    }

    @Provides
    @Singleton
    fun provideSettingBSScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService,
        nudgeBaselineDatabase: NudgeBaselineDatabase
    ): SettingBSRepository {
        return SettingBSRepositoryImpl(prefRepo, apiService,nudgeBaselineDatabase)
    }

    @Provides
    @Singleton
    fun providesSettingScreenUseCase(
        repository: SettingBSRepository
    ): SettingBSUserCase {
        return SettingBSUserCase(
            getUserDetailsUseCase = GetUserDetailsUseCase(repository),
            logoutUseCase = LogoutUseCase(repository),
            saveLanguageScreenOpenFromUseCase = SaveLanguageScreenOpenFromUseCase(repository),
            clearLocalDBUseCase = ClearLocalDBUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideProfileBSRepository(
        prefRepo: PrefRepo
    ): ProfileBSRepository {
        return ProfileBSRepositoryImpl(prefRepo)
    }

    @Provides
    @Singleton
    fun provideProfileBSUseCase(
        repository: ProfileBSRepository
    ): ProfileBSUseCase {
        return ProfileBSUseCase(
            getIdentityNumberUseCase = GetIdentityNumberUseCase(repository),
            getUserEmailUseCase = GetUserEmailUseCase(repository),
            getUserMobileNumberUseCase = GetProfileMobileNumberUseCase(repository),
            getUserNameUseCase = GetUserNameUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun providesSearchScreenUseCase(
        sectionListScreenRepository: SectionListScreenRepository
    ): SearchScreenUseCase {
        return SearchScreenUseCase(
            getSectionListForSurveyUseCase = GetSectionListForSurveyUseCase(
                sectionListScreenRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideCasteListRepository(
        prefRepo: PrefRepo
    ): CasteListRepository {
        return CasteListRepositoryImpl(prefRepo)
    }

    @Provides
    @Singleton
    fun provideEventsWriterUseCase(
        eventsWriterRepository: EventsWriterRepository
    ): EventsWriterUserCase {
        return EventsWriterUserCase(eventsWriterRepository)
    }

    @Provides
    @Singleton
    fun provideEventsWriterRepository(
        prefRepo: PrefRepo,
        surveyEntityDao: SurveyEntityDao,
        missionEntityDao: MissionEntityDao,
        didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
        eventsDao: EventsDao,
        eventDependencyDao: EventDependencyDao,
        nudgeBaselineDatabase: NudgeBaselineDatabase,
        eventWriterHelper: EventWriterHelperImpl
    ): EventsWriterRepository {
        return EventsWriterRepositoryImpl(
            prefRepo = prefRepo,
            surveyEntityDao = surveyEntityDao,
            didiSectionProgressEntityDao = didiSectionProgressEntityDao,
            eventsDao = eventsDao,
            eventDependencyDao = eventDependencyDao,
            missionEntityDao = missionEntityDao,
        )
    }

    @Provides
    @Singleton
    fun providesEventWriterHelper(
        prefRepo: PrefRepo,
        repositoryImpl: EventsWriterRepositoryImpl,
        eventsDao: EventsDao,
        eventDependencyDao: EventDependencyDao,
        surveyEntityDao: SurveyEntityDao,
        surveyeeEntityDao: SurveyeeEntityDao,
        questionEntityDao: QuestionEntityDao,
        optionItemDao: OptionItemDao,
        taskDao: ActivityTaskDao,
        activityDao: MissionActivityDao,
        missionEntityDao: MissionEntityDao,
        didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
        baselineDatabase: NudgeBaselineDatabase
    ): EventWriterHelper {
        return EventWriterHelperImpl(
            prefRepo = prefRepo,
            repositoryImpl = repositoryImpl,
            eventsDao = eventsDao,
            eventDependencyDao = eventDependencyDao,
            surveyEntityDao = surveyEntityDao,
            surveyeeEntityDao = surveyeeEntityDao,
            questionEntityDao = questionEntityDao,
            optionItemDao = optionItemDao,
            taskDao = taskDao,
            activityDao = activityDao,
            missionEntityDao = missionEntityDao,
            didiSectionProgressEntityDao = didiSectionProgressEntityDao,
            baselineDatabase = baselineDatabase
        )
    }

    @Singleton
    @Provides
    fun provideFormResponseSummaryScreenUseCase(
        repository: FormQuestionResponseRepository,
        eventsWriterRepository: EventsWriterRepository
    ): FormResponseSummaryScreenUseCase {
        return FormResponseSummaryScreenUseCase(
            getFormQuestionResponseUseCase = GetFormQuestionResponseUseCase(repository),
            deleteFormQuestionResponseUseCase = DeleteFormQuestionResponseUseCase(repository),
            eventsWriterUseCase = EventsWriterUserCase(eventsWriterRepository)
        )
    }

   @Singleton
   @Provides
   fun provideExportImportRepository(
       prefRepo: PrefRepo,
       nudgeBaselineDatabase: NudgeBaselineDatabase
   ): ExportImportRepository {
       return ExportImportRepositoryImpl(prefRepo, nudgeBaselineDatabase)
   }

    @Singleton
    @Provides
    fun provideExportImportUseCase(repository: ExportImportRepository):ExportImportUseCase{
        return ExportImportUseCase(
            getExportOptionListUseCase = GetExportOptionListUseCase(repository),
            clearLocalDBExportUseCase = ClearLocalDBExportUseCase(repository),
            getUserDetailsExportUseCase = GetUserDetailsExportUseCase(repository)
        )
    }
}