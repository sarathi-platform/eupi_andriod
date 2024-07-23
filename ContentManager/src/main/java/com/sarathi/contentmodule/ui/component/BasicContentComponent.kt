package com.sarathi.contentmodule.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.theme.black1
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_100_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_26_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.white
import com.sarathi.contentmodule.R
import com.sarathi.contentmodule.constants.Constants.ALPHA_10
import com.sarathi.contentmodule.constants.Constants.BLANK_STRING
import com.sarathi.dataloadingmangement.download_manager.FileType


@Composable
fun BasicContentComponent(
    contentType: String = BLANK_STRING,
    isLimitContentData: Boolean = false,
    totalContent: Int = 0,
    contentTitle: String = BLANK_STRING,
    onClick: () -> Unit
) {
    ContentView(
        contentType = contentType,
        contentTitle = contentTitle,
        isLimitContentData = isLimitContentData,
        totalContent = totalContent,
        onClick = onClick
    )
}

@Composable
private fun ContentView(
    contentType: String,
    contentTitle: String,
    isLimitContentData: Boolean,
    totalContent: Int,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                onClick()
            }
    ) {
        if (isLimitContentData) {
            ButtonComponent(title = stringResource(R.string.more, totalContent))
        } else {
            Box(
                modifier = Modifier
                    .padding(start = dimen_16_dp, end = dimen_16_dp)
                    .size(60.dp)
                    .clip(shape = CircleShape)
                    .border(
                        dimen_1_dp, color = blueDark, RoundedCornerShape(dimen_100_dp)
                    )
                    .background(color = Color.Transparent)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ContentData(contentType = contentType)
            }
            if (contentTitle.isNotBlank()) {
                Text(
                    modifier = Modifier.width(dimen_60_dp),
                    text = contentTitle,
                    style = smallTextStyle.copy(color = blueDark),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ContentData(contentType: String, modifier: Modifier = Modifier) {
    when (contentType.toUpperCase(Locale.current)) {
        FileType.IMAGE.name -> {
            val painter: Painter = painterResource(id = R.drawable.ic_image_placeholder)
            Image(
                modifier = Modifier.then(modifier),
                painter = painter, contentDescription = null, colorFilter = ColorFilter.tint(
                    blueDark
                )
            )
        }

        FileType.AUDIO.name -> {
            val painter: Painter = painterResource(id = R.drawable.ic_audio_placeholder)
            Image(
                modifier = Modifier.then(modifier),
                painter = painter, contentDescription = null, colorFilter = ColorFilter.tint(
                    blueDark
                )
            )
        }

        FileType.VIDEO.name -> {
            ImageOverlay(
                resId = R.drawable.ic_video_placeholder
            )
        }

        FileType.FILE.name -> {
            val painter: Painter =
                painterResource(id = R.drawable.ic_file_place_holder_icon)
            Image(
                modifier = Modifier.then(modifier),
                painter = painter, contentDescription = null, colorFilter = ColorFilter.tint(
                    blueDark
                )
            )
        }

        FileType.TEXT.name -> {
            val painter: Painter = painterResource(id = R.drawable.baseline_text_fields_24)
            Image(
                painter = painter, contentDescription = null, colorFilter = ColorFilter.tint(
                    blueDark
                )
            )
        }
    }
}

@Composable
fun ImageOverlay(resId: Int, modifier: Modifier = Modifier) {
    val painter: Painter = painterResource(id = resId)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(black1.copy(ALPHA_10))
        ) {
            Image(
                painter = painter,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                contentDescription = BLANK_STRING,
                colorFilter = ColorFilter.tint(
                    blueDark
                )
            )
        }

        Box(
            modifier = Modifier
                .size(dimen_26_dp)
                .clip(shape = CircleShape)
                .background(color = white),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = blueDark,

                )
        }
    }
}

