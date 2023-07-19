package com.patsurvey.nudge

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.patsurvey.nudge.activities.settings.SettingViewModel
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.request.*
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SyncHelper (
    val settingViewModel: SettingViewModel,
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val exceptionHandler : CoroutineExceptionHandler,
    val villegeListDao: VillageListDao,
    val didiDao: DidiDao,
    var job: Job?,
    val showLoader : MutableState<Boolean>,
    var syncPercentage : MutableState<Float>,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao
){

    private val pendingTimerTime : Long= 10000
    private var isPending = 0
    fun syncDataToServer(networkCallbackListener: NetworkCallbackListener){
        NudgeLogger.d("SyncHelper","sync progress started")
        addTolasToNetwork(networkCallbackListener)
    }

    private fun startSyncTimer(networkCallbackListener: NetworkCallbackListener){
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                when (isPending) {
                    1 -> {
                        checkTolaAddStatus(networkCallbackListener)
                    }
                    2 -> {
                        checkTolaDeleteStatus(networkCallbackListener)
                    }
                    3 -> {
                        checkTolaUpdateStatus(networkCallbackListener)
                    }
                    4 -> {
                        checkAddDidiStatus(networkCallbackListener)
                    }
                    5 -> {
                        checkDeleteDidiStatus(networkCallbackListener)
                    }
                    6 -> {
                        checkUpdateDidiStatus(networkCallbackListener)
                    }
                    7 -> {
                        checkDidiWealthStatus(networkCallbackListener)
                    }
                    8 -> {
                        checkDidiPatStatus(networkCallbackListener)
                    }
                    9 -> {
                        checkVOStatus(networkCallbackListener)
                    }
                }
            }
        },pendingTimerTime)
    }

    fun checkTolaUpdateStatus(networkCallbackListener : NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchAllPendingTolaNeedToUpdate(true,"")
            if(tolaList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                tolaList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        tolaList.forEach { tola ->
                            if (transactionIdResponse.transactionId == tola.transactionId) {
                                tolaDao.updateNeedToPost(tola.id,false)
                                tolaDao.updateTolaTransactionId(tola.id,"")
                            }
                        }
                    }
                    addDidisToNetwork(networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                addDidisToNetwork(networkCallbackListener)
            }
        }
    }

    fun checkDeleteDidiStatus(networkCallbackListener : NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllPendingDidiNeedToDelete(DidiStatus.DIID_DELETED.ordinal,"",0)
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didiDao.deleteDidi(didi.id)
                            }
                        }
                    }
                    updateDidiToNetwork(networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                updateDidiToNetwork(networkCallbackListener)
            }
        }
    }

    fun checkTolaDeleteStatus(networkCallbackListener : NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchAllPendingTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal,"")
            if(tolaList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                tolaList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        tolaList.forEach { tola ->
                            if (transactionIdResponse.transactionId == tola.transactionId) {
                                tolaDao.deleteTola(tola.id)
                            }
                        }
                    }
                    updateTolasToNetwork(networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
                updateTolasToNetwork(networkCallbackListener)
            }
        }
    }

    private fun checkVOStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingVOStatusStatusDidi(true,"")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didiDao.updateNeedToPostVO(false,didi.id)
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    callWorkFlowAPIForStep(5)
                    delay(1500)
                    withContext(Dispatchers.Main){
                        delay(1000)
                        syncPercentage.value = 1f
                        settingViewModel.stepFifthSyncStatus.value = 2
                        networkCallbackListener.onSuccess()
                    }
                } else {
                    withContext(Dispatchers.Main){
                        delay(1000)
                        syncPercentage.value = 1f
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                callWorkFlowAPIForStep(5)
                delay(1500)
                withContext(Dispatchers.Main){
                    delay(1000)
                    syncPercentage.value = 1f
                    settingViewModel.stepFifthSyncStatus.value = 2
                    networkCallbackListener.onSuccess()
                }
            }
        }
    }

    private fun checkDidiPatStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingPatStatusDidi(true,"")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatusForPat(TransactionIdRequest("PAT",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didiDao.updateDidiNeedToPostPat(didi.id,false)
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    updateVoStatusToNetwork(networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
                updateVoStatusToNetwork(networkCallbackListener)
            }
        }
    }

    private fun checkDidiWealthStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingWealthStatusDidi(true,"")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didiDao.updateDidiNeedToPostWealthRank(didi.id,false)
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    savePATSummeryToServer(networkCallbackListener)
                } else {
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                savePATSummeryToServer(networkCallbackListener)
            }
        }
    }

    private fun checkUpdateDidiStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllPendingDidiNeedToUpdate(true,"",0)
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didi.transactionId = ""
                                didiDao.updateNeedToPost(didi.id,false)
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    updateWealthRankingToNetwork(networkCallbackListener)
                } else{
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                updateWealthRankingToNetwork(networkCallbackListener)
            }
        }
    }

    private fun checkAddDidiStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingDidi(true,"")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didi.serverId = transactionIdResponse.referenceId
                            }
                            didiDao.updateDidiDetailAfterSync(id = didi.id, serverId = didi.serverId, needsToPost = false, transactionId = "", createdDate = didi.createdDate?:0, modifiedDate = didi.modifiedDate?:0)
                        }
                    }
                    deleteDidisToNetwork(networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
                deleteDidisToNetwork(networkCallbackListener)
            }
        }
    }

    private fun uploadDidiImagesToServer(context : Context,location: String){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllDidiNeedToPostImage(true)
            for(didi in didiList) {
                NudgeLogger.d("Synchelper", "uploadDidiImage: $didi.id :: $location")
                try {
                    val uri = didi.localPath.toUri()
                    NudgeLogger.d(
                        "Synchelper",
                        "uploadDidiImage Prev: $uri.toFile().totalSpace} "
                    )
                    val compressedImageFile =
                        compressImage(uri.toString(), context, uri.toFile().name)
                    val requestFile = RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        File(compressedImageFile)
                    )
                    val imageFilePart = MultipartBody.Part.createFormData(
                        "file",
                        File(compressedImageFile).name,
                        requestFile
                    )
                    val requestDidiId = RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        didi.id.toString()
                    )
                    val requestUserType = RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                    )
                    val requestLocation =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), location)
                    NudgeLogger.d(
                        "Synchelper",
                        "uploadDidiImage Details: ${requestDidiId.contentType().toString()}"
                    )
                    val imageUploadResponse = apiService.uploadDidiImage(
                        imageFilePart,
                        requestDidiId,
                        requestUserType,
                        requestLocation
                    )
                    NudgeLogger.d(
                        "Synchelper",
                        "uploadDidiImage imageUploadRequest: ${imageUploadResponse.data ?: ""}"
                    )
                    if (imageUploadResponse.status == SUCCESS) {
                        didiDao.updateNeedToPostImage(didi.id, false)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    fun uploadDidiImage(context: Context, uri: Uri, didiId: Int, location:String) {
        job = MyApplication.appScopeLaunch(Dispatchers.IO + exceptionHandler) {


        }
    }

    private fun checkTolaAddStatus(networkCallbackListener :NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchPendingTola(true,"")
            if(tolaList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                tolaList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        tolaList.forEach { tola ->
                            if (transactionIdResponse.transactionId == tola.transactionId) {
                                tola.serverId = transactionIdResponse.referenceId
                            }
                        }
                    }
                    updateTolaNeedTOPostList(tolaList,networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                deleteTolaToNetwork(networkCallbackListener)
            }
        }
    }

    fun addTolasToNetwork(networkCallbackListener: NetworkCallbackListener) {
        Log.e("add tola","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                settingViewModel.stepOneSyncStatus.value = 1
            }
            val tolaList = tolaDao.fetchTolaNeedToPost(true,"",0)
            val jsonTola = JsonArray()
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                Log.e("tola need to post","$tolaList.size")
                val response = apiService.addCohort(jsonTola)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0].transactionId.isNullOrEmpty())) {
                            response.data.forEach { tolaDataFromNetwork ->
                                tolaList.forEach { tola ->
                                    if (TextUtils.equals(tolaDataFromNetwork.name, tola.name)) {
                                        tola.serverId = tolaDataFromNetwork.id
                                        tola.createdDate = tolaDataFromNetwork.createdDate
                                        tola.modifiedDate = tolaDataFromNetwork.modifiedDate
                                    }
                                    tolaDao.updateTolaDetailAfterSync(
                                        id = tola.id,
                                        serverId = tola.serverId,
                                        needsToPost = false,
                                        transactionId = "",
                                        createdDate = tola.createdDate?:0L,
                                        modifiedDate = tola.modifiedDate?:0L
                                    )
                                    Log.e("tola after update", "$tolaList.size")
                                }
                            }
                            checkTolaAddStatus(networkCallbackListener)
                            withContext(Dispatchers.Main) {
                                delay(1000)
                                syncPercentage.value = 0.2f
                            }
                        } else {
                            for (i in 0 until response.data.size){
                                tolaList[i].transactionId = response.data[i].transactionId
                            }
                            updateLocalTransactionIdToLocalTola(tolaList,networkCallbackListener)
                            withContext(Dispatchers.Main) {
                                delay(1000)
                                syncPercentage.value = 0.1f
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkTolaAddStatus(networkCallbackListener)
            }
        }
    }

    private fun updateLocalTransactionIdToLocalTola(tolaList: List<TolaEntity>, networkCallbackListener: NetworkCallbackListener) {
        tolaList.forEach{tola->
            tola.transactionId?.let { tolaDao.updateTolaTransactionId(tola.id, it) }
        }
        isPending = 1
        startSyncTimer(networkCallbackListener)
    }

    fun updateTolaNeedTOPostList(tolaList: List<TolaEntity>,networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateTolaListWithIds(tolaList,networkCallbackListener)
        }
    }

    private fun updateTolaListWithIds(tolaList: List<TolaEntity>,networkCallbackListener: NetworkCallbackListener) {
        Log.e("tola updated","$tolaList.size")
        for(tola in tolaList) {
            tolaDao.updateTolaDetailAfterSync(
                id = tola.id,
                serverId = tola.serverId,
                needsToPost = false,
                transactionId = "",
                createdDate = tola.createdDate?:0L,
                modifiedDate = tola.modifiedDate?:0L
            )
        }
        deleteTolaToNetwork(networkCallbackListener)
    }

    private fun updateTolasToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                settingViewModel.syncPercentage.value = 0.14f
            }
            val tolaList = tolaDao.fetchAllTolaNeedToUpdate(true,"",0)
            val jsonTola = JsonArray()
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    jsonTola.add(EditCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                Log.e("tola need to post","$tolaList.size")
                val response = apiService.editCohort(jsonTola)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0].transactionId.isNullOrEmpty())) {
                            tolaList.forEach { tola ->
                                tolaDao.updateNeedToPost(tola.id,false)
                            }
                            checkTolaUpdateStatus(networkCallbackListener)
                        } else {
                            for (i in 0 until response.data.size){
                                tolaList[i].transactionId = response.data[i].transactionId
                                tolaList[i].transactionId?.let { it1 ->
                                    tolaDao.updateTolaTransactionId(tolaList[i].id,
                                        it1
                                    )
                                }
                            }
                            isPending = 3
                            startSyncTimer(networkCallbackListener)
                            withContext(Dispatchers.Main) {
                                delay(1000)
                                syncPercentage.value = 0.1f
                            }
                        }
                    }
                }
                else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkTolaUpdateStatus(networkCallbackListener)
            }
        }
    }

    private fun deleteTolaToNetwork(networkCallbackListener: NetworkCallbackListener) {
        Log.e("delete tola","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                settingViewModel.syncPercentage.value = 0.07f
            }
            val tolaList = tolaDao.fetchAllTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal)
            val jsonTola = JsonArray()
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    jsonTola.add(DeleteTolaRequest(tola.serverId, localModifiedDate = System.currentTimeMillis()).toJson())
                }
                Log.e("tola need to post","$tolaList.size")
                val response = apiService.deleteCohort(jsonTola)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0]?.transactionId.isNullOrEmpty())) {
                            tolaList.forEach { tola ->
                                tolaDao.deleteTola(tola.id)
                            }
                            checkTolaDeleteStatus(networkCallbackListener)
                       } else {
                            for (i in 0 until response.data.size){
                                tolaList[i].transactionId = response.data[i]?.transactionId
                                tolaList[i].transactionId?.let { it1 ->
                                    tolaDao.updateTolaTransactionId(tolaList[i].id,
                                        it1
                                    )
                                }
                            }
                            isPending = 2
                            startSyncTimer(networkCallbackListener)
                            withContext(Dispatchers.Main) {
                                delay(1000)
                                syncPercentage.value = 0.1f
                            }
                        }
                    }
                }
                else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                checkTolaDeleteStatus(networkCallbackListener)
            }
        }
    }

    fun addDidisToNetwork(networkCallbackListener: NetworkCallbackListener) {
        callWorkFlowAPIForStep(1)
        Log.e("add didi","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                settingViewModel.stepOneSyncStatus.value = 3
                settingViewModel.stepTwoSyncStatus.value = 1
                settingViewModel.syncPercentage.value = 0.2f
            }
            val didiList = didiDao.fetchAllDidiNeedToAdd(true,"",0)
            for(didi in didiList){
                val tola = tolaDao.fetchSingleTolaFromServerId(didi.cohortId)
                if (tola != null) {
                    didi.cohortId = tola.serverId
                }
            }
            val jsonDidi = JsonArray()
            if (didiList.isNotEmpty()) {
                for (didi in didiList) {
                    jsonDidi.add(AddDidiRequest.getRequestObjectForDidi(didi).toJson())
                }
                val response = apiService.addDidis(jsonDidi)
                if (response.status.equals(SUCCESS, true)) {
                    if(response.data?.get(0)?.transactionId.isNullOrEmpty()) {
                        response.data?.let {
                            response.data.forEach { didiFromNetwork ->
                                didiList.forEach { didi ->
                                    if (TextUtils.equals(didiFromNetwork.name, didi.name)) {
                                        didi.serverId = didiFromNetwork.id
                                        didi.createdDate = didiFromNetwork.createdDate
                                        didi.modifiedDate = didiFromNetwork.modifiedDate
                                    }
                                    didiDao.updateDidiDetailAfterSync(id = didi.id, serverId = didi.serverId, needsToPost = false, transactionId = "", createdDate = didi.createdDate?:0, modifiedDate = didi.modifiedDate?:0)
                                }
                            }
                        }
                        checkAddDidiStatus(networkCallbackListener)
                        withContext(Dispatchers.Main) {
                            delay(1000)
                            syncPercentage.value = 0.4f
                        }
                    } else {
                        for (i in 0..(response.data?.size?.minus(1) ?: 0)){
                            didiList[i].transactionId = response.data?.get(i)?.transactionId
                            didiList[i].transactionId?.let {
                                didiDao.updateDidiTransactionId(didiList[i].id,
                                    it
                                )
                            }
                        }
                        isPending = 4
                        startSyncTimer(networkCallbackListener)
                        withContext(Dispatchers.Main) {
                            delay(1000)
                            syncPercentage.value = 0.3f
                        }
                    }
                } else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkAddDidiStatus(networkCallbackListener)
            }
        }
    }

    fun updateDidiToNetwork(networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                settingViewModel.syncPercentage.value = 0.34f
            }
            val didiList = didiDao.fetchAllDidiNeedToUpdate(true,"",0)
            if (didiList.isNotEmpty()) {
                val didiRequestList = arrayListOf<EditDidiRequest>()
                didiList.forEach { didi->
                    didiRequestList.add(EditDidiRequest(didi.serverId,didi.name,didi.address,didi.guardianName,didi.castId,didi.cohortId))
                }
                val response = apiService.updateDidis(didiRequestList)
                if (response.status.equals(SUCCESS, true)) {
                    if(response.data?.get(0)?.transactionId.isNullOrEmpty()) {
                        response.data?.let {
                            response.data.forEach { _ ->
                                didiList.forEach { didi ->
                                    didiDao.updateNeedToPost(didi.id,false)
                                }
                            }
                        }
                        updateDidisNeedTOPostList(didiList,networkCallbackListener)
                        withContext(Dispatchers.Main) {
                            delay(1000)
                            syncPercentage.value = 0.4f
                        }
                    } else {
                        for (i in 0..(response.data?.size?.minus(1) ?: 0)){
                            didiList[i].transactionId = response.data?.get(i)?.transactionId
                            didiList[i].transactionId?.let {
                                didiDao.updateDidiTransactionId(didiList[i].id,
                                    it
                                )
                            }
                        }
                        isPending = 6
                        startSyncTimer(networkCallbackListener)
                        withContext(Dispatchers.Main) {
                            delay(1000)
                            syncPercentage.value = 0.3f
                        }
                    }
                } else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkUpdateDidiStatus(networkCallbackListener)
            }
        }
    }

    private fun callWorkFlowAPIForStep(step: Int) {
        NudgeLogger.d("SyncHelper","callWorkFlowAPIForStep -> called")
//        val villageId = prefRepo.getSelectedVillage().id
        val stepList = stepsListDao.getAllStepsByOrder(step,true).sortedBy { it.orderNumber }
        NudgeLogger.e("SyncHelper","callWorkFlowAPIForStep called -> $stepList -> $step")
        callWorkFlowAPI(stepList)
        /*when(step){
            1->{
                if(stepList[stepList.map { it.orderNumber }.indexOf(step)].needToPost){
                    callWorkFlowAPI(villageId,stepList[stepList.map { it.orderNumber }.indexOf(step)].id)
                }
            }
            2->{
                if(stepList[stepList.map { it.orderNumber }.indexOf(step)].needToPost){
                    callWorkFlowAPI(villageId,stepList[stepList.map { it.orderNumber }.indexOf(step)].id)
                }
            }
            3->{
                if(stepList[stepList.map { it.orderNumber }.indexOf(step)].needToPost){
                    callWorkFlowAPI(villageId,stepList[stepList.map { it.orderNumber }.indexOf(step)].id)
                }
            }
            4->{
                if(stepList[stepList.map { it.orderNumber }.indexOf(step)].needToPost){
                    callWorkFlowAPI(villageId,stepList[stepList.map { it.orderNumber }.indexOf(step)].id)
                }
            }
            5->{
                if(stepList[stepList.map { it.orderNumber }.indexOf(step)].needToPost){
                    callWorkFlowAPI(villageId,stepList[stepList.map { it.orderNumber }.indexOf(step)].id)
                }
            }
        }*/
    }

    fun updateDidisNeedTOPostList(didiList : List<DidiEntity>,networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateDidiListWithServerIds(didiList,networkCallbackListener)
        }
    }

    private fun updateDidiListWithServerIds(oldDidiList: List<DidiEntity>,networkCallbackListener: NetworkCallbackListener) {
        oldDidiList.forEach(){ didiEntity ->
            didiEntity.needsToPost = false
            didiEntity.transactionId = ""
            didiDao.updateDidiDetailAfterSync(id = didiEntity.id, serverId = didiEntity.serverId, needsToPost = false, transactionId = "", createdDate = didiEntity.createdDate?:0, modifiedDate = didiEntity.modifiedDate?:0)
        }
        checkUpdateDidiStatus(networkCallbackListener)
    }

    private fun deleteDidisToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                settingViewModel.syncPercentage.value = 0.27f
            }
            val didiList = didiDao.fetchAllDidiNeedToDelete(DidiStatus.DIID_DELETED.ordinal)
            val jsonDidi = JsonArray()
            if (didiList.isNotEmpty()) {
                for (didi in didiList) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("id", didi.serverId)
                    jsonDidi.add(jsonObject)
                }
                Log.e("tola need to post","$didiList.size")
                val response = apiService.deleteDidi(jsonDidi)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0].transactionId.isNullOrEmpty())) {
                            didiList.forEach { tola ->
                                didiDao.deleteDidi(tola.id)
                            }
                            checkDeleteDidiStatus(networkCallbackListener)
                        } else {
                            for (i in 0 until response.data.size){
                                didiList[i].transactionId = response.data[i].transactionId
                                didiList[i].transactionId?.let { it1 ->
                                    didiDao.updateDidiTransactionId(didiList[i].id,
                                        it1
                                    )
                                }
                            }
                            isPending = 5
                            startSyncTimer(networkCallbackListener)
                            withContext(Dispatchers.Main) {
                                delay(1000)
                                syncPercentage.value = 0.1f
                            }
                        }
                    }
                }
                else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkDeleteDidiStatus(networkCallbackListener)
            }
        }
    }

    fun updateWealthRankingToNetwork(networkCallbackListener: NetworkCallbackListener){
        Log.e("add didi","called")
        callWorkFlowAPIForStep(2)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                settingViewModel.stepTwoSyncStatus.value = 3
                settingViewModel.stepThreeSyncStatus.value = 1
                settingViewModel.syncPercentage.value = 0.4f
            }
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList=didiDao.getAllNeedToPostDidiRanking(true)
                    if (needToPostDidiList.isNotEmpty()) {
                        val didiWealthRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        val didiStepRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        needToPostDidiList.forEach { didi ->
                            didiWealthRequestList.add(EditDidiWealthRankingRequest(didi.serverId, StepType.WEALTH_RANKING.name,didi.wealth_ranking, localModifiedDate = System.currentTimeMillis()))
                            didiStepRequestList.add(EditDidiWealthRankingRequest(didi.serverId, StepType.SOCIAL_MAPPING.name,StepStatus.COMPLETED.name, localModifiedDate = System.currentTimeMillis()))
                        }
                        didiWealthRequestList.addAll(didiStepRequestList)
                        val updateWealthRankResponse = apiService.updateDidiRanking(didiWealthRequestList)
                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                            val didiListResponse = updateWealthRankResponse.data
                            if(!didiListResponse?.get(0)?.transactionId.isNullOrEmpty()){
                                val size = needToPostDidiList.indices
                                for(i in size) {
                                    val serverResponseDidi = updateWealthRankResponse.data?.get(i)
                                    val localDidi = needToPostDidiList[i]
                                    serverResponseDidi?.transactionId?.let {
                                        didiDao.updateDidiTransactionId(localDidi.id,
                                            it
                                        )
                                    }
                                }
                                isPending = 7
                                startSyncTimer(networkCallbackListener)
                                withContext(Dispatchers.Main) {
                                    delay(1000)
                                    syncPercentage.value = 0.5f
                                }
                            } else {
                                needToPostDidiList.forEach { didi ->
                                    didiDao.updateDidiNeedToPostWealthRank(didi.id,false)
                                }
                                withContext(Dispatchers.Main) {
                                    delay(1000)
                                    syncPercentage.value = 0.6f
                                }
                                checkDidiWealthStatus(networkCallbackListener)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onFailed()
                            }
                        }
                        if(!updateWealthRankResponse.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(prefRepo,updateWealthRankResponse.lastSyncTime)
                        }
                    } else {
                        checkDidiWealthStatus(networkCallbackListener)
                    }

                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main){
                    networkCallbackListener.onFailed()
                    settingViewModel.onCatchError(ex, ApiType.DIDI_EDIT_API)
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun savePATSummeryToServer(networkCallbackListener: NetworkCallbackListener){
        callWorkFlowAPIForStep(3)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.Main) {
                    delay(1000)
                    settingViewModel.stepThreeSyncStatus.value = 3
                    settingViewModel.stepFourSyncStatus.value = 1
                    settingViewModel.syncPercentage.value = 0.6f
                }
                val didiIDList= answerDao.fetchPATSurveyDidiList()
                if(didiIDList.isNotEmpty()){
                    var optionList= emptyList<OptionsItem>()
                    val answeredDidiList: java.util.ArrayList<PATSummarySaveRequest> = arrayListOf()
                    var surveyId =0
                    var scoreDidiList: java.util.ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                    val userType=if((prefRepo.getPref(PREF_KEY_TYPE_NAME, "") ?: "").equals(BPC_USER_TYPE, true)) USER_BPC else USER_CRP
                    didiIDList.forEachIndexed { index, didi ->
                        Log.d("SyncHelper", "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
                        val qList: java.util.ArrayList<AnswerDetailDTOListItem> = arrayListOf()
                        val needToPostQuestionsList = answerDao.getAllNeedToPostQuesForDidi(didi.id)
                        if (needToPostQuestionsList.isNotEmpty()) {
                            needToPostQuestionsList.forEach {
                                surveyId = questionDao.getQuestion(it.questionId).surveyId ?: 0
                                if (!it.type.equals(QuestionType.Numeric_Field.name, true)) {
                                    optionList = listOf(
                                        OptionsItem(
                                            optionId = it.optionId,
                                            optionValue = it.optionValue,
                                            count = 0,
                                            summary = it.summary,
                                            display = it.answerValue,
                                            weight = 0,
                                            isSelected = false
                                        )
                                    )
                                } else {
                                    val numOptionList =
                                        numericAnswerDao.getSingleQueOptions(it.questionId, it.didiId)
                                    val tList: java.util.ArrayList<OptionsItem> = arrayListOf()
                                    if (numOptionList.isNotEmpty()) {
                                        numOptionList.forEach { numOption ->
                                            tList.add(
                                                OptionsItem(
                                                    optionId = numOption.optionId,
                                                    optionValue = 0,
                                                    count = numOption.count,
                                                    summary = it.summary,
                                                    display = it.answerValue,
                                                    weight = numOption.weight,
                                                    isSelected = false
                                                )
                                            )
                                        }
                                        optionList = tList
                                    }else{
                                        tList.add(
                                            OptionsItem(
                                                optionId = it.optionId,
                                                optionValue = 0,
                                                count = 0,
                                                summary = it.summary,
                                                display = it.answerValue,
                                                weight = it.weight,
                                                isSelected = false
                                            )
                                        )

                                        optionList = tList
                                    }

                                }
                                try {
                                    qList.add(
                                        AnswerDetailDTOListItem(
                                            questionId = it.questionId,
                                            section = it.actionType,
                                            options = optionList,
                                            assetAmount = it.assetAmount
                                        )
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        val passingMark=questionDao.getPassingScore()
                        var comment= BLANK_STRING
                        comment = if(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||  didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal)
                            BLANK_STRING
                        else {
                            if(didi.patSurveyStatus==PatSurveyStatus.COMPLETED.ordinal && didi.section2Status==PatSurveyStatus.NOT_STARTED.ordinal){
                                TYPE_EXCLUSION
                            } else {
                                if (didi.score < passingMark)
                                    LOW_SCORE
                                else {
                                    BLANK_STRING
                                }
                            }
                        }
                        scoreDidiList.add(
                            EditDidiWealthRankingRequest(
                                id = if (didi.serverId == 0) didi.id else didi.serverId,
                                score = didi.score,
                                comment =comment,
                                type = if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                                result = if(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||  didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) DIDI_NOT_AVAILABLE
                                else {
                                    if (didi.forVoEndorsement == 0) DIDI_REJECTED else COMPLETED_STRING
                                }
                            )
                        )
                        answeredDidiList.add(
                            PATSummarySaveRequest(
                                villageId = prefRepo.getSelectedVillage().id,
                                surveyId = surveyId,
                                beneficiaryId = didi.serverId,
                                languageId = prefRepo.getAppLanguageId() ?: 2,
                                stateId = prefRepo.getSelectedVillage().stateId,
                                totalScore = didi.score,
                                userType = userType,
                                beneficiaryName = didi.name,
                                answerDetailDTOList = qList,
                                patSurveyStatus = didi.patSurveyStatus,
                                section2Status = didi.section2Status,
                                section1Status = didi.section1Status,
                                shgFlag = didi.shgFlag
                            )
                        )
                    }
                    if(answeredDidiList.isNotEmpty()){
                        withContext(Dispatchers.IO){
                            val saveAPIResponse= apiService.savePATSurveyToServer(answeredDidiList)
                            if(saveAPIResponse.status.equals(SUCCESS,true)){
                                if(saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                    didiIDList.forEach { didiItem ->
                                        didiDao.updateNeedToPostPAT(
                                            false,
                                            didiItem.id,
                                            prefRepo.getSelectedVillage().id
                                        )
                                    }
                                    withContext(Dispatchers.Main) {
                                        delay(1000)
                                        syncPercentage.value = 0.8f
                                    }
                                    checkDidiPatStatus(networkCallbackListener)
                                } else {
                                    for (i in didiIDList.indices){
                                        saveAPIResponse.data?.get(i)?.let {
                                            didiDao.updateDidiTransactionId(didiIDList[i].id,
                                                it.transactionId)
                                        }
                                        didiDao.updateDidiNeedToPostPat(didiIDList[i].id,true)
                                    }
                                    isPending = 8
                                    startSyncTimer(networkCallbackListener)
                                    withContext(Dispatchers.Main) {
                                        delay(1000)
                                        syncPercentage.value = 0.7f
                                    }
                                }
                                savePatScoreToServer(scoreDidiList)
                            } else {
                                withContext(Dispatchers.Main){
                                    networkCallbackListener.onFailed()
                                }
                            }
                            if(!saveAPIResponse.lastSyncTime.isNullOrEmpty()){
                                updateLastSyncTime(prefRepo,saveAPIResponse.lastSyncTime)
                            }
                        }
                    } else {
                        checkDidiPatStatus(networkCallbackListener)
                    }
                } else {
                    checkDidiPatStatus(networkCallbackListener)
                }
            }  catch (ex:Exception){
                withContext(Dispatchers.Main){
                    networkCallbackListener.onFailed()
                    settingViewModel.onCatchError(ex, ApiType.STATUS_CALL_BACK_API)
                }
                ex.printStackTrace()
            }
        }
    }

    private fun savePatScoreToServer(scoreDidiList: java.util.ArrayList<EditDidiWealthRankingRequest>) {
        if(scoreDidiList.isNotEmpty()) {
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                apiService.updateDidiScore(scoreDidiList)
            }
        }
    }

    fun updateVoStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        callWorkFlowAPIForStep(4)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.Main) {
                    delay(1000)
                    settingViewModel.stepFifthSyncStatus.value = 1
                    settingViewModel.stepFourSyncStatus.value = 3
                    settingViewModel.syncPercentage.value = 0.8f
                }
                withContext(Dispatchers.IO){
                    val needToPostDidiList=didiDao.fetchAllVONeedToPostStatusDidi(needsToPostVo = true, transactionId = "")
                    if(needToPostDidiList.isNotEmpty()){
                        val didiRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        needToPostDidiList.forEach { didi->
                            didi.voEndorsementStatus.let {
                                if (it == DidiEndorsementStatus.ENDORSED.ordinal) {
                                    didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId,StepType.VO_ENDROSEMENT.name, ACCEPTED,
                                        localModifiedDate = System.currentTimeMillis()))
                                } else if (it == DidiEndorsementStatus.REJECTED.ordinal) {
                                    didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId,StepType.VO_ENDROSEMENT.name, DidiEndorsementStatus.REJECTED.name,
                                        localModifiedDate = System.currentTimeMillis()))
                                }
                            }
                        }
                        val updateWealthRankResponse=apiService.updateDidiRanking(didiRequestList)
                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                            val didiListResponse = updateWealthRankResponse.data
                            if (didiListResponse?.get(0)?.transactionId != null) {
                                for (i in didiListResponse.indices) {
                                    val didiResponse = didiListResponse[i]
                                    val didi = needToPostDidiList[i]
                                    didiResponse.transactionId?.let {
                                        didiDao.updateDidiTransactionId(didi.id,
                                            it
                                        )
                                    }
                                }
                                isPending = 9
                                startSyncTimer(networkCallbackListener)
                            } else {
                                if (didiListResponse != null) {
                                    for (i in didiRequestList.indices) {
                                        val didi = didiRequestList[i]
                                        didiDao.updateNeedToPostVOWithServerId(false, didi.id)
                                        didiDao.updateDidiTransactionIdWithServerId(didi.id, "")
                                        //commenting for now since it was having some issues.
                                        /*didiDao.updateNeedToPostVO(false, didi.id)
                                        didiDao.updateDidiTransactionId(didi.id, "")*/
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    delay(1000)
                                    settingViewModel.stepFifthSyncStatus.value = 2
                                    syncPercentage.value = 1f
                                }
                                checkVOStatus(networkCallbackListener)
                            }
                        } else {
                            withContext(Dispatchers.Main){
                                networkCallbackListener.onFailed()
                            }
                        }
                        if(!updateWealthRankResponse.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(prefRepo,updateWealthRankResponse.lastSyncTime)
                        }
                    } else {
                        checkVOStatus(networkCallbackListener)
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main){
                    networkCallbackListener.onFailed()
                    settingViewModel.onCatchError(ex, ApiType.DIDI_EDIT_API)
                }
            }
        }
    }

    fun getStepOneDataSizeInSync(stepOneMutableString : MutableState<String>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var sizeToBeShown = ""
            val tolaList = tolaDao.fetchTolaNeedToPost(true, "",0)
            if (tolaList.isNotEmpty()) {
                val jsonTola = JsonArray()
                for (tola in tolaList) {
                    jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                sizeToBeShown = getSizeToBeShown(jsonTola.toString().toByteArray().size)
                Log.e("num of step 1", "$tolaList.size")
                Log.e("size of step 2", sizeToBeShown)
                stepOneMutableString.value = sizeToBeShown
            }
        }
    }

    fun getStepTwoDataSizeInSync(stepOneMutableString : MutableState<String>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var sizeToBeShown = ""
            val didiList = didiDao.fetchAllDidiNeedToPost(true, "")
            if (didiList.isNotEmpty()) {
                val didiJson = JsonArray()
                for (didi in didiList) {
                    didiJson.add(AddDidiRequest.getRequestObjectForDidi(didi).toJson())
                }
                sizeToBeShown = getSizeToBeShown(didiJson.toString().toByteArray().size)
                Log.e("num of step 2", "$didiList.size")
                Log.e("size of step 2", sizeToBeShown)
                stepOneMutableString.value = sizeToBeShown
            }
        }
    }

    fun getStepThreeDataSizeInSync(stepOneMutableString : MutableState<String>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var sizeToBeShown = ""
            val didiWealthList = didiDao.getAllNeedToPostDidiRanking(true)
            if (didiWealthList.isNotEmpty()) {
                val jsonDidi = JsonArray()
                for (didi in didiWealthList) {
                    jsonDidi.add(EditDidiWealthRankingRequest(didi.id, StepType.WEALTH_RANKING.name,didi.wealth_ranking,
                        localModifiedDate = System.currentTimeMillis()).toJson())
                }
                sizeToBeShown = getSizeToBeShown(jsonDidi.toString().toByteArray().size)
                Log.e("num of step 3", "$didiWealthList.size")
                Log.e("size of step 3", sizeToBeShown)
                stepOneMutableString.value = sizeToBeShown
            }
        }
    }

    fun getStepFourDataSizeInSync(stepOneMutableString : MutableState<String>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var sizeToBeShown = ""
            val didiIDList= answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)
            if(didiIDList.isNotEmpty()) {
                /*val answeredDidiList = fetchAnswerDidiList(didiIDList)
                if (answeredDidiList.isNotEmpty()) {
                    val jsonDidi = JsonArray()
                    for (didi in answeredDidiList) {
                        jsonDidi.add(didi.toJson())
                    }
                    sizeToBeShown = getSizeToBeShown(jsonDidi.toString().toByteArray().size)
                    Log.e("num of step 4", "$answeredDidiList.size")
                    Log.e("size of step 4", sizeToBeShown)
                    stepOneMutableString.value = sizeToBeShown
                }*/
            }
        }
    }

    fun getStepFiveDataSizeInSync(stepOneMutableString : MutableState<String>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var sizeToBeShown = ""
            val tolaList = didiDao.getAllNeedToPostPATDidi(needsToPostPAT = true, villageId = prefRepo.getSelectedVillage().id)
            if (tolaList.isNotEmpty()) {
                val jsonTola = JsonArray()
                for (didi in tolaList) {
                    jsonTola.add(AddDidiRequest.getRequestObjectForDidi(didi).toJson())
                }
                sizeToBeShown = getSizeToBeShown(jsonTola.toString().toByteArray().size)
                Log.e("num of step 5", "$tolaList.size")
                Log.e("size of step 5", sizeToBeShown)
                stepOneMutableString.value = sizeToBeShown
            }
        }
    }

    fun callWorkFlowAPI(steps: List<StepListEntity>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                NudgeLogger.e("SyncHelper","callWorkFlowAPI called")
                val addWorkFlowRequest = mutableListOf<AddWorkFlowRequest>()
                val editWorkFlowRequest = mutableListOf<EditWorkFlowRequest>()
                val needToEditStep = mutableListOf<StepListEntity>()
                val needToAddStep = mutableListOf<StepListEntity>()
                for(step in steps){
                    if (step.workFlowId > 0) {
                        editWorkFlowRequest.add((EditWorkFlowRequest(
                            step.workFlowId,
                            StepStatus.getStepFromOrdinal(step.isComplete)
                        )))
                        needToEditStep.add(step)
                    } else {
                        needToAddStep.add(step)
                        addWorkFlowRequest.add((AddWorkFlowRequest(
                            StepStatus.INPROGRESS.name, step.villageId,
                            step.programId, step.id
                        )))
                    }
                }
                if (addWorkFlowRequest.size > 0) {

                    NudgeLogger.e("SyncHelper", "callWorkFlowAPI addWorkFlowRequest: $addWorkFlowRequest \n\n")

                    val addWorkFlowResponse = apiService.addWorkFlow(Collections.unmodifiableList(addWorkFlowRequest))

                    NudgeLogger.e("SyncHelper","callWorkFlowAPI response: status: ${addWorkFlowResponse.status}, message: ${addWorkFlowResponse.message}, data: ${addWorkFlowResponse.data} \n\n")

                    if (addWorkFlowResponse.status.equals(SUCCESS, true)) {
                        addWorkFlowResponse.data?.let {
                            if (addWorkFlowResponse.data[0].transactionId.isNullOrEmpty()) {
                                for (i in addWorkFlowResponse.data.indices) {
                                    val step = needToAddStep[i]
                                    stepsListDao.updateOnlyWorkFlowId(
                                        it[i].id,
                                        step.villageId,
                                        step.id
                                    )
                                    step.workFlowId = it[0].id
                                    NudgeLogger.e(
                                        "SyncHelper",
                                        "callWorkFlowAPI stepsListDao.updateOnlyWorkFlowId before stepId: $step.stepId, it[0].id: ${it[0].id}, villageId: $step.villageId"
                                    )
                                }
                                NudgeLogger.e(
                                    "SyncHelper",
                                    "callWorkFlowAPI stepsListDao.updateOnlyWorkFlowId after"
                                )
                                delay(100)
                                needToAddStep.addAll(needToEditStep)
                                updateStepsToServer(needToAddStep)
                            }
                        }
                    }

                } else if(needToEditStep.size>0){
                    updateStepsToServer(needToEditStep)
                }

            }catch (ex:Exception){
                settingViewModel.onCatchError(ex, ApiType.WORK_FLOW_API)
//                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    private fun updateStepsToServer(needToEdiStep: MutableList<StepListEntity>) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val requestForStepUpdation = mutableListOf<EditWorkFlowRequest>()
            for (step in needToEdiStep) {
                requestForStepUpdation.add(
                    EditWorkFlowRequest(
                        step.workFlowId,
                        StepStatus.getStepFromOrdinal(step.isComplete)
                    )
                )
            }

            val responseForStepUpdation =
                apiService.editWorkFlow(requestForStepUpdation)

            NudgeLogger.e(
                "SyncHelper",
                "callWorkFlowAPI response: status: ${responseForStepUpdation.status}, message: ${responseForStepUpdation.message}, data: ${responseForStepUpdation.data} \n\n"
            )


            if (responseForStepUpdation.status.equals(SUCCESS, true)) {
                responseForStepUpdation.data?.let {

                    for(i in responseForStepUpdation.data.indices) {
                        val step = needToEdiStep[i]
                        stepsListDao.updateWorkflowId(
                            step.stepId,
                            step.workFlowId,
                            step.villageId,
                            step.status
                        )

                        NudgeLogger.e(
                            "SyncHelper",
                            "callWorkFlowAPI stepsListDao.updateWorkflowId after "
                        )
                        NudgeLogger.e(
                            "SyncHelper",
                            "callWorkFlowAPI stepsListDao.updateNeedToPost before stepId: $step.stepId"
                        )
                        stepsListDao.updateNeedToPost(step.id, step.villageId, false)
                        NudgeLogger.e(
                            "SyncHelper",
                            "callWorkFlowAPI stepsListDao.updateNeedToPost after stepId: $step.stepId"
                        )

                    }
                }
            }
            if (!responseForStepUpdation.lastSyncTime.isNullOrEmpty()) {
                updateLastSyncTime(
                    prefRepo,
                    responseForStepUpdation.lastSyncTime
                )
            }
        }
    }

    private fun getSizeToBeShown(size : Int) : String{
        var sizeToBeShown = ""
        if(size < 1024) {
            sizeToBeShown = "$size Bytes"
        } else if(size > 1024 && size < (1024*1024)) {
            val sizeInKB = size/1024
            sizeToBeShown = "$sizeInKB Bytes"
        } else if(size > 1024) {
            val sizeInMB = size/(1024*1024)
            sizeToBeShown = "$sizeInMB Bytes"
        }
        return sizeToBeShown
    }
}