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
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.utils.ACCEPTED
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BPC_VERIFICATION_STEP_ORDER
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_A_PDF_NAME
import com.patsurvey.nudge.utils.FORM_B_PDF_NAME
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_FORM_C_PAGE_COUNT
import com.patsurvey.nudge.utils.PREF_FORM_D_PAGE_COUNT
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_FORM_C_AND_D_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.compressImage
import com.patsurvey.nudge.utils.getFileNameFromURL
import com.patsurvey.nudge.utils.longToString
import com.patsurvey.nudge.utils.updateLastSyncTime
import com.patsurvey.nudge.utils.uriFromFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class FormPictureScreenViewModel @Inject constructor(
    val repository: FormPictureScreenRepository
): BaseViewModel() {

    lateinit var outputDirectory: File
    var cameraExecutor: ExecutorService

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
    val formsCClicked = mutableStateOf(0)
    val formsDClicked = mutableStateOf(0)

    val retakeImageIndex =
        mutableStateOf(-1)

    var photoUri: Uri = Uri.EMPTY
    var shouldShowPhoto = mutableStateOf(false)

    val pageItemClicked = mutableStateOf("")

//    { formName } _page_ ${ formPictureScreenViewModel.formDPageList.value.size + 1 }

    val imagePath = mutableStateOf("")

    val uri = mutableStateOf(Uri.EMPTY)

    var imagePathForCapture = ""
    var tempUri: Uri = Uri.EMPTY

//    init {
//        cameraExecutor = Executors.newSingleThreadExecutor()
//    }

    val villageEntity = mutableStateOf<VillageEntity?>(null)
    val formAAvailable = mutableStateOf(false)
    val formBAvailable = mutableStateOf(false)
    val showAPILoader = mutableStateOf(false)

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()
        setVillage(repository.prefRepo.getSelectedVillage().id)
        for (i in 1..5) {
            formCPageList.value = formCPageList.value.also {
                Log.d("FormPictureScreenViewModel", "init: ${getFormPathKey(getFormSubPath(FORM_C, i))}")
                val imagePath =
                    repository.prefRepo.getPref(getFormPathKey(getFormSubPath(FORM_C, i)), "")
                if (imagePath != "") {
                    it.add(it.size + 1)
                }
                Log.d("FormPictureScreenViewModel", "init: FORM_C -> ${it}")
            }
            formDPageList.value = formDPageList.value.also {
                Log.d("FormPictureScreenViewModel", "init: ${getFormPathKey(getFormSubPath(FORM_D, i))}")
                val imagePath =
                    repository.prefRepo.getPref(getFormPathKey(getFormSubPath(FORM_D, i)), "")
                if (imagePath != "") {
                    it.add(it.size + 1)
                }
                Log.d("FormPictureScreenViewModel", "init: FORM_D -> ${it}")
            }
        }
    }

    fun setVillage(villageId: Int) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            var village = repository.fetchVillageForLanguage(villageId)
            withContext(Dispatchers.Main) {
                villageEntity.value = village
            }
        }
    }

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
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath}")
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
        repository.prefRepo.savePref(getFormPathKey(formName)
            /*"${PREF_FORM_PATH}_${prefRepo.getSelectedVillage().name}_$formName"*/, formPath)
    }

    fun markVoEndorsementComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val existingList = repository.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            repository.markVOEndorsementStatusComplete(
                villageId = villageId,
                stepId = stepId,
                updatedCompletedStepsList = updatedCompletedStepsList
            )
        }
    }

    fun updateDidiVoEndorsementStatus() {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("FormPictureScreenViewModel", "updateDidiVoEndorsementStatus called")
            val didiList = repository.getAllDidisForVillage()
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
                    repository.updateBeneficiaryProcessStatus(didi.id, updatedStatus)

                    NudgeLogger.d("FormPictureScreenViewModel", "updateDidiVoEndorsementStatus-> didiDao.updateBeneficiaryProcessStatus  after = updatedStatus: $updatedStatus \n\n")

                } else if (didi.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(BeneficiaryProcessStatusModel("VO_ENDORSEMENT", "REJECTED"))

                    NudgeLogger.d("FormPictureScreenViewModel", "updateDidiVoEndorsementStatus-> didiDao.updateBeneficiaryProcessStatus  before = updatedStatus: $updatedStatus \n\n")

                    repository.updateBeneficiaryProcessStatus(didi.id, updatedStatus)

                    NudgeLogger.d("FormPictureScreenViewModel", "updateDidiVoEndorsementStatus-> didiDao.updateBeneficiaryProcessStatus  after = updatedStatus: $updatedStatus \n\n")

                } else {
                    repository.updateNeedToPostVO(
                        needsToPostVo = false,
                        didiId = didi.id,
                        villageId = didi.villageId
                    )
                }
            }
        }
    }

    fun saveVoEndorsementDate() {
        val currentTime = System.currentTimeMillis()
        repository.prefRepo.savePref(PREF_VO_ENDORSEMENT_COMPLETION_DATE_+repository.prefRepo.getSelectedVillage().id, currentTime)
    }

    override fun onServerError(error: ErrorModel?) {
        NudgeLogger.d("FormPictureScreenViewModel", "onServerError -> onServerError: message = ${error?.message}")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        NudgeLogger.d("FormPictureScreenViewModel", "onServerError -> onServerError: message = ${errorModel?.message}, api = ${errorModel?.apiName?.name}")
    }

    fun updateFormCImageCount(size: Int) {
        repository.prefRepo.savePref(PREF_FORM_C_PAGE_COUNT, size)
    }
    fun updateFormDImageCount(size: Int) {
        repository.prefRepo.savePref(PREF_FORM_D_PAGE_COUNT, size)
    }

    fun updateVoStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            var onFailCounter = 0
            NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork called")
            try {
                val needToPostDidiList = repository.getAllNeedToPostVoDidi(
                    needsToPostVo = true
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
                                         didi.serverId,
                                        StepType.VO_ENDROSEMENT.name,
                                        ACCEPTED,
                                        rankingEdit = false,
                                        name = didi.name,
                                        address = didi.address,
                                        guardianName = didi.guardianName,
                                        villageId = didi.villageId,
                                    )
                                )
                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> updateVoStatusRequest:" +
                                        " $updateVoStatusRequest \n\n")

                                val updateVoStatusResponse = repository.updateDidiRanking(
                                    updateVoStatusRequest
                                )

                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> " +
                                        "updateVoStatusResponse: status = ${updateVoStatusResponse.status}, message = ${updateVoStatusResponse.message}, data = ${updateVoStatusResponse.data.toString()}")

                                if (updateVoStatusResponse.status.equals(SUCCESS, true)) {
                                    if (updateVoStatusResponse.data?.get(0)?.transactionId?.isNullOrEmpty() != true) {
                                        NudgeLogger.d(
                                            "FormPictureScreenViewModel",
                                            "updateVoStatusToNetwork -> didiDao.updateNeedToPostVO before:" +
                                                    "didiId = ${didi.id}, villageId = ${didi.villageId}, needsToPostVo = false"
                                        )
                                        updateVoStatusResponse.data?.get(0)?.transactionId?.let { transitionId ->
                                            repository.updateDidiTransactionId(didi.id, transitionId)
                                        }

                                        /*repository.updateNeedToPostVO(
                                            needsToPostVo = false,
                                            didi.id,
                                            didi.villageId
                                        )*/

                                        NudgeLogger.d(
                                            "FormPictureScreenViewModel",
                                            "updateVoStatusToNetwork -> didiDao.updateNeedToPostVO after"
                                        )
                                    }

                                } else {
                                    NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> " +
                                            "updateVoStatusResponse: onFailed")
                                    onFailCounter = onFailCounter++
                                    networkCallbackListener.onFailed()
                                }
                                if (!updateVoStatusResponse.lastSyncTime.isNullOrEmpty()) {
                                    updateLastSyncTime(
                                        repository.prefRepo,
                                        updateVoStatusResponse.lastSyncTime
                                    )
                                }
                            } else if (it == DidiEndorsementStatus.REJECTED.ordinal) {
                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> didi.voEndorsementStatus: DidiEndorsementStatus.ENDORSED.ordinal \n\n")

                                val updateVoStatusRequest = listOf(
                                    EditDidiWealthRankingRequest(
                                        didi.serverId,
                                        StepType.VO_ENDROSEMENT.name,
                                        DidiEndorsementStatus.REJECTED.name,
                                        rankingEdit = false,
                                        name = didi.name,
                                        address = didi.address,
                                        guardianName = didi.guardianName,
                                        villageId = didi.villageId,
                                    )
                                )
                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> updateVoStatusRequest:" +
                                        " $updateVoStatusRequest \n\n")

                                val updateVoStatusResponse = repository.updateDidiRanking(
                                    updateVoStatusRequest
                                )
                                NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> " +
                                        "updateVoStatusResponse: status = ${updateVoStatusResponse.status}, message = ${updateVoStatusResponse.message}, data = ${updateVoStatusResponse.data.toString()}")


                                if (updateVoStatusResponse.status.equals(SUCCESS, true)) {
                                    if (updateVoStatusResponse.data?.get(0)?.transactionId?.isNullOrEmpty() != true) {
                                        NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> didiDao.updateNeedToPostVO before:" +
                                                "didiId = ${didi.id}, villageId = ${didi.villageId}, needsToPostVo = false")

                                        updateVoStatusResponse.data?.get(0)?.transactionId?.let { transitionId ->
                                            repository.updateDidiTransactionId(didi.id, transitionId)
                                        }

                                        /*repository.updateNeedToPostVO(
                                            needsToPostVo = false,
                                            didi.id,
                                            didi.villageId
                                        )*/


                                        NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> didiDao.updateNeedToPostVO after")
                                    }

                                } else {
                                    NudgeLogger.d("FormPictureScreenViewModel", "updateVoStatusToNetwork -> " +
                                            "updateVoStatusResponse: onFailed")
                                    onFailCounter = onFailCounter++
                                    networkCallbackListener.onFailed()
                                }

                                if (!updateVoStatusResponse.lastSyncTime.isNullOrEmpty()) {
                                    updateLastSyncTime(
                                        repository.prefRepo,
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
                val dbResponse=repository.getStepForVillage(villageId, stepId)
                NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> dbResponse = $dbResponse")

                val stepList = repository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> stepList = $stepList")

                if(dbResponse.workFlowId>0){
                    val primaryWorkFlowRequest = listOf(
                        EditWorkFlowRequest(stepList[stepList.map { it.orderNumber }.indexOf(5)].workFlowId,
                            StepStatus.COMPLETED.name, longToString(repository.prefRepo.getPref(
                                PREF_VO_ENDORSEMENT_COMPLETION_DATE_ +repository.prefRepo.getSelectedVillage().id,System.currentTimeMillis())),
                            villageId,
                            programsProcessId = stepList[stepList.map { it.orderNumber }
                                .indexOf(5)].id)
                    )
                    NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> primaryWorkFlowRequest = $primaryWorkFlowRequest")

                    val response = repository.editWorkFlow(
                        primaryWorkFlowRequest
                    )
                    NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> " +
                            "response: status = ${response.status}, " +
                            "message = ${response.message}, " +
                            "data = ${response.data.toString()}")


                    if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                repository.updateWorkflowId(
                                    stepId = stepList[stepList.map { it.orderNumber }.indexOf(5)].id,
                                    workflowId = stepList[stepList.map { it.orderNumber }
                                        .indexOf(5)].workFlowId,
                                    villageId=villageId,
                                   status =  it[0].status
                                )
                            }
                        NudgeLogger.d(
                            "FormPictureScreenViewModel",
                            "callWorkFlowAPI -> stepsListDao.updateNeedToPost before: stepId = ${
                                stepList[stepList.map { it.orderNumber }
                                    .indexOf(5)].id
                            }, villageId = $villageId, needToPost = false \n")

                        repository.updateNeedToPost(
                            stepId = stepId,
                            villageId = villageId,
                            needsToPost = false
                        )

                        NudgeLogger.d(
                            "FormPictureScreenViewModel",
                            "callWorkFlowAPI -> stepsListDao.updateNeedToPost after \n"
                        )
                        val bpcStep = stepList[stepList.map { it.orderNumber }
                            .indexOf(BPC_VERIFICATION_STEP_ORDER)]
                        if (bpcStep.workFlowId != 0) {
                            val bpcStepWorkFlowRequest = listOf(
                                EditWorkFlowRequest(
                                    bpcStep.workFlowId,
                                    StepStatus.INPROGRESS.name,
                                    System.currentTimeMillis().toString(),
                                    villageId,
                                    programsProcessId = bpcStep.id
                                )
                            )
                            val bpcStepWorkFlowResponse =
                                repository.editWorkFlow(bpcStepWorkFlowRequest)
                            if (bpcStepWorkFlowResponse.status.equals(SUCCESS, true)) {
                                bpcStepWorkFlowResponse.data?.let {
                                    repository.updateWorkflowId(
                                        stepId = stepList[stepList.map { it.orderNumber }.indexOf(
                                            BPC_VERIFICATION_STEP_ORDER
                                        )].id,
                                        workflowId = stepList[stepList.map { it.orderNumber }
                                            .indexOf(BPC_VERIFICATION_STEP_ORDER)].workFlowId,
                                        villageId,
                                        it[0].status
                                    )
                                }
                            }
                        } else {
                            val bpcStepWorkFlowRequest = listOf(
                                AddWorkFlowRequest(
                                    programId = bpcStep.programId,
                                    status = StepStatus.INPROGRESS.name,
                                    villageId = bpcStep.villageId,
                                    programsProcessId = bpcStep.stepId
                                )
                            )
                            val bpcStepWorkFlowResponse =
                                repository.addWorkFlow(bpcStepWorkFlowRequest)
                            if (bpcStepWorkFlowResponse.status.equals(SUCCESS, true)) {
                                bpcStepWorkFlowResponse.data?.let {
                                    repository.updateWorkflowId(
                                        stepId,
                                        it[0].id,
                                        villageId,
                                        it[0].status
                                    )
                                }
                            }
                        }
                        networkCallbackListener.onSuccess()
                    } else {
                        NudgeLogger.d("FormPictureScreenViewModel", "callWorkFlowAPI -> onFailed")
                        networkCallbackListener.onFailed()
                        onError(tag = "FormPictureScreenViewModel", "Error : ${response.message}")
                    }

                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        updateLastSyncTime(repository.prefRepo, response.lastSyncTime)
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
        return "${PREF_FORM_PATH}_${repository.prefRepo.getSelectedVillage().id}_${subPath}"
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
                        repository.prefRepo.getSelectedVillage().id.toString()
                    )
                val requestUserType =
                    RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        if (repository.prefRepo.isUserBPC()) USER_BPC else USER_CRP
                    )
                val response = repository.uploadDocument(formList = formList, villageId = requestVillageId, userType = requestUserType)
                if(response.status == SUCCESS){
                    repository.prefRepo.savePref(
                        PREF_NEED_TO_POST_FORM_C_AND_D_ + repository.prefRepo.getSelectedVillage().id,false)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                onCatchError(ex, ApiType.DOCUMENT_UPLOAD_API)
            }
        }
    }

    fun getImageFileName(context: Context, formName: String): File {
        val directory = getImagePath(context)
        return File(directory, "${formName}.png")
    }

    fun updateVoEndorsementEditFlag() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            repository.updateVoEndorsementEditFlag(voEndorsementEdit =  false)
        }
    }

    fun isFormAAvailableForVillage(context: Context, villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val formCFilePath =
                File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_A_PDF_NAME}_${villageId}.pdf")
            if (formCFilePath.isFile && formCFilePath.exists()) {
                withContext(Dispatchers.Main) {
                    formAAvailable.value = true
                }
            } else {
                if (repository.prefRepo.isUserBPC()) {
                    if (repository.poorDidiListDao.getAllPoorDidisForVillage(villageId = villageId).any { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit }) {
                        withContext(Dispatchers.Main) {
                            formAAvailable.value = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            formAAvailable.value = false
                        }
                    }
                } else {
                    if (repository.didiDao.getAllDidisForVillage(villageId = villageId).any { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit }
                    ) {
                        withContext(Dispatchers.Main) {
                            formAAvailable.value = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            formAAvailable.value = false
                        }
                    }
                }
            }
        }
    }
    fun isFormBAvailableForVillage(context: Context, villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val formBFilePath =
                File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_B_PDF_NAME}_${villageId}.pdf")
            if (formBFilePath.isFile && formBFilePath.exists()) {
                withContext(Dispatchers.Main) {
                    formBAvailable.value = true
                }
            } else {
                if (repository.prefRepo.isUserBPC()) {
                    if (repository.poorDidiListDao.getAllPoorDidisForVillage(villageId = villageId).any { it.forVoEndorsement == 1 && !it.patEdit }
                    ) {
                        withContext(Dispatchers.Main) {
                            formBAvailable.value = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            formBAvailable.value = false
                        }
                    }
                } else {
                    if (repository.didiDao.getAllDidisForVillage(villageId = villageId).any { it.forVoEndorsement == 1 && !it.patEdit }
                    ) {
                        withContext(Dispatchers.Main) {
                            formBAvailable.value = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            formBAvailable.value = false
                        }
                    }
                }
            }
        }

        /*val formBFilePath = File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_B_PDF_NAME}_${villageId}.pdf")
        formBAvailabe.value = formBFilePath.isFile && formBFilePath.exists()*/
        /*job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val filteredStepList = stepList.filter { it.name.equals("Pat Survey", true) }
            if (filteredStepList[0] != null) {
                formBAvailabe.value = filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
            } else {
                formBAvailabe.value = false
            }
        }*/
    }

    fun showLoaderForTime(time : Long){
        showAPILoader.value = true
        Timer().schedule(object : TimerTask(){
            override fun run() {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    withContext(Dispatchers.Main) {
                        showAPILoader.value = false
                    }
                }
            }
        },time)
    }


}