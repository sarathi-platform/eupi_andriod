package com.patsurvey.nudge.activities.ui.vo_endorsement

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ACCEPTED
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_FORM_C_PAGE_COUNT
import com.patsurvey.nudge.utils.PREF_FORM_D_PAGE_COUNT
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.PREF_SOCIAL_MAPPING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
import com.patsurvey.nudge.utils.compressImage
import com.patsurvey.nudge.utils.getFileNameFromURL
import com.patsurvey.nudge.utils.longToString
import com.patsurvey.nudge.utils.updateLastSyncTime
import com.patsurvey.nudge.utils.uriFromFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class FormPictureScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val didiDao: DidiDao,
    val apiService: ApiService
): BaseViewModel() {

    lateinit var outputDirectory: File
    lateinit var cameraExecutor: ExecutorService

    val shouldShowCamera = mutableStateOf(Pair<String, Boolean>("", false))

    val formCPageList =
        mutableStateOf(mutableListOf<Int>())

    val formDPageList =
        mutableStateOf(mutableListOf<Int>())

    val formCImageList =
        mutableStateOf(mutableMapOf<String, String>())


    val formDImageList =
        mutableStateOf(mutableMapOf<String, String>())


    val formsClicked = mutableStateOf(0)

    val retakeImageIndex =
        mutableStateOf(-1)

    lateinit var photoUri: Uri
    var shouldShowPhoto = mutableStateOf(false)

    val pageItemClicked = mutableStateOf("")

//    { formName } _page_ ${ formPictureScreenViewModel.formDPageList.value.size + 1 }

    val imagePath = mutableStateOf("")

    val uri = mutableStateOf (Uri.EMPTY)
//    init {
//        cameraExecutor = Executors.newSingleThreadExecutor()
//    }

    fun setUri(context: Context) {
        uri.value = uriFromFile(context, File(imagePath.value))
    }

    fun setCameraExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun setUpOutputDirectory(activity: MainActivity) {
//        outputDirectory = /*getOutputDirectory(activity)*/ getImagePath(activity)
        outputDirectory = getOutputDirectory(activity)
    }

    private fun getImagePath(context: Context): File {
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}")
    }

    private fun getOutputDirectory(activity: MainActivity): File {
        val mediaDir = activity.externalMediaDirs.firstOrNull()?.let { file ->
            File(file, activity.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else activity.filesDir
    }

    fun saveFormPath(formPath: String, formName: String){
        Log.d("FormPictureScreen_saveFormPath", "prefKey: ${PREF_FORM_PATH}_${formName}, formPath: $formPath ")
        prefRepo.savePref(getFormPathKey(formName)
            /*"${PREF_FORM_PATH}_${prefRepo.getSelectedVillage().name}_$formName"*/, formPath)
    }

    fun markVoEndorsementComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val existingList = villageListDao.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
            stepsListDao.markStepAsCompleteOrInProgress(stepId, StepStatus.COMPLETED.ordinal,villageId)
            stepsListDao.updateNeedToPost(stepId, villageId, true)
            val stepDetails=stepsListDao.getStepForVillage(villageId, stepId)
            if(stepDetails.orderNumber<stepsListDao.getAllSteps().size){
                stepsListDao.markStepAsInProgress((stepDetails.orderNumber+1),
                    StepStatus.INPROGRESS.ordinal,villageId)
                stepsListDao.updateNeedToPost(stepDetails.id, villageId, true)
            }
            prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", true)
        }
    }

    fun updateDidiVoEndorsementStatus() {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("FormPictureScreenViewModel", "updateDidiVoEndorsementStatus called")
            val didiList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
            didiList.forEach {didi ->
                NudgeLogger.d("FormPictureScreenViewModel", "updateDidiVoEndorsementStatus didi: $didi \n\n")
                if (didi.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(BeneficiaryProcessStatusModel("VO_ENDORSEMENT", "ACCEPTED"))
                    NudgeLogger.d("FormPictureScreenViewModel", "updateDidiVoEndorsementStatus-> didiDao.updateBeneficiaryProcessStatus  before = updatedStatus: $updatedStatus \n\n")
                    didiDao.updateBeneficiaryProcessStatus(didi.id, updatedStatus)

                    NudgeLogger.d("FormPictureScreenViewModel", "updateDidiVoEndorsementStatus-> didiDao.updateBeneficiaryProcessStatus  after = updatedStatus: $updatedStatus \n\n")

                } else if (didi.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(BeneficiaryProcessStatusModel("VO_ENDORSEMENT", "REJECTED"))

                    NudgeLogger.d("FormPictureScreenViewModel", "updateDidiVoEndorsementStatus-> didiDao.updateBeneficiaryProcessStatus  before = updatedStatus: $updatedStatus \n\n")

                    didiDao.updateBeneficiaryProcessStatus(didi.id, updatedStatus)

                    NudgeLogger.d("FormPictureScreenViewModel", "updateDidiVoEndorsementStatus-> didiDao.updateBeneficiaryProcessStatus  after = updatedStatus: $updatedStatus \n\n")

                } else {
                    didiDao.updateNeedToPostVO(false, didiId = didi.id, didi.villageId)
                }
            }
        }
    }

    fun saveVoEndorsementDate() {
        val currentTime = System.currentTimeMillis()
        prefRepo.savePref(PREF_VO_ENDORSEMENT_COMPLETION_DATE_+prefRepo.getSelectedVillage().id, currentTime)
    }

    override fun onServerError(error: ErrorModel?) {
        NudgeLogger.d("FormPictureScreenViewModel", "onServerError -> onServerError: message = ${error?.message}")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        NudgeLogger.d("FormPictureScreenViewModel", "onServerError -> onServerError: message = ${errorModel?.message}, api = ${errorModel?.apiName?.name}")
    }

    fun updateFormCImageCount(size: Int) {
        prefRepo.savePref(PREF_FORM_C_PAGE_COUNT, size)
    }
    fun updateFormDImageCount(size: Int) {
        prefRepo.savePref(PREF_FORM_D_PAGE_COUNT, size)
    }

    fun updateVoStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            var onFailCounter = 0
            NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork called")
            try {
                val needToPostDidiList = didiDao.getAllNeedToPostVoDidi(
                    needsToPostVo = true,
                    villageId = prefRepo.getSelectedVillage().id
                )

                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> needToPostDidiList: $needToPostDidiList \n\n")

                if (needToPostDidiList.isNotEmpty()) {
                    needToPostDidiList.forEach { didi ->
                        NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> didi: $didi \n\n")
                        didi.voEndorsementStatus.let {
                            if (it == DidiEndorsementStatus.ENDORSED.ordinal) {
                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> didi.voEndorsementStatus: DidiEndorsementStatus.ENDORSED.ordinal \n\n")

                                val updateVoStatusRequest = listOf(
                                    EditDidiWealthRankingRequest(
                                        if (didi.serverId == 0) didi.id else didi.serverId,
                                        StepType.VO_ENDROSEMENT.name,
                                        ACCEPTED
                                    )
                                )
                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> updateVoStatusRequest:" +
                                        " $updateVoStatusRequest \n\n")

                                val updateVoStatusResponse = apiService.updateDidiRanking(
                                    updateVoStatusRequest
                                )

                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> " +
                                        "updateVoStatusResponse: status = ${updateVoStatusResponse.status}, message = ${updateVoStatusResponse.message}, data = ${updateVoStatusResponse.data.toString()}")

                                if (updateVoStatusResponse.status.equals(SUCCESS, true)) {
                                    NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> didiDao.updateNeedToPostVO before:" +
                                            "didiId = ${didi.id}, villageId = ${didi.villageId}, needsToPostVo = false")
                                    didiDao.updateNeedToPostVO(needsToPostVo = false, didi.id, didi.villageId)

                                    NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> didiDao.updateNeedToPostVO after")

                                } else {
                                    NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> " +
                                            "updateVoStatusResponse: onFailed")
                                    onFailCounter = onFailCounter++
                                    networkCallbackListener.onFailed()
                                }
                                if (!updateVoStatusResponse.lastSyncTime.isNullOrEmpty()) {
                                    updateLastSyncTime(
                                        prefRepo,
                                        updateVoStatusResponse.lastSyncTime
                                    )
                                }
                            } else if (it == DidiEndorsementStatus.REJECTED.ordinal) {
                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> didi.voEndorsementStatus: DidiEndorsementStatus.ENDORSED.ordinal \n\n")

                                val updateVoStatusRequest = listOf(
                                    EditDidiWealthRankingRequest(
                                        if (didi.serverId == 0) didi.id else didi.serverId,
                                        StepType.VO_ENDROSEMENT.name,
                                        DidiEndorsementStatus.REJECTED.name
                                    )
                                )
                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> updateVoStatusRequest:" +
                                        " $updateVoStatusRequest \n\n")

                                val updateVoStatusResponse = apiService.updateDidiRanking(
                                    updateVoStatusRequest
                                )
                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> " +
                                        "updateVoStatusResponse: status = ${updateVoStatusResponse.status}, message = ${updateVoStatusResponse.message}, data = ${updateVoStatusResponse.data.toString()}")


                                if (updateVoStatusResponse.status.equals(SUCCESS, true)) {
                                    NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> didiDao.updateNeedToPostVO before:" +
                                            "didiId = ${didi.id}, villageId = ${didi.villageId}, needsToPostVo = false")

                                    didiDao.updateNeedToPostVO(false, didi.id, didi.villageId)

                                    NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> didiDao.updateNeedToPostVO after")
                                } else {
                                    NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> " +
                                            "updateVoStatusResponse: onFailed")
                                    onFailCounter = onFailCounter++
                                    networkCallbackListener.onFailed()
                                }

                                if (!updateVoStatusResponse.lastSyncTime.isNullOrEmpty()) {
                                    updateLastSyncTime(
                                        prefRepo,
                                        updateVoStatusResponse.lastSyncTime
                                    )
                                }
                            }
                        }
                    }
                    networkCallbackListener.onSuccess()
                } else {
                    NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> onSuccess")
                    networkCallbackListener.onSuccess()
                }
            } catch (ex: Exception) {
                onCatchError(ex, ApiType.DIDI_EDIT_API)
                networkCallbackListener.onFailed()
                onError("FormPictureScreenViewModel", "updateVoStatusToNetwork-> onError: ${ex.message}, \n${ex.stackTrace}")
            }
        }
    }

    fun callWorkFlowAPI(villageId: Int,stepId: Int, networkCallbackListener: NetworkCallbackListener){
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> called")
            try {
                val dbResponse=stepsListDao.getStepForVillage(villageId, stepId)
                NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> dbResponse = $dbResponse")

                val stepList = stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> stepList = $stepList")

                if(dbResponse.workFlowId>0){
                    val primaryWorkFlowRequest = listOf(
                        EditWorkFlowRequest(stepList[stepList.map { it.orderNumber }.indexOf(5)].workFlowId,
                            StepStatus.COMPLETED.name, longToString(prefRepo.getPref(
                                PREF_VO_ENDORSEMENT_COMPLETION_DATE_ +prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                        )
                    )
                    NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> primaryWorkFlowRequest = $primaryWorkFlowRequest")

                    val response = apiService.editWorkFlow(
                        primaryWorkFlowRequest
                    )
                    NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> " +
                            "response: status = ${response.status}, " +
                            "message = ${response.message}, " +
                            "data = ${response.data.toString()}")


                    if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(
                                    stepId = stepList[stepList.map { it.orderNumber }.indexOf(5)].id,
                                    workflowId = stepList[stepList.map { it.orderNumber }.indexOf(5)].workFlowId,
                                    villageId,
                                    it[0].status)
                            }
                        NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost before: stepId = ${stepList[stepList.map { it.orderNumber }
                            .indexOf(5)].id}, villageId = $villageId, needToPost = false \n")

                        stepsListDao.updateNeedToPost(stepId, villageId, false)

                        NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost after \n")
                        networkCallbackListener.onSuccess()
                    } else {
                        NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> onFailed")
                        networkCallbackListener.onFailed()
                        onError(tag = "FormPictureScreenViewModel", "Error : ${response.message}")
                    }

                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        updateLastSyncTime(prefRepo, response.lastSyncTime)
                    }
                }

            }catch (ex:Exception){
                NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> onFailed")
                networkCallbackListener.onFailed()
                onError(tag = "FormPictureScreenViewModel", "Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.WORK_FLOW_API)
            }
        }
    }


    fun getFormPathKey(subPath: String): String {
        //val subPath formPictureScreenViewModel.pageItemClicked.value
        //"${PREF_FORM_PATH}_${formPictureScreenViewModel.prefRepo.getSelectedVillage().name}_${subPath}"
        return "${PREF_FORM_PATH}_${prefRepo.getSelectedVillage().name}_${subPath}"
    }

    fun getFormSubPath(formName: String, pageNumber: Int): String {
        return "${formName}_page_$pageNumber"
    }

    fun uploadFormsCAndD(context: Context) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            val formList = arrayListOf<MultipartBody.Part>()
            try {
                if (formCImageList.value.isNotEmpty()) {
                    formCImageList.value.onEachIndexed { index, it ->
                        if (it.value.isNotEmpty()) {
                            val pageKey = getFormPathKey(File(it.value).nameWithoutExtension)
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
                if (formDImageList.value.isNotEmpty()) {
                    formDImageList.value.onEachIndexed { index, it ->
                        if (it.value.isNotEmpty()) {
                            val pageKey = getFormPathKey(File(it.value).nameWithoutExtension)
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
                        prefRepo.getSelectedVillage().id.toString()
                    )
                val requestUserType =
                    RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        if (prefRepo.isUserBPC()) USER_BPC else USER_CRP
                    )
                apiService.uploadDocument(formList, requestVillageId, requestUserType)
            } catch (ex: Exception) {
                ex.printStackTrace()
                onCatchError(ex, ApiType.DOCUMENT_UPLOAD_API)
            }
        }
    }

}