package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.red
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.surveymanager.R

@Composable
fun RadioOptionTypeComponent(
    isMandatory: Boolean = false,
    isContent: Boolean = false,
    selectedValue: String = "",
    onOptionSelected: (optionValue: String, optionId: Int) -> Unit
) {
    val yesNoButtonViewHeight = remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.fillMaxWidth(.9f),
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = textColorDark
                        )
                    ) {
//                            append(optionItemEntityState.optionItemEntity?.display)
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
            if (isContent) {
                Spacer(modifier = Modifier.size(dimen_8_dp))
                Icon(
                    painter = painterResource(id = R.drawable.info_icon),
                    contentDescription = "question info button",
                    Modifier
                        .size(dimen_18_dp)
                        .clickable {
                        },

                    tint = blueDark
                )
            }
        }

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
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RadioOptionTypeComponentPreview() {
}