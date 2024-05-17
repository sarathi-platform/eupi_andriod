package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.BLANK_STRING
import com.sarathi.missionactivitytask.ui.basic_content.constants.ContentShape
import com.sarathi.missionactivitytask.ui.basic_content.constants.ContentType
import com.sarathi.missionactivitytask.ui.theme.black1
import com.sarathi.missionactivitytask.ui.theme.lightGray2
import com.sarathi.missionactivitytask.ui.theme.smallTextStyleMediumWeight2
import com.sarathi.missionactivitytask.ui.theme.textColorDark
import com.sarathi.missionactivitytask.ui.theme.white


@Composable
@Preview(showBackground = true)
fun BasicContentComponent(
    contentType: String = BLANK_STRING,
    contentUrl: String = BLANK_STRING,
    contentShape: ContentShape = ContentShape.CIRCLE,
    contentTitle: String = "",
    moreContentData: String = "+5 More Data"
) {
    ContentView(
        contentType = contentType,
        contentTitle = contentTitle,
        moreContentData = moreContentData
    )
}

@Composable
private fun ContentView(
    contentType: String,
    contentTitle: String = BLANK_STRING,
    moreContentData: String = BLANK_STRING
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            ContentData(contentType = contentType, moreContentData = moreContentData)
        }
        if (contentTitle.isNotBlank()) {
            Text(
                text = contentTitle,
                fontSize = 10.sp,
                style = smallTextStyleMediumWeight2
            )
        }
    }
}

@Composable
fun ContentData(contentType: String, moreContentData: String) {
    when (contentType) {
        ContentType.IMAGE.name -> {
            val painter: Painter = painterResource(id = R.drawable.ic_sarathi_logo)
            Image(painter = painter, contentDescription = null)
        }

        ContentType.VIDEO.name -> {
            ImageOverlay(resId = R.drawable.ic_mission_inprogress)
        }

        ContentType.FILE.name -> {
            val painter: Painter =
                painterResource(id = R.drawable.ic_mission_inprogress)
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

