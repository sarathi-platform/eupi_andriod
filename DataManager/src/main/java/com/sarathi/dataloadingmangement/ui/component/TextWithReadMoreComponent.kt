package com.sarathi.dataloadingmangement.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.commonUi.ShowSingleButtonCustomDialog
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.summaryCardViewBlue
import com.sarathi.dataloadingmangement.R

@Composable
fun TextWithReadMoreComponent(
    modifier: Modifier = Modifier.fillMaxWidth(),
    textStyle: TextStyle = defaultTextStyle,
    title: String = BLANK_STRING,
    maxLines: Int = 1,
    contentData: String = BLANK_STRING
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
    ) {
        TextWithReadMore(
            text = contentData,
            textStyle = textStyle,
            maxLines = maxLines,
            onClickReadMore = { showDialog = true })

        if (showDialog) {
            ShowSingleButtonCustomDialog(
                title = title,
                message = contentData,
                positiveButtonTitle = stringResource(id = R.string.close),
                onPositiveButtonClick = {
                    showDialog = false
                })
        }
    }
}

@Composable
fun TextWithReadMore(
    text: String,
    textStyle: TextStyle,
    readMoreButtonLabel: String = "See all",
    maxLines: Int,
    onClickReadMore: () -> Unit
) {
    var expand by remember { mutableStateOf(false) }

    Column {
        val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
        Text(
            modifier = Modifier
                .align(Alignment.Start),
            text = text,
            lineHeight = 24.sp,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                layoutResult.value = result
            },
            style = textStyle.copy(color = blueDark),
        )
        if (layoutResult.value != null && !expand && layoutResult.value!!.hasVisualOverflow) {

            LinkTextButtonWithIcon(
                modifier = Modifier
                    .align(Alignment.Start),
                title = readMoreButtonLabel,
                textColor = summaryCardViewBlue,
                iconTint = summaryCardViewBlue
            ) {
                onClickReadMore()
            }
        }
    }
}