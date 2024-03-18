package com.patsurvey.nudge.activities.ui.bpc.bpc_village_screen

import android.content.Context
import android.text.TextUtils
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.activities.ui.progress.VillageSelectionRepository
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.VillageEntity
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
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.database.dao.UserDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.NudgeLogger
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
    val languageListDao: LanguageListDao,
    val questionListDao: QuestionListDao,
    val trainingVideoDao: TrainingVideoDao,
    val numericAnswerDao: NumericAnswerDao,
    val answerDao: AnswerDao,
    val bpcSummaryDao: BpcSummaryDao,
    val poorDidiListDao: PoorDidiListDao,
    val userDao: UserDao,
    val lastSelectedTolaDao: LastSelectedTolaDao,
    val villageSelectionRepository: VillageSelectionRepository
): BaseViewModel() {

    val showLoader = mutableStateOf(false)
    val villageSelected = mutableStateOf(0)

    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    var _filterVillageList = MutableStateFlow(listOf<VillageEntity>())
    val filterVillageList: StateFlow<List<VillageEntity>> get() = _filterVillageList
    val showUserChangedDialog = mutableStateOf(false)

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
            villageSelectionRepository.fetchPatQuestionsFromNetwork()
            _villagList.value = it.villageList
            _filterVillageList.value = villageList.value
            showLoader.value = false
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

    fun refreshVillageData() {
        showLoader.value = true
        villageSelectionRepository.fetchUserAndVillageDetails(forceRefresh = true) {
            _villagList.value = it.villageList
            _filterVillageList.value = villageList.value
            showLoader.value = false
        }
    }

    fun clearLocalDB(context: Context) {
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
            clearSharedPreference()
            init()
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

}