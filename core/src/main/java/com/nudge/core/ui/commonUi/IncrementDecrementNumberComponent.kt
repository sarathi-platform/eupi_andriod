package com.nudge.core.ui.commonUi

import android.content.Context
import android.text.TextUtils
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.BLANK_STRING
import com.nudge.core.R
import com.nudge.core.onlyNumberField
import com.nudge.core.showCustomToast
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.redOffline
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.nudge.core.value

@Composable
fun IncrementDecrementNumberComponent(
    title: String = BLANK_STRING,
    currentValue: String? = "0",
    isEditAllowed: Boolean = true,
    onAnswerSelection: (selectValue: String) -> Unit,
    maxValue: Int = MAXIMUM_RANGE,
    editNotAllowedMsg: String = BLANK_STRING,
    isMandatory: Boolean = false,
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        if (title?.isNotBlank() == true) {
            QuestionComponent(title = title, isRequiredField = isMandatory)
        }
        IncrementDecrementCounter(
            label = BLANK_STRING,
            isEditAllowed = isEditAllowed,
            currentCount = currentValue.value("0"),
            maxValue = maxValue,
            onAnswerSelection = onAnswerSelection,
            editNotAllowedMsg = editNotAllowedMsg,
        )

    }
}

@Composable
fun IncrementDecrementCounter(
    label: String? = BLANK_STRING,
    isEditAllowed: Boolean,
    currentCount: String,
    maxValue: Int,
    onAnswerSelection: (selectValue: String) -> Unit,
    editNotAllowedMsg: String,
) {

    val currentCountState: MutableState<String> = remember(currentCount) {
        mutableStateOf(currentCount ?: "")
    }

    val context = LocalContext.current

    Column {
        if (!label.isNullOrEmpty()) {
            Text(text = label, style = defaultTextStyle, color = textColorDark)
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
                                    if (isEditAllowed) {
                                        val incrementValue = incDecValue(0, currentCountState.value)
                                        if (maxValue.toString() >= incrementValue) {
                                            currentCountState.value = incrementValue
                                            onAnswerSelection(if (currentCountState.value.isEmpty()) "0" else currentCountState.value)
                                        }
                                    } else {
                                        editNotAllowedToastBar(editNotAllowedMsg, context)
                                    }
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
                        value = currentCountState.value,
                        readOnly = false,
                        onValueChange = {
                            if (isEditAllowed) {
                                if (onlyNumberField(it)) {
                                    val currentIt = if (it.isEmpty()) 0 else it.toInt()
                                    if (currentIt <= maxValue) {
                                        currentCountState.value = it.ifEmpty { "" }
                                        onAnswerSelection(it)
                                    }
                                }
                            } else {
                                editNotAllowedToastBar(editNotAllowedMsg, context)
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
                            if (isEditAllowed) {
                                val incrementValue = incDecValue(1, currentCountState.value)
                                if (maxValue.toString() >= incrementValue) {
                                    currentCountState.value = incrementValue
                                    onAnswerSelection(if (currentCountState.value.isEmpty()) "0" else currentCountState.value)
                                }
                            } else {
                                editNotAllowedToastBar(editNotAllowedMsg, context)
                            }
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
        CustomVerticalSpacer(size = dimen_6_dp)
    }
}


private fun editNotAllowedToastBar(editNotAllowedMsg: String, context: Context) {
    if (!TextUtils.isEmpty(editNotAllowedMsg)) {
        showCustomToast(
            context,
            editNotAllowedMsg
        )
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
    IncrementDecrementNumberComponent(
        title = "IncrementDecrementView",
        "0",
        onAnswerSelection = {})
}


const val MAXIMUM_RANGE = 999999
