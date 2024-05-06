package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.summaryCardViewBlue
import com.nrlm.baselinesurvey.ui.theme.textColorDark

@Composable
fun TextWithReadMoreComponent(
    modifier: Modifier = Modifier.padding(horizontal = dimen_10_dp),
    textStyle: TextStyle = largeTextStyle,
    color: Color = textColorDark,
    maxLines: Int = 4,
    contentData: String = "Please ask the Didis if they have any income from the following:\\n\\n Farming\\n\\n Livestock rearing\\n\\n Manual casual labour \\n\\n Selling bamboo shoots, mushrooms, fruits, spices etc.\\n\\n MNREGA\\n\\n Work from migrating to another city or place\\n\\n Self-employment work like cleaning, carpentry, small business, handicraft, handloom etc.\\n\\n Shop"
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        TextWithReadMore(
            text = contentData,
            textStyle = textStyle,
            maxLines = maxLines,
            onClickReadMore = { showDialog = true })

        if (showDialog) {
            ShowCustomDialog(title = stringResource(R.string.content),
                message = contentData,
                negativeButtonTitle = stringResource(id = R.string.close),
                onNegativeButtonClick = {
                    showDialog = false
                },
                onPositiveButtonClick = {

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
            text = text,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            maxLines = if (expand) Int.MAX_VALUE else maxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                layoutResult.value = result
            },
            style = textStyle,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (layoutResult.value != null && !expand && layoutResult.value!!.hasVisualOverflow) {

            LinkTextButtonWithIcon(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 8.dp),
                title = stringResource(R.string.read_more),
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