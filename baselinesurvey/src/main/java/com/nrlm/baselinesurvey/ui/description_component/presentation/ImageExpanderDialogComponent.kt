package com.nrlm.baselinesurvey.ui.description_component.presentation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberImagePainter
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.greyTransparentColor
import com.nrlm.baselinesurvey.ui.theme.lightGrayTranslucent
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.ui.theme.yellowBg
import java.io.File

@Composable
fun ImageExpanderDialogComponent(imagePath: String, onCloseClick: () -> Unit) {
    Dialog(
        onDismissRequest = {
                           onCloseClick()
        }, properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
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
                    if (imagePath.isNotEmpty()) {
                        Image(
                            painter = rememberImagePainter(
                                imagePath
                               /* Uri.fromFile(
                                    File(
                                        imagePath
                                    )
                                )*/
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
                            modifier = androidx.compose.ui.Modifier
                                .background(white)
                                .padding(10.dp)
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
                        /*Text(
                            text = didi.name,
                            style = TextStyle(
                                color = white,
                                fontSize = 16.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .wrapContentWidth()
                                .constrainAs(titleText) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                }
                        )*/

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "close camera",
                            tint = white,
                            modifier = Modifier
                                .constrainAs(closeButton) {
                                    end.linkTo(parent.end)
                                    top.linkTo(parent.top)
                                }
                                .width(28.dp)
                                .height(28.dp)
                                .padding(3.dp)
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