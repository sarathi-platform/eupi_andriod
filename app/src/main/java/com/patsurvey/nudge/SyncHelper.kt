package com.patsurvey.nudge

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nudge.core.json
import com.patsurvey.nudge.activities.settings.SettingViewModel
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.AddDidiRequest
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.model.request.AnswerDetailDTOListItem
import com.patsurvey.nudge.model.request.DeleteTolaRequest
import com.patsurvey.nudge.model.request.EditCohortRequest
import com.patsurvey.nudge.model.request.EditDidiRequest
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ACCEPTED
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.COMPLETED_STRING
import com.patsurvey.nudge.utils.DIDI_NOT_AVAILABLE
import com.patsurvey.nudge.utils.DIDI_REJECTED
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.ExclusionType
import com.patsurvey.nudge.utils.FLAG_RATIO
import com.patsurvey.nudge.utils.FLAG_WEIGHT
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.LOW_SCORE
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PAT_SURVEY
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_FORM_C_AND_D_
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_SOCIAL_MAPPING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_TRANSECT_WALK_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TolaStatus
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.VERIFIED_STRING
import com.patsurvey.nudge.utils.calculateScore
import com.patsurvey.nudge.utils.compressImage
import com.patsurvey.nudge.utils.findImageLocationFromPath
import com.patsurvey.nudge.utils.getFileNameFromURL
import com.patsurvey.nudge.utils.longToString
import com.patsurvey.nudge.utils.toWeightageRatio
import com.patsurvey.nudge.utils.updateLastSyncTime
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.Collections
import java.util.Timer
import java.util.TimerTask

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
    private val TAG="SyncHelper"
    private val pendingTimerTime : Long= 10000
    private var isPending = 0
    fun syncDataToServer(networkCallbackListener: NetworkCallbackListener){
        NudgeLogger.d("SyncHelper","sync progress started")
        addTolasToNetwork(networkCallbackListener)
    }

    fun startSyncTimer(networkCallbackListener: NetworkCallbackListener) {
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

                val checkTolaUpdateStatusRequest = TransactionIdRequest("",ids)
                NudgeLogger.d("SyncHelper","checkTolaUpdateStatus checkTolaUpdateStatusRequest request=> ${checkTolaUpdateStatusRequest.json()}")

                val response = apiService.getPendingStatus(checkTolaUpdateStatusRequest)
                NudgeLogger.d("SyncHelper","checkTolaUpdateStatus checkTolaUpdateStatusRequest response=> ${response.json()}")

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
            val didiList = didiDao.fetchAllPendingDidiNeedToDelete(
                DidiStatus.DIID_DELETED.ordinal,
                ""
            )
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
                val checkTolaDeleteStatusRequest = TransactionIdRequest("",ids)
                NudgeLogger.d("SyncHelper","checkTolaDeleteStatus checkTolaDeleteStatusRequest request=> ${checkTolaDeleteStatusRequest.json()}")
                val response = apiService.getPendingStatus(checkTolaDeleteStatusRequest)
                NudgeLogger.d("SyncHelper", "checkTolaDeleteStatus checkTolaDeleteStatusRequest response=> ${response.json()}")
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
                    uploadFormsCAndD(MyApplication.applicationContext())
                    callWorkFlowAPIForStep(5, networkCallbackListener)
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
                uploadFormsCAndD(MyApplication.applicationContext())
                callWorkFlowAPIForStep(5, networkCallbackListener)
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
                val checkDidiPatStatusRequest = TransactionIdRequest("PAT",ids)
                NudgeLogger.d("SyncHelper","checkDidiPatStatus checkDidiPatStatusRequest request=> ${checkDidiPatStatusRequest.json()}")
                val response = apiService.getPendingStatusForPat(checkDidiPatStatusRequest)
                NudgeLogger.d("SyncHelper","checkDidiPatStatus checkDidiPatStatusRequest response=> ${response.json()}")
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
                NudgeLogger.d(TAG,"checkDidiWealthStatus Request=> ${ids.json()}")
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                NudgeLogger.d(TAG,"checkDidiWealthStatus Response=> ${response.json()}")
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
            val didiList = didiDao.fetchAllPendingDidiNeedToUpdate(true, "")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                NudgeLogger.d(TAG,"checkUpdateDidiStatus Request=> ${ids.json()}")
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                NudgeLogger.d(TAG,"checkUpdateDidiStatus Response=> ${response.json()}")
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

                val checkAddDidiStatusRequest = TransactionIdRequest("",ids)
                NudgeLogger.d("SyncHelper", "checkAddDidiStatus checkAddDidiStatusRequest request => ${checkAddDidiStatusRequest.json()}")
                val response = apiService.getPendingStatus(checkAddDidiStatusRequest)
                NudgeLogger.d("SyncHelper", "checkAddDidiStatus checkAddDidiStatusRequest response => ${response.json()}")

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

    private fun uploadDidiImagesToServer(context : Context){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllDidiNeedsToPostImageWithLimit(true)
            NudgeLogger.d("Synchelper", "uploadDidiImage DidiList: ${didiList} :: Size: ${didiList.size}")
            if(didiList.isNotEmpty()){
                val imageFilePart = ArrayList<MultipartBody.Part>()
                val requestDidiId = ArrayList<RequestBody>()
                val requestUserType = ArrayList<RequestBody>()
                val requestLocation = ArrayList<RequestBody>()
                try {
                    for(didi in didiList) {
                        if(imageFilePart.size == 5) {
                            break
                        }
                        val path = findImageLocationFromPath(didi.localPath)
                        NudgeLogger.d("Synchelper", "uploadDidiImage: $didi.id :: $path[1]")
                        val uri = path[0]
                        NudgeLogger.d(
                            "Synchelper",
                            "uploadDidiImage Prev: ${uri}"
                        )
                        val compressedImageFile =
                            compressImage(uri.toString(), context, getFileNameFromURL(uri))
                        val requestFile = RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            File(compressedImageFile)
                        )
                        imageFilePart.add(MultipartBody.Part.createFormData(
                            "files",
                            File(compressedImageFile).name,
                            requestFile
                        ))
                        requestDidiId.add(RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            didi.serverId.toString()
                        ))
                        requestUserType.add(RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                        ))
                        requestLocation.add(RequestBody.create("multipart/form-data".toMediaTypeOrNull(), path.get(1)))
                        NudgeLogger.d(
                            "Synchelper",
                            "uploadDidiImage Details: ${requestDidiId[requestDidiId.size-1].contentType().toString()}"
                        )
                    }
                    val imageUploadResponse = apiService.uploadDidiBulkImage(
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
                        for(i in didiList.indices) {
                            if(i == 5)
                                break
                            didiDao.updateNeedsToPostImage(didiList[i].id, false)
                        }
//                        if(didiList.size>5) {
                            uploadDidiImagesToServer(context)
//                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    fun getFormPathKey(subPath: String,villageId: Int): String {
        //val subPath formPictureScreenViewModel.pageItemClicked.value
        //"${PREF_FORM_PATH}_${formPictureScreenViewModel.prefRepo.getSelectedVillage().name}_${subPath}"
        return "${PREF_FORM_PATH}_${villageId}_${subPath}"
    }

    fun uploadFormsCAndD(context: Context) {
        job = MyApplication.appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            val languageId = prefRepo.getAppLanguageId() ?: 2
            val villageList = villegeListDao.getAllVillages(languageId)
            for (village in villageList) {
                if (prefRepo.getPref(
                        PREF_NEED_TO_POST_FORM_C_AND_D_ + village.id,
                        false
                    )
                ) {
                    uploadFormCAndD(village.id, context)
                }
            }
        }
    }

    fun getFormSubPath(formName: String, pageNumber: Int): String {
        return "${formName}_page_$pageNumber"
    }

    fun uploadFormCAndD(villageId : Int,context: Context) {
        job = MyApplication.appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            val formList = arrayListOf<MultipartBody.Part>()
            val villageId = villegeListDao.getVillage(villageId).id
            try {
                val formCImageList = (mutableMapOf<String, String>())
                for (i in 0..4) {
                    formCImageList[getFormSubPath(FORM_C, i)] =
                        prefRepo.getPref(getFormPathKey(getFormSubPath(FORM_C, i), villageId), "").toString()
                }
                val formDImageList = (mutableMapOf<String, String>())
                for (i in 0..4) {
                    formDImageList[getFormSubPath(FORM_D, i)] =
                        prefRepo.getPref(getFormPathKey(getFormSubPath(FORM_D, i), villageId), "").toString()
                }
                if (formCImageList.isNotEmpty()) {
                    formCImageList.onEachIndexed { index, it ->
                        if (it.value.isNotEmpty()) {
//                        val pageKey = getFormPathKey(File(it.value).nameWithoutExtension)
                            val compressedFormC =
                                compressImage(it.value, context, getFileNameFromURL(it.value))
                            val requestFormC = RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                File(compressedFormC)
                            )
                            val formCFilePart = MultipartBody.Part.createFormData(
                                "formC",
                                File(compressedFormC).name,
                                requestFormC
                            )
//                              prefRepo.savePref(pageKey,File(compressedFormC).absolutePath)
                            formList.add(formCFilePart)
                        }

                    }
                }
                if (formDImageList.isNotEmpty()) {
                    formDImageList.onEachIndexed { index, it ->
                        if (it.value.isNotEmpty()) {
//                        val pageKey = getFormPathKey(File(it.value).nameWithoutExtension)
                            val compressedFormD =
                                compressImage(it.value, context, getFileNameFromURL(it.value))
                            val requestFormD = RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                File(compressedFormD)
                            )
                            val formDFilePart = MultipartBody.Part.createFormData(
                                "formD",
                                File(compressedFormD).name,
                                requestFormD
                            )
//                                prefRepo.savePref(pageKey,File(compressedFormD).absolutePath)
                            formList.add(formDFilePart)
                        }

                    }
                }

                val requestVillageId =
                    RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        villageId.toString()
                    )
                val requestUserType =
                    RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                    )
                val response = apiService.uploadDocument(formList, requestVillageId, requestUserType)
                if(response.status == SUCCESS){
                    prefRepo.savePref(
                        PREF_NEED_TO_POST_FORM_C_AND_D_ + villageId,false)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                settingViewModel.onCatchError(ex, ApiType.DOCUMENT_UPLOAD_API)
            }
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
                val checkTolaAddStatusRequest = TransactionIdRequest("",ids)
                NudgeLogger.d("SyncHelper","checkTolaAddStatus checkTolaAddStatusRequest request=> ${checkTolaAddStatusRequest.json()}")
                val response = apiService.getPendingStatus(checkTolaAddStatusRequest)
                NudgeLogger.d("SyncHelper","checkTolaAddStatus checkTolaAddStatusRequest response=> ${response.json()}")
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
                NudgeLogger.d("SyncHelper","addCohort Request=> ${Gson().toJson(jsonTola)}")
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
                NudgeLogger.d("SyncHelper","editCohort Request=> ${Gson().toJson(jsonTola)}")
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
                    val localDidiListForTola = didiDao.getDidisForTola(if (tola.serverId == 0) tola.id else tola.serverId)
                    if (localDidiListForTola.isEmpty()) {
                        jsonTola.add(
                            DeleteTolaRequest(
                                tola.serverId,
                                localModifiedDate = System.currentTimeMillis(),
                                tola.name,
                                tola.villageId,
                                tola.localUniqueId ?: ""
                            ).json()
                        )
                    }
                }
                NudgeLogger.d("SyncHelper","deleteTolaToNetwork -> tola need to post :${tolaList.size}")
                NudgeLogger.d("SyncHelper","deleteTolaToNetwork -> jsonTola :${jsonTola}")
                val response = apiService.deleteCohort(jsonTola)
                NudgeLogger.d("SyncHelper","deleteCohort Request=>${Gson().toJson(jsonTola)}")
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
        callWorkFlowAPIForStep(1, networkCallbackListener)
        Log.e("add didi","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                settingViewModel.stepOneSyncStatus.value = 3
                settingViewModel.stepTwoSyncStatus.value = 1
                settingViewModel.syncPercentage.value = 0.2f
            }
            val didiList = didiDao.fetchAllDidiNeedToAdd(true,"",0, DidiStatus.DIDI_ACTIVE.ordinal)
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
                NudgeLogger.d("SyncHelper","addDidis Request=>${Gson().toJson(jsonDidi)}")
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
            val didiList = didiDao.fetchAllDidiNeedToUpdate(true, "")
            if (didiList.isNotEmpty()) {
                val didiRequestList = arrayListOf<EditDidiRequest>()
                didiList.forEach { didi->
                    didiRequestList.add(EditDidiRequest(didi.serverId,didi.name,didi.address,didi.guardianName,didi.castId,didi.cohortId,didi.villageId,didi.cohortName))
                }
                NudgeLogger.d("SyncHelper","updateDidiToNetwork updateDidis Request=> ${didiRequestList.json()}")
                val response = apiService.updateDidis(didiRequestList)
                NudgeLogger.d("SyncHelper","updateDidiToNetwork updateDidis response=> ${response.json()}")
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

    private fun callWorkFlowAPIForStep(step: Int, networkCallbackListener: NetworkCallbackListener) {
        NudgeLogger.d("SyncHelper","callWorkFlowAPIForStep -> called")
//        val villageId = prefRepo.getSelectedVillage().id
        val stepList = stepsListDao.getAllStepsByOrder(step,true).sortedBy { it.orderNumber }
        NudgeLogger.e("SyncHelper","callWorkFlowAPIForStep called -> $stepList -> $step")
        callWorkFlowAPI(stepList, networkCallbackListener)
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
            val didiList = didiDao.getDidisToBeDeleted(
                activeStatus = DidiStatus.DIID_DELETED.ordinal,
                needsToPostDeleteStatus = true,
                transactionId = ""
            )
            val jsonDidi = JsonArray()
            if (didiList.isNotEmpty()) {
                for (didi in didiList) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("id", didi.serverId)
                    jsonDidi.add(jsonObject)
                }
                Log.e("tola need to post","$didiList.size")
                val response = apiService.deleteDidi(jsonDidi)
                NudgeLogger.d("SyncHelper","deleteDidi Request=> ${Gson().toJson(jsonDidi)}")
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
        callWorkFlowAPIForStep(2, networkCallbackListener)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                settingViewModel.stepTwoSyncStatus.value = 3
                settingViewModel.stepThreeSyncStatus.value = 1
                settingViewModel.syncPercentage.value = 0.4f
            }
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList = didiDao.getAllNeedToPostDidiRanking(true)
                    if (needToPostDidiList.isNotEmpty()) {
                        val didiWealthRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        val didiStepRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        needToPostDidiList.forEach { didi ->
                            didiWealthRequestList.add(EditDidiWealthRankingRequest(didi.serverId, StepType.WEALTH_RANKING.name,didi.wealth_ranking, rankingEdit = didi.rankingEdit, localModifiedDate = System.currentTimeMillis(),  name = didi.name,
                                address = didi.address,
                                guardianName = didi.guardianName,
                                villageId = didi.villageId,
                                deviceId = didi.localUniqueId
                            )
                            )
                            didiStepRequestList.add(EditDidiWealthRankingRequest(didi.serverId, StepType.SOCIAL_MAPPING.name,StepStatus.COMPLETED.name, rankingEdit = didi.rankingEdit, localModifiedDate = System.currentTimeMillis() ,
                                name = didi.name,
                                address = didi.address,
                                guardianName = didi.guardianName,
                                villageId = didi.villageId,
                                deviceId = didi.localUniqueId
                            )
                            )
                        }
                        didiWealthRequestList.addAll(didiStepRequestList)
                        NudgeLogger.d("SyncHelper","updateWealthRankingToNetwork updateDidiRanking Request=> ${Gson().toJson(didiWealthRequestList)}")
                        val updateWealthRankResponse = apiService.updateDidiRanking(didiWealthRequestList)
                        NudgeLogger.d("SyncHelper","updateWealthRankingToNetwork updateDidiRanking updateWealthRankResponse=> ${updateWealthRankResponse.json()}")
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
        callWorkFlowAPIForStep(3, networkCallbackListener)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.Main) {
                    delay(1000)
                    settingViewModel.stepThreeSyncStatus.value = 3
                    settingViewModel.stepFourSyncStatus.value = 1
                    settingViewModel.syncPercentage.value = 0.6f
                }
                val didiIDList= answerDao.fetchPATSurveyDidiList()
                uploadDidiImagesToServer(MyApplication.applicationContext())
                if(didiIDList.isNotEmpty()){
                    var optionList: List<OptionsItem>
                    val answeredDidiList: java.util.ArrayList<PATSummarySaveRequest> = arrayListOf()
                    var surveyId =0
                    var scoreDidiList: java.util.ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                    val userType=if((prefRepo.getPref(PREF_KEY_TYPE_NAME, "") ?: "").equals(BPC_USER_TYPE, true)) USER_BPC else USER_CRP
                    didiIDList.forEachIndexed { index, didi ->
                        NudgeLogger.d("SyncHelper", "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
                        calculateDidiScore(didiId = didi.id)
                        val didiEntity = didiDao.getDidi(didi.id)
                        delay(100)
                        didi.score = didiDao.getDidiScoreFromDb(didi.id)
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
                                            weight = it.weight,
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
                                                    optionValue = numOption.optionValue,
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
                    val passingMark = questionDao.getPassingScore()
                    var comment = BLANK_STRING
                        comment =
                            if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                PatSurveyStatus.NOT_AVAILABLE.name
                            } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                BLANK_STRING
                            } else {
                                if ((didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.NOT_STARTED.ordinal)
                                    || (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal)) {
                                    TYPE_EXCLUSION
                                } else {
                                    if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.COMPLETED.ordinal && didi.score < passingMark) {
                                        LOW_SCORE
                                    } else {
                                        BLANK_STRING
                                    }
                                }
                            }
                        scoreDidiList.add(
                            EditDidiWealthRankingRequest(
                                id = didi.serverId,
                                score = didi.score,
                                comment = comment,
                                type = if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                                result = if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                    DIDI_NOT_AVAILABLE
                                } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                    PatSurveyStatus.INPROGRESS.name
                                } else {
                                    if (didi.forVoEndorsement == 0 || didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal) DIDI_REJECTED else {
                                        if (prefRepo.isUserBPC())
                                            VERIFIED_STRING
                                        else
                                            COMPLETED_STRING
                                    }
                                },
                                rankingEdit = didi.patEdit,
                                shgFlag = SHGFlag.fromInt(didi.shgFlag).name,
                                ableBodiedFlag = AbleBodiedFlag.fromInt(didi.ableBodiedFlag).name,
                                name = didi.name,
                                address = didiEntity.address,
                                guardianName = didiEntity.guardianName,
                                villageId = didi.villageId,
                                deviceId = didiEntity.localUniqueId
                            )
                        )
                        val stateId = villegeListDao.getVillage(didi.villageId).stateId
                        val patSummarySaveRequest = PATSummarySaveRequest(
                            villageId = didi.villageId,
                            surveyId = surveyId,
                            cohortName = didiEntity.cohortName,
                            beneficiaryAddress = didiEntity.address,
                            guardianName = didiEntity.guardianName,
                            beneficiaryId = didi.serverId,
                            languageId = prefRepo.getAppLanguageId() ?: 2,
                            stateId = stateId,
                            totalScore = didi.score,
                            userType = userType,
                            beneficiaryName = didi.name,
                            answerDetailDTOList = qList,
                            patSurveyStatus = didi.patSurveyStatus,
                            section2Status = didi.section2Status,
                            section1Status = didi.section1Status,
                            shgFlag = didi.shgFlag,
                            patExclusionStatus = didi.patExclusionStatus ?: 0
                        )
                        NudgeLogger.d(
                            "SyncHelper",
                            "savePATSummeryToServer patSummarySaveRequest: ${patSummarySaveRequest.json()}"
                        )

                        answeredDidiList.add(
                            patSummarySaveRequest
                        )
                }
                if (answeredDidiList.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        NudgeLogger.d(
                        "SyncHelper",
                        "savePATSummeryToServer patSummarySaveRequest Request=>: ${answeredDidiList.json()}"
                    )
                        val saveAPIResponse = apiService.savePATSurveyToServer(answeredDidiList)
                        NudgeLogger.d("SyncHelper","savePATSummeryToServer patSummarySaveRequest response=> ${saveAPIResponse.json()}")
                        if (saveAPIResponse.status.equals(SUCCESS, true)) {
                            if (saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                didiIDList.forEach { didiItem ->
                                    didiDao.updateNeedToPostPAT(
                                        false,
                                        didiItem.id
                                    )
                                }
                                withContext(Dispatchers.Main) {
                                    delay(1000)
                                    syncPercentage.value = 0.8f
                                }
                                checkDidiPatStatus(networkCallbackListener)
                            } else {
                                for (i in didiIDList.indices) {
                                    saveAPIResponse.data?.get(i)?.let {
                                        didiDao.updateDidiTransactionId(
                                            didiIDList[i].id,
                                            it.transactionId
                                        )
                                    }
                                    didiDao.updateDidiNeedToPostPat(didiIDList[i].id, true)
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
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onFailed()
                            }
                        }
                        if (!saveAPIResponse.lastSyncTime.isNullOrEmpty()) {
                            updateLastSyncTime(prefRepo, saveAPIResponse.lastSyncTime)
                        }
                    }
                } else {
                    checkDidiPatStatus(networkCallbackListener)
                }
            } else {
                checkDidiPatStatus(networkCallbackListener)
            }
        } catch (ex: Exception) {
            withContext(Dispatchers.Main) {
                networkCallbackListener.onFailed()
                settingViewModel.onCatchError(ex, ApiType.CRP_PAT_SAVE_ANSWER_SUMMARY)
            }
            ex.printStackTrace()
        }
    }
}

    private fun savePatScoreToServer(scoreDidiList: java.util.ArrayList<EditDidiWealthRankingRequest>) {
        if(scoreDidiList.isNotEmpty()) {
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                NudgeLogger.d("SyncHelper","savePatScoreToServer updateDidiScore Request=> ${scoreDidiList.json()}")
                val updateDidiScoreResponse = apiService.updateDidiScore(scoreDidiList)
                NudgeLogger.d("SyncHelper","savePatScoreToServer updateDidiScore updateDidiScoreResponse=> ${updateDidiScoreResponse.json()}")
            }
        }
    }

    fun updateVoStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        callWorkFlowAPIForStep(4, networkCallbackListener)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.Main) {
                    delay(1000)
                    settingViewModel.stepFifthSyncStatus.value = 1
                    settingViewModel.stepFourSyncStatus.value = 3
                    settingViewModel.syncPercentage.value = 0.8f
                }
                withContext(Dispatchers.IO){
                    val needToPostDidiList = didiDao.fetchAllVONeedToPostStatusDidi(
                        needsToPostVo = true,
                        transactionId = ""
                    )
                    if(needToPostDidiList.isNotEmpty()){
                        val didiRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        needToPostDidiList.forEach { didi->
                            didi.voEndorsementStatus.let {
                                if (it == DidiEndorsementStatus.ENDORSED.ordinal) {
                                    didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId,StepType.VO_ENDROSEMENT.name, ACCEPTED,
                                        localModifiedDate = System.currentTimeMillis(), rankingEdit = didi.voEndorsementEdit,  name = didi.name,
                                        address = didi.address,
                                        guardianName = didi.guardianName,
                                        villageId = didi.villageId,
                                        deviceId = didi.localUniqueId
                                    )
                                    )
                                } else if (it == DidiEndorsementStatus.REJECTED.ordinal) {
                                    didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId,StepType.VO_ENDROSEMENT.name, DidiEndorsementStatus.REJECTED.name,
                                        localModifiedDate = System.currentTimeMillis(), rankingEdit = didi.voEndorsementEdit,  name = didi.name,
                                        address = didi.address,
                                        guardianName = didi.guardianName,
                                        villageId = didi.villageId,
                                        deviceId = didi.localUniqueId
                                    )
                                    )
                                }
                            }
                        }
                        NudgeLogger.d("SyncHelper","updateVoStatusToNetwork Request=> ${Gson().toJson(didiRequestList)}")
                        val updateWealthRankResponse=apiService.updateDidiRanking(didiRequestList)
                        NudgeLogger.d("SyncHelper","updateVoStatusToNetwork updateWealthRankResponse=> ${updateWealthRankResponse.json()}")
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
            val didiList = didiDao.fetchAllDidiNeedToPost(true, "", 0)
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
                        localModifiedDate = System.currentTimeMillis(),
                        name = didi.name,
                        address = didi.address,
                        guardianName = didi.guardianName,
                        villageId = didi.villageId,
                        deviceId = didi.localUniqueId
                    ).toJson()
                    )
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

    fun callWorkFlowAPI(steps: List<StepListEntity>, networkCallbackListener: NetworkCallbackListener){
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
                            StepStatus.getStepFromOrdinal(step.isComplete),
                            villageId = step.villageId,
                            programsProcessId = step.id
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
                    NudgeLogger.d("SyncHelper","addWorkFlow Request=> ${Gson().toJson(Collections.unmodifiableList(addWorkFlowRequest))}")
                    val addWorkFlowResponse = apiService.addWorkFlow(Collections.unmodifiableList(addWorkFlowRequest))
                    NudgeLogger.d("SyncHelper","callWorkFlowAPI response: status: ${addWorkFlowResponse.status}, message: ${addWorkFlowResponse.message}, data: ${addWorkFlowResponse.data?.json()} \n\n")

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
                                    NudgeLogger.d(
                                        "SyncHelper",
                                        "callWorkFlowAPI stepsListDao.updateOnlyWorkFlowId before stepId: $step.stepId, it[0].id: ${it[0].id}"
                                    )
                                }
                                NudgeLogger.d(
                                    "SyncHelper",
                                    "callWorkFlowAPI stepsListDao.updateOnlyWorkFlowId after"
                                )
                                delay(100)
                                needToAddStep.addAll(needToEditStep)
                                updateStepsToServer(needToAddStep, networkCallbackListener)
                            }
                        }
                    } else {
                        settingViewModel.onCatchError(ApiResponseFailException(addWorkFlowResponse.message), ApiType.ADD_WORK_FLOW_API)
                        NudgeLogger.e("SyncHelper",
                            "callWorkFlowAPI ApiResponseFailException: step -> ${steps.json()}")
                        networkCallbackListener.onFailed()
                    }

                } else if(needToEditStep.size>0){
                    updateStepsToServer(needToEditStep, networkCallbackListener)
                }

            } catch (ex:Exception){
                settingViewModel.onCatchError(ex, ApiType.ADD_WORK_FLOW_API)
                NudgeLogger.e("SyncHelper",
                    "callWorkFlowAPI exception = step -> ${steps.json()}")
                networkCallbackListener.onFailed()
//                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    private fun updateStepsToServer(needToEdiStep: MutableList<StepListEntity>, networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val requestForStepUpdation = mutableListOf<EditWorkFlowRequest>()
                for (step in needToEdiStep) {
                    var stepCompletionDate = BLANK_STRING
                    if(step.isComplete == StepStatus.COMPLETED.ordinal){
                        if(step.id == 40){
                            stepCompletionDate =longToString(prefRepo.getPref(
                                PREF_TRANSECT_WALK_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                        }

                        if(step.id == 41){
                            stepCompletionDate =longToString(prefRepo.getPref(
                                PREF_SOCIAL_MAPPING_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                        }

                        if(step.id == 46){
                            stepCompletionDate =longToString(prefRepo.getPref(
                                PREF_WEALTH_RANKING_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                        }

                        if(step.id == 43){
                            stepCompletionDate =longToString(prefRepo.getPref(
                                PREF_PAT_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                        }
                        if(step.id == 44){
                            stepCompletionDate =longToString(prefRepo.getPref(
                                PREF_VO_ENDORSEMENT_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                        }
                    }

                    requestForStepUpdation.add(
                        EditWorkFlowRequest(
                            step.workFlowId,
                            StepStatus.getStepFromOrdinal(step.isComplete),
                            stepCompletionDate,
                            villageId = step.villageId,
                            programsProcessId = step.id
                        )
                    )
                }
                NudgeLogger.d("SyncHelper","editWorkFlow Request=> ${Gson().toJson(requestForStepUpdation)}")
                val responseForStepUpdation =
                    apiService.editWorkFlow(requestForStepUpdation)

                NudgeLogger.e(
                    "SyncHelper",
                    "callWorkFlowAPI response: status: ${responseForStepUpdation.status}, message: ${responseForStepUpdation.message}, data: ${responseForStepUpdation.data?.json()} \n\n"
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
                    if (!responseForStepUpdation.lastSyncTime.isNullOrEmpty()) {
                        updateLastSyncTime(
                            prefRepo,
                            responseForStepUpdation.lastSyncTime
                        )
                    }
                } else {
                    settingViewModel.onCatchError(ApiResponseFailException(responseForStepUpdation.message), ApiType.WORK_FLOW_API)
                    NudgeLogger.e("SyncHelper",
                        "callWorkFlowAPI ApiResponseFailException: step -> ${needToEdiStep.json()}")
                    networkCallbackListener.onFailed()
                }

            } catch (ex: Exception) {
                settingViewModel.onCatchError(ex, ApiType.WORK_FLOW_API)
                NudgeLogger.e("SyncHelper",
                    "callWorkFlowAPI exception = step -> ${needToEdiStep.json()}")
                networkCallbackListener.onFailed()
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

    private fun calculateDidiScore(didiId: Int) {
        NudgeLogger.d("SyncHelper", "calculateDidiScore didiId: ${didiId}")
        var passingMark = 0
        var isDidiAccepted = false
        var comment = LOW_SCORE
//        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val _inclusiveQueList = answerDao.getAllInclusiveQues(didiId = didiId)
            if (_inclusiveQueList.isNotEmpty()) {
                var totalWightWithoutNumQue = answerDao.getTotalWeightWithoutNumQues(didiId)
                NudgeLogger.d(
                    "PatSectionSummaryViewModel",
                    "calculateDidiScore: $totalWightWithoutNumQue"
                )
                val numQueList =
                    _inclusiveQueList.filter { it.type == QuestionType.Numeric_Field.name }
                if (numQueList.isNotEmpty()) {
                    numQueList.forEach { answer ->
                        val numQue = questionDao.getQuestion(answer.questionId)
                        passingMark = numQue.surveyPassingMark ?: 0
                        if (numQue.questionFlag?.equals(FLAG_WEIGHT, true) == true) {
                            val weightList = toWeightageRatio(numQue.json.toString())
                            if (weightList.isNotEmpty()) {
                                val newScore = calculateScore(
                                    weightList,
                                    answer.totalAssetAmount?.toDouble() ?: 0.0,
                                    false
                                )
                                totalWightWithoutNumQue += newScore
                                NudgeLogger.d(
                                    "PatSectionSummaryViewModel",
                                    "calculateDidiScore: totalWightWithoutNumQue += newScore -> $totalWightWithoutNumQue"
                                )
                            }
                        } else if (numQue.questionFlag?.equals(FLAG_RATIO, true) == true) {
                            val ratioList = toWeightageRatio(numQue.json.toString())
                            val newScore = calculateScore(
                                ratioList,
                                answer.totalAssetAmount?.toDouble() ?: 0.0,
                                true
                            )
                            totalWightWithoutNumQue += newScore
                            NudgeLogger.d(
                                "PatSectionSummaryViewModel",
                                "calculateDidiScore: for Flag FLAG_RATIO totalWightWithoutNumQue += newScore -> $totalWightWithoutNumQue"
                            )
                        }
                    }
                }
                // TotalScore


                if (totalWightWithoutNumQue >= passingMark) {
                    isDidiAccepted = true
                    comment = BLANK_STRING
                    didiDao.updateVOEndorsementDidiStatus(
                        prefRepo.getSelectedVillage().id,
                        didiId,
                        1
                    )
                } else {
                    isDidiAccepted = false
                    didiDao.updateVOEndorsementDidiStatus(
                        prefRepo.getSelectedVillage().id,
                        didiId,
                        0
                    )
                }
                NudgeLogger.d("SyncHelper", "calculateDidiScore totalWightWithoutNumQue: $totalWightWithoutNumQue")
                didiDao.updateDidiScore(
                    score = totalWightWithoutNumQue,
                    comment = comment,
                    didiId = didiId,
                    isDidiAccepted = isDidiAccepted
                )
                /*if (prefRepo.isUserBPC()) {
                    bpcSelectedDidiDao.updateSelDidiScore(
                        score = totalWightWithoutNumQue,
                        comment = comment,
                        didiId = didiId,
                    )
                }*/
            } else {
                NudgeLogger.d("SyncHelper", "calculateDidiScore totalWightWithoutNumQue: ${0.0}")
                didiDao.updateDidiScore(
                    score = 0.0,
                    comment = TYPE_EXCLUSION,
                    didiId = didiId,
                    isDidiAccepted = false
                )
                /*if (prefRepo.isUserBPC()) {
                    bpcSelectedDidiDao.updateSelDidiScore(
                        score = 0.0,
                        comment = TYPE_EXCLUSION,
                        didiId = didiId,
                    )
                }*/
            }
//                didiDao.updateModifiedDateServerId(System.currentTimeMillis(), didiId)
//        }
    }
}