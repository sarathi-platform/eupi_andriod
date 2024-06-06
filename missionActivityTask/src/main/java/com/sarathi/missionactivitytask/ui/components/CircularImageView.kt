package com.sarathi.missionactivitytask.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import coil.compose.rememberImagePainter
import com.nudge.core.BLANK_STRING
import com.nudge.core.getImagePathFromString
import com.sarathi.missionactivitytask.R
import java.io.File

const val DEFAULT_IMAGE_CONTENT_DESCRIPTION = "didi image placeholder"

@Composable
fun CircularImageViewComponent(modifier: Modifier = Modifier, imageProperties: ImageProperties) {
    Box(
        modifier = Modifier
            .then(modifier)
    ) {
        if (imageProperties.path != BLANK_STRING) {
            Image(
                painter = rememberImagePainter(
                    Uri.fromFile(
                        File(
                            imageProperties.path.getImagePathFromString()
                        )
                    )
                ),
                contentDescription = imageProperties.contentDescription,
                contentScale = imageProperties.contentScale,
                alpha = imageProperties.alpha,
                alignment = imageProperties.alignment,
                colorFilter = imageProperties.colorFilter,
                modifier = Modifier
                    .align(Alignment.Center)
                    .then(imageProperties.modifier)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.didi_icon),
                contentDescription = DEFAULT_IMAGE_CONTENT_DESCRIPTION,
                modifier = Modifier
                    .align(Alignment.Center)
                    .then(imageProperties.modifier)
            )
        }

    }
}
