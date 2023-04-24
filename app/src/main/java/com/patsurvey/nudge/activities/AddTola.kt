package com.patsurvey.nudge.activities

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.LocationUtil

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
                    Text(text = "Enter Name", style = mediumTextStyle, color = placeholderGrey)
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
                        painter = painterResource(id = R.drawable.icon_get_location),
                        contentDescription = "Get Location",
                        modifier = Modifier.absolutePadding(top = 4.dp),
                        tint = blueDark,

                        )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (locationAdded) "Location Added" else "Get Location",
                        textAlign = TextAlign.Center,
                        style = mediumTextStyle,
                        color = blueDark
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
            ) {
                ButtonNegative(
                    buttonTitle = "Cancel",
                    modifier = Modifier
                        .weight(1f)
                ) {
                    onCancelClicked()
                }
                Spacer(modifier = Modifier.width(10.dp))
                ButtonPositive(
                    buttonTitle = "Save",
                    modifier = Modifier
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
    tolaName: String,
    isLocationAvailable: Boolean,
    deleteButtonClicked: () -> Unit,
    editButtonClicked: () -> Unit
) {
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
        Column {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
            )
            {
                OutlineButtonWithIcon(
                    buttonTitle = "Delete",
                    icon = R.drawable.baseline_delete_icon,
                    contentColor = redDark,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    deleteButtonClicked()
                }
                Spacer(modifier = Modifier.width(10.dp))
                OutlineButtonWithIcon(
                    buttonTitle = "edit",
                    icon = R.drawable.baseline_edit_icn,
                    contentColor = blueDark,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    editButtonClicked()
                }
            }
        }
    }
}