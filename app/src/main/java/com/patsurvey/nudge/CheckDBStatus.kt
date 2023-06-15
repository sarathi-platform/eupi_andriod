package com.patsurvey.nudge

import androidx.compose.runtime.MutableState
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.TolaDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckDBStatus (val viewModel : BaseViewModel){

    fun isFirstStepNeedToBeSync(isNeedToBeSync : MutableState<Int>, tolaDao : TolaDao) {
        viewModel.job = CoroutineScope(Dispatchers.IO + viewModel.exceptionHandler).launch {
            if (tolaDao.fetchTolaNeedToPost(true, "").isEmpty()
                && tolaDao.fetchPendingTola(true, "").isEmpty())
            {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    fun isSecondStepNeedToBeSync(isNeedToBeSync : MutableState<Int>, didiDao :DidiDao) {
        viewModel.job = CoroutineScope(Dispatchers.IO + viewModel.exceptionHandler).launch {
            if(didiDao.fetchAllDidiNeedToPost(true,"").isEmpty()
                && didiDao.fetchPendingDidi(true,"").isEmpty()) {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    fun isThirdStepNeedToBeSync(isNeedToBeSync : MutableState<Int>, didiDao : DidiDao){
        viewModel.job = CoroutineScope(Dispatchers.IO + viewModel.exceptionHandler).launch {
            if (didiDao.getAllNeedToPostDidiRanking(true).isEmpty()
                && didiDao.fetchPendingWealthStatusDidi(true, "").isEmpty()
            ) {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    fun isFourthStepNeedToBeSync(isNeedToBeSync : MutableState<Int>, answerDao : AnswerDao,didiDao: DidiDao,prefRepo : PrefRepo) {
        viewModel.job = CoroutineScope(Dispatchers.IO + viewModel.exceptionHandler).launch {
            if (answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id).isEmpty()
                && didiDao.fetchPendingPatStatusDidi(true, "").isEmpty()
            ) {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
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
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }
}