package com.patsurvey.nudge.activities

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.activities.survey.PatSectionSummaryRepository
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.FLAG_RATIO
import com.patsurvey.nudge.utils.FLAG_WEIGHT
import com.patsurvey.nudge.utils.ForVOEndorsementType
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
    val patSectionRepository: PatSectionSummaryRepository
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
    val isBPCVerificationStepComplete =mutableStateOf(StepStatus.INPROGRESS.ordinal)
    val sectionType = mutableStateOf(TYPE_EXCLUSION)

    private var _inclusiveQueList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val  inclusiveQueList: StateFlow<List<SectionAnswerEntity>> get() = _inclusiveQueList

    fun updatePATEditAndStepStatus(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = patSectionRepository.getAllStepsForVillage() /*stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)*/
                .sortedBy { it.orderNumber }
            isPATStepComplete.value =
                stepList[stepList.map { it.orderNumber }.indexOf(4)].isComplete
            if(patSectionRepository.prefRepo.isUserBPC()){
                isBPCVerificationStepComplete.value =
                    stepList[stepList.map { it.orderNumber }.indexOf(6)].isComplete
            }
            if (patSectionRepository.prefRepo.questionScreenOpenFrom() == PageFrom.NOT_AVAILABLE_STEP_COMPLETE_SUMMARY_PAGE.ordinal
                && isPATStepComplete.value == StepStatus.COMPLETED.ordinal
            ) {
                patSectionRepository.updateVillageStepStatus(didiId = didiId)
                delay(100)
            }
        }
    }

    fun setDidiDetailsFromDb(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
           val localDidiDetails=patSectionRepository.getDidiFromDB(didiId = didiId)
            languageQuestionList = patSectionRepository.getAllQuestionForLanguage()
            val questionList = patSectionRepository.getQuestionForType(TYPE_EXCLUSION)
            val inclusionQuestionList =patSectionRepository.getQuestionForType(TYPE_INCLUSION)
            val localAnswerList = patSectionRepository.getAnswerForDidi(actionType = TYPE_EXCLUSION,didiId=didiId)
            val localSummeryList =patSectionRepository.getAnswerForDidi(actionType = TYPE_INCLUSION,didiId=didiId)
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
              patSectionRepository.setPatSurveyComplete(didiId = didiId,status=status)
            }
        }
    }
    fun setPATSection1Complete(didiId: Int,status:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                patSectionRepository.updatePatSection1Status(didiId = didiId, section1 = status)
            }
        }
    }
    fun setPATSection2Complete(didiId: Int,status:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                patSectionRepository.updatePatSection2Status(didiId = didiId, section2 = status)

            }
        }
    }

    fun getQuestionAnswerListForSectionOne(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val questionList = patSectionRepository.getQuestionForType(TYPE_EXCLUSION)
            val localAnswerList = patSectionRepository.getAnswerForDidi(TYPE_EXCLUSION, didiId = didiId)
            val yesQuesCount = patSectionRepository.fetchOptionYesCount(didiId = didiId)
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
                patSectionRepository.updateExclusionStatus(
                    didiId = didiId,
                    patExclusionStatus = exclusionStatus,
                    crpComment = crpComment
                )
            }
        }
    }

    fun calculateDidiScore(didiId: Int) {
        var passingMark = 0
        var isDidiAccepted = false
        var comment = LOW_SCORE
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                _inclusiveQueList.value = patSectionRepository.getAllInclusiveQues(didiId)
                if (_inclusiveQueList.value.isNotEmpty()) {
                    var totalWightWithoutNumQue = patSectionRepository.getTotalWeightWithoutNumQues(didiId)
                    NudgeLogger.d("PatSectionSummaryViewModel", "calculateDidiScore: $totalWightWithoutNumQue")
                    val numQueList =
                        _inclusiveQueList.value.filter { it.type == QuestionType.Numeric_Field.name }
                    if (numQueList.isNotEmpty()) {
                        numQueList.forEach { answer ->
                            val numQue = patSectionRepository.getQuestion(answer.questionId)
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
                        patSectionRepository.updateVOEndorsementDidiStatus(
                            didiId = didiId,
                            status = ForVOEndorsementType.ACCEPTED.ordinal
                        )
                    } else {
                        isDidiAccepted = false
                        patSectionRepository.updateVOEndorsementDidiStatus(
                            didiId = didiId,
                            status = ForVOEndorsementType.REJECTED.ordinal
                        )
                    }
                    Log.d("TAG", "calculateDidiScorePATSection:  $totalWightWithoutNumQue  :: $didiId :: $isDidiAccepted")

                    patSectionRepository.updateDidiScoreInDB(
                        score = totalWightWithoutNumQue,
                        comment = comment,
                        didiId = didiId,
                        isDidiAccepted = isDidiAccepted
                    )
                }
                else {
                    Log.d("TAG", "calculateDidiScorePATSection else :  0.0  :: $didiId :: $isDidiAccepted")

                    patSectionRepository.updateDidiScoreInDB(
                        score = 0.0,
                        comment = TYPE_EXCLUSION,
                        didiId = didiId,
                        isDidiAccepted = false
                    )
                }
                patSectionRepository.updateModifiedDateServerId(didiId)
            }
        }
    }

    fun updateVOEndorseAfterDidiRejected(didiId:Int,forVoEndorsementStatus:Int){
        job = CoroutineScope(Dispatchers.IO +exceptionHandler).launch {
            if(didiEntity.value.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal){
                patSectionRepository.updateVOEndorsementStatus(didiId = didiId,
                    status = DidiEndorsementStatus.REJECTED.ordinal)
            }
            patSectionRepository.updateVOEndorsementDidiStatus(
                didiId = didiId,
                status = forVoEndorsementStatus
            )
        }
    }
}
