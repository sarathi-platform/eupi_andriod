package com.nrlm.baselinesurvey.ui.question_screen.presentation.questionComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.MAXIMUM_RANGE
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.CustomOutlineTextField
import com.nrlm.baselinesurvey.ui.common_components.VerticalAnimatedVisibilityComponent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.greenOnline
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.red
import com.nrlm.baselinesurvey.ui.theme.redOffline
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.onlyNumberField

@Composable
fun IncrementDecrementView(
    title: String = BLANK_STRING,
    currentValue: String? = "0",
    showQuestion: OptionItemEntityState = OptionItemEntityState.getEmptyStateObject(),
    onAnswerSelection: (selectValue: String) -> Unit,
    isRequiredField: Boolean = false
) {
    val currentCount: MutableState<String> = remember {
        mutableStateOf(currentValue ?: "")
    }
    VerticalAnimatedVisibilityComponent(visible = showQuestion.showQuestion) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            if (!title.equals(BLANK_STRING)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = textColorDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans
                            )
                        ) {
                            append(title)
                        }
                        if (isRequiredField) {
                            withStyle(
                                style = SpanStyle(
                                    color = red,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = NotoSans
                                )
                            ) {
                                append("*")
                            }
                        }
                    }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(width = 1.dp, shape = RoundedCornerShape(6.dp), color = lightGray2)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(
                                RoundedCornerShape(
                                    topStart = 6.dp, bottomStart = 6.dp
                                )
                            )
                            .background(
                                white, RoundedCornerShape(
                                    topStart = 6.dp, bottomStart = 6.dp
                                )
                            ), contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(), horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .clip(
                                        RoundedCornerShape(
                                            topEnd = 6.dp, bottomEnd = 6.dp
                                        )
                                    )
                                    .background(
                                        white, RoundedCornerShape(
                                            topEnd = 6.dp, bottomEnd = 6.dp
                                        )
                                    )
                                    .clickable {
                                        currentCount.value = incDecValue(0, currentCount.value)
                                        onAnswerSelection(if (currentCount.value.isEmpty()) "0" else currentCount.value)
                                    }, contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    painter = painterResource(id = R.drawable.minus_icon),
                                    contentDescription = "decrement counter",
                                    tint = redOffline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                    }
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(lightGray2)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        CustomOutlineTextField(
                            value = currentCount.value,
                            readOnly = false,
                            onValueChange = {
                                if (onlyNumberField(it)) {
                                    var isValidCount = true
                                    if (isValidCount) {
                                        val currentIt = if (it.isEmpty()) 0 else it.toInt()
                                        if (currentIt <= MAXIMUM_RANGE) {
                                            currentCount.value = it.ifEmpty { "" }
                                            onAnswerSelection(it)
                                        }
                                    }
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "", style = TextStyle(
                                        fontFamily = NotoSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                    ), color = textColorDark, modifier = Modifier.fillMaxWidth()
                                )
                            },
                            textStyle = TextStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true,
                            maxLines = 1,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = textColorDark,
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Number,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(lightGray2)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clip(
                                RoundedCornerShape(
                                    topEnd = 6.dp, bottomEnd = 6.dp
                                )
                            )
                            .background(
                                white, RoundedCornerShape(
                                    topEnd = 6.dp, bottomEnd = 6.dp
                                )
                            )
                            .clickable {
                                currentCount.value = incDecValue(1, currentCount.value)
                                onAnswerSelection(if (currentCount.value.isEmpty()) "0" else currentCount.value)
                            }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.plus_icon),
                            contentDescription = "increment counter",
                            tint = greenOnline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

        }
    }

}

fun incDecValue(operation: Int, value: String): String {
    var intValue = 0
    if (value.isNotEmpty()) {
        intValue = value.toInt()
    }

    if (operation == 0) {
        if (intValue > 0) intValue--
    } else {
        if (intValue < MAXIMUM_RANGE) {
            if (value == BLANK_STRING) {
                intValue
            } else {
                intValue++
            }
        }
    }
    return if (intValue < 0) BLANK_STRING else intValue.toString()
}

@Preview(showBackground = true)
@Composable
fun IncrementDecrementViewPreview() {
    IncrementDecrementView(
        title = "IncrementDecrementView",
        "0",
        onAnswerSelection = {})
}