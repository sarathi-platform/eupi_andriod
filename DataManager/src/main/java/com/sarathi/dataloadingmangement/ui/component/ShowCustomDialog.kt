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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.black100Percent
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_300_dp
import com.nudge.core.ui.theme.dimen_5_dp
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
                        .background(color = white, shape = RoundedCornerShape(6.dp)),
                ) {
                    Column(
                        Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                .heightIn(max = dimen_300_dp)
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

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {

                            if (!negativeButtonTitle.isNullOrEmpty()) {
                                ButtonPositive(
                                    buttonTitle = negativeButtonTitle,
                                    isArrowRequired = false,
                                    isActive = true,
                                    modifier = Modifier.weight(1f)
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
