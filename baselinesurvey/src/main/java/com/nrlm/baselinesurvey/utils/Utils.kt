package com.nrlm.baselinesurvey.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import com.google.gson.Gson
import com.nrlm.baselinesurvey.AUTO_CALCULATE_CONDITION_DELIMITER
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.CONDITIONS_DELIMITER
import com.nrlm.baselinesurvey.DELIMITER_TIME
import com.nrlm.baselinesurvey.DELIMITER_YEAR
import com.nrlm.baselinesurvey.HOURS
import com.nrlm.baselinesurvey.MINUTE
import com.nrlm.baselinesurvey.MONTHS
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.YEAR
import com.nrlm.baselinesurvey.ZERO_RESULT
import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.model.FormResponseObjectDto
import com.nrlm.baselinesurvey.model.datamodel.ComplexSearchState
import com.nrlm.baselinesurvey.model.datamodel.ConditionsDto
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.QuestionList
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.model.datamodel.TagMappingDto
import com.nrlm.baselinesurvey.model.datamodel.ValuesDto
import com.nrlm.baselinesurvey.ui.Constants.ItemType
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.ButtonNegative
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.MainTitle
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.entity.FormTypeOption
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.greyBorder
import com.nudge.core.Core
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.DEFAULT_LANGUAGE_ID
import com.nudge.core.DEFAULT_LANGUAGE_LOCAL_NAME
import com.nudge.core.DEFAULT_LANGUAGE_NAME
import com.nudge.core.enums.EventName
import com.nudge.core.utils.state.DialogState
import com.nudge.core.utils.state.rememberDialogState
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID


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

fun setKeyboardToPan(context: Activity) {
    context.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
}

fun getUniqueIdForEntity(): String {
    return UUID.randomUUID().toString().replace("-", "") + "|" + System.currentTimeMillis()
}

