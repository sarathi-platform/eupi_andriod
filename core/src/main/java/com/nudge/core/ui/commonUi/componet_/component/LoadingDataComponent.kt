package com.nudge.core.ui.commonUi.componet_.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.R
import com.nudge.core.enums.ApiStatus
import com.nudge.core.ui.commonUi.CustomLinearProgressIndicator
import com.nudge.core.ui.commonUi.CustomProgressState
import com.nudge.core.ui.commonUi.CustomSpacer
import com.nudge.core.ui.commonUi.DEFAULT_PROGRESS_VALUE
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.greenDark
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.redOffline
import com.nudge.core.ui.theme.smallerTextStyleNormalWeight
import com.nudge.core.ui.theme.smallestTextStyle
import com.nudge.core.ui.theme.white


@Composable
fun LoadingDataComponent(
    modifier: Modifier = Modifier,
    title: String = BLANK_STRING,
    subTitle: String = BLANK_STRING,
    isVisible: Boolean = false,
    apiStatus: ApiStatus = ApiStatus.INPROGRESS,
    isMultipleDataDownloading: Boolean = false,
    onViewDetailsClick: () -> Unit
) {
    val progressState = CustomProgressState(DEFAULT_PROGRESS_VALUE, BLANK_STRING)
    val isViewVisible = remember { mutableStateOf(isVisible) }
    AnimatedVisibility(
        visible = isViewVisible.value,
        modifier = Modifier.clipToBounds(),
        enter = expandVertically() + fadeIn(initialAlpha = 0.6f),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimen_6_dp)
                .background(
                    when (apiStatus) {
                        ApiStatus.INPROGRESS -> white
                        ApiStatus.SUCCESS -> white
                        ApiStatus.FAILED -> redOffline
                        else -> greenOnline
                    }
                )
                .then(modifier)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isViewVisible.value = false
                    },
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(dimen_10_dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_6_dp)
            ) {
                Row {
                    Column(
                        modifier = Modifier.weight(2f)

                    ) {

                        Text(
                            text = title,
                            style = smallerTextStyleNormalWeight,
                            color = blueDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()

                        )
                        if (subTitle.isNotEmpty()) {
                            Text(
                                text = subTitle,
                                style = smallestTextStyle,
                                color = blueDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    if (apiStatus == ApiStatus.INPROGRESS || apiStatus == ApiStatus.FAILED) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(dimen_18_dp)
                                .align(Alignment.CenterVertically)
                                .padding(end = dimen_2_dp),
                            color = white
                        )
                    }
                    if (apiStatus == ApiStatus.SUCCESS) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_check),
                            contentDescription = null,
                            modifier = Modifier
                                .size(dimen_18_dp)
                                .align(Alignment.CenterVertically)
                        )
                    }


                    CustomSpacer()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(white)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (!isMultipleDataDownloading) {
                            CustomLinearProgressIndicator(
                                progressBarModifier = Modifier
                                    .height(dimen_4_dp)
                                    .padding(vertical = 1.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                progressState = progressState,
                                color = greenOnline
                            )
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {

                                Text(
                                    text = "Multiple screens data Downloading Multiple screens data Downloading...",
                                    style = smallerTextStyleNormalWeight,
                                    color = greenDark,
                                    maxLines = 1,
                                    textAlign = TextAlign.Start,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(horizontal = dimen_5_dp)
                                        .weight(1f)

                                )
                                Text(
                                    text = "View Details",
                                    style = smallerTextStyleNormalWeight,
                                    color = blueDark,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.clickable {
                                        onViewDetailsClick()
                                    }
                                )
                                Spacer(Modifier.size(dimen_2_dp))
                                Image(
                                    painter = painterResource(id = R.drawable.ic_arrow_forward_ios_24),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(dimen_10_dp)
                                        .align(Alignment.CenterVertically)
                                )


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
fun LoadingDataComponentPreview() {

    LoadingDataComponent(
        title = "Mission Screen",
        subTitle = "asd",
        isVisible = false,
        apiStatus = ApiStatus.INPROGRESS,
        isMultipleDataDownloading = true
    ) {}
}