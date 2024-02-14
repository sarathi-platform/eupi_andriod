package com.nrlm.baselinesurvey.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import com.google.gson.Gson
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.CONDITIONS_DELIMITER
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_CODE
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_LOCAL_NAME
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_NAME
import com.nrlm.baselinesurvey.activity.MainActivity
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.model.FormResponseObjectDto
import com.nrlm.baselinesurvey.model.datamodel.ConditionsDto
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.model.response.ContentList
import com.nrlm.baselinesurvey.model.datamodel.QuestionList
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.entity.FormTypeOption
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

fun uriFromFile(context: Context, file: File): Uri {
    try {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
        } else {
            Uri.fromFile(file)
        }
    } catch (ex: Exception) {
        return Uri.EMPTY
        Log.e("uriFromFile", "exception", ex)
    }
}

fun getAuthImageFileNameFromURL(url: String): String{
    return url.substring(url.lastIndexOf('=') + 1, url.length)
}

fun getFileNameFromURL(url: String): String{
    return url.substring(url.lastIndexOf('/') + 1, url.length)
}

fun getImagePath(context: Context, imagePath:String): File {
    val imageName = getFileNameFromURL(imagePath)
    return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${imageName}")
}

fun getDefaultLanguage(): LanguageEntity {
    return LanguageEntity(
        id = DEFAULT_LANGUAGE_ID,
        language = DEFAULT_LANGUAGE_NAME,
        langCode = DEFAULT_LANGUAGE_CODE,
        orderNumber = 1,
        localName = DEFAULT_LANGUAGE_LOCAL_NAME
    )
}
fun showCustomToast(
    context: Context?,
    msg: String){
    Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
}

fun onlyNumberField(value:String):Boolean{
    if(value.isDigitsOnly() && value != "_" && value != "N"){
        return true
    }
    return false
}

fun changeMilliDateToDate(millDate:Long):String?{
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    return dateFormat.format(millDate)
}

fun longToString(value:Long):String{
    return try {
        value.toString()
    }catch (ex:Exception){
        BLANK_STRING
    }
}

fun intToString(value:Int):String{
    return try {
        value.toString()
    }catch (ex:Exception){
        BLANK_STRING
    }
}

fun stringToInt(string: String):Int{
    var intValue=0
    if(string!=null){
        intValue = if(string.isEmpty())
            0
        else string.toInt()
    }
    return intValue
}

fun setKeyboardToPan(context: MainActivity) {
    context.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
}

fun setKeyboardToReadjust(context: MainActivity) {
    context.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
}


fun getAuthImagePath(context: Context, imagePath:String): File {
    val imageName = getAuthImageFileNameFromURL(imagePath)
    return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${imageName}")
}

fun createMultiLanguageVillageRequest(localLanguageList: List<LanguageEntity>):String {
    var requestString:StringBuilder= StringBuilder()
    var request:String= "2"
    if(localLanguageList.isNotEmpty()){
        localLanguageList.forEach {
            requestString.append("${it.id}-")
        }
    }else request = "2"
    if(requestString.contains("-")){
        request= requestString.substring(0,requestString.length-1)
    }
    return request
}

fun List<DidiSectionProgressEntity>.findItemBySectionId(sectionId:Int): DidiSectionProgressEntity {
    return this[this.map { it.sectionId }.indexOf(sectionId)]
}

fun List<OptionsItem>.findItemBySectionId(optionId:Int): OptionsItem {
    return this[this.map { it.optionId }.indexOf(optionId)]
}

