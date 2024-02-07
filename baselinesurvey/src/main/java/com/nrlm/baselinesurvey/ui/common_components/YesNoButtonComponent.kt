package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
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
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.red
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun YesNoButtonComponent(
    title: String = "Select", isMandatory: Boolean = false,
    onOptionSelected: (Int) -> Unit
) {
    val yesNoButtonViewHeight = remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current
    val shgFlag = remember {
        mutableStateOf(-1)
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
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
                    append(title)
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
            },

            )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .border(
                    1.dp,
                    color = lightGray2,
                    RoundedCornerShape(6.dp)
                )
                .background(white, shape = RoundedCornerShape(6.dp))
                .padding(0.dp)
        ) {
            Row(
                Modifier
                    .padding(0.dp)
                    .onGloballyPositioned { coordinates ->
                        yesNoButtonViewHeight.value =
                            with(localDensity) { coordinates.size.height.toDp() }

                    }
            )

            {
                TextButton(
                    onClick = {
                        shgFlag.value = SHGFlag.YES.value
                        onOptionSelected(SHGFlag.YES.value)
                    }, modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 6.dp,
                                bottomStart = 6.dp
                            )
                        )
                        .background(
                            if (shgFlag.value == SHGFlag.YES.value) blueDark else Color.Transparent,
                            RoundedCornerShape(
                                topStart = 6.dp,
                                bottomStart = 6.dp
                            )
                        )
                ) {
                    Text(
                        text = stringResource(id = R.string.option_yes),
                        color = if (shgFlag.value == SHGFlag.YES.value) white else textColorDark
                    )
                }
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(yesNoButtonViewHeight.value)
                        .background(lightGray2)
                )
                TextButton(
                    onClick = {
                        shgFlag.value = SHGFlag.NO.value
                        onOptionSelected(SHGFlag.NO.value)
                    }, modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topEnd = 6.dp,
                                bottomEnd = 6.dp
                            )
                        )
                        .background(
                            if (shgFlag.value == SHGFlag.NO.value) blueDark else Color.Transparent,
                            RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                        )
                ) {
                    Text(
                        text = stringResource(id = R.string.option_no),
                        color = if (shgFlag.value == SHGFlag.NO.value) white else textColorDark
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

}

enum class SHGFlag(val value: Int) {
    YES(1),
    NO(2),
    NOT_MARKED(-1);

    companion object {
        fun fromInt(shgFlagValue: Int): SHGFlag {
            return when (shgFlagValue) {
                YES.value -> YES
                NO.value -> NO
                else -> NOT_MARKED
            }
        }

        fun fromSting(shgFlag: String): SHGFlag {
            return when (shgFlag) {
                "YES" -> YES
                "NO" -> NO
                else -> NOT_MARKED
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            YES -> "YES"
            NO -> "NO"
            NOT_MARKED -> "NOT MARKED"
        }
    }

}