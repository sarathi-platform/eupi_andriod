package com.sarathi.dataloadingmangement.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.summaryCardViewBlue
import com.sarathi.dataloadingmangement.R

@Composable
fun TextWithReadMoreComponent(
    textStyle: TextStyle = defaultTextStyle,
    title: String = BLANK_STRING,
    maxLines: Int = 1,
    contentData: String = BLANK_STRING
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = dimen_5_dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextWithReadMore(
            text = contentData,
            textStyle = textStyle,
            maxLines = maxLines,
            onClickReadMore = { showDialog = true })

        if (showDialog) {
            ShowCustomDialog(title = title,
                message = contentData,
                negativeButtonTitle = stringResource(id = R.string.close),
                onNegativeButtonClick = {
                    showDialog = false
                },
                onPositiveButtonClick = {
                    //Here click listener is not need as we are showing only one button i.e negative button
                })
        }
    }
}

@Composable
fun TextWithReadMore(
    text: String, textStyle: TextStyle, maxLines: Int, onClickReadMore: () -> Unit
) {
    var expand by remember { mutableStateOf(false) }

    Column {
        val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
        Text(
            modifier = Modifier
                .align(Alignment.Start),
            text = text,
            lineHeight = 24.sp,
            maxLines = if (expand) Int.MAX_VALUE else 1,
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
                title = "See all",
                textColor = summaryCardViewBlue,
                iconTint = summaryCardViewBlue
            ) {
                onClickReadMore()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Surface(color = Color.White) {
        TextWithReadMoreComponent()
    }
}