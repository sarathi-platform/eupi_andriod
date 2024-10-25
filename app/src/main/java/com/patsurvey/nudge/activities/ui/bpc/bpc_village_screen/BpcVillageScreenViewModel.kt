package com.patsurvey.nudge.activities.ui.bpc.bpc_village_screen

import android.content.Context
import android.text.TextUtils
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.nudge.core.usecase.language.LanguageConfigUseCase
import com.nudge.syncmanager.database.SyncManagerDatabase
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.progress.VillageSelectionRepository
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LastSelectedTolaDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.database.dao.UserDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.showCustomToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BpcVillageScreenViewModel @Inject constructor(
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val tolaDao: TolaDao,
    val didiDao: DidiDao,
    val casteListDao: CasteListDao,
    val questionListDao: QuestionListDao,
    val trainingVideoDao: TrainingVideoDao,
    val numericAnswerDao: NumericAnswerDao,
    val answerDao: AnswerDao,
    val bpcSummaryDao: BpcSummaryDao,
    val poorDidiListDao: PoorDidiListDao,
    val userDao: UserDao,
    val lastSelectedTolaDao: LastSelectedTolaDao,
    val villageSelectionRepository: VillageSelectionRepository,
    private val syncManagerDatabase: SyncManagerDatabase,
    private val fetchAppConfigFromNetworkUseCase: FetchAppConfigFromNetworkUseCase,
    private val languageConfigUseCase: LanguageConfigUseCase,
    val prefRepo:PrefRepo
    ): BaseViewModel() {

    val showLoader = mutableStateOf(false)
    val villageSelected = mutableStateOf(0)

    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    var _filterVillageList = MutableStateFlow(listOf<VillageEntity>())
    val filterVillageList: StateFlow<List<VillageEntity>> get() = _filterVillageList
    val showUserChangedDialog = mutableStateOf(false)
    fun getStateId():Int{
        return prefRepo.getStateId()
    }
    fun init () {
        showLoader.value = true
        fetchUserAndVillageDetails()
    }

    fun compareWithPreviousUser(context: Context) {
        if (TextUtils.isEmpty(villageSelectionRepository.prefRepo.getPreviousUserMobile()) || villageSelectionRepository.prefRepo.getPreviousUserMobile()
                .equals(villageSelectionRepository.prefRepo.getMobileNumber())
        ) {
            init()
        } else {
            showUserChangedDialog.value = true
        }
    }
    fun isUserBpc() = villageSelectionRepository.isUserBPC()

    private fun fetchUserAndVillageDetails() {
        villageSelectionRepository.fetchUserAndVillageDetails(forceRefresh = false) {
            villageSelectionRepository.fetchCastList(isRefresh = false)
            villageSelectionRepository.fetchPatQuestionsFromNetwork(prefRepo.getPageOpenFromOTPScreen())
            fetchAppConfig()
            _villagList.value = it.villageList
            _filterVillageList.value = villageList.value
            showLoader.value = false
        }
    }

    fun fetchAppConfig() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            fetchAppConfigFromNetworkUseCase.invoke()
            languageConfigUseCase.invoke()
        }
    }

    fun updateSelectedVillage(villageList: List<VillageEntity>) {
        NudgeLogger.d(
            "VillageAndVoBoxForBottomSheet",
            "villageList.value[villageSelected.value] = ${villageList[villageSelected.value]}"
        )
        villageSelectionRepository.saveSelectedVillage(villageList[villageSelected.value])
    }


    fun saveVillageListAfterTokenRefresh(mVillageList: List<VillageEntity>) {
        _villagList.value = mVillageList
        _filterVillageList.value=villageList.value
    }

    fun performQuery(query: String) {
        _filterVillageList.value = if (query.isNotEmpty()) {
            val filteredList = ArrayList<VillageEntity>()
            villageList.value.forEach { village ->
                if (village.name.lowercase().contains(query.lowercase())) {
                    filteredList.add(village)
                }
            }
            filteredList
        } else {
            villageList.value
        }
    }

    fun downloadImageItem(context: Context, image: String) {
        villageSelectionRepository.downloadImageItem(context, image)
    }

    fun saveVideosToDb(context: Context) {
        villageSelectionRepository.saveVideosToDb(context)
    }

    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    fun saveSettingOpenFrom(fromPage: Int) {
        villageSelectionRepository.saveSettingOpenFrom(fromPage)
    }

    fun refreshVillageData(context: Context) {
        showLoader.value = true
        villageSelectionRepository.fetchUserAndVillageDetails(forceRefresh = true) {
            if (it.success) {
                _villagList.value = it.villageList
                _filterVillageList.value = villageList.value
                villageSelectionRepository.fetchPatQuestionsFromNetwork(true)
                villageSelectionRepository.fetchCastList(isRefresh = true)
                showCustomToast(context, context.getString(R.string.fetched_successfully))

            } else {
                showCustomToast(
                    context,
                    context.getString(R.string.refresh_failed_please_try_again)
                )

            }
            showLoader.value = false

        }
    }

    fun clearLocalDB(context: Context, onDataClearComplete: () -> Unit) {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            casteListDao.deleteCasteTable()
            tolaDao.deleteAllTola()
            didiDao.deleteAllDidi()
            lastSelectedTolaDao.deleteAllLastSelectedTola()
            numericAnswerDao.deleteAllNumericAnswers()
            answerDao.deleteAllAnswers()
            questionListDao.deleteQuestionTable()
            stepsListDao.deleteAllStepsFromDB()
            userDao.deleteAllUserDetail()
            villageListDao.deleteAllVilleges()
            bpcSummaryDao.deleteAllSummary()
            poorDidiListDao.deleteAllDidis()
            syncManagerDatabase.eventsDao().deleteAllEvents()
            syncManagerDatabase.eventsDependencyDao().deleteAllDependentEvents()
            clearSharedPreference()
            init()
            onDataClearComplete()
        }
    }

    private fun clearSharedPreference() {
        val prefRepo = villageSelectionRepository.prefRepo
        val languageId = prefRepo.getAppLanguageId()
        val language = prefRepo.getAppLanguage()
        val accessToken = prefRepo.getAccessToken()
        val mobileNo = prefRepo.getMobileNumber()
        prefRepo.clearSharedPreference()
        prefRepo.saveAppLanguage(language)
        prefRepo.saveAppLanguageId(languageId)
        prefRepo.saveAccessToken(accessToken ?: "")
        prefRepo.saveMobileNumber(mobileNo)
    }

    fun logout() {
        val prefRepo = villageSelectionRepository.prefRepo
        prefRepo.saveAccessToken("")
        prefRepo.saveMobileNumber("")
        prefRepo.saveSettingOpenFrom(0)
    }

    fun savePageOpenFromOTPScreen() {
        prefRepo.savePageOpenFromOTPScreen(false)
    }
}