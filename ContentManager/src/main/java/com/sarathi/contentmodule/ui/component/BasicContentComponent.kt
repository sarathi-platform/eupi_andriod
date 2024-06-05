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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.events.theme.black1
import com.nudge.core.ui.events.theme.blueDark
import com.nudge.core.ui.events.theme.lightGray2
import com.nudge.core.ui.events.theme.smallTextStyleMediumWeight2
import com.nudge.core.ui.events.theme.textColorDark
import com.nudge.core.ui.events.theme.white
import com.sarathi.contentmodule.R
import com.sarathi.contentmodule.constants.Constants.BLANK_STRING
import com.sarathi.contentmodule.download_manager.FileType
import com.sarathi.contentmodule.utils.ContentShape


@Composable
fun BasicContentComponent(
    contentType: String = BLANK_STRING,
    contentUrl: String = BLANK_STRING,
    contentValue: String = BLANK_STRING,
    contentShape: ContentShape = ContentShape.CIRCLE,
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
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        if (isLimitContentData) {
            ButtonComponent(title = "+${totalContent} More")
        } else {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .size(60.dp)
                    .clip(shape = CircleShape)
                    .border(
                        1.dp, color = lightGray2, RoundedCornerShape(100.dp)
                    )
                    .background(color = Color.Transparent)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ContentData(contentType = contentType)
            }
            if (contentTitle.isNotBlank()) {
                Text(
                    text = contentTitle,
                    fontSize = 10.sp,
                    style = smallTextStyleMediumWeight2.copy(color = blueDark)
                )
            }
        }
    }
}

@Composable
fun ContentData(contentType: String) {
    when (contentType) {
        FileType.Image.name -> {
            val painter: Painter = painterResource(id = R.drawable.ic_image_placeholder)
            Image(painter = painter, contentDescription = null)
        }

        FileType.Audio.name -> {
            val painter: Painter = painterResource(id = R.drawable.ic_audio_placeholder)
            Image(painter = painter, contentDescription = null)
        }

        FileType.Video.name -> {
            ImageOverlay(resId = R.drawable.ic_video_placeholder)
        }

        FileType.File.name -> {
            val painter: Painter =
                painterResource(id = R.drawable.ic_file_place_holder_icon)
            Image(painter = painter, contentDescription = null)
        }
    }
}

@Composable
fun ImageOverlay(resId: Int) {
    val painter: Painter = painterResource(id = resId)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(black1.copy(.10f))
        ) {
            Image(
                painter = painter,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                contentDescription = "",
            )
        }

        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(shape = CircleShape)
                .background(color = white),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = textColorDark,
            )
        }
    }
}

