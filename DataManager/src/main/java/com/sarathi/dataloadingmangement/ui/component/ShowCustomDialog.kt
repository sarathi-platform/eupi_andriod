package com.sarathi.dataloadingmangement.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.black100Percent
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_300_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.white


@Composable
fun ShowCustomDialog(
    title: String = BLANK_STRING,
    message: String,
    positiveButtonTitle: String? = BLANK_STRING,
    negativeButtonTitle: String? = BLANK_STRING,
    dismissOnClickOutside: Boolean? = false,
    dismissOnBackPress: Boolean? = true,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = { }, properties = DialogProperties(
            dismissOnClickOutside = dismissOnClickOutside ?: true,
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
                                    align = TextAlign.Start
                                )
                            }
                            //Divider(thickness = 1.dp, color = greyBorder)
                        }
                        Box(
                            modifier = Modifier
                                .heightIn(max = dimen_300_dp, min = dimen_20_dp)
                                .padding(horizontal = dimen_5_dp)
                                .verticalScroll(scrollState)
                        ) {
                            Text(
                                text = message,
                                style = defaultTextStyle.copy(color = blueDark),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = Int.MAX_VALUE, // Allow unlimited lines for scrolling
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Clip // Disable ellipsis when scrolling
                            )
                        }

                        Spacer(modifier = Modifier.height(dimen_4_dp))

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

                            Spacer(modifier = Modifier.width(dimen_8_dp))
                            positiveButtonTitle?.let {
                                if (it.isNotEmpty()) {
                                    ButtonPositive(
                                        buttonTitle = it,
                                        isActive = true,
                                        isArrowRequired = false,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = dimen_1_dp)
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
fun ShowCustomDialogPreview() {
    ShowCustomDialog(
        title = "Exit",
        message = "Are you sure you want to exit?",
        dismissOnClickOutside = true,
        onNegativeButtonClick = {},
        onPositiveButtonClick = {},
        positiveButtonTitle = "yes",
        negativeButtonTitle = "no"
    )
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
