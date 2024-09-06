package com.nudge.core.ui.commonUi


import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.nudge.core.ui.theme.brownDark
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.yellowBg

@Composable
fun CircularImageViewComponent(modifier: Modifier = Modifier, imagePath: Uri) {
    Box(
        modifier = modifier
            .border(width = dimen_2_dp, shape = CircleShape, color = brownDark)
            .clip(CircleShape)
            .width(55.dp)
            .height(55.dp)
            .background(color = yellowBg)
            .then(modifier)
    ) {
        Image(
            painter = rememberImagePainter(
                imagePath
            ),
            contentDescription = "didi image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .align(Alignment.Center)
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                .width(45.dp)
                .height(45.dp)
        )
    }

}
