package com.sarathi.missionactivitytask.ui.basic_content.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.BLANK_STRING
import com.sarathi.missionactivitytask.ui.basic_content.constants.ContentShape


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
        //lightGray2
        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .size(60.dp)
                .clip(shape = CircleShape)
                .border(
                    1.dp, color = Color.Black, RoundedCornerShape(100.dp)
                )
                .background(color = Color.Transparent)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ContentData(contentType = contentType, moreContentData = moreContentData)
        }
        if (contentTitle.isNotBlank()) {
            //style = smallTextStyleMediumWeight2
            Text(
                text = contentTitle,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ContentData(contentType: String, moreContentData: String) {
//    if (contentType.equals(ContentType.IMAGE.name)) {
//        val painter: Painter = painterResource(id = R.drawable.nrlm_logo)
//        Image(painter = painter, contentDescription = null)
//    } else if (contentType.equals(ContentType.VIDEO.name)) {
//        ImageOverlay(resId = R.drawable.ttn_logo)
//    } else if (contentType.equals(ContentType.FILE.name)) {
//        val painter: Painter =
//            painterResource(id = R.drawable.baseline_food_security_sutritional_diversity)
//        Image(painter = painter, contentDescription = null)
//    } else if (contentType.equals(ContentType.MORE_DATA.name)) {
//        Text(
//            text = moreContentData,
//            textAlign = TextAlign.Center,
//            style = smallerTextStyle,
//            fontSize = 10.sp
//        )
//    }
}

@Composable
fun ImageOverlay(resId: Int) {
    val painter: Painter = painterResource(id = resId)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "",
        )
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.Center),
            contentAlignment = Alignment.Center

        ) {
            //textColorDark
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.Black,
            )
        }
    }

}

