package com.sarathi.missionactivitytask.ui.components


import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.nudge.core.ui.theme.brownDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_28_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_3_dp
import com.nudge.core.ui.theme.dimen_45_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.greyTransparentColor
import com.nudge.core.ui.theme.lightGrayTranslucent
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.white
import com.nudge.core.ui.theme.yellowBg
import com.sarathi.surveymanager.R

@Composable
fun CircularImageViewComponent(
    modifier: Modifier = Modifier,
    imagePath: Uri,
    onImageClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(width = dimen_2_dp, shape = CircleShape, color = brownDark)
            .clip(CircleShape)
            .width(55.dp)
            .height(55.dp)
            .background(color = yellowBg)
            .clickable {
                onImageClick()
            }
            .then(modifier)
    ) {
            Image(
                painter = rememberAsyncImagePainter(
                    imagePath
                ),
                contentDescription = "didi image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .width(dimen_45_dp)
                    .height(dimen_45_dp)
            )
        }
    }

@Composable
fun ShowDidiImageDialog(didiName: String, imagePath: Uri, onCloseClick: () -> Unit) {
    Dialog(
        onDismissRequest = { }, properties = DialogProperties(
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = greyTransparentColor,
                        shape = RoundedCornerShape(6.dp)
                    ),
                verticalArrangement = Arrangement.Center
            ) {

                Box {
                    if (imagePath != Uri.EMPTY) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                imagePath
                            ),
                            contentDescription = "didi image",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                .fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .background(white)
                                .padding(dimen_10_dp)
                                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                .fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                    .fillMaxSize()
                                    .background(color = yellowBg),
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.didi_icon),
                                    contentDescription = "Placeholder didi image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .padding(30.dp)
                                        .align(Alignment.Center)
                                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                        .fillMaxSize()
                                )
                            }
                        }

                    }
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(lightGrayTranslucent)
                    ) {
                        val (titleText, closeButton) = createRefs()
                        Text(
                            text = didiName,
                            style = mediumTextStyle,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(horizontal = dimen_5_dp)
                                .wrapContentWidth()
                                .constrainAs(titleText) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                }
                        )

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "close camera",
                            tint = white,
                            modifier = Modifier
                                .constrainAs(closeButton) {
                                    end.linkTo(parent.end)
                                    top.linkTo(parent.top)
                                }
                                .width(dimen_28_dp)
                                .height(dimen_28_dp)
                                .padding(dimen_3_dp)
                                .clickable {
                                    onCloseClick()
                                }
                        )


                    }

                }
            }


        }
    }
}
