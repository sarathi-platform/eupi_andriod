package com.nudge.core.ui.commonUi.componet_.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nudge.core.BLANK_STRING
import com.nudge.core.R
import com.nudge.core.enums.ApiStatus
import com.nudge.core.ui.commonUi.CustomSpacer
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.redOffline
import com.nudge.core.ui.theme.smallTextStyleNormalWeight
import com.nudge.core.ui.theme.smallerTextStyleNormalWeight
import com.nudge.core.ui.theme.white


@Composable
fun LoadingDataComponent(
    modifier: Modifier = Modifier,
    title: String = BLANK_STRING,
    subTitle: String = BLANK_STRING,
    isVisible: Boolean = true,
    apiStatus: ApiStatus = ApiStatus.INPROGRESS
) {

    AnimatedVisibility(
        visible = isVisible,
        modifier = Modifier.clipToBounds(),
        enter = expandVertically() + fadeIn(initialAlpha = 0.6f),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    when (apiStatus) {
                        ApiStatus.INPROGRESS -> greenOnline
                        ApiStatus.SUCCESS -> greenOnline
                        ApiStatus.FAILED -> redOffline
                        else -> greenOnline
                    }
                )
                .then(modifier)
        ) {

            Row {
                Column(
                    modifier = Modifier.weight(2f)

                ) {

                    Text(
                        text = title,
                        style = smallTextStyleNormalWeight,
                        color = white,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()

                    )
                    if (subTitle.isNotEmpty()) {
                        Text(
                            text = subTitle,
                            style = smallerTextStyleNormalWeight,
                            color = white,
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
                        color = white,
                        strokeWidth = dimen_2_dp
                    )
                }
                if (apiStatus == ApiStatus.SUCCESS) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_check),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            white
                        ),
                        modifier = Modifier
                            .size(dimen_18_dp)
                            .align(Alignment.CenterVertically)
                    )
                }
                CustomSpacer()
            }


        }
    }

}

@Preview(showBackground = true)
@Composable
fun LoadingDataComponentPreview() {

    LoadingDataComponent(
        title = "Data is Loading..",
        subTitle = "For API Survey",
        isVisible = true,
        apiStatus = ApiStatus.SUCCESS
    )
}