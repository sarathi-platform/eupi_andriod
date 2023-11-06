package com.nrlm.baselinesurvey.di

import com.nrlm.baselinesurvey.activity.domain.repository.MainActivityRepository
import com.nrlm.baselinesurvey.activity.domain.repository.MainActivityRepositoryImpl
import com.nrlm.baselinesurvey.activity.domain.use_case.IsLoggedInUseCase
import com.nrlm.baselinesurvey.activity.domain.use_case.MainActivityUseCase
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.dao.VillageListDao
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.splash.domain.repository.SplashScreenRepository
import com.nrlm.baselinesurvey.splash.domain.repository.SplashScreenRepositoryImpl
import com.nrlm.baselinesurvey.splash.domain.use_case.FetchLanguageFromNetworkConfigUseCase
import com.nrlm.baselinesurvey.splash.domain.use_case.LoggedInUseCase
import com.nrlm.baselinesurvey.splash.domain.use_case.SaveLanguageConfigUseCase
import com.nrlm.baselinesurvey.splash.domain.use_case.SaveQuestionImageUseCase
import com.nrlm.baselinesurvey.splash.domain.use_case.SplashScreenUseCase
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
import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepository
import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.language.domain.use_case.GetLanguageListFromDbUseCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.GetSelectedVillageUseCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.GetVillageDetailUseCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.LanguageScreenUseCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.SaveSelectedLanguageUseCase
import com.nrlm.baselinesurvey.ui.language.domain.use_case.SaveSelectedVillageUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepository
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.GetSectionUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.QuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepository
import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.GetSectionListUseCase
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.SectionListScreenUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.SurveyeeListScreenRepository
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.SurveyeeListScreenRepositoryImpl
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchDataUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchSurveyFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchSurveyeeListFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchUserDetailFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.GetSurveyeeListUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.SurveyeeScreenUseCase
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
        languageListDao: LanguageListDao
    ): SplashScreenRepository {
        return SplashScreenRepositoryImpl(prefRepo, apiService, languageListDao)
    }

    @Provides
    @Singleton
    fun provideSplashUseCases(repository: SplashScreenRepository): SplashScreenUseCase {
        return SplashScreenUseCase(
            fetchLanguageConfigFromNetworkUseCase = FetchLanguageFromNetworkConfigUseCase(repository),
            saveLanguageConfigUseCase = SaveLanguageConfigUseCase(repository),
            saveQuestionImageUseCase = SaveQuestionImageUseCase(repository),
            loggedInUseCase = LoggedInUseCase(repository)
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
            saveSelectedLanguageUseCase = SaveSelectedLanguageUseCase(languageScreenRepository)
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
        languageListDao: LanguageListDao
    ): SurveyeeListScreenRepository {
        return SurveyeeListScreenRepositoryImpl(prefRepo, apiService, surveyeeEntityDao, languageListDao)
    }

    @Provides
    @Singleton
    fun provideSurveyeeScreenUseCase(surveyeeListScreenRepository: SurveyeeListScreenRepository): SurveyeeScreenUseCase {
        return SurveyeeScreenUseCase(
           getSurveyeeListUseCase = GetSurveyeeListUseCase(surveyeeListScreenRepository)
        )
    }

    @Provides
    @Singleton
    fun provideSectionListScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService,
        surveyEntityDao: SurveyEntityDao,
        sectionEntityDao: SectionEntityDao,
        questionEntityDao: QuestionEntityDao
    ): SectionListScreenRepository {
        return SectionListScreenRepositoryImpl(prefRepo, apiService, surveyEntityDao, sectionEntityDao, questionEntityDao)
    }

    @Provides
    @Singleton
    fun providesSectionListScreenUseCase(
        sectionListScreenRepository: SectionListScreenRepository
    ): SectionListScreenUseCase {
        return SectionListScreenUseCase(
            GetSectionListUseCase(sectionListScreenRepository)
        )
    }

    @Provides
    @Singleton
    fun provideQuestionScreenRepository(
        prefRepo: PrefRepo,
        apiService: ApiService
    ): QuestionScreenRepository {
        return QuestionScreenRepositoryImpl(prefRepo, apiService)
    }

    @Provides
    @Singleton
    fun providesQuestionScreenUseCase(
        questionScreenRepository: QuestionScreenRepository
    ): QuestionScreenUseCase {
        return QuestionScreenUseCase(
            getSectionUseCase = GetSectionUseCase(questionScreenRepository)
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
        questionEntityDao: QuestionEntityDao
    ): DataLoadingScreenRepository {
        return DataLoadingScreenRepositoryImpl(prefRepo, apiService, languageListDao, surveyeeEntityDao, surveyEntityDao, sectionEntityDao, questionEntityDao)
    }

    @Provides
    @Singleton
    fun provideFetchDataUseCaseUseCase(
        repository: DataLoadingScreenRepository
    ): FetchDataUseCase {
        return FetchDataUseCase(
            fetchSurveyeeListFromNetworkUseCase = FetchSurveyeeListFromNetworkUseCase(repository),
            fetchUserDetailFromNetworkUseCase = FetchUserDetailFromNetworkUseCase(repository),
            fetchSurveyFromNetworkUseCase = FetchSurveyFromNetworkUseCase(repository)
        )
    }

}