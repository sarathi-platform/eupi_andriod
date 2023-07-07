package com.patsurvey.nudge.utils

import androidx.lifecycle.MutableLiveData
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.interfaces.ApiService

object NudgeCore {

    private var prefRepo: PrefRepo? = null
    private var apiService: ApiService? = null
    private var tolaDao: TolaDao? = null
    private var stepsListDao: StepsListDao? = null
    private var villageListDao: VillageListDao? = null
    private var didiDao: DidiDao? = null

    val tolaCount: MutableLiveData<Map<Int, Int>> = MutableLiveData(mutableMapOf())
    val didiCount: MutableLiveData<Map<Int, Int>> = MutableLiveData(mutableMapOf())
    val poorDidiCount: MutableLiveData<Map<Int, Int>> = MutableLiveData(mutableMapOf())
    val ultraPoorDidiCount: MutableLiveData<Map<Int, Int>> = MutableLiveData(mutableMapOf())
    val endorsedDidiCount: MutableLiveData<Map<Int, Int>> = MutableLiveData(mutableMapOf())


    fun init(
        prefRepo: PrefRepo,
        apiService: ApiService,
        tolaDao: TolaDao,
        stepsListDao: StepsListDao,
        villageListDao: VillageListDao,
        didiDao: DidiDao,
        
    ) {
        setPrefRepo(prefRepo)
        setApiServices(apiService)
        setTolaDao(tolaDao)
        setDidiDao(didiDao)
        setStepListDao(stepsListDao)
        setVillageListDao(villageListDao)
    }

    fun cleanUp(){
        prefRepo = null
        apiService = null
        tolaDao = null
        stepsListDao = null
        villageListDao = null
        didiDao = null
    }

    private fun setPrefRepo(mPrefRepo: PrefRepo) {
        prefRepo = mPrefRepo
    }

    private fun setApiServices(mApiService: ApiService) {
        apiService = mApiService
    }

    private fun setTolaDao(mTolaDao: TolaDao) {
        tolaDao = mTolaDao
    }

    private fun setStepListDao(mStepsListDao: StepsListDao) {
        stepsListDao = mStepsListDao
    }

    private fun setVillageListDao(mVillageListDao: VillageListDao) {
        villageListDao = mVillageListDao
    }

    private fun setDidiDao(mDidiDao: DidiDao) {
        didiDao = mDidiDao
    }

    fun fetchTolaCountForVillages() {

    }


}