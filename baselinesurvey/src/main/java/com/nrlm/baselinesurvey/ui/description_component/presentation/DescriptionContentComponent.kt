package com.nrlm.baselinesurvey.ui.description_component.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_400_px
import com.nrlm.baselinesurvey.ui.theme.dimen_450_px
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.quesOptionTextStyle
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.DescriptionContentType
import com.nrlm.baselinesurvey.utils.states.DescriptionContentState

@Composable
fun DescriptionContentComponent(
    modifier: Modifier = Modifier,
    buttonClickListener: () -> Unit,
    imageClickListener: (imageTypeDescriptionContent: String) -> Unit,
    videoLinkClicked: (videoTypeDescriptionContent: String) -> Unit,
    descriptionContentState: DescriptionContentState,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.then(modifier)
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimen_16_dp, horizontal = dimen_18_dp),
                contentAlignment = Alignment.Center
            ) {
                //TODO need to remove below check after getting correct paraphrase
                if (descriptionContentState.subTextTypeDescriptionContent.isBlank()) {
                    Text(
                        text = descriptionContentState.textTypeDescriptionContent/*selectedSectionDescription.value*/,
                        color = textColorDark,
                        style = if (descriptionContentState.subTextTypeDescriptionContent.isNotBlank()) largeTextStyle else quesOptionTextStyle,
                        modifier = Modifier.padding(horizontal = dimen_10_dp)
                    )
                }
            }
            if (descriptionContentState.subTextTypeDescriptionContent.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = dimen_16_dp, end = dimen_16_dp, bottom = dimen_18_dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = descriptionContentState.subTextTypeDescriptionContent,
                        color = textColorDark,
                        style = quesOptionTextStyle,
                        modifier = Modifier.padding(horizontal = dimen_10_dp)
                    )
                }
            }
            if (descriptionContentState.imageTypeDescriptionContent != BLANK_STRING) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimen_16_dp, horizontal = dimen_18_dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberImagePainter(
                            descriptionContentState.imageTypeDescriptionContent
                            /*Uri.fromFile(
                                File(
                                    imagePath
                                )
                            )*/,
                            builder = {
                                size(dimen_450_px, dimen_400_px)
                            }
                        ),
                        contentDescription = "didi image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.clickable {
                            imageClickListener(descriptionContentState.imageTypeDescriptionContent)
                        }
                    )
                }
            }

            if (descriptionContentState.videoTypeDescriptionContent != BLANK_STRING) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimen_16_dp, horizontal = dimen_18_dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = descriptionContentState.videoTypeDescriptionContent/*selectedSectionDescription.value*/,
                        color = blueDark,
                        style = smallerTextStyleNormalWeight,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(horizontal = dimen_10_dp)
                            .clickable {
                                videoLinkClicked(descriptionContentState.videoTypeDescriptionContent)
                            }
                    )
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
                        text = stringResource(R.string.ok_text),
                        color = white,
                        style = smallerTextStyle
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

val descriptionContentStateSample = DescriptionContentState(
    listOf(DescriptionContentType.TEXT_TYPE_DESCRIPTION_CONTENT),
    "Sample Text",
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg",
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
)

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun DescriptionContentComponentPreview() {
    Box {
        DescriptionContentComponent(
            buttonClickListener = { /*TODO*/ },
            imageClickListener = { imageTypeDescriptionContent -> },
            videoLinkClicked = {},
            descriptionContentState = descriptionContentStateSample
        )
    }
}