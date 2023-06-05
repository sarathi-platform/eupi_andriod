package com.patsurvey.nudge.activities.ui.transect_walk

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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

    val focusManager = LocalFocusManager.current

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
            .padding(horizontal = 16.dp, vertical = 10.dp)
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
                    Text(text = "Enter Name", style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ), color = placeholderGrey)
                },
                textStyle = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
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
                    .padding(vertical = 10.dp)
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
                        location = LocationUtil.getLocation(activity) ?: LocationCoordinates()
                        if ((location!!.lat != null && location!!.long != null) && (location?.lat != 0.0 && location?.long != 0.0)) locationAdded =
                            true
                        focusManager.clearFocus()
                    }
                    .height(45.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_get_location),
                        contentDescription = "Get Location",
                        modifier = Modifier.absolutePadding(top = 2.dp),
                        tint = blueDark,

                        )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (locationAdded) stringResource(R.string.location_added_text) else stringResource(R.string.get_location_text),
                        textAlign = TextAlign.Center,
                        style = smallTextStyle,
                        color = if (locationAdded) greenOnline else blueDark,
                        modifier = Modifier.absolutePadding(bottom = 2.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                ButtonOutline(
                    buttonTitle = stringResource(R.string.cancel_tola_text),
                    outlineColor = redDark,
                    textColor = redDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .weight(1f)
                ) {
                    onCancelClicked()
                }

                Spacer(modifier = Modifier.width(6.dp))

                ButtonPositive(
                    buttonTitle = stringResource(id = R.string.save_tola_text),
                    isArrowRequired = false,
                    isActive = mTolaName.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .weight(1f)
                ) {
                    onSaveClicked(mTolaName, location)
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

    val context = LocalContext.current
    val activity = context as Activity

    val focusManager = LocalFocusManager.current

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
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .then(modifier),
    ) {
        ConstraintLayout {
            val (contentBox, showBox, editBox) = createRefs()
            if (!showEditView) {
                Column(modifier = Modifier
                    .constrainAs(contentBox) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }) {
                    Text(
                        text = tolaName,
                        modifier = Modifier.fillMaxWidth(),
                        style = buttonTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = textColorDark
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
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
                            text = if (isLocationAvailable) stringResource(id = R.string.location_added_text) else stringResource(id = R.string.not_added),
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
                    mTolaName = tolaName
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
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
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
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = textColorDark, modifier = Modifier
                                .absolutePadding(top = 2.dp)
                                .clickable {
                                    showEditView = false
                                })
                        }
                        OutlinedTextField(
                            value = mTolaName,
                            onValueChange = {
                                mTolaName = it
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.enter_name_text),
                                    style = TextStyle(
                                        fontFamily = NotoSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    ),
                                    color = placeholderGrey
                                )
                            },
                            textStyle = TextStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
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
                                .padding(vertical = 10.dp)
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
                                    location =
                                        LocationUtil.getLocation(activity) ?: LocationCoordinates()
                                    if (location!!.lat != null && location!!.long != null) locationAdded =
                                        true
                                    focusManager.clearFocus()
                                }
                                .height(45.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = if (locationAdded) R.drawable.baseline_location_icn else R.drawable.icon_get_location),
                                    contentDescription = "Get Location",
                                    modifier = Modifier.absolutePadding(top = 2.dp),
                                    tint = blueDark,

                                    )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (locationAdded) stringResource(R.string.location_added_text) else stringResource(R.string.get_location_text),
                                    textAlign = TextAlign.Center,
                                    style = smallTextStyle,
                                    color = if (locationAdded) greenOnline else blueDark,
                                    modifier = Modifier.absolutePadding(bottom = 2.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!isTransectWalkCompleted) {
                                ButtonOutline(
                                    buttonTitle = stringResource(id = R.string.delete_tola_text),
                                    outlineColor = redDark,
                                    textColor = redDark,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(45.dp)
                                        .weight(1f)
                                ) {
                                    deleteButtonClicked()
                                    showEditView = false
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            ButtonPositive(
                                buttonTitle = stringResource(id = R.string.save_tola_text),
                                isArrowRequired = false,
                                isActive = mTolaName.isNotEmpty(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)
                                    .weight(1f)
                            ) {
                                if (mTolaName.isNotEmpty()) {
                                    saveButtonClicked(mTolaName, location)
                                    showEditView = false
                                } else {
                                    showCustomToast(context, context.getString(R.string.enter_tola_name_message))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}