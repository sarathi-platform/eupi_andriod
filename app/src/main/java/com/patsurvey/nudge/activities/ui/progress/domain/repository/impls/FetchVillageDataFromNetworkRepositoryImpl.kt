package com.patsurvey.nudge.activities.ui.progress.domain.repository.impls

import com.nrlm.baselinesurvey.PREF_PROGRAM_NAME
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchVillageDataFromNetworkRepository
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DOUBLE_ZERO
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_SOCIAL_MAPPING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_TRANSECT_WALK_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.findCompleteValue
import com.patsurvey.nudge.utils.formatRatio
import com.patsurvey.nudge.utils.stringToDouble
import javax.inject.Inject

class FetchVillageDataFromNetworkRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val casteListDao: CasteListDao,
    private val villageListDao: VillageListDao,
    private val stepListDao: StepsListDao,
    private val didiDao: DidiDao,
    private val tolaDao: TolaDao,
    private val questionListDao: QuestionListDao,
    private val answerDao: AnswerDao,
    private val numericAnswerDao: NumericAnswerDao,
) : FetchVillageDataFromNetworkRepository {

    private val LOGGER_TAG = FetchVillageDataFromNetworkRepositoryImpl::class.java.simpleName

    override suspend fun fetchStepListForVillageFromNetwork(villageId: Int) {
        try {
            val response = apiService.getStepsList(villageId)
            if (response.status.equals(SUCCESS, true)) {
                response.data?.let { it ->
                    if (it.stepList.isNotEmpty()) {
                        it.stepList.forEach { step ->
                            step.villageId = villageId
                            step.isComplete = findCompleteValue(step.status).ordinal

                            saveStepCompletionDates(step, villageId)
                        }
                        stepListDao.insertAll(it.stepList)
                        coreSharedPrefs.savePref(PREF_PROGRAM_NAME, it.programName)
                    }
                }
            } else {
                val ex = ApiResponseFailException(response.message)
                throw ex
            }
        } catch (ex: Exception) {
            throw ex
        }
    }


    override suspend fun fetchDidiListForVillageFromNetwork(villageId: Int) {
        try {
            val response = apiService.getDidisFromNetwork(villageId)
            if (response.status.equals(SUCCESS, true)) {
                response.data?.let {
                    if (it.didiList.isNotEmpty()) {
                        it.didiList.forEach { didi ->
                            try {
                                val tolaName = getTolaName(didi.cohortId)
                                val casteName = getCasteName(didi.castId)

                                val didiEntity =
                                    DidiEntity.getDidiEntity(didi, casteName, villageId, tolaName)
                                didiDao.insertDidi(
                                    didiEntity
                                )
                                // TODO Handle didi image download
                            } catch (ex: Exception) {
                                CoreLogger.e(
                                    tag = LOGGER_TAG,
                                    msg =
                                    "fetchDidiListForVillageFromNetwork -> villageId: $villageId, Exception: ${ex.message}",
                                    ex = ex,
                                    stackTrace = true
                                )
                            }
                        }
                    }
                }
            } else {
                val ex = ApiResponseFailException(response.message)
                throw ex
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun fetchTolaListForVillageFromNetwork(villageId: Int) {
        try {
            val response = apiService.getCohortFromNetwork(villageId)
            if (response.status.equals(SUCCESS, true)) {
                response.data?.let {
                    if (it.isNotEmpty()) {
                        response.data.forEach { tola ->
                            tola.serverId = tola.id
                        }
                        tolaDao.insertAll(it)
                    }
                }
            } else {
                val ex = ApiResponseFailException(response.message)
                throw ex
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun fetchSavedAnswerForVillageFromNetwork(villageId: Int) {
        try {
            val response = apiService.fetchPATSurveyToServer(listOf(villageId))
            if (response.status.equals(SUCCESS, true)) {
                response.data?.let {
                    val answerList: ArrayList<SectionAnswerEntity> =
                        arrayListOf()
                    val numAnswerList: ArrayList<NumericAnswerEntity> =
                        arrayListOf()

                    it.forEach { item ->
                        if (item.userType.equals(USER_CRP, true)) {
                            try {

                                didiDao.updatePATProgressStatus(
                                    patSurveyStatus = item.patSurveyStatus
                                        ?: 0,
                                    section1Status = item.section1Status
                                        ?: 0,
                                    section2Status = item.section2Status
                                        ?: 0,
                                    didiId = item.beneficiaryId ?: 0,
                                    shgFlag = item.shgFlag ?: -1,
                                    patExclusionStatus = item.patExclusionStatus ?: 0
                                )
                            } catch (ex: Exception) {
                                CoreLogger.e(
                                    tag = "TAG",
                                    msg = "fetchSavedAnswerForVillageFromNetwork -> updatePATProgressStatus: Exception: ${ex.message}",
                                    ex = ex,
                                    stackTrace = true
                                )
                            }

                            if (item?.answers?.isNotEmpty() == true) {
                                item?.answers?.forEach { answersItem ->
                                    val quesDetails =
                                        questionListDao.getQuestionForLanguage(
                                            answersItem?.questionId
                                                ?: 0,
                                            coreSharedPrefs.getAppLanguageId()
                                                ?: 2
                                        )
                                    if (answersItem?.questionType?.equals(
                                            QuestionType.Numeric_Field.name
                                        ) == true
                                    ) {
                                        answerList.add(
                                            SectionAnswerEntity(
                                                id = 0,
                                                optionId = 0,
                                                didiId = item.beneficiaryId
                                                    ?: 0,
                                                questionId = answersItem?.questionId
                                                    ?: 0,
                                                villageId = item.villageId
                                                    ?: 0,
                                                actionType = answersItem?.section
                                                    ?: TYPE_EXCLUSION,
                                                weight = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                    0
                                                )?.weight) else 0,
                                                summary = answersItem?.summary,
                                                optionValue = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                    0
                                                )?.optionValue) else 0,
                                                totalAssetAmount = if (quesDetails?.questionFlag.equals(
                                                        QUESTION_FLAG_WEIGHT
                                                    )
                                                ) answersItem?.totalWeight?.toDouble() else stringToDouble(
                                                    formatRatio(answersItem?.ratio ?: DOUBLE_ZERO)
                                                ),
                                                needsToPost = false,
                                                answerValue = (if (quesDetails?.questionFlag.equals(
                                                        QUESTION_FLAG_WEIGHT
                                                    )
                                                ) answersItem?.totalWeight?.toDouble() else stringToDouble(
                                                    formatRatio(answersItem?.ratio ?: DOUBLE_ZERO)
                                                )).toString(),
                                                type = answersItem?.questionType
                                                    ?: QuestionType.RadioButton.name,
                                                assetAmount = answersItem?.assetAmount
                                                    ?: "0",
                                                questionFlag = quesDetails?.questionFlag
                                                    ?: BLANK_STRING
                                            )
                                        )

                                        if (answersItem.options?.isNotEmpty() == true) {

                                            answersItem?.options?.forEach { optionItem ->
                                                numAnswerList.add(
                                                    NumericAnswerEntity(
                                                        id = 0,
                                                        optionId = optionItem?.optionId
                                                            ?: 0,
                                                        questionId = answersItem?.questionId
                                                            ?: 0,
                                                        weight = optionItem?.weight
                                                            ?: 0,
                                                        didiId = item.beneficiaryId
                                                            ?: 0,
                                                        count = optionItem?.count
                                                            ?: 0,
                                                        optionValue = optionItem?.optionValue ?: 0
                                                    )
                                                )
                                            }

                                        }
                                    } else {
                                        answerList.add(
                                            SectionAnswerEntity(
                                                id = 0,
                                                optionId = answersItem?.options?.get(
                                                    0
                                                )?.optionId ?: 0,
                                                didiId = item.beneficiaryId
                                                    ?: 0,
                                                questionId = answersItem?.questionId
                                                    ?: 0,
                                                villageId = item.villageId
                                                    ?: 0,
                                                actionType = answersItem?.section
                                                    ?: TYPE_EXCLUSION,
                                                weight = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                    0
                                                )?.weight) else 0,
                                                summary = answersItem?.summary,
                                                optionValue = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                    0
                                                )?.optionValue) else 0,
                                                totalAssetAmount = if (quesDetails?.questionFlag.equals(
                                                        QUESTION_FLAG_WEIGHT
                                                    )
                                                ) answersItem?.totalWeight?.toDouble() else stringToDouble(
                                                    formatRatio(answersItem?.ratio ?: DOUBLE_ZERO)
                                                ),
                                                needsToPost = false,
                                                answerValue = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                                                    0
                                                )?.display
                                                    ?: BLANK_STRING) else BLANK_STRING,
                                                type = answersItem?.questionType
                                                    ?: QuestionType.RadioButton.name
                                            )
                                        )
                                    }

                                }
                            }
                        }
                    }
                    if (answerList.isNotEmpty()) {
                        answerDao.insertAll(answerList)
                    }
                    if (numAnswerList.isNotEmpty()) {
                        numericAnswerDao.insertAll(numAnswerList)
                    }
                }
            } else {
                val ex = ApiResponseFailException(response.message)
                throw ex
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun updateVillageDataLoadingStatus(villageId: Int, isDataLoaded: Int) {
        villageListDao.updateVillageDataLoadStatus(villageId, isDataLoadTriedOnce = isDataLoaded)
    }

    private fun saveStepCompletionDates(step: StepListEntity, villageId: Int) {
        if (step.id == 40) {
            coreSharedPrefs.savePref(
                PREF_TRANSECT_WALK_COMPLETION_DATE_ + villageId,
                step.localModifiedDate ?: System.currentTimeMillis()
            )
        }

        if (step.id == 41) {
            coreSharedPrefs.savePref(
                PREF_SOCIAL_MAPPING_COMPLETION_DATE_ + villageId,
                step.localModifiedDate ?: System.currentTimeMillis()
            )
        }

        if (step.id == 46) {
            coreSharedPrefs.savePref(
                PREF_WEALTH_RANKING_COMPLETION_DATE_ + villageId,
                step.localModifiedDate ?: System.currentTimeMillis()
            )
        }

        if (step.id == 43) {
            coreSharedPrefs.savePref(
                PREF_PAT_COMPLETION_DATE_ + villageId,
                step.localModifiedDate ?: System.currentTimeMillis()
            )
        }
        if (step.id == 44) {
            coreSharedPrefs.savePref(
                PREF_VO_ENDORSEMENT_COMPLETION_DATE_ + villageId,
                step.localModifiedDate ?: System.currentTimeMillis()
            )
        }
    }

    private fun getTolaName(cohortId: Int): String {
        var tolaName = BLANK_STRING
        val tolaNameFromDb =
            tolaDao.fetchSingleTola(cohortId)
        tolaNameFromDb?.let {
            tolaName = it.name
        }

        return tolaName
    }

    private fun getCasteName(casteId: Int): String {
        var casteName = BLANK_STRING
        val casteNameFromDb =
            casteListDao.getCaste(casteId, coreSharedPrefs?.getAppLanguageId() ?: 2)
        casteNameFromDb?.let {
            casteName = it.casteName
        }

        return casteName
    }


}