val sampleSetcion1 = Sections(
    sectionId = 1,
    sectionName = "Financial Inclusion",
    sectionOrder = 1,
    sectionIcon = "sample_step_icon_1",
//    sectionIcon = R.drawable.sample_step_icon_1,
    sectionDetails = "Please check if the family is getting ration through the public distribution system (PDS) of the government or not?",
    questionList = listOf(
        QuestionList(
            questionId = 1,
            questionDisplay = "Did everyone in your family have at least 2 meals per day in the last 1 month?",
            questionSummary = "Please check if the family is getting ration through the public distribution system (PDS) of the government or not? \n\nPlease check the granary/ where they store their grain and also check with neighbors also to understand the food security of the family",
            order = 1,
            type = "RadioButton",
            gotoQuestionId = 2,
            options = listOf(
                OptionsItem(
                    optionId = 1,
                    display = "YES",
                    weight = 1,
                    summary = "YES",
                    optionValue = 1,
                    // optionImage = R.drawable.icon_check,
                    optionImage = "",
                    optionType = ""
                ),
                OptionsItem(
                    optionId = 2,
                    display = "NO",
                    weight = 0,
                    summary = "NO",
                    optionValue = 0,
                    //  optionImage = R.drawable.icon_close,
                    optionImage = "",
                    optionType = ""
                )
            ),
            /*questionImageUrl = "Section1_GovtService.webp",*/
        ),
        QuestionList(
            questionId = 2,
            questionDisplay = "Does the family have a working <b>2-wheeler</b>?",
            questionSummary = "Please check if the family is getting ration through the public distribution system (PDS) of the government or not?",
            order =
            2,
            type =
            "RadioButton",
            gotoQuestionId =
            3,
            options = listOf(
                OptionsItem(
                    optionId =
                    1,
                    display =
                    "YES",
                    weight =
                    1,
                    summary =
                    "YES",
                    optionValue =
                    1,
//                    optionImage =
//                    R.drawable.icon_check,
                    optionImage = "",
                    optionType =
                    ""
                ),
                OptionsItem(
                    optionId =
                    2,
                    display =
                    "NO",
                    weight =
                    0,
                    summary =
                    "NO",
                    optionValue =
                    0,
//                    optionImage =
//                    R.drawable.icon_close,
                    optionImage = "",
                    optionType =
                    ""
                )
            ),
            /*questionImageUrl =
            "Section1_2wheeler.webp"*/
        ),
        QuestionList(
            questionId = 3,
            questionDisplay = "Does the family have a working <b>Colour Television or Fridge</b>?",
            questionSummary = "Does the family have a working <b>Colour Television or Fridge</b>?",
            order = 3,
            type = "RadioButton",
            gotoQuestionId = 4,
            options = listOf(
                OptionsItem(
                    optionId = 1,
                    display = "YES",
                    weight = 1,
                    summary = "YES",
                    optionValue = 1,
                    //  optionImage = R.drawable.icon_check,
//                    optionImage =R.drawable.icon_check,
                    optionImage = "",
                    optionType = ""
                ),
                OptionsItem(
                    optionId = 2,
                    display = "NO",
                    weight = 0,
                    summary = "NO",
                    optionValue = 0,
                    // optionImage = R.drawable.icon_close,
//                    optionImage = R.drawable.icon_close,
                    optionImage = "",
                    optionType = ""
                )
            )
            /*questionImageUrl = "Section1_ColourTV.webp"*/
            )

    ),
    contentList = listOf(ContentList(BLANK_STRING, BLANK_STRING))
)
val sampleSection2 = Sections(
    sectionId = 2,
    sectionName = "Food Security",
    sectionOrder = 2,
    sectionDetails = "Please check the granary/ where they store their grain and also check with neighbors also to understand the food security of the family",
    sectionIcon = "sample_step_icon_3",
//    sectionIcon = R.drawable.sample_step_icon_3,
    questionList = listOf(
        QuestionList(
            questionId = 18,
            questionDisplay = "Is this a <b>woman headed</b> family?",
            questionSummary = "Is this a <b>woman headed</b> family?",
            order = 18,
            type = "RadioButton",
            gotoQuestionId = 19,
            options = listOf(
                OptionsItem(
                    optionId = 6,
                    display = "NO",
                    weight = 0,
                    summary = "NO",
                    optionValue = 0,
//                    optionImage = R.drawable.icon_close,
                    optionImage = "",
                    optionType = ""
                ),
                OptionsItem(
                    optionId = 5,
                    display = "YES",
                    weight = 2,
                    summary = "YES",
                    optionValue = 1,
//                    optionImage = R.drawable.icon_check,
                    optionImage = "",
                    optionType = ""
                )
            )/*,
            questionImageUrl = "Section1and2_AdultFemale_WomanHeaded.webp",*/
        ),
        QuestionList(
            questionId = 21,
            questionDisplay = "What is the <b>educational status </b> of adult members in the family?",
            questionSummary = "What is the <b>educational status </b> of adult members in the family?",
            order = 21,
            type =
            "List",
            gotoQuestionId =
            22,
            options = listOf(
                OptionsItem(
                    optionId =
                    30,
                    display =
                    "Atleast <b>1 adult </b> literate member who has <b> Passed Class 10</b>",
                    weight =
                    0,
                    summary =
                    "Atleast 1 adult > Class 10",
                    optionValue =
                    1,
                    optionImage =
                    "",
                    optionType =
                    ""
                ),
                OptionsItem(
                    optionId =
                    31,
                    display =
                    "Atleast <b>1 adult</b> literate member who can read, write Bangla/ Kok Borok but has <b>not Passed Class 10</b>",
                    weight =
                    1,
                    summary =
                    "Atleast 1 literate adult < Class 10",
                    optionValue =
                    2,
                    optionImage =
                    "",
                    optionType =
                    ""
                ),
                OptionsItem(
                    optionId =
                    32,
                    display =
                    "\"<b>No adult</b> in the family is literate (cannot read or write Bangla / Kok-Borok)",
                    weight =
                    2,
                    summary =
                    "No literate adult",
                    optionValue =
                    3,
                    optionImage =
                    "",
                    optionType =
                    ""
                )
            )/*,
            questionImageUrl =
            "Section1_2wheeler.webp",*/
        ),
        QuestionList(
            questionId = 12,
            questionDisplay = "How much is your current savings? (Select all that apply)",
            questionSummary = "How much is your current savings? (Select all that apply)",
            order = 12,
            type = "Grid",
            gotoQuestionId = 13,
            options = listOf(
                OptionsItem(
                    optionId = 1,
                    display = "Bank",
                    weight = 1,
                    summary = "Bank",
                    optionValue = 0,
                    optionImage = "",
                    optionType = ""
                ),
                OptionsItem(
                    optionId = 2,
                    display = "Cash at home",
                    weight = 2,
                    summary = "Cash at home",
                    optionValue = 1,
                    optionImage = "",
                    optionType = ""
                ),
                OptionsItem(
                    optionId = 3,
                    display = "General",
                    weight = 3,
                    summary = "General",
                    optionValue = 3,
                    optionImage = "",
                    optionType = ""
                ),
                OptionsItem(
                    optionId = 4,
                    display = "Other",
                    weight = 4,
                    summary = "Other",
                    optionValue = 4,
                    optionImage = "",
                    optionType = ""
                )
            )/*,
            questionImageUrl = "Section1_ColourTV.webp",*/
        )
    ),
    contentList = listOf(ContentList(BLANK_STRING, BLANK_STRING))
)
/*val sampleSetcion3 = Sections(
    sectionId = 3,
    sectionOrder = 1,
//    sectionIcon = "sample_step_icon_2",
    sectionIcon = R.drawable.sample_step_icon_2,
    questionList = listOf(
        QuestionEntity(
            id = 1,
            questionId = 1,
            questionDisplay = "Is anyone in the household engaged in <b>Government service</b>?",
            questionSummary = "Is anyone in the household engaged in <b>Government service</b>?",
            order = 1,
            type = "RadioButton",
            gotoQuestionId = 2,
            options = listOf(
                OptionsItem(
                    optionId = 1,
                    display = "YES",
                    weight = 1,
                    summary = "YES",
                    optionValue = 1,
                    optionImage = R.drawable.icon_check,
                    optionType = ""
                ),
                OptionsItem(
                    optionId = 2,
                    display = "NO",
                    weight = 0,
                    summary = "NO",
                    optionValue = 0,
                    optionImage = R.drawable.icon_close,
                    optionType = ""
                )
            ),
            questionImageUrl = "Section1_GovtService.webp",
        ),
        QuestionEntity(
            id = 2,
            questionId = 2,
            questionDisplay = "Does the family have a working <b>2-wheeler</b>?",
            questionSummary = "Does the family have a working <b>2-wheeler</b>?",
            order =
            2,
            type =
            "RadioButton",
            gotoQuestionId =
            3,
            options = listOf(
                OptionsItem(
                    optionId = 1,
                    display = "YES",
                    weight = 1,
                    summary = "YES",
                    optionValue = 1,
                    optionImage = R.drawable.icon_check,
                    optionType = ""
                ),
                OptionsItem(
                    optionId = 2,
                    display = "NO",
                    weight = 0,
                    summary = "NO",
                    optionValue = 0,
                    optionImage = R.drawable.icon_close,
                    optionType = ""
                )
            ),
            questionImageUrl =
            "Section1_2wheeler.webp",
        ),
        QuestionEntity(
            id = 3,
            questionId = 3,
            questionDisplay = "Does the family have a working <b>Colour Television or Fridge</b>?",
            questionSummary = "Does the family have a working <b>Colour Television or Fridge</b>?",
            order = 3,
            type = "RadioButton",
            gotoQuestionId = 4,
            options = listOf(
                OptionsItem(
                    optionId = 1,
                    display = "YES",
                    weight = 1,
                    summary = "YES",
                    optionValue = 1,
                    optionImage = R.drawable.icon_check,
                    optionType = ""
                ),
                OptionsItem(
                    optionId = 2,
                    display = "NO",
                    weight = 0,
                    summary = "NO",
                    optionValue = 0,
                    optionImage = R.drawable.icon_close,
                    optionType = ""
                )
            ),
            questionImageUrl = "Section1_ColourTV.webp",

            )
    )
)*/
val firstSampleList = listOf<Sections>(sampleSetcion1, sampleSection2)
//val secondSampleList = listOf<Sections>(sampleSetcion3)

fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun openSettings(context: Context) {
    val appSettingsIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:${context.packageName}")
    ).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
    }
    appSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    (context as MainActivity).startActivity(appSettingsIntent)
}

fun List<SectionEntity>.getSectionIndexById(sectionId: Int): Int {
    return this.sortedBySectionOrder().map { it.sectionId }.indexOf(sectionId)
}

fun List<SectionEntity>.getSectionIndexByOrder(sectionOrder: Int): Int {
    return this.sortedBySectionOrder().map { it.sectionOrder }.indexOf(sectionOrder)
}

fun List<SectionEntity>.sortedBySectionOrder(): List<SectionEntity> {
    return this/*.sortedBy { it.sectionOrder }*/ //TODO Uncomment this when order numbers are received from backend
}
fun String.toCamelCase() =
    split(" ").joinToString(" "){
        it.capitalize(Locale.US)
    }


inline fun <reified T : Any> T.json(): String = Gson().toJson(this, T::class.java)

fun storeGivenAnswered(
    formTypeOption: FormTypeOption,
    optionId: Int,
    selectedValue: String,
    referenceId: String
): QuestionTypeEvent {
    val formQuestionResponseEntity = FormQuestionResponseEntity(surveyId = formTypeOption.surveyId,
        sectionId = formTypeOption.sectionId,
        didiId = formTypeOption.didiId,
        questionId = formTypeOption.questionId,
        optionId = optionId,
        selectedValue = selectedValue,
        referenceId = referenceId
    )
    return QuestionTypeEvent.SaveFormQuestionResponseEvent(
        formQuestionResponseEntity
    )
}

