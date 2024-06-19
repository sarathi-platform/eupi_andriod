package com.nrlm.baselinesurvey.ui.common_components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.buttonTextStyle
import com.nrlm.baselinesurvey.ui.theme.greyBorder
import com.nrlm.baselinesurvey.ui.theme.mediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleMediumWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun DialogComponent(
    title: String,
    message: String,
    isBulletShow: Boolean? = false,
    list: List<String>? = emptyList(),
    setShowDialog: (Boolean) -> Unit,
    positiveButtonClicked: () -> Unit
) {
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Start,
                        style = buttonTextStyle,
                        maxLines = 1,
                        color = textColorDark,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isBulletShow == false) {
                        Text(
                            text = message,
                            textAlign = TextAlign.Start,
                            style = smallTextStyleMediumWeight,
                            color = textColorDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        if (list != null) {
                            BulletList(items = list)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ButtonNegative(
                            buttonTitle = stringResource(id = R.string.cancel_tola_text),
                            isArrowRequired = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            setShowDialog(false)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.yes_text),
                            isArrowRequired = false,
                            isActive = true,
                            modifier = Modifier.weight(1f)
                        ) {
                            positiveButtonClicked()
                            setShowDialog(false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowCustomDialog(
    title: String,
    message: String,
    positiveButtonTitle: String? = BLANK_STRING,
    negativeButtonTitle: String? = BLANK_STRING,
    dismissOnBackPress: Boolean? = true,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit
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
                        .background(color = white, shape = RoundedCornerShape(6.dp)),
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
                                    buttonTitle = negativeButtonTitle
                                        ?: stringResource(id = R.string.cancel_tola_text),
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
                                    ButtonPositive(
                                        buttonTitle = it,
                                        isArrowRequired = false,
                                        isActive = true,
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

@Composable
fun BulletList(
    modifier: Modifier = Modifier,
    lineSpacing: Dp = 0.dp,
    items: List<String>,
) {
    Column(modifier = modifier) {
        items.forEach {
            Row {
                Text(
                    text = "\u2022",
                    textAlign = TextAlign.Start,
                    style = buttonTextStyle,
                    maxLines = 1,
                    color = textColorDark,
                )
                Text(
                    text = it,
                    textAlign = TextAlign.Start,
                    style = smallTextStyleMediumWeight,
                    color = textColorDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp)
                )
            }
            if (lineSpacing > 0.dp && it != items.last()) {
                Spacer(modifier = Modifier.height(lineSpacing))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowDialogPreview() {
    DialogComponent(
        "Title",
        "New Message",
        setShowDialog = {},
        list = emptyList(),
        positiveButtonClicked = {})
}