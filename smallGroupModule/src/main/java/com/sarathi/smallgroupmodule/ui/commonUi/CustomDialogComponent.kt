package com.sarathi.smallgroupmodule.ui.commonUi

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.black100Percent
import com.nudge.core.ui.theme.deleteButtonBg
import com.nudge.core.ui.theme.greyBorder

@Composable
fun CustomDialogComponent(
    title: String,
    message: String,
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
                                    buttonTitle = negativeButtonTitle,
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
                                    CustomButton(
                                        buttonTitle = it,
                                        buttonColor = deleteButtonBg,
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