fun saveFormQuestionResponseEntity(
    formTypeOption: FormTypeOption,
    optionId: Int,
    selectedValue: String,
    referenceId: String
): FormQuestionResponseEntity {

    return FormQuestionResponseEntity(
        surveyId = formTypeOption.surveyId,
        sectionId = formTypeOption.sectionId,
        didiId = formTypeOption.didiId,
        questionId = formTypeOption.questionId,
        optionId = optionId,
        selectedValue = selectedValue,
        referenceId = referenceId
    )
}

fun List<FormQuestionResponseEntity>.mapFormQuestionResponseToFromResponseObjectDto(
    optionsItemEntityList: List<OptionItemEntity>
): List<FormResponseObjectDto> {
    val householdMembersList = mutableListOf<FormResponseObjectDto>()
    val referenceIdMap = this.groupBy { it.referenceId }
    referenceIdMap.forEach { formQuestionResponseEntityList ->
        val householdMember = FormResponseObjectDto()
        val householdMemberDetailsMap = mutableMapOf<Int, String>()
        householdMember.referenceId = formQuestionResponseEntityList.key
        householdMember.questionId = formQuestionResponseEntityList.value.first().questionId
        formQuestionResponseEntityList.value.forEachIndexed { index, formQuestionResponseEntity ->
            val option = optionsItemEntityList.find { it.optionId == formQuestionResponseEntity.optionId }
            householdMemberDetailsMap.put(option?.optionId ?: -1, formQuestionResponseEntity.selectedValue)
            householdMember.memberDetailsMap = householdMemberDetailsMap
        }
        householdMembersList.add(householdMember)
    }
    return householdMembersList
}

fun QuestionList.convertQuestionListToOptionItemEntity(sectionId: Int, surveyId: Int): OptionItemEntity {
    var optionItemEntity = OptionItemEntity(
        id = 0,
        sectionId = sectionId,
        surveyId = surveyId,
        questionId = this.questionId,
        optionId = this.questionId,
        display = this.questionDisplay,
        weight = this.options?.first()?.weight,
        optionType = this.type,
        summary = this.questionSummary,
        values = emptyList(),
    )
    val valuesList = mutableListOf<String>()
    this.options.forEach {
        when (it?.optionType) {
            QuestionType.SingleSelectDropdown.name -> {
                it.values.let { it1 -> valuesList.addAll(it1) }
            }
            else -> {
                valuesList.add(it?.display ?: BLANK_STRING)
            }
        }
    }
    optionItemEntity = optionItemEntity.copy(
        values = valuesList
    )
    return optionItemEntity
}

fun List<FormQuestionResponseEntity>.getResponseForOptionId(optionId: Int): FormQuestionResponseEntity? {
    if (optionId == -1)
        return null
    return this.find { it.optionId == optionId }
}

