package com.patsurvey.nudge.activities.ui.vo_endorsement

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VoEndorsementSummaryViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val answerDao: AnswerDao,
    val questionListDao: QuestionListDao
):BaseViewModel() {

    private val _didiEntity = MutableStateFlow(
        DidiEntity(
            id = 0,
            name = "",
            address = "",
            guardianName = "",
            relationship = "",
            castId = 0,
            castName = "",
            cohortId = 0,
            cohortName = "",
            villageId = 0,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis(),
            shgFlag = SHGFlag.NOT_MARKED.value
        )
    )

     val _selectedDidiEntity = MutableStateFlow(
        DidiEntity(
            id = 0,
            name = "",
            address = "",
            guardianName = "",
            relationship = "",
            castId = 0,
            castName = "",
            cohortId = 0,
            cohortName = "",
            villageId = 0,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis(),
            shgFlag = SHGFlag.NOT_MARKED.value
        )
    )
    val didiEntity: StateFlow<DidiEntity> get() = _didiEntity
    val selectedDidiEntity: StateFlow<DidiEntity> get() = _selectedDidiEntity

    private val _answerSection1List = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerSection1List: StateFlow<List<SectionAnswerEntity>> get() = _answerSection1List

    private val _answerSection2List = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerSection2List: StateFlow<List<SectionAnswerEntity>> get() = _answerSection2List

    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList

    private val _quesList = MutableStateFlow(listOf<QuestionEntity>())
    val quesList: StateFlow<List<QuestionEntity>> get() = _quesList

    val selPageIndex = mutableStateOf(0)
    val quesImageUrl= mutableStateOf(BLANK_STRING)

    fun setDidiDetailsFromDb(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localDidiDetails=didiDao.getDidi(didiId)
            val localQuesList=questionListDao.getAllQuestionsForLanguage(prefRepo.getAppLanguageId()?:2)
            val localDidiList = didiDao.fetchVOEndorseStatusDidi(prefRepo.getSelectedVillage().id) /*answerDao.fetchAllDidisForVO(prefRepo.getSelectedVillage().id)*/
            withContext(Dispatchers.IO){
                 selPageIndex.value= localDidiList.map { it.id }.indexOf(didiId)
                _quesList.emit((localQuesList))
                _didiEntity.emit(localDidiDetails)
                _didiList.emit(localDidiList)
            }
        }
    }

    fun getSummaryQuesList(didiId: Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localSec1List = answerDao.getAnswerForDidi(didiId = didiId, actionType = TYPE_EXCLUSION)
            val localSec2List = answerDao.getAnswerForDidi(didiId = didiId, actionType = TYPE_INCLUSION)
            withContext(Dispatchers.IO){
                if(localSec1List.isNotEmpty()){
                    localSec1List.forEach { answer->
                        val quesimage= _quesList.value.filter { it.questionId == answer.questionId }
                      if(quesimage.isNotEmpty()){
                          answer.questionImageUrl=quesimage[0].questionImageUrl
                      }
                    }
                }
                _answerSection1List.emit(localSec1List)
                _answerSection2List.emit(localSec2List)
            }
        }
    }


    fun updateVoEndorsementStatus(didiId: Int,status:Int){
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            val villageId = prefRepo.getSelectedVillage().id
            NudgeLogger.e("VoEndorsementSummaryViewModel",
                "updateVoEndorsementStatus -> didiDao.updateVOEndorsementStatus before: villageId = $villageId, didiId = $didiId" +
                        "status: $status")

            didiDao.updateVOEndorsementStatus(villageId = villageId, didiId, status)

            NudgeLogger.e("VoEndorsementSummaryViewModel",
                "updateVoEndorsementStatus -> didiDao.updateVOEndorsementStatus after")

            NudgeLogger.e("VoEndorsementSummaryViewModel",
                "updateVoEndorsementStatus -> didiDao.updateNeedToPostVO before: didiId = $didiId, villageId: $villageId, needsToPostVo = true")

            didiDao.updateNeedToPostVO(
                didiId = didiId,
                needsToPostVo = true,
                villageId = villageId
            )

            NudgeLogger.e("VoEndorsementSummaryViewModel",
                "updateVoEndorsementStatus -> didiDao.updateNeedToPostVO after")
        }
    }

    fun updateDidiDetailsForBox(didiId: Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localDidiDetails=didiDao.getDidi(didiId)
            withContext(Dispatchers.IO){
                _didiEntity.emit(localDidiDetails)
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        NudgeLogger.e("VoEndorsementSummaryViewModel", "onServerError -> error: ${error.toString()}")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        NudgeLogger.e("VoEndorsementSummaryViewModel", "onServerError -> errorMessage: ${errorModel?.message}, api: ${errorModel?.apiName}")
    }
}