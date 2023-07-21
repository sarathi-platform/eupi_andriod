package com.patsurvey.nudge.activities.survey

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.text.isDigitsOnly
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.lightGray2
import com.patsurvey.nudge.activities.ui.theme.placeholderGrey
import com.patsurvey.nudge.activities.ui.theme.quesOptionTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.utils.ASSET_VALUE_LENGTH
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.IncrementDecrementView
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QUESTION_FLAG_RATIO
import com.patsurvey.nudge.utils.onlyNumberField
import com.patsurvey.nudge.utils.roundOffDecimalPoints
import com.patsurvey.nudge.utils.showToast


@Composable
fun NumericFieldTypeQuestion(
    modifier: Modifier,
    questionNumber: Int,
    question: String,
    questionId: Int,
    didiId: Int,
    questionFlag:String,
    optionList: List<OptionsItem>,
    totalValueTitle:String,
    viewModel: QuestionScreenViewModel? = null,
    showNextButton: Boolean = true,
    onSubmitClick: () -> Unit
) {
val context = LocalContext.current

    Box {
        ConstraintLayout(modifier = modifier
            .fillMaxSize()
            .align(Alignment.TopCenter)) {
            val (questionBox, optionBox, submitBox) = createRefs()
            Text(
                modifier = Modifier
                    .border(
                        BorderStroke(1.dp, lightGray2),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(14.dp)
                    .fillMaxWidth()
                    .constrainAs(questionBox) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    },
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("$questionNumber.")
                    }
                    append(" $question")
                },
                style = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = textColorDark
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .constrainAs(optionBox) {
                        start.linkTo(parent.start)
                        top.linkTo(questionBox.bottom)
                        bottom.linkTo(submitBox.top)
                        height = Dimension.fillToConstraints
                    }
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(optionList) { index, option ->

                        IncrementDecrementView(modifier = Modifier,
                            option.display ?: BLANK_STRING,
                            option.count ?: 0,
                            questionFlag = questionFlag,
                            optionImageUrl = option.optionImage?: BLANK_STRING,
                            optionValue = option.optionValue,
                            optionList = optionList,
                            onDecrementClick = {
                                if(viewModel?.prefRepo?.questionScreenOpenFrom() != PageFrom.DIDI_LIST_PAGE.ordinal)
                                    viewModel?.updateDidiQuesSection(didiId, PatSurveyStatus.INPROGRESS.ordinal)
                                val numericAnswerEntity = NumericAnswerEntity(
                                    optionId = option.optionId ?: 0,
                                    weight = option.weight ?: 1,
                                    questionId = questionId,
                                    count = it,
                                    didiId = didiId,
                                    id = 0,
                                    questionFlag = questionFlag
                                )
                                option.count = it
                                viewModel?.updateNumericAnswer(numericAnswerEntity,index,optionList)
                            },
                            onIncrementClick = {
                                if(viewModel?.prefRepo?.questionScreenOpenFrom() != PageFrom.DIDI_LIST_PAGE.ordinal)
                                    viewModel?.updateDidiQuesSection(didiId, PatSurveyStatus.INPROGRESS.ordinal)
                                val numericAnswerEntity = NumericAnswerEntity(
                                    optionId = option.optionId ?: 0,
                                    weight = option.weight ?: 1,
                                    questionId = questionId,
                                    count = it,
                                    didiId = didiId,
                                    id = 0,
                                    questionFlag = questionFlag
                                )
                                option.count = it
                                viewModel?.updateNumericAnswer(numericAnswerEntity,index,optionList)
                            },
                            onValueChange = {
                                if(viewModel?.prefRepo?.questionScreenOpenFrom() != PageFrom.DIDI_LIST_PAGE.ordinal)
                                    viewModel?.updateDidiQuesSection(didiId, PatSurveyStatus.INPROGRESS.ordinal)
                                val numericAnswerEntity = NumericAnswerEntity(
                                    optionId = option.optionId ?: 0,
                                    weight = option.weight ?: 1,
                                    questionId = questionId,
                                    count = if(it.isEmpty()) 0 else it.toInt(),
                                    didiId = didiId,
                                    id = 0,
                                    questionFlag = questionFlag
                                )
                                option.count = if(it.isEmpty()) 0 else it.toInt()
                                viewModel?.updateNumericAnswer(numericAnswerEntity,index,optionList)
                            }, onLimitFailed = {
                                showToast(context, context.getString(R.string.earning_member_can_not_be_more_than_family_members))
                            })
                    }

                    item {
                        Text(
                            text = totalValueTitle?: BLANK_STRING,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 5.dp, start = 5.dp),
                            style = TextStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp)
                                .padding(bottom = 60.dp)
                                .background(Color.White)
                                .wrapContentHeight()
                                .border(
                                    width = 1.dp,
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black
                                )
                        ) {
                            OutlinedTextField(
                                value = roundOffDecimalPoints(viewModel?.totalAmount?.value?:0.00).toString(),
                                readOnly = true,
                                onValueChange = {
                                        viewModel?.totalAmount?.value = it.toDouble()                                },
                                placeholder = {
                                    Text(
                                        text = stringResource(id = R.string.enter_amount), style = TextStyle(
                                            fontFamily = NotoSans,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp
                                        ), color = placeholderGrey
                                    )
                                },
                                textStyle = TextStyle(
                                    fontFamily = NotoSans,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp
                                ),
                                singleLine = true,
                                maxLines = 1,
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor = textColorDark,
                                    backgroundColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                ), keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    autoCorrect = true,
                                    keyboardType = KeyboardType.Number,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .padding(top = 20.dp)
                    .padding(bottom = 8.dp)
                    .constrainAs(submitBox) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                if (showNextButton) {
                    ButtonPositive(
                        buttonTitle = stringResource(id = R.string.next),
                        isArrowRequired = false,
                        isActive = true,
                        modifier = Modifier.height(45.dp)
                    ) {
                        onSubmitClick()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NumericFieldTypeQuestionPreview() {
    val optionList = mutableListOf<OptionsItem>()
    for (i in 1..5) {
        optionList.add(OptionsItem("Option Value $i", i + 1, i, 1, "Summery"))
    }
   /* NumericFieldTypeQuestion(
        modifier = Modifier,
        questionNumber = 1,
        question = "How many Goats?",
        questionId = 1,
        didiId = 1,
        optionList = optionList,
        totalValueTitle="Total Value",
        questionFlag = "ratio",
        onAssetValueChange = {}
    ) {}*/
}


@Composable
fun NumericOptionCard(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    index: Int,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(6.dp))
        .background(if (selectedIndex == index) blueDark else languageItemActiveBg)
        .clickable {
            onOptionSelected(index)
        }
        .padding(horizontal = 10.dp)
        .then(modifier)) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopStart,
        ) {
            Row(
                Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buttonTitle,
                    color = if (selectedIndex == index) Color.White else Color.Black,
                    style = quesOptionTextStyle
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun NumericOptionCardPreview() {
    NumericOptionCard(
        modifier = Modifier, "Option", index = 0,
        onOptionSelected = {}, selectedIndex = 0
    )
}

