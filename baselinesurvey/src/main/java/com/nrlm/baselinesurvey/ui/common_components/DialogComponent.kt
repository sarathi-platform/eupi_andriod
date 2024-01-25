package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.buttonTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleMediumWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark

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