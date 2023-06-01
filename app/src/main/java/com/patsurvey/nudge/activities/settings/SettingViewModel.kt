package com.patsurvey.nudge.activities.settings

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.gson.JsonArray
import com.patsurvey.nudge.SyncHelper
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.SettingOptionModel
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.isInternetAvailable
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.SYNC_FAILED
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.showCustomToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class SettingViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val villegeListDao: VillageListDao,
    val didiDao: DidiDao

):BaseViewModel() {
    private val _optionList = MutableStateFlow(listOf<SettingOptionModel>())
    val optionList: StateFlow<List<SettingOptionModel>> get() = _optionList

    fun createSettingMenu(list:ArrayList<SettingOptionModel>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                _optionList.value = list
            }
        }
    }
    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    fun syncDataOnServer(context: Context) {
        if(isInternetAvailable(context)){
            val syncHelper = SyncHelper(prefRepo,apiInterface,tolaDao,stepsListDao,exceptionHandler, villegeListDao, didiDao,job)
            syncHelper.syncDataToServer(object :
                NetworkCallbackListener {
                    override fun onSuccess() {
                    }

                    override fun onFailed() {
                        showCustomToast(context, SYNC_FAILED)
                    }
            })
        }
    }

}