fun  List<QuestionEntity>.findIndexForQuestionId(questionId: Int): Int {
    return this.map { it.questionId }.indexOf(questionId)
}

fun List<SectionAnswerEntity>.findQuestionForQuestionId(questionId: Int): SectionAnswerEntity? {
    return this.find { it.questionId == questionId }
}

fun List<InputTypeQuestionAnswerEntity>.mapToOptionItem(optionsItemEntityList: List<OptionItemEntity>): List<OptionItemEntity> {
    val mOptionsItemList = mutableListOf<OptionItemEntity>()
    this.forEach { inputTypeQuestionAnswerEntity ->
        if (optionsItemEntityList.any { it.optionId == inputTypeQuestionAnswerEntity.optionId })
            optionsItemEntityList.find { it.optionId == inputTypeQuestionAnswerEntity.optionId }?.let { optionsItemEntity -> mOptionsItemList.add(optionsItemEntity) }
    }
    return mOptionsItemList
}

fun List<InputTypeQuestionAnswerEntity>.findOptionFromId(optionsItemEntity: OptionItemEntity): InputTypeQuestionAnswerEntity? {
    return this.find { it.optionId == optionsItemEntity.optionId }
}

fun SnapshotStateList<QuestionEntityState>.findIndexOfListById(questionId: Int?): Int {
    if (questionId == null)
        return -1

    return this.map { it.questionId }.indexOf(questionId)
}

fun SnapshotStateList<QuestionEntityState>.findQuestionEntityStateById(questionId: Int?): QuestionEntityState? {
    if (questionId == null)
        return null
    val tempList = this.distinctBy { it.questionId }
    return tempList.find { it.questionId == questionId }
}

fun List<OptionItemEntityState>.findIndexOfOptionById(optionId: Int?): Int {
    if (optionId == null)
        return -1
    return this.map { it.optionId }.indexOf(optionId)
}

fun QuestionEntityState.getAnswerOptionForSingleAnswerOption(): OptionItemEntity? {
    return this.answerdOptionList.first()
}

fun QuestionEntityState.getAnswerOptionIdForSingleAnswerOption(): Int? {
    return this.answerdOptionList.first().optionId
}

fun List<OptionItemEntityState>.updateOptionItemEntityListStateForQuestionByCondition(conditionResult: Boolean): List<OptionItemEntityState> {
    val updatedOptionItemEntityStateList = mutableListOf<OptionItemEntityState>()
    this.forEach { optionItemEntityStateForQuestion ->
        val updatedOptionItemEntityState = optionItemEntityStateForQuestion.copy(
            showQuestion = conditionResult
        )
        updatedOptionItemEntityStateList.add(updatedOptionItemEntityState)
    }
    return updatedOptionItemEntityStateList
}

fun ConditionsDto.checkCondition(userInputValue: String): Boolean {
    val condition = this.value.split(CONDITIONS_DELIMITER, ignoreCase = true)
    try {
        val result = when(checkStringOperator(this.operator)){
            Operator.EQUAL_TO -> {
                userInputValue.equals(condition.first(), ignoreCase = true)
            }
            Operator.LESS_THAN -> {
                userInputValue.toInt() < condition.first().toInt()
            }
            Operator.IN_BETWEEN -> {
                userInputValue.toInt() > condition.first().toInt() && userInputValue.toInt() < condition.last().toInt()
            }
            /*Operator.LESS_THAN_EQUAL_TO ->{
                if(totalAmount <= (if(!isRatio) stringToDouble(it.weightage) else stringToDouble(it.ratio))){
                    score = it.score.toDouble()
                    return@breaking
                }
            }
            Operator.MORE_THAN -> {
                if(totalAmount > (if(!isRatio) stringToDouble(it.weightage) else stringToDouble(it.ratio))){
                    score = it.score.toDouble()
                    return@breaking
                }
            }
            Operator.MORE_THAN_EQUAL_TO -> {
                if(totalAmount >= (if(!isRatio) stringToDouble(it.weightage) else stringToDouble(it.ratio))){
                    score = it.score.toDouble()
                    return@breaking
                }
            }*/
            else -> {
                false
            }
        }
        return result
    } catch (ex: Exception) {
        return false
    }
}

fun checkStringOperator(operator:String) = when(operator){
    "==" ->Operator.EQUAL_TO
    "=" ->Operator.EQUAL_TO
    "<" ->Operator.LESS_THAN
    "<=" ->Operator.LESS_THAN_EQUAL_TO
    ">" ->Operator.MORE_THAN
    ">=" ->Operator.MORE_THAN_EQUAL_TO
    "><" -> Operator.IN_BETWEEN
    else->{}
}