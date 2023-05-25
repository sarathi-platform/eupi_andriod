package com.patsurvey.nudge.activities.survey

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.google.gson.Gson
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.EditTextWithTitle
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.model.dataModel.AnswerOptionModel
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.IncrementDecrementView


@Composable
fun NumericFieldTypeQuestion(
    modifier: Modifier,
    questionNumber: Int,
    question: String,
    questionId: Int,
    didiId: Int,
    optionList: List<AnswerOptionModel>,
    viewModel: QuestionScreenViewModel?=null,
    onSubmitClick:()->Unit
) {
    var totalAssetAmount by rememberSaveable { mutableStateOf(viewModel?.totalAssetAmount) }
    var selectedIndex by remember { mutableStateOf(-1) }
    var totalAsset by remember { mutableStateOf(0) }
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .border(
                    BorderStroke(1.dp, lightGray2),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(14.dp)
                .fillMaxWidth(),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = textColorBlueLight,
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
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(optionList) { index, option ->
                    IncrementDecrementView(modifier = Modifier, option.optionText, 0,
                        onDecrementClick = {
                            val numericAnswerEntity = NumericAnswerEntity(
                                optionId = option.id,
                                weight = option.weight ?: 1,
                                questionId = questionId,
                                count = it,
                                didiId = didiId,
                                id = 0
                            )
                            viewModel?.updateNumericAnswer(numericAnswerEntity)
                        },
                        onIncrementClick = {
                            val numericAnswerEntity = NumericAnswerEntity(
                                optionId = option.id,
                                weight = option.weight ?: 1,
                                questionId = questionId,
                                count = it,
                                didiId = didiId,
                                id = 0
                            )
                            viewModel?.updateNumericAnswer(numericAnswerEntity)
                        },
                        onValueChange = {
                        })
                }

                item {
                    Text(
                        text = stringResource(id = R.string.productive_asset_owned_by_family),
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
                            .background(Color.White)
                            .wrapContentHeight()
                            .border(width = 1.dp, shape = RoundedCornerShape(6.dp), color = Color.Black)
                    ) {
                        OutlinedTextField(
                            readOnly = false,
                            value = if ((totalAssetAmount?.value
                                    ?: 0) <= 0
                            ) BLANK_STRING else (totalAssetAmount?.value ?: 0).toString(),
                            onValueChange = {
                                if (it.isEmpty()) {
                                    totalAssetAmount?.value = 0
                                } else {
                                    totalAssetAmount?.value = it.toInt()
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "Enter Amount", style = TextStyle(
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .padding(top = 20.dp)
                            .padding(bottom = 100.dp)
                    ) {
                        ButtonPositive(buttonTitle = "Submit", isArrowRequired = false) {
                          onSubmitClick()
                        }
                }

            }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NumericFieldTypeQuestionPreview() {
    val optionList= mutableListOf<AnswerOptionModel>()
    for (i in 1..5){
        optionList.add(AnswerOptionModel(i,"Option Value $i",false))
    }
    NumericFieldTypeQuestion(
        modifier = Modifier,
        questionNumber = 1,
        question ="How many Goats?" ,
        questionId = 1,
        didiId = 1,
        optionList = optionList,
        onSubmitClick = {}
    )
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
                    color = if(selectedIndex == index) Color.White else Color.Black,
                    style = quesOptionTextStyle
                )
            }
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(5.dp))
    }

}

fun calculateTotalAmount(weight:Int){

}

@Preview(showBackground = true)
@Composable
fun NumericOptionCardPreview(){
    NumericOptionCard(modifier = Modifier,"Option", index = 0,
        onOptionSelected = {}, selectedIndex = 0)
}

