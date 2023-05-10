package com.patsurvey.nudge.activities.ui.transect_walk

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.*

@Composable
fun AddTolaBox(
    modifier: Modifier = Modifier,
    tolaName: String = "",
    isLocationAvailable: Boolean = false,
    onSaveClicked: (name: String, location: LocationCoordinates?) -> Unit,
    onCancelClicked: () -> Unit
) {
    val activity = LocalContext.current as Activity
    var mTolaName by remember {
        mutableStateOf(tolaName)
    }
    var locationAdded by remember {
        mutableStateOf(isLocationAvailable)
    }
    var location: LocationCoordinates? by remember { mutableStateOf(LocationCoordinates()) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = brownLight,
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .padding(16.dp)
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .scrollable(rememberScrollState(), orientation = Orientation.Vertical),
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        append(stringResource(id = R.string.tola_name_text))
                    }
                    withStyle(
                        style = SpanStyle(
                            color = red,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("*")
                    }
                }
            )
            OutlinedTextField(
                value = mTolaName,
                onValueChange = {
                    mTolaName = it
                },
                placeholder = {
                    Text(text = "Enter Name", style = buttonTextStyle, color = placeholderGrey)
                },
                textStyle = buttonTextStyle,
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = textColorDark,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = borderGrey,
                    unfocusedIndicatorColor = borderGrey,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = greyBorder,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = true,
                            color = Color.Black
                        )

                    ) {
                        location = LocationUtil
                            .getLocation(activity)
                            ?.also {
                                locationAdded = true
                            } ?: LocationCoordinates()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_get_location),
                        contentDescription = "Get Location",
                        modifier = Modifier.absolutePadding(top = 4.dp),
                        tint = blueDark,

                        )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (locationAdded) "Location Added" else stringResource(R.string.get_location_text),
                        textAlign = TextAlign.Center,
                        style = buttonTextStyle,
                        color = if (locationAdded) greenOnline else blueDark
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
            ) {
                ButtonPositive(
                    buttonTitle = stringResource(id = R.string.save_tola_text),
                    isArrowRequired = false,
                    isActive = mTolaName.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onSaveClicked(mTolaName, location)
                }
                Spacer(modifier = Modifier.height(10.dp))
                ButtonOutline(
                    buttonTitle = stringResource(R.string.cancel_tola_text),
                    outlineColor = redDark,
                    textColor = redDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onCancelClicked()
                }

            }
        }
    }
}

@Composable
fun TolaBox(
    modifier: Modifier = Modifier,
    tolaName: String = "khabd",
    tolaLocation: LocationCoordinates?,
    isLocationAvailable: Boolean = true,
    isTransectWalkCompleted: Boolean = false,
    deleteButtonClicked: () -> Unit,
    saveButtonClicked: (newName: String, newLocation: LocationCoordinates?) -> Unit
) {
    var showEditView by remember { mutableStateOf(false) }

    val activity = LocalContext.current as Activity
    var mTolaName by remember {
        mutableStateOf(tolaName)
    }
    var locationAdded by remember {
        mutableStateOf(isLocationAvailable)
    }
    var location: LocationCoordinates? by remember {
        mutableStateOf(
            tolaLocation ?: LocationCoordinates()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderGreyLight,
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp))
            .background(bgGreyLight)
            .padding(16.dp)
            .then(modifier),
    ) {
        ConstraintLayout {
            val (contentBox, showBox, editBox) = createRefs()
            if (!showEditView) {
                Column(modifier = Modifier
                    .constrainAs(contentBox) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }) {


                    Text(
                        text = tolaName,
                        modifier = Modifier.fillMaxWidth(),
                        style = largeTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = textColorDark
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    )
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_location_icn),
                            contentDescription = "Location Icon",
                            modifier = Modifier.absolutePadding(top = 2.dp),
                            tint = Color(0xFFDE0101)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLocationAvailable) "Location Added" else "Not Added",
                            style = smallTextStyleNormalWeight,
                            color = textColorDark
                        )
                    }
                }
                TextButtonWithIcon(
                    modifier = Modifier
                        .constrainAs(showBox) {
                            bottom.linkTo(contentBox.bottom)
                            top.linkTo(contentBox.top)
                            end.linkTo(parent.end)
                        })
                {
                    showEditView = true
                }
            }
            Column(modifier = Modifier
                .constrainAs(editBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }) {
                AnimatedVisibility(
                    visible = showEditView,
                ) {
                    Column(
                        modifier = Modifier,
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = textColorDark,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = NotoSans
                                    )
                                ) {
                                    append("Tola Name")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = red,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = NotoSans
                                    )
                                ) {
                                    append("*")
                                }
                            }
                        )
                        OutlinedTextField(
                            value = mTolaName,
                            onValueChange = {
                                mTolaName = it
                            },
                            placeholder = {
                                Text(
                                    text = "Enter Name",
                                    style = mediumTextStyle,
                                    color = placeholderGrey
                                )
                            },
                            textStyle = mediumTextStyle,
                            singleLine = true,
                            maxLines = 1,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = textColorDark,
                                backgroundColor = Color.White,
                                focusedIndicatorColor = borderGrey,
                                unfocusedIndicatorColor = borderGrey,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = greyBorder,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(
                                        bounded = true,
                                        color = Color.Black
                                    )

                                ) {
                                    location = LocationUtil
                                        .getLocation(activity)
                                        ?.also {
                                            locationAdded = true
                                        } ?: LocationCoordinates()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center)
                                    .padding(vertical = 14.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = if (locationAdded) R.drawable.baseline_location_icn else R.drawable.icon_get_location),
                                    contentDescription = "Get Location",
                                    modifier = Modifier.absolutePadding(top = 4.dp),
                                    tint = blueDark,

                                    )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (locationAdded) "Location Added" else "Get Location",
                                    textAlign = TextAlign.Center,
                                    style = mediumTextStyle,
                                    color = if (locationAdded) greenOnline else blueDark
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp)
                        ) {
                            ButtonPositive(
                                buttonTitle = stringResource(id = R.string.save_tola_text),
                                isArrowRequired = false,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                saveButtonClicked(mTolaName, location)
                                showEditView = false
                            }
                            if (!isTransectWalkCompleted) {
                                Spacer(modifier = Modifier.height(10.dp))
                                ButtonOutline(
                                    buttonTitle = stringResource(id = R.string.delete_tola_text),
                                    outlineColor = redDark,
                                    textColor = redDark,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    deleteButtonClicked()
                                    showEditView = false
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}