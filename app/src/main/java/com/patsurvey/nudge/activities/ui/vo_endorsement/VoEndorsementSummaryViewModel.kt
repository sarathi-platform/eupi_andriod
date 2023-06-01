package com.patsurvey.nudge.activities.ui.vo_endorsement

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.network.model.ErrorModel
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
    val answerDao: AnswerDao
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

    val selPageIndex = mutableStateOf(0)

    fun setDidiDetailsFromDb(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localDidiDetails=didiDao.getDidi(didiId)
            val localDidiList = answerDao.fetchAllDidisForVO(prefRepo.getSelectedVillage().id)
            withContext(Dispatchers.IO){
                 selPageIndex.value= localDidiList.map { it.id }.indexOf(didiId)
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
                _answerSection1List.emit(localSec1List)
                _answerSection2List.emit(localSec2List)
            }
        }
    }


    fun updateVoEndorsementStatus(didiId: Int,status:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                didiDao.updateVOEndorsementStatus(prefRepo.getSelectedVillage().id,didiId,status)
            }
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
        TODO("Not yet implemented")
    }
}