fun setKeyboardToReadjust(context: Activity) {
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
    Core().getMainActivityContext()?.startActivity(appSettingsIntent)
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

fun getEventForMiscQuestionAnswers(
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
    referenceId: String,
    selectedIds: List<Int> = emptyList()
): FormQuestionResponseEntity {

    return FormQuestionResponseEntity(
        surveyId = formTypeOption.surveyId,
        sectionId = formTypeOption.sectionId,
        didiId = formTypeOption.didiId,
        questionId = formTypeOption.questionId,
        optionId = optionId,
        selectedValue = selectedValue,
        referenceId = referenceId,
        selectedValueId = selectedIds
    )
}

fun List<FormQuestionResponseEntity>.mapFormQuestionResponseToFromResponseObjectDto(
    optionsItemEntityList: List<OptionItemEntity>, questionTag: Int?
): List<FormResponseObjectDto> {
    val householdMembersList = mutableListOf<FormResponseObjectDto>()
    val referenceIdMap = this.groupBy { it.referenceId }
    referenceIdMap.forEach { formQuestionResponseEntityList ->
        val householdMember = FormResponseObjectDto()
        val householdMemberDetailsMap = mutableMapOf<Int, String>()
        val selectedValueMap = mutableMapOf<Int, List<Int>>()
        householdMember.referenceId = formQuestionResponseEntityList.key
        householdMember.questionId = formQuestionResponseEntityList.value.first().questionId
        householdMember.questionTag = tagList.findTagForId(questionTag ?: -1)
        formQuestionResponseEntityList.value.forEachIndexed { index, formQuestionResponseEntity ->
            householdMemberDetailsMap.put(
                formQuestionResponseEntity.optionId,
                formQuestionResponseEntity.selectedValue
            )
            selectedValueMap.put(
                formQuestionResponseEntity.optionId,
                formQuestionResponseEntity.selectedValueId
            )
            /*var option = optionsItemEntityList.find { it.optionId == formQuestionResponseEntity.optionId }
            if (option==null) {
                optionsItemEntityList.forEach { optionItemEntity ->
                    optionItemEntity.conditions?.forEach { conditionsDto ->
                        conditionsDto?.resultList?.forEach { questionItem ->
                            option = questionItem.options?.find { it?.optionId == formQuestionResponseEntity.optionId }?.convertToOptionItemEntity(formQuestionResponseEntity.questionId, formQuestionResponseEntity.sectionId, formQuestionResponseEntity.surveyId, optionItemEntity.languageId!!)
                            if (option != null) {
                                householdMemberDetailsMap.put(option?.optionId ?: formQuestionResponseEntity.optionId, formQuestionResponseEntity.selectedValue)
                            }
                        }
                    }
                }
            }*/

            householdMember.memberDetailsMap = householdMemberDetailsMap
            householdMember.selectedValueId = selectedValueMap
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
        weight = if (options?.isEmpty() == true) 0 else this.options?.first()?.weight,
        optionType = this.type,
        order = this.order ?: -1,
        summary = this.questionSummary,
        values = emptyList(),
        contentEntities = this.contentList ?: listOf(),
        conditional = this.conditional
    )
    val valuesList = mutableListOf<ValuesDto>()
    val conditions = mutableListOf<ConditionsDto>()
    this.options?.forEach {
        it?.conditions?.forEach { condition ->
            condition?.let { it1 -> conditions.add(it1) }
        }
        when (it?.optionType) {
            QuestionType.SingleSelectDropdown.name,
            QuestionType.SingleSelectDropDown.name -> {
                it.values.let { it1 -> valuesList.addAll(it1) }
            }
            else -> {
                valuesList.add(ValuesDto(id = it?.optionId ?: 0, it?.display ?: BLANK_STRING))
            }
        }
    }
    optionItemEntity = optionItemEntity.copy(
        values = valuesList,
        conditions = conditions
    )
    return optionItemEntity
}

fun QuestionList.convertFormTypeQuestionListToOptionItemEntity(sectionId: Int, surveyId: Int, languageId: Int): List<OptionItemEntity> {
    val optionsItemEntityList = mutableListOf<OptionItemEntity>()

    this.options?.forEach { optionsItem ->
        val optionItemEntity = OptionItemEntity(
            id = 0,
            optionId = optionsItem?.optionId,
            questionId = this.questionId,
            sectionId = sectionId,
            surveyId = surveyId,
            display = optionsItem?.display,
            weight = optionsItem?.weight,
            optionValue = optionsItem?.optionValue,
            summary = optionsItem?.summary,
            count = optionsItem?.count,
            optionImage = optionsItem?.optionImage,
            optionType = optionsItem?.optionType,
            conditional = true,
            order = this.order ?: -1,
            values = optionsItem?.values,
            languageId = languageId,
            optionTag = this.attributeTag ?: 0,
            contentEntities = optionsItem?.contentList ?: listOf(),
            conditions = optionsItem?.conditions
        )
        optionsItemEntityList.add(optionItemEntity)
    }

    return optionsItemEntityList
}

fun List<FormQuestionResponseEntity>.getResponseForOptionId(optionId: Int): FormQuestionResponseEntity? {
    if (optionId == -1)
        return null
    return this.find { it.optionId == optionId }
}

fun List<FormQuestionResponseEntity>.findOptionExist(optionId: Int): Boolean {
    return this.find { it.optionId == optionId }!=null
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

fun SnapshotStateList<OptionItemEntityState>.findIndexOfListByOptionId(optionId: Int?): Int {
    if (optionId == null)
        return  -1

    return this.map { it.optionId }.indexOf(optionId)
}

fun List<OptionItemEntityState>.findIndexOfListByOptionId(optionId: Int?): Int {
    if (optionId == null)
        return  -1

    return this.map { it.optionId }.indexOf(optionId)
}

fun SnapshotStateList<QuestionEntityState>.findQuestionEntityStateById(questionId: Int?): QuestionEntityState? {
    if (questionId == null)
        return null
    val tempList = this.distinctBy { it.questionId }
    return tempList.find { it.questionId == questionId }
}

fun InputTypeQuestionAnswerEntity.getOptionItemEntityFromInputTypeQuestionAnswer(sectionDetails: SectionListItem): OptionItemEntity? {
    val question =  sectionDetails.questionList.find { it.questionId == this.questionId }
    var mOptionItemEntity = sectionDetails.optionsItemMap[question?.questionId]?.find { it.optionId == this.optionId }
    mOptionItemEntity = mOptionItemEntity?.copy(selectedValue = this.inputValue)
    return mOptionItemEntity
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

fun List<OptionItemEntityState>.updateOptionsForNoneCondition(
    conditionResult: Boolean,
    optionId: Int,
    noneOptionUnselected: Boolean
): List<OptionItemEntityState> {
    val updatedOptionItemEntityStateList = mutableListOf<OptionItemEntityState>()
    if (noneOptionUnselected) {
        this.forEach { optionItemEntityStateForQuestion ->
            val updatedOptionItemEntityState = optionItemEntityStateForQuestion.copy(
                isOptionEnabled = true
            )
            updatedOptionItemEntityStateList.add(updatedOptionItemEntityState)
        }
    } else {
        this.forEach { optionItemEntityStateForQuestion ->
            if (optionItemEntityStateForQuestion.optionId != optionId) {
                val updatedOptionItemEntityState = optionItemEntityStateForQuestion.copy(
                    isOptionEnabled = conditionResult
                )
                updatedOptionItemEntityStateList.add(updatedOptionItemEntityState)
            } else {
                val updatedOptionItemEntityState = optionItemEntityStateForQuestion.copy(
                    isOptionEnabled = true
                )
                updatedOptionItemEntityStateList.add(updatedOptionItemEntityState)
            }
        }
    }
    return updatedOptionItemEntityStateList
}

fun QuestionList.convertToOptionItemEntity(
    sectionId: Int,
    surveyId: Int,
    questionId: Int,
    languageId: Int
): OptionItemEntity {
    return OptionItemEntity(
        id = 0,
        questionId = questionId,
        sectionId = sectionId,
        surveyId = surveyId,
        display = this.questionDisplay,
        weight = 0,
        optionValue = 0,
        summary = this.questionSummary,
        count = 0,
        optionImage = this.imageIcon,
        optionType = this.type,
        conditional = this.conditional,
        order = this.order ?: -1,
        languageId = languageId
    )
}

//TODO Test and optimize this extension function.
fun <T> SnapshotStateList<T>.updateListAtIndex(index: Int, item: T?): SnapshotStateList<T> {
    return when (item) {
        is QuestionEntityState -> {
            if (index != -1) {
                this.removeAt(index)
                val mIndex = (item?.questionEntity?.order ?: 0) - 1
                this.add(if (mIndex != -1) index else index, item)
            }
            this
        }
        else -> {
            this
        }
    }
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
                userInputValue.toInt() >= condition.first().toInt() && userInputValue.toInt() <= condition.last().toInt()
            }
            Operator.NOT_EQUAL_TO -> {
                !userInputValue.equals(condition.first(), ignoreCase = true)
            }
            Operator.LESS_THAN_EQUAL_TO ->{
                userInputValue.toInt() <= condition.first().toInt()
            }
            Operator.MORE_THAN -> {
                userInputValue.toInt() > condition.first().toInt()
            }
            Operator.MORE_THAN_EQUAL_TO -> {
                userInputValue.toInt() >= condition.first().toInt()
            }

            else -> {
                false
            }
        }
        return result
    } catch (ex: Exception) {
        return false
    }
}

fun ConditionsDto.checkConditionForMultiSelectDropDown(userInputValue: List<String>): Boolean {
    val condition = this.value.split(CONDITIONS_DELIMITER, ignoreCase = true)
    if (userInputValue.isEmpty())
        return false

    try {
        val result = when (checkStringOperator(this.operator)) {
            Operator.EQUAL_TO -> {
                userInputValue.contains(condition.first())
            }

            /*Operator.LESS_THAN -> {
                userInputValue.toInt() < condition.first().toInt()
            }

            Operator.IN_BETWEEN -> {
                userInputValue.toInt() >= condition.first()
                    .toInt() && userInputValue.toInt() <= condition.last().toInt()
            }

            Operator.NOT_EQUAL_TO -> {
                !userInputValue.equals(condition.first(), ignoreCase = true)
            }

            Operator.LESS_THAN_EQUAL_TO -> {
                userInputValue.toInt() <= condition.first().toInt()
            }

            Operator.MORE_THAN -> {
                userInputValue.toInt() > condition.first().toInt()
            }

            Operator.MORE_THAN_EQUAL_TO -> {
                userInputValue.toInt() >= condition.first().toInt()
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

fun isNumeric(toCheck: String): Boolean {
    val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
    return toCheck.matches(regex)
}

fun ConditionsDto.calculateResultForFormula(formQuestionResponseEntity: List<FormQuestionResponseEntity>): String {
    try {
        val optionIdList = this.value.extractIdsFromValue()
        val filteredResponseList =
            formQuestionResponseEntity.filter { optionIdList?.contains(it.optionId.toString()) == true }
                .sortedBy { it.optionId }
        var input = this.value

        if (filteredResponseList.isEmpty())
            return BLANK_STRING
        val tempList = ArrayList<String>()
        input.split(" ").filter { it != "" }.forEach { va ->
            if (va.isNotEmpty() && isNumeric(va)) {
                tempList.add(filteredResponseList.findResponseEntityByOptionId(va.toInt()).selectedValue)
            } else {
                tempList.add(va)
            }
        }
        var actualvalue = BLANK_STRING
        for (v in tempList) {
            actualvalue += v
        }
        val result = CalculatorUtils.calculate(actualvalue)
        Log.d("TAG", "calculateResultForFormula: $result")
        return result.toString()
    } catch (ex: Exception) {
        BaselineLogger.e("Utils", "calculateResultForFormula -> exception: ${ex.message}", ex)
        return ZERO_RESULT
    }

}

fun String.extractIdsFromValue(): List<String>? {
    return this.replace("(", "")?.replace(")", "")?.split(" ")?.filterNot { it.equals("*") || it.equals("-") || it.equals("+") || it.equals("-") }
}

fun List<FormQuestionResponseEntity>.findResponseEntityByOptionId(optionId: Int): FormQuestionResponseEntity {
    return this[this.map { it.optionId }.indexOf(optionId)]
}

fun checkStringOperator(operator:String) = when(operator){
    "==" ->Operator.EQUAL_TO
    "=" ->Operator.EQUAL_TO
    "<" ->Operator.LESS_THAN
    "<=" ->Operator.LESS_THAN_EQUAL_TO
    ">" ->Operator.MORE_THAN
    ">=" ->Operator.MORE_THAN_EQUAL_TO
    "><" -> Operator.IN_BETWEEN
    "<>" -> Operator.NOT_EQUAL_TO
    "*" -> Operator.MULTIPLY
    "-" -> Operator.SUBTRACT
    "+" -> Operator.ADD
    "/" -> Operator.DIVIDE
    else->Operator.NO_OPERATOR
}

fun  SnapshotStateList<QuestionEntityState>.getSizeOfVisibleQuestions(): Int {
    return this.distinctBy { it.questionId }.filter { it.showQuestion }.size
}

fun List<OptionItemEntity>.getIndexById(optionId: Int): Int {
    return this.map { it.optionId }.indexOf(optionId)
}

fun OptionsItem?.convertToOptionItemEntity(questionId: Int, sectionId: Int, surveyId: Int, languageId: Int): OptionItemEntity {
    return OptionItemEntity(
        id = 0,
        optionId = this?.optionId,
        questionId = questionId,
        sectionId = sectionId,
        surveyId = surveyId,
        display = this?.display,
        weight = this?.weight,
        optionValue = this?.optionValue,
        summary = this?.summary,
        count = this?.count,
        optionImage = this?.optionImage,
        optionType = this?.optionType,
        conditional = this?.conditional!!,
        order = this.order,
        values = this.values,
        languageId = languageId,
        conditions = this.conditions
    )
}

fun List<OptionsItem?>?.convertToListOfOptionItemEntity(questionId: Int, sectionId: Int, surveyId: Int, languageId: Int): List<OptionItemEntity> {
    val optionsItemEntityList = mutableListOf<OptionItemEntity>()
    this?.forEach {
        val optionItemEntity = it?.convertToOptionItemEntity(
            questionId = questionId,
            sectionId = sectionId,
            surveyId = surveyId,
            languageId = languageId
        )
        optionsItemEntityList.add(optionItemEntity!!)
    }
    return optionsItemEntityList
}

fun List<SectionListItem>.convertToComplexSearchState(): List<ComplexSearchState> {
    val complexSearchStateList = mutableListOf<ComplexSearchState>()

    this.forEach { section ->
        val complexSearchState = ComplexSearchState(section.sectionId, itemType = ItemType.Section, sectionName = section.sectionName, questionTitle = BLANK_STRING, isSectionSearchOnly = true)
        complexSearchStateList.add(complexSearchState)
    }
    this.forEach { section ->
        section.questionList.forEach { question ->
            val complexSearchStateForQuestion = ComplexSearchState(
                itemId = question.questionId ?: -1,
                itemParentId = question.sectionId,
                itemType = ItemType.Question,
                sectionName = section.sectionName,
                questionTitle = question.questionDisplay ?: BLANK_STRING,
                isSectionSearchOnly = false
            )
            complexSearchStateList.add(complexSearchStateForQuestion)
        }
    }

    return complexSearchStateList
}

fun <T> getParentEntityMapForEvent(eventItem: T, eventName: EventName): Map<String, String> {
    return when (eventName) {

        EventName.ADD_SECTION_PROGRESS_FOR_DIDI_EVENT -> {
            emptyMap()
        }

        else -> {
            emptyMap()
        }
    }
}
fun String.evaluateValue(): Boolean {
    return this.isBlank() || this == "00" || this == "0"
}
fun formatHrsYearEventData(selectedValue: String): String {
    val response: List<String>
    if (selectedValue.contains(DELIMITER_YEAR)) {
        response = selectedValue.split(DELIMITER_YEAR)
        val evaluateYearVal: String =
            if (response.first().evaluateValue()) {
                "${response[1]} $MONTHS"
            } else if (response[1].evaluateValue()) {
                "${response.first()} $YEAR"
            } else {
                "${response.first()} $YEAR ${response[1]} $MONTHS"
            }
        return evaluateYearVal
    } else if (selectedValue.contains(DELIMITER_TIME)) {
        response = selectedValue.split(DELIMITER_TIME)
        val evaluateTimeVal: String =
            if (response.first().evaluateValue()
            ) {
                "${response[1]} $MINUTE"
            } else if (response[1].evaluateValue()) {
                "${response.first()} $HOURS"
            } else {
                "${response.first()} $HOURS ${response[1]} $MINUTE"
            }
        return evaluateTimeVal
    }
    return selectedValue
}
fun OptionItemEntity.convertToSaveAnswerEventOptionItemDto(type: QuestionType?): List<SaveAnswerEventOptionItemDto> {
    val saveAnswerEventOptionItemDtoList = mutableListOf<SaveAnswerEventOptionItemDto>()

    if (type == null)
        return saveAnswerEventOptionItemDtoList

    when (type) {
        QuestionType.RadioButton -> {
            val mSaveAnswerEventOptionItemDto =
                SaveAnswerEventOptionItemDto(this.optionId ?: 0, this.display, tag = this.optionTag)
            saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
        }

        QuestionType.List,
        QuestionType.SingleSelect -> {
            val mSaveAnswerEventOptionItemDto =
                SaveAnswerEventOptionItemDto(this.optionId ?: 0, this.display, tag = this.optionTag)
            saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
        }

        QuestionType.SingleSelectDropDown,
        QuestionType.SingleSelectDropdown -> {
            val mSaveAnswerEventOptionItemDto =
                SaveAnswerEventOptionItemDto(
                    this.optionId ?: 0,
                    this.selectedValue,
                    tag = this.optionTag,
                    selectedValueWithIds = this.values?.find { it.id == this.selectedValueId }
                        ?.let { listOf(it) } ?: emptyList()
                )
            saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
        }

        QuestionType.HrsMinPicker,
        QuestionType.YrsMonthPicker -> {
            val mSaveAnswerEventOptionItemDto =
                SaveAnswerEventOptionItemDto(
                    this.optionId ?: 0,
                    formatHrsYearEventData(this.selectedValue ?: BLANK_STRING),
                    tag = this.optionTag
                )
            saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
        }
        QuestionType.Input,
        QuestionType.InputText,
        QuestionType.InputNumber,
        QuestionType.InputNumberEditText -> {
            val mSaveAnswerEventOptionItemDto =
                SaveAnswerEventOptionItemDto(
                    this.optionId ?: 0,
                    this.selectedValue,
                    optionDesc = this.display ?: BLANK_STRING,
                    tag = this.optionTag
                )
            saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
        }

        else -> {

        }
    }

    return saveAnswerEventOptionItemDtoList
}

fun List<OptionItemEntity>.convertToSaveAnswerEventOptionItemsDto(type: QuestionType?): List<SaveAnswerEventOptionItemDto> {
    val saveAnswerEventOptionItemDtoList = mutableListOf<SaveAnswerEventOptionItemDto>()

    if (type == null)
        return saveAnswerEventOptionItemDtoList
    this.forEach {
        when (type) {
            QuestionType.RadioButton -> {
                val mSaveAnswerEventOptionItemDto =
                    SaveAnswerEventOptionItemDto(it.optionId ?: 0, it.display, tag = it.optionTag)
                saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
            }

            QuestionType.List,
            QuestionType.SingleSelect -> {
                val mSaveAnswerEventOptionItemDto =
                    SaveAnswerEventOptionItemDto(it.optionId ?: 0, it.display, tag = it.optionTag)
                saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
            }

            QuestionType.SingleSelectDropDown,
            QuestionType.SingleSelectDropdown -> {
                val mSaveAnswerEventOptionItemDto =
                    SaveAnswerEventOptionItemDto(
                        it.optionId ?: 0,
                        it.selectedValue,
                        tag = it.optionTag,
                        selectedValueWithIds = it.values?.find { valuesDto -> valuesDto.id == it.selectedValueId }
                            ?.let { listOf(it) } ?: emptyList()
                    )
                saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
            }

            QuestionType.Input,
            QuestionType.InputText,
            QuestionType.InputNumber,
            QuestionType.InputNumberEditText -> {
                val mSaveAnswerEventOptionItemDto =
                    SaveAnswerEventOptionItemDto(
                        it.optionId ?: 0,
                        it.selectedValue,
                        optionDesc = it.display ?: BLANK_STRING,
                        tag = it.optionTag
                    )
                saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
            }

            QuestionType.HrsMinPicker,
            QuestionType.YrsMonthPicker -> {
                val mSaveAnswerEventOptionItemDto =
                    SaveAnswerEventOptionItemDto(
                        it.optionId ?: 0,
                        formatHrsYearEventData(it.selectedValue ?: BLANK_STRING),
                        tag = it.optionTag
                    )
                saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
            }

            QuestionType.MultiSelect,
            QuestionType.Grid -> {

                val mSaveAnswerEventOptionItemDto =
                    SaveAnswerEventOptionItemDto(it.optionId ?: 0, it.display, tag = it.optionTag)
                saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)

            }

            else -> {

            }

        }
    }
    return saveAnswerEventOptionItemDtoList
}
fun List<OptionItemEntity>.convertToSaveAnswerEventOptionItemDto(type: QuestionType): List<SaveAnswerEventOptionItemDto> {
    val saveAnswerEventOptionItemDtoList = mutableListOf<SaveAnswerEventOptionItemDto>()
    when (type) {
        QuestionType.MultiSelect,
        QuestionType.Grid -> {
            this.forEach {
                val mSaveAnswerEventOptionItemDto =
                    SaveAnswerEventOptionItemDto(it.optionId ?: 0, it.display, tag = it.optionTag)
                saveAnswerEventOptionItemDtoList.add(mSaveAnswerEventOptionItemDto)
            }
        }

        else -> {

        }
    }

    return saveAnswerEventOptionItemDtoList
}


fun List<FormQuestionResponseEntity>.convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto(
    type: QuestionType,
    optionsItemEntityList: List<OptionItemEntityState>,
    forExcel: Boolean = false
): List<SaveAnswerEventOptionItemDto> {
    val saveAnswerEventOptionItemDtoList = mutableListOf<SaveAnswerEventOptionItemDto>()
    if (type == QuestionType.Form) {
        this.forEach { formQuestionResponseEntity ->
                val saveAnswerEventOptionItemDto = SaveAnswerEventOptionItemDto(
                    optionId = formQuestionResponseEntity.optionId,
                    selectedValue = formQuestionResponseEntity.selectedValue,
                    referenceId = formQuestionResponseEntity.referenceId,
                    optionDesc = optionsItemEntityList.find { it.optionId == formQuestionResponseEntity.optionId }?.optionItemEntity?.display
                        ?: BLANK_STRING,
                    selectedValueWithIds = optionsItemEntityList.find { it.optionId == formQuestionResponseEntity.optionId }?.optionItemEntity?.values?.filter {
                        formQuestionResponseEntity.selectedValueId.contains(
                            it.id
                        )
                    } ?: emptyList(),
                    tag = optionsItemEntityList.find { it.optionId == formQuestionResponseEntity.optionId }?.optionItemEntity?.optionTag
                        ?: 0
                )
                saveAnswerEventOptionItemDtoList.add(saveAnswerEventOptionItemDto)
//            }

        }
    }

    return saveAnswerEventOptionItemDtoList
}

fun OptionItemEntity.convertOptionItemEntityToSaveAnswerEventOptionItemDtoForFormWithNone(
    userId: String,
    didiId: Int,
    referenceId: String
): List<SaveAnswerEventOptionItemDto> {

    val saveAnswerEventOptionItemDtoList = mutableListOf<SaveAnswerEventOptionItemDto>()
    val formQuestionResponseEntity =
        this.convertOptionItemEntityToFormResponseEntityForFormWithNone(
            userId = userId,
            didiId = didiId,
            referenceId = referenceId
        )

    val saveAnswerEventOptionItemDto = SaveAnswerEventOptionItemDto(
        optionId = formQuestionResponseEntity.optionId,
        selectedValue = formQuestionResponseEntity.selectedValue,
        referenceId = formQuestionResponseEntity.referenceId,
        optionDesc = this.display
            ?: BLANK_STRING,
        tag = this.optionTag,
        selectedValueWithIds = this.values?.filter {
            formQuestionResponseEntity.selectedValueId.contains(
                it.id
            )
        } ?: emptyList()
    )

    saveAnswerEventOptionItemDtoList.add(saveAnswerEventOptionItemDto)


    return saveAnswerEventOptionItemDtoList

}

fun OptionItemEntity.convertOptionItemEntityToFormResponseEntityForFormWithNone(
    userId: String,
    didiId: Int,
    referenceId: String
): FormQuestionResponseEntity {
    val formQuestionResponseEntity = FormQuestionResponseEntity(
        userId = userId,
        didiId = didiId,
        surveyId = this.surveyId,
        sectionId = this.sectionId,
        questionId = this.questionId ?: 0,
        optionId = this.optionId ?: 0,
        selectedValue = this.selectedValue ?: BLANK_STRING,
        referenceId = referenceId,
        selectedValueId = listOf(selectedValueId)
    )
    return formQuestionResponseEntity
}

fun List<OptionItemEntity>.toOptionItemStateList(): List<OptionItemEntityState> {
    val optionsItemEntityStateList = ArrayList<OptionItemEntityState>()
    this.forEach { optionItemEntity ->
        optionsItemEntityStateList.add(
            OptionItemEntityState(
                optionItemEntity.optionId,
                optionItemEntity,
                !optionItemEntity.conditional
            )
        )
    }
    return optionsItemEntityStateList
}

fun List<FormResponseObjectDto>.convertFormResponseObjectToSaveAnswerEventOptionDto(
    optionsItemEntityList: List<OptionItemEntity>
): List<SaveAnswerEventOptionItemDto> {
    val saveAnswerEventOptionItemDtoList = mutableListOf<SaveAnswerEventOptionItemDto>()

    this.forEach { formResponseObjectDto ->
        formResponseObjectDto.memberDetailsMap.forEach { memberDetails ->
            val saveAnswerEventOptionItemDto = SaveAnswerEventOptionItemDto(
                optionId = memberDetails.key,
                selectedValue = memberDetails.value,
                referenceId = formResponseObjectDto.referenceId,
                optionDesc = optionsItemEntityList.find { it.optionId == memberDetails.key }?.display
                    ?: BLANK_STRING,
                tag = optionsItemEntityList.find { it.optionId == memberDetails.key }?.optionTag
                    ?: 0

            )
            saveAnswerEventOptionItemDtoList.add(saveAnswerEventOptionItemDto)
        }
    }

    return saveAnswerEventOptionItemDtoList
}

fun List<InputTypeQuestionAnswerEntity>.convertInputTypeQuestionToEventOptionItemDto(
    questionId: Int,
    questionType: QuestionType,
    optionsItemEntity: List<OptionItemEntityState>
): List<SaveAnswerEventOptionItemDto> {
    val saveAnswerEventOptionItemDtoList = mutableListOf<SaveAnswerEventOptionItemDto>()

    if (questionType != QuestionType.InputNumber)
        return emptyList()

    if (questionId == 0 || questionId == -1)
        return emptyList()

    val filteredAnswerListForQuestion = this.filter { it.questionId == questionId }
    filteredAnswerListForQuestion.forEach { inputTypeQuestionAnswerEntity ->
        val saveAnswerEventOptionItemDto = SaveAnswerEventOptionItemDto(
            optionId = inputTypeQuestionAnswerEntity.optionId,
            selectedValue = inputTypeQuestionAnswerEntity.inputValue,
            optionDesc = optionsItemEntity.find { it.optionId == inputTypeQuestionAnswerEntity.optionId }?.optionItemEntity?.display
                ?: BLANK_STRING,
            tag = optionsItemEntity.find { it.optionId == inputTypeQuestionAnswerEntity.optionId }?.optionItemEntity?.optionTag
                ?: 0

        )
        saveAnswerEventOptionItemDtoList.add(saveAnswerEventOptionItemDto)
    }

    return saveAnswerEventOptionItemDtoList
}

fun List<FormResponseObjectDto>.getIndexForReferenceId(referenceId: String): Int {
    return this.map { it.referenceId }.indexOf(referenceId)
}


//TODO remove this list and fetch this from server.
val tagList: List<TagMappingDto> = listOf(
    TagMappingDto(id = 1, name = "Personal Info"),
    TagMappingDto(id = 2, name = "HouseHold Details"),
    TagMappingDto(id = 3, name = "Food Security"),
    TagMappingDto(id = 4, name = "Social Inclusion"),
    TagMappingDto(id = 5, name = "Women Empowerment"),
    TagMappingDto(id = 6, name = "Financial Inclusion"),
    TagMappingDto(id = 7, name = "Household Entitlements"),
    TagMappingDto(id = 8, name = "Livelihood Sources"),
    TagMappingDto(id = 9, name = "Name"),
    TagMappingDto(id = 10, name = "Age"),
    TagMappingDto(id = 11, name = "Photo"),
    TagMappingDto(id = 12, name = "Caste"),
    TagMappingDto(id = 13, name = "Aadhar"),
    TagMappingDto(id = 14, name = "Voter"),
    TagMappingDto(id = 15, name = "Martial Status"),
    TagMappingDto(id = 16, name = "Village Name"),
    TagMappingDto(id = 17, name = "Village Organization Name"),
    TagMappingDto(id = 18, name = "Hamlet / Tola Name"),
    TagMappingDto(id = 19, name = "Small group name"),
    TagMappingDto(id = 20, name = "House No"),
    TagMappingDto(id = 21, name = "Able-bodied women"),
    TagMappingDto(id = 22, name = "UPCM name"),
    TagMappingDto(id = 23, name = "UPCM contact"),
    TagMappingDto(id = 24, name = "HouseHoldCount"),
    TagMappingDto(id = 25, name = "Had 2 Meals"),
    TagMappingDto(id = 26, name = "Food Shortage months"),
    TagMappingDto(id = 27, name = "Owns Kitchen Garden"),
    TagMappingDto(id = 28, name = "Last 7 days food consumption"),
    TagMappingDto(id = 29, name = "Ration Card Type"),
    TagMappingDto(id = 30, name = "PDS Items"),
    TagMappingDto(id = 31, name = "Last 1 month"),
    TagMappingDto(id = 32, name = "Last 12 months"),
    TagMappingDto(id = 33, name = "At least once"),
    TagMappingDto(id = 34, name = "At least thrice"),
    TagMappingDto(id = 35, name = "SHG Membership Status"),
    TagMappingDto(id = 36, name = "SHG name"),
    TagMappingDto(id = 37, name = "SHG Membership Tenure"),
    TagMappingDto(id = 38, name = "SHG Savings Account"),
    TagMappingDto(id = 39, name = "Amount in SHG Savings"),
    TagMappingDto(id = 40, name = "SHG Loan Status"),
    TagMappingDto(id = 41, name = "Amount of SHG Loan"),
    TagMappingDto(id = 42, name = "Decision Making"),
    TagMappingDto(id = 43, name = "Monetary contribution"),
    TagMappingDto(id = 44, name = "HasBankAccount"),
    TagMappingDto(id = 45, name = "Savings Beyond SHG"),
    TagMappingDto(id = 46, name = "Debt Status"),
    TagMappingDto(id = 47, name = "Debt Sources"),
    TagMappingDto(id = 48, name = "Active Insurance"),
    TagMappingDto(id = 49, name = "Government Scheme"),
    TagMappingDto(id = 50, name = "Has family financial support"),
    TagMappingDto(id = 51, name = "Income Sources Count"),
    TagMappingDto(id = 52, name = "Income Soruces"),
    TagMappingDto(id = 53, name = "Farming Income"),
    TagMappingDto(id = 54, name = "Livestock Income"),
    TagMappingDto(id = 55, name = "Small Business Income"),
    TagMappingDto(id = 56, name = "Casual Labour - Agri Income"),
    TagMappingDto(id = 57, name = "Total Income"),
    TagMappingDto(id = 58, name = "Household Information"),
    TagMappingDto(id = 59, name = "Public Infra"),
    TagMappingDto(id = 60, name = "Key programme"),
    TagMappingDto(id = 61, name = "SHG meeting"),
    TagMappingDto(id = 62, name = "Savings Storage"),
    TagMappingDto(id = 63, name = "NonMarketOutcomes"),
    TagMappingDto(id = 64, name = "Civic Engagement"),
    TagMappingDto(id = 65, name = "Political Participation"),
    TagMappingDto(id = 66, name = "Manual Casual Labour"),
    TagMappingDto(id = 67, name = "NTFP"),
    TagMappingDto(id = 68, name = "MGNREGA"),
    TagMappingDto(id = 69, name = "Remittance from migration"),
    TagMappingDto(id = 70, name = "Self-employment / Shop"),
    TagMappingDto(id = 71, name = "Others"),
    TagMappingDto(id = 72, name = "Name"),
    TagMappingDto(id = 73, name = "Relationship"),
    TagMappingDto(id = 74, name = "Age"),
    TagMappingDto(id = 75, name = "Anganwadi"),
    TagMappingDto(id = 76, name = "Immunized"),
    TagMappingDto(id = 77, name = "School"),
    TagMappingDto(id = 78, name = "Education"),
    TagMappingDto(id = 79, name = "Marital Status"),
    TagMappingDto(id = 80, name = "Disabled"),
    TagMappingDto(id = 84, name = "Reason to leave SHG"),
)
/*listOf(
TagMappingDto(id = 1, name = "FoodSecurtiy"),
TagMappingDto(id = 2, name = "Had 2 Meals per day"),
TagMappingDto(id = 3, name = "Food Shortage"),
TagMappingDto(id = 4, name = "Owns Kitchen Garden"),
TagMappingDto(id = 5, name = "Last 7 days food consumption"),
TagMappingDto(id = 6, name = "Ration Card type"),
TagMappingDto(id = 7, name = "PDS Items"),
TagMappingDto(id = 8, name = "last 1 month"),
TagMappingDto(id = 9, name = "last 12 months"),
TagMappingDto(id = 12, name = "SocialInclusion"),
TagMappingDto(id = 13, name = "SHG Membership Status"),
TagMappingDto(id = 14, name = "SHG Name"),
TagMappingDto(id = 15, name = "SHG Membership Tenure"),
TagMappingDto(id = 16, name = "SHG Savings Account"),
TagMappingDto(id = 17, name = "Amount in SHG Savings"),
TagMappingDto(id = 18, name = "SHG Loan Status"),
TagMappingDto(id = 19, name = "Amount of SHG Loan"),
TagMappingDto(id = 20, name = "WomenEmpowerment"),
TagMappingDto(id = 21, name = "Decision Making"),
TagMappingDto(id = 22, name = "Monetary contribution"),
TagMappingDto(id = 23, name = "FinancialInclusion"),
TagMappingDto(id = 24, name = "HasBankAccount"),
TagMappingDto(id = 25, name = "SavingsBeyondSHG"),
TagMappingDto(id = 26, name = "DebtStatus"),
TagMappingDto(id = 27, name = "DebtSources"),
TagMappingDto(id = 28, name = "Active Insurance"),
TagMappingDto(id = 29, name = "LivelihoodSources"),
TagMappingDto(id = 30, name = "Has family financial support"),
TagMappingDto(id = 31, name = "IncomeSourcesCount"),
TagMappingDto(id = 32, name = "IncomeSources"),
TagMappingDto(id = 37, name = "Farming"),
TagMappingDto(id = 38, name = "Livestock"),
TagMappingDto(id = 39, name = "Small Business"),
TagMappingDto(id = 40, name = "NonMarketOutcomes"),
TagMappingDto(id = 42, name = "Civic Engagement"),
TagMappingDto(id = 43, name = "Political Participation"),
TagMappingDto(id = 10, name = "At least once"),
TagMappingDto(id = 11, name = "At least thrice"),
TagMappingDto(id = 44, name = "Household Information"),
TagMappingDto(id = 45, name = "Public Infra"),
TagMappingDto(id = 46, name = "Key programme"),
)*/

fun List<TagMappingDto>.findTagForId(id: Int): String {
    if (id == -1)
        return BLANK_STRING

    return this.find { it.id == id }?.name ?: BLANK_STRING
}

fun List<TagMappingDto>.findIdFromTag(tag: String): Int {
    if (tag == BLANK_STRING)
        return -1
    return this.find { it.name == tag }?.id ?: -1
}

fun String.getImagePathFromString(): String {
    return try {
        this.split("|").first()
    } catch (ex: Exception) {
        BaselineLogger.e("Utils", "String.getImagePathFromString(): exception: ${ex.message}", ex)
        BLANK_STRING
    }
}

fun numberInEnglishFormat(number: Int, range: IntRange?): String {
    var mNumber = number
    if (range != null) {
        mNumber = number.coerceIn(range)
    }

    return String.format(Locale.ENGLISH, "%s", mNumber)
}

fun numberInEnglishFormat(number: String): String {
    if (TextUtils.isEmpty(number))
        return BLANK_STRING

    if (number.all { it == '0' })
        return number

    val formatter = NumberFormat.getInstance(Locale.ENGLISH)
    val mNumber = formatter.parse(number)

    return formatter.format(mNumber).replace(CONDITIONS_DELIMITER, BLANK_STRING)
}

fun List<FormQuestionResponseEntity>.findUnchangedOptions(storeCacheForResponse: List<FormQuestionResponseEntity>): List<FormQuestionResponseEntity> {
    val unchangedList = this.toMutableList()
    unchangedList.removeAll(storeCacheForResponse)
    return unchangedList
}

@Composable
fun ShowCustomDialog(
    title: String,
    message: String,
    positiveButtonTitle: String? = BLANK_STRING,
    negativeButtonTitle: String? = BLANK_STRING,
    dialogState: DialogState = rememberDialogState(),
    dismissOnBackPress: Boolean? = true,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit
) {
    Dialog(
        onDismissRequest = { }, properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = dismissOnBackPress ?: true
        )
    ) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .background(color = Color.White, shape = RoundedCornerShape(6.dp)),
                ) {
                    Column(
                        Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!title.isNullOrEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                            ) {
                                MainTitle(
                                    title,
                                    Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    align = TextAlign.Center
                                )
                            }
                            Divider(thickness = 1.dp, color = greyBorder)
                        }
                        Text(
                            text = message,
                            style = TextStyle(
                                color = black100Percent,
                                fontSize = 16.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Normal,
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .wrapContentWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {

                            if (!negativeButtonTitle.isNullOrEmpty()) {
                                ButtonNegative(
                                    buttonTitle = negativeButtonTitle
                                        ?: stringResource(id = R.string.cancel_tola_text),
                                    isArrowRequired = false,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onNegativeButtonClick()
                                }

                            } else {
                                Spacer(modifier = Modifier.weight(2f))
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            positiveButtonTitle?.let {
                                if (!it.isNullOrEmpty()) {
                                    ButtonPositive(
                                        buttonTitle = it,
                                        isArrowRequired = false,
                                        isActive = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = 2.dp)
                                    ) {
                                        onPositiveButtonClick()
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

fun openShareSheet(fileUriList: ArrayList<Uri>?, title: String, type: String) {
    if(fileUriList?.isNotEmpty() == true){
        try {


            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.setType(type)
            shareIntent.putExtra(Intent.EXTRA_TITLE, title)
            val chooserIntent = Intent.createChooser(shareIntent, title)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUriList)
                val resInfoList: List<ResolveInfo> =
                    BaselineCore.getAppContext().packageManager
                        .queryIntentActivities(chooserIntent, PackageManager.MATCH_DEFAULT_ONLY)

                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    BaselineCore.getAppContext().grantUriPermission(
                        packageName,
                        fileUriList[0],
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }else{
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUriList)
            }
            BaselineCore.startExternalApp(chooserIntent)
        } catch (ex: Exception) {
            BaselineLogger.e("ExportImportViewModel", "openShareSheet :${ex.message}", ex)
        }
    }else{
       showCustomToast(BaselineCore.getAppContext(), BaselineCore.getAppContext().getString(R.string.no_data_available_at_the_moment))

    }

}

fun getAutoCalculationConditionConditions(value: String): Pair<Int, String>? {
    try {
        val s = value.split(AUTO_CALCULATE_CONDITION_DELIMITER)
        if (s.isEmpty())
            return null

        if (s.size > 2)
            return null

        val questionId = s.first().toInt()

        if (questionId.equals(0))
            return null

        val comparisonCondition = s.last()

        if (comparisonCondition.trim().isEmpty())
            return null

        return Pair(questionId, comparisonCondition)

    } catch (ex: Exception) {
        BaselineLogger.e(
            "Utils",
            "getAutoCalculationConditionConditions -> exception: $ex",
            ex,
            true
        )
        return null
    }

}


