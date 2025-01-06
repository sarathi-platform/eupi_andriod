package com.nudge.core.ui.commonUi.componet_.component

import androidx.annotation.DrawableRes
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
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.black100Percent
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.greyBorder
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.white


@Composable
fun ShowCustomDialog(
    title: String = BLANK_STRING,
    message: String,
    @DrawableRes icon: Int? = null,
    positiveButtonTitle: String? = BLANK_STRING,
    negativeButtonTitle: String? = BLANK_STRING,
    dismissOnBackPress: Boolean? = true,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            onNegativeButtonClick()
        }, properties = DialogProperties(
            dismissOnClickOutside = dismissOnBackPress ?: false,
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
                        .background(color = white, shape = RoundedCornerShape(6.dp)),
                ) {
                    Column(
                        Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (title.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimen_6_dp),
                                modifier = Modifier
                            ) {
                                icon?.let {
                                    Icon(
                                        painterResource(icon),
                                        null,
                                        modifier = Modifier
                                            .weight(0.2f)
                                            .align(Alignment.CenterVertically)
                                    )
                                }

                                MainTitle(
                                    title,
                                    Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    align = TextAlign.Start
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
                                    isActive = true,
                                    buttonTitle = negativeButtonTitle,
                                    isArrowRequired = false,
                                    modifier = Modifier.weight(1f),

                                ) {
                                    onNegativeButtonClick()
                                }

                            } else {
                                Spacer(modifier = Modifier.weight(2f))
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            positiveButtonTitle?.let {
                                if (it.isNotEmpty()) {
                                    ButtonPositive(
                                        buttonTitle = it,
                                        isActive = true,
                                        isArrowRequired = false,
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

@Composable
fun MainTitle(title: String, modifier: Modifier, align: TextAlign = TextAlign.Start) {
    Text(
        text = title,
        style = mediumTextStyle,
        color = black100Percent,
        modifier = modifier,
        maxLines = 1,
        textAlign = align,
        overflow = TextOverflow.Ellipsis,
    )
}

data class AlertDialogState(val showDialog: Boolean = false)