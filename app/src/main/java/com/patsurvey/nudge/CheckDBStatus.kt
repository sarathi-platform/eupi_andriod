package com.patsurvey.nudge

import androidx.compose.runtime.MutableState
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.TolaStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CheckDBStatus(val viewModel: BaseViewModel) {


    fun isFirstStepNeedToBeSync(tolaDao: TolaDao, onResult: (Int) -> Unit) {
        viewModel.job = CoroutineScope(Dispatchers.IO + viewModel.exceptionHandler).launch {
            if (tolaDao.fetchTolaNeedToPost(true, "", 0).isEmpty()
                && tolaDao.fetchPendingTola(true, "").isEmpty()
                && tolaDao.fetchAllTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal).isEmpty()
                && tolaDao.fetchAllPendingTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal, "")
                    .isEmpty()
                && tolaDao.fetchAllTolaNeedToUpdate(true, "", 0).isEmpty()
                && tolaDao.fetchAllPendingTolaNeedToUpdate(true, "").isEmpty()
            ) {
                NudgeLogger.d(
                    "CheckDBStatus",
                    "isFirstStepNeedToBeSync -> isNeedToBeSync.value = 2"
                )
                withContext(Dispatchers.Main) {
                    onResult(2)
                }
            } else {
                NudgeLogger.d("CheckDBStatus", "isFirstStepNeedToBeSync -> isNeedToBeSync.value = 0")
                onResult(0)
            }

        }
    }
    fun isSecondStepNeedToBeSync(didiDao :DidiDao, onResult:(Int)->Unit) {
        viewModel.job = CoroutineScope(Dispatchers.IO + viewModel.exceptionHandler).launch {
            if(didiDao.fetchAllDidiNeedToPost(true,"", 0).isEmpty()
                && didiDao.fetchPendingDidi(true,"").isEmpty()
                && didiDao.fetchAllDidiNeedToDelete(DidiStatus.DIID_DELETED.ordinal, true, "", 0).isEmpty()
                && didiDao.fetchAllPendingDidiNeedToDelete(DidiStatus.DIID_DELETED.ordinal,"",0).isEmpty()
                && didiDao.fetchAllDidiNeedToUpdate(true,"",0).isEmpty()
                && didiDao.fetchAllPendingDidiNeedToUpdate(true,"",0).isEmpty()) {
                NudgeLogger.d("CheckDBStatus", "isSecondStepNeedToBeSync -> isNeedToBeSync.value = 2")
                withContext(Dispatchers.Main) {
                    onResult(2)
                }
            } else {
                NudgeLogger.d("CheckDBStatus", "isSecondStepNeedToBeSync -> isNeedToBeSync.value = 0")
                onResult(0)
            }
        }

    }

    fun isThirdStepNeedToBeSync(didiDao : DidiDao, onResult:(Int)->Unit){
        viewModel.job = CoroutineScope(Dispatchers.IO + viewModel.exceptionHandler).launch {
            if (didiDao.getAllNeedToPostDidiRanking(true, 0).isEmpty()
                && didiDao.fetchPendingWealthStatusDidi(true, "").isEmpty()
            ) {
                NudgeLogger.d("CheckDBStatus", "isThirdStepNeedToBeSync -> isNeedToBeSync.value = 2")
                withContext(Dispatchers.Main){
                    onResult(2)

                }
            } else {
                NudgeLogger.d("CheckDBStatus", "isThirdStepNeedToBeSync -> isNeedToBeSync.value = 0")
                withContext(Dispatchers.Main){
                    onResult(0)
                }
            }
        }
    }

    fun isFourthStepNeedToBeSync(answerDao : AnswerDao,didiDao: DidiDao,prefRepo : PrefRepo, onResult:(Int)->Unit) {
        viewModel.job = CoroutineScope(Dispatchers.IO + viewModel.exceptionHandler).launch {
            if (answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id).isEmpty()
                && didiDao.fetchPendingPatStatusDidi(true, "").isEmpty()
            ) {
                NudgeLogger.d("CheckDBStatus", "isFourthStepNeedToBeSync -> isNeedToBeSync.value = 2")
                withContext(Dispatchers.Main){
                    onResult(2)

                }
            } else{
                NudgeLogger.d("CheckDBStatus", "isFourthStepNeedToBeSync -> isNeedToBeSync.value = 0")
                withContext(Dispatchers.Main){
                    onResult(0)
                }
            }
        }
    }

    fun isFifthStepNeedToBeSync(isNeedToBeSync : MutableState<Int>,  didiDao: DidiDao,prefRepo : PrefRepo) {
        viewModel.job = CoroutineScope(Dispatchers.IO + viewModel.exceptionHandler).launch {
            if (didiDao.getAllNeedToPostPATDidi(
                    needsToPostPAT = true,
                    villageId = prefRepo.getSelectedVillage().id
                ).isEmpty()
                && didiDao.getAllNeedToPostPATDidi(
                    needsToPostPAT = true,
                    villageId = prefRepo.getSelectedVillage().id
                ).isEmpty()
            ) {
                NudgeLogger.d("CheckDBStatus", "isFifthStepNeedToBeSync -> isNeedToBeSync.value = 2")
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d("CheckDBStatus", "isFifthStepNeedToBeSync -> isNeedToBeSync.value = 0")
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 0
                }
            }
        }
    }
}