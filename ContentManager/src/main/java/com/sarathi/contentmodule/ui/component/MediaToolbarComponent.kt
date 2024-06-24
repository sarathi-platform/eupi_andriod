package com.sarathi.contentmodule.ui.component

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.largeTextStyle
import com.sarathi.contentmodule.R
import com.sarathi.contentmodule.media.getActivity

@Composable
fun MediaToolbarComponent(
    title: String,
    modifier: Modifier = Modifier,
    onBackIconClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    val activity = getActivity()
    val configuration = LocalConfiguration.current
    TopAppBar(
        backgroundColor = Color.White,
        modifier = modifier
    ) {
        IconButton(
            onClick = onBackIconClick,
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                tint = blueDark,
                contentDescription = "Back Button"
            )
        }

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(0.dp, 0.dp, 0.dp, 7.dp),
                text = title,
                color = blueDark,
                textAlign = TextAlign.Center,
                style = largeTextStyle
            )
        }

        Row {
            IconButton(
                onClick = {
                    val isLandscape =
                        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                    activity?.requestedOrientation = if (isLandscape) {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                },
                modifier = Modifier
            ) {
                Icon(
                    painter = painterResource(id = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) R.drawable.content_fullscreen else R.drawable.content_fullscreen_exit_24),
                    contentDescription = "Full Screen"
                )
            }

//            IconButton(
//                onClick = { onDownloadClick() },
//                modifier = Modifier
//            ) {
//                Icon(painterResource(id = R.drawable.baseline_download_for_offline_24), contentDescription = "Download Button")
//            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ToolbarComponentPreview() {
    MediaToolbarComponent(title = "Setting", modifier = Modifier,
        onBackIconClick = { /* Handle back icon click */ },
        onDownloadClick = {
            // Handle download
        }
    )
}