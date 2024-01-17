package com.patsurvey.nudge.activities.ui.bpc.bpc_village_screen

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.google.gson.JsonSyntaxException
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.ui.progress.VillageSelectionRepository
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.download.AndroidDownloader
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BPC_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.HEADING_QUESTION_TYPE
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PAT_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.QUESTION_FLAG_RATIO
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BpcVillageScreenViewModel @Inject constructor(
    val villageSelectionRepository: VillageSelectionRepository
): BaseViewModel() {

    val showLoader = mutableStateOf(false)
    val villageSelected = mutableStateOf(0)

    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    var _filterVillageList = MutableStateFlow(listOf<VillageEntity>())
    val filterVillageList: StateFlow<List<VillageEntity>> get() = _filterVillageList

    fun init () {
        showLoader.value = true
        fetchUserAndVillageDetails()
    }

    fun isUserBpc() = villageSelectionRepository.isUserBPC()

    private fun fetchUserAndVillageDetails() {
        villageSelectionRepository.fetchUserAndVillageDetails() {
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
        villageSelectionRepository.fetchUserAndVillageDetails() {
            _villagList.value = it.villageList
            _filterVillageList.value = villageList.value
            showLoader.value = false
        }
    }

}