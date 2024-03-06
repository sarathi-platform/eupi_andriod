package com.nrlm.baselinesurvey.ui.common_components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.brownDark
import com.nrlm.baselinesurvey.ui.theme.yellowBg
import java.io.File

@Composable
fun CircularImageViewComponent(modifier: Modifier = Modifier, imagePath: String = BLANK_STRING) {
    Box(
        modifier = modifier
            .border(width = 2.dp, shape = CircleShape, color = brownDark)
            .clip(CircleShape)
            .width(55.dp)
            .height(55.dp)
            .background(color = yellowBg)
            .then(modifier)
    ) {
        if (imagePath != BLANK_STRING) {
            Image(
                painter = rememberImagePainter(
                    Uri.fromFile(
                        File(
                            imagePath
                        )
                    )
                ),
                contentDescription = "didi image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .width(45.dp)
                    .height(45.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.didi_icon),
                contentDescription = "didi image placeholder",
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(45.dp)
                    .height(48.dp)
            )
        }

    }
}