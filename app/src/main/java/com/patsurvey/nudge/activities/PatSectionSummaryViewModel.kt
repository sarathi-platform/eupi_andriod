package com.patsurvey.nudge.activities

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcNonSelectedDidiDao
import com.patsurvey.nudge.database.dao.BpcSelectedDidiDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ExclusionType
import com.patsurvey.nudge.utils.FLAG_RATIO
import com.patsurvey.nudge.utils.FLAG_WEIGHT
import com.patsurvey.nudge.utils.LOW_SCORE
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
import com.patsurvey.nudge.utils.calculateScore
import com.patsurvey.nudge.utils.toWeightageRatio
import com.patsurvey.nudge.utils.updateStepStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PatSectionSummaryViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao,
    val bpcNonSelectedDidiDao: BpcNonSelectedDidiDao,
    val bpcSelectedDidiDao: BpcSelectedDidiDao,
    val stepsListDao: StepsListDao
) : BaseViewModel() {

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
            shgFlag = SHGFlag.NOT_MARKED.value,
            ableBodiedFlag = AbleBodiedFlag.NOT_MARKED.value
        )
    )
    val didiEntity: StateFlow<DidiEntity> get() = _didiEntity

    private val _questionList = MutableStateFlow(listOf<QuestionEntity>())
    private var languageQuestionList = listOf<QuestionEntity>()
    val questionList: StateFlow<List<QuestionEntity>> get() = _questionList
    private val _inclusionQuestionList = MutableStateFlow(listOf<QuestionEntity>())
    val inclusionQuestionList: StateFlow<List<QuestionEntity>> get() = _inclusionQuestionList

    private val _answerList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerList: StateFlow<List<SectionAnswerEntity>> get() = _answerList

    private val _answerSummeryList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerSummeryList: StateFlow<List<SectionAnswerEntity>> get() = _answerSummeryList
    val isYesSelected = mutableStateOf(false)
    val isPATStepComplete =mutableStateOf(StepStatus.INPROGRESS.ordinal)
    val sectionType = mutableStateOf(TYPE_EXCLUSION)

    private var _inclusiveQueList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val  inclusiveQueList: StateFlow<List<SectionAnswerEntity>> get() = _inclusiveQueList

    fun updatePATEditAndStepStatus(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
                .sortedBy { it.orderNumber }
            isPATStepComplete.value =
                stepList[stepList.map { it.orderNumber }.indexOf(4)].isComplete
            if (prefRepo.questionScreenOpenFrom() == PageFrom.NOT_AVAILABLE_STEP_COMPLETE_SUMMARY_PAGE.ordinal
                && isPATStepComplete.value == StepStatus.COMPLETED.ordinal
            ) {
                updateStepStatus(
                    stepsListDao = stepsListDao,
                    didiDao = didiDao,
                    didiId = didiId,
                    prefRepo = prefRepo,
                    printTag = "PatSectionSummaryViewModel ONE"
                )
                delay(100)
            }
        }
    }

    fun setDidiDetailsFromDb(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
           val localDidiDetails=didiDao.getDidi(didiId)
            languageQuestionList = questionListDao.getAllQuestionsForLanguage(prefRepo.getAppLanguageId()?:2)
            val questionList = questionListDao.getQuestionForType(TYPE_EXCLUSION,prefRepo.getAppLanguageId()?:2)
            val inclusionQuestionList = questionListDao.getQuestionForType(TYPE_INCLUSION,prefRepo.getAppLanguageId()?:2)
            val localAnswerList = answerDao.getAnswerForDidi(TYPE_EXCLUSION, didiId = didiId)
            val localSummeryList = answerDao.getAnswerForDidi(TYPE_INCLUSION, didiId = didiId)
            if(sectionType.value.equals(TYPE_INCLUSION,true)){
                calculateDidiScore(localDidiDetails.id)
            }
            withContext(Dispatchers.IO){
                _didiEntity.emit(localDidiDetails)
                _questionList.emit(questionList)
                _inclusionQuestionList.emit(inclusionQuestionList)
                _answerList.emit(localAnswerList)
                _answerSummeryList.emit(localSummeryList)
            }
        }
    }

    fun getQuestionSummary(questionId : Int) : String{
        var summary = ""
        for(question in languageQuestionList){
            if(question.questionId == questionId)
                summary = question.questionSummary.toString()
        }
        return summary
    }

    fun getOptionForLanguage(questionId : Int,optionId : Int, answerValue:String) : String{
        var optionText = ""
        for(question in languageQuestionList){
            if(question.questionId == questionId) {
                for (option in question.options){
                    if(option.optionId == optionId) {
                        optionText = option.summary.toString()
                        break
                    } else if(optionId == 0){
                      if(question.type.equals(QuestionType.Numeric_Field.name,true)){
                          optionText=answerValue
                          break
                        }
                    }
                }
            }
        }
        return optionText
    }

    fun setPATSurveyComplete(didiId: Int,status:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                didiDao.updateQuesSectionStatus(didiId,status)
                didiDao.updateDidiNeedToPostPat(didiId, true)
                if(prefRepo.isUserBPC()){
                    val selectedDidi = bpcSelectedDidiDao.fetchSelectedDidi(didiId)
                    selectedDidi?.let {
                        bpcSelectedDidiDao.updateSelDidiPatSurveyStatus(didiId,status)
                    }
                    val nonSelectedDidi = bpcNonSelectedDidiDao.getNonSelectedDidi(didiId)
                    nonSelectedDidi?.let {
                        bpcNonSelectedDidiDao.updateNonSelDidiPatSurveyStatus(didiId,status)

                    }
                }
            }
        }
    }

    fun updatePATExclusionStatus(didiId: Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                didiDao.updateExclusionStatus(didiId = didiId,
                    patExclusionStatus = ExclusionType.NO_EXCLUSION.ordinal,
                    crpComment = BLANK_STRING)
            }
           }
        }
    fun setPATSection1Complete(didiId: Int,status:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                didiDao.updatePatSection1Status(didiId,status)

                if(prefRepo.isUserBPC()){
                    val selectedDidi = bpcSelectedDidiDao.fetchSelectedDidi(didiId)
                    selectedDidi?.let {
                        bpcSelectedDidiDao.updateSelDidiPatSection1Status(didiId,status)
                    }
                    val nonSelectedDidi = bpcNonSelectedDidiDao.getNonSelectedDidi(didiId)
                    nonSelectedDidi?.let {
                        bpcNonSelectedDidiDao.updateNonSelDidiPatSection1Status(didiId,status)
                    }
                }
            }
        }
    }
    fun setPATSection2Complete(didiId: Int,status:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                didiDao.updatePatSection2Status(didiId,status)
                if(prefRepo.isUserBPC()){
                    val selectedDidi = bpcSelectedDidiDao.fetchSelectedDidi(didiId)
                    selectedDidi?.let {
                        bpcSelectedDidiDao.updateSelDidiPatSection2Status(didiId,status)
                    }
                    val nonSelectedDidi = bpcNonSelectedDidiDao.getNonSelectedDidi(didiId)
                     nonSelectedDidi?.let {
                         bpcNonSelectedDidiDao.updateNonSelDidiPatSection2Status(didiId,status)

                    }
                }

            }
        }
    }

    fun getQuestionAnswerListForSectionOne(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val questionList = questionListDao.getQuestionForType(TYPE_EXCLUSION,prefRepo.getAppLanguageId()?:2)
            val localAnswerList = answerDao.getAnswerForDidi(TYPE_EXCLUSION, didiId = didiId)
            val yesQuesCount = answerDao.fetchOptionYesCount(didiId = didiId,QuestionType.RadioButton.name,TYPE_EXCLUSION)
            withContext(Dispatchers.IO) {
                try {
                    _questionList.emit(questionList)
                    _answerList.emit(localAnswerList)
                    if(yesQuesCount>0){
                        isYesSelected.value=true
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        }
    }

    override fun onServerError(error: ErrorModel?) {

    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    fun updateExclusionStatus(didiId: Int, exclusionStatus:Int, crpComment:String){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                     didiDao.updateExclusionStatus(didiId,exclusionStatus, crpComment)
            }
        }
    }

    fun calculateDidiScore(didiId: Int) {
        var passingMark = 0
        var isDidiAccepted = false
        var comment = LOW_SCORE
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                _inclusiveQueList.value = answerDao.getAllInclusiveQues(didiId = didiId)
                if (_inclusiveQueList.value.isNotEmpty()) {
                    var totalWightWithoutNumQue = answerDao.getTotalWeightWithoutNumQues(didiId)
                    NudgeLogger.d("PatSectionSummaryViewModel", "calculateDidiScore: $totalWightWithoutNumQue")
                    val numQueList =
                        _inclusiveQueList.value.filter { it.type == QuestionType.Numeric_Field.name }
                    if (numQueList.isNotEmpty()) {
                        numQueList.forEach { answer ->
                            val numQue = questionListDao.getQuestion(answer.questionId)
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
                                    NudgeLogger.d("PatSectionSummaryViewModel", "calculateDidiScore: totalWightWithoutNumQue += newScore -> $totalWightWithoutNumQue")
                                }
                            } else if (numQue.questionFlag?.equals(FLAG_RATIO, true) == true) {
                                val ratioList = toWeightageRatio(numQue.json.toString())
                                val newScore = calculateScore(
                                    ratioList,
                                    answer.totalAssetAmount?.toDouble() ?: 0.0,
                                    true
                                )
                                totalWightWithoutNumQue += newScore
                                NudgeLogger.d("PatSectionSummaryViewModel", "calculateDidiScore: for Flag FLAG_RATIO totalWightWithoutNumQue += newScore -> $totalWightWithoutNumQue")
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
                    didiDao.updateDidiScore(
                        score = totalWightWithoutNumQue,
                        comment = comment,
                        didiId = didiId,
                        isDidiAccepted = isDidiAccepted
                    )
                    if (prefRepo.isUserBPC()) {
                        bpcSelectedDidiDao.updateSelDidiScore(
                            score = totalWightWithoutNumQue,
                            comment = comment,
                            didiId = didiId,
                        )
                    }
                }
                else {
                    didiDao.updateDidiScore(
                        score = 0.0,
                        comment = TYPE_EXCLUSION,
                        didiId = didiId,
                        isDidiAccepted = false
                    )
                    if (prefRepo.isUserBPC()) {
                        bpcSelectedDidiDao.updateSelDidiScore(
                            score = 0.0,
                            comment = TYPE_EXCLUSION,
                            didiId = didiId,
                        )
                    }
                }
                didiDao.updateModifiedDateServerId(System.currentTimeMillis(), didiId)
            }
        }
    }
}
