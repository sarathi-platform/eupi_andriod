package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.red
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun RadioOptionTypeComponent(
    optionItemEntityState: OptionItemEntityState,
    isMandatory: Boolean = false,
    selectedValue: String = BLANK_STRING,
    onOptionSelected: (optionValue: String) -> Unit
) {
    val yesNoButtonViewHeight = remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current

    val selectedValue = remember {
        mutableStateOf(selectedValue)
    }

    VerticalAnimatedVisibilityComponent(visible = optionItemEntityState.showQuestion) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = textColorDark
                        )
                    ) {
                        append(optionItemEntityState.optionItemEntity?.display)
                    }
                    withStyle(
                        style = SpanStyle(
                            color = red,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        if (isMandatory) {
                            append(" *")
                        }
                    }
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(white, shape = RoundedCornerShape(6.dp))
                    .padding(0.dp)
            ) {
                Row(
                    Modifier
                        .onGloballyPositioned { coordinates ->
                            yesNoButtonViewHeight.value =
                                with(localDensity) { coordinates.size.height.toDp() }

                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    optionItemEntityState.optionItemEntity?.values?.forEach {  optionValueText ->
                        TextButton(
                            onClick = {
                                selectedValue.value = optionValueText
                                onOptionSelected(optionValueText)
                            }, modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (selectedValue.value.equals(
                                            optionValueText,
                                            ignoreCase = true
                                        )
                                    ) blueDark else Color.Transparent,
                                    RoundedCornerShape(
                                        topStart = 6.dp,
                                        bottomStart = 6.dp,
                                        bottomEnd = 6.dp,
                                        topEnd = 6.dp
                                    )
                                )
                                .border(1.dp, color = lightGray2, RoundedCornerShape(6.dp))

                        ) {
                            Text(
                                text = optionValueText,
                                color = if (selectedValue.value.equals(optionValueText, ignoreCase = true)) white else textColorDark
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
        }
    }

}