package com.patsurvey.nudge.activities

import android.graphics.drawable.Icon
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*

@Composable
fun BlueButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    isArrowRequired: Boolean = false,
    shouldBeActive: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .padding(vertical = 14.dp)
            .background(Color.Transparent)
            .width(160.dp)
            .indication(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White
                )
            )
            .then(modifier),
        enabled = shouldBeActive,
        colors = ButtonDefaults.buttonColors(if (shouldBeActive) blueDark else languageItemActiveBg),
        shape = RoundedCornerShape(6.dp),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .width(160.dp)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = buttonText,
                color = if (shouldBeActive) Color.White else languageItemInActiveBorderBg,
                modifier = Modifier,
                style = mediumTextStyle
            )
            if (isArrowRequired) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Forward arrow",
                    tint = if (shouldBeActive) Color.White else languageItemInActiveBorderBg,
                    modifier = Modifier.absolutePadding(top = 4.dp)

                )
            }
        }
    }
}

@Composable
fun ButtonPositive(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    isArrowRequired: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(blueDark)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White
                )

            ) {
                onClick()
            }
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier
                .padding(14.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = buttonTitle,
                color = Color.White,
                style = buttonTextStyle
            )
            if (isArrowRequired) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Positive Button",
                    tint = Color.White,
                    modifier = Modifier
                        .absolutePadding(top = 4.dp, left = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ButtonNegative(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(languageItemActiveBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.Black
                )

            ) {
                onClick()
            }
            .padding(horizontal = 10.dp)
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_arrow_back),
                contentDescription = "Negative Button",
                modifier = Modifier
                    .absolutePadding(top = 2.dp),
                colorFilter = ColorFilter.tint(blueDark)
            )
            Text(
                text = buttonTitle,
                color = blueDark,
                style = buttonTextStyle
            )
        }
    }
}


@Composable
fun ButtonOutline(
    modifier: Modifier = Modifier,
    buttonTitle: String = "Add Tola",
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = {
            onClick()
        },
        border = BorderStroke(1.dp, greyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Button",
                tint = blueDark,
                modifier = Modifier.absolutePadding(top = 4.dp, right = 2.dp)
            )
            Text(
                text = buttonTitle,
                color = blueDark,
                style = mediumTextStyle,
            )
        }
    }
}

@Composable
fun DoubleButtonBox(
    modifier: Modifier = Modifier,
    positiveButtonText: String,
    negativeButtonRequired: Boolean = true,
    negativeButtonText: String,
    positiveButtonOnClick: () -> Unit,
    negativeButtonOnClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth()
            .background(Color.White)
            .shadow(20.dp, shape = RectangleShape, clip = true)
            .then(modifier)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                if (negativeButtonRequired) {
                    ButtonNegative(
                        modifier = Modifier.weight(1f),
                        buttonTitle = negativeButtonText
                    ) {
                        negativeButtonOnClick()
                    }
                    Spacer(modifier = Modifier.padding(10.dp))
                }
                ButtonPositive(
                    modifier = Modifier.weight(1.25f),
                    buttonTitle = positiveButtonText
                ) {
                    positiveButtonOnClick()
                }
            }
        }
    }
}

@Composable
fun OutlineButtonWithIcon(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    icon: Int,
    contentColor: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = {
            onClick()
        },
        border = BorderStroke(1.dp, greyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(id = icon),
                contentDescription = "Add Button",
                tint = contentColor,
                modifier = Modifier.absolutePadding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = buttonTitle,
                color = contentColor,
                style = smallTextStyleMediumWeight,
            )
        }
    }
}


@Composable
fun SimpleActionButton(
    buttonTitle: String,
    textColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White
                )

            ) {
                onClick()
            }
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier
                .padding(14.dp)
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = buttonTitle,
                color = textColor,
                style = buttonTextStyle
            )
        }
    }
}