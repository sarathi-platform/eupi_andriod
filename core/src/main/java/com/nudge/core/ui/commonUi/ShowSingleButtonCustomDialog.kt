package com.nudge.core.ui.commonUi

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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.commonUi.componet_.component.MainTitle
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.black100Percent
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_16_sp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.greyBorder
import com.nudge.core.ui.theme.white


@Composable
fun ShowSingleButtonCustomDialog(
    title: String = BLANK_STRING,
    message: String,
    positiveButtonTitle: String? = BLANK_STRING,
    dismissOnBackPress: Boolean? = true,
    onPositiveButtonClick: () -> Unit
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
                        .background(color = white, shape = RoundedCornerShape(dimen_6_dp)),
                ) {
                    Column(
                        Modifier.padding(vertical = dimen_16_dp, horizontal = dimen_16_dp),
                        verticalArrangement = Arrangement.spacedBy(dimen_8_dp)
                    ) {
                        if (title.isNotEmpty()) {
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
                            Divider(thickness = dimen_1_dp, color = greyBorder)
                        }
                        Text(
                            text = message,
                            style = TextStyle(
                                color = black100Percent,
                                fontSize = dimen_16_sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Normal,
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = dimen_5_dp)
                                .wrapContentWidth()
                        )
                        Spacer(modifier = Modifier.height(dimen_4_dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            positiveButtonTitle?.let {
                                if (it.isNotEmpty()) {
                                    ButtonPositive(
                                        buttonTitle = it,
                                        isActive = true,
                                        isArrowRequired = false,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = dimen_2_dp)
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

@Preview(showBackground = true)
@Composable
fun ShowSingleButtonCustomDialogPReview() {
    ShowSingleButtonCustomDialog(
        message = "2- The 'Save' button should be visible",
        positiveButtonTitle = "Close",
        onPositiveButtonClick = {

        }
    )
}
