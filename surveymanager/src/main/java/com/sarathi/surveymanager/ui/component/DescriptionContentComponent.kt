package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.smallerTextStyle
import com.nudge.core.ui.theme.smallerTextStyleNormalWeight
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.ui.component.TextWithReadMoreComponent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.utils.DescriptionContentState

@Composable
fun DescriptionContentComponent(
    modifier: Modifier = Modifier,
    buttonClickListener: () -> Unit,
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    descriptionContentState: DescriptionContentState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.then(modifier)
    ) {

        Column {
            if (descriptionContentState.contentDescription.isNotEmpty()) {
                descriptionContentState.contentDescription.forEach { content ->
                    content?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = dimen_16_dp, horizontal = dimen_18_dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (content?.contentType.equals("text")) {
                                TextWithReadMoreComponent(
                                    contentData = content?.contentValue ?: BLANK_STRING,
                                    textStyle = TextStyle(
                                        fontFamily = NotoSans,
                                        fontSize = 12.sp
                                    )
                                )
                            } else {
                                Text(
                                    text = content?.contentKey ?: BLANK_STRING,
                                    color = blueDark,
                                    style = smallerTextStyleNormalWeight,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier
                                        .padding(horizontal = dimen_10_dp)
                                        .clickable {
                                            navigateToMediaPlayerScreen(content)
                                        }
                                )
                            }
                        }
                    }

                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimen_16_dp, horizontal = dimen_24_dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        buttonClickListener()
                    }, shape = RoundedCornerShape(
                        roundedCornerRadiusDefault
                    ), colors = ButtonDefaults.buttonColors(
                        backgroundColor = blueDark,
                        contentColor = white
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 25.dp),
                        text = stringResource(R.string.ok),
                        color = white,
                        style = smallerTextStyle
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


//val descriptionContentStateSample = DescriptionContentState(
//    listOf(DescriptionContentType.TEXT_TYPE_DESCRIPTION_CONTENT),
//    "Sample Text",
//    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg",
//    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
//)

//@Preview(showSystemUi = true, showBackground = true)
//@Composable
//fun DescriptionContentComponentPreview() {
//    Box {
//        DescriptionContentComponent(
//            buttonClickListener = { /*TODO*/ },
//            imageClickListener = { imageTypeDescriptionContent -> },
//            videoLinkClicked = {},
//            descriptionContentState = descriptionContentStateSample
//        )
//    }
//}