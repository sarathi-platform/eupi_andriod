package com.sarathi.smallgroupmodule.ui.commonUi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.BLANK_STRING
import com.nudge.core.R
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.buttonTextStyle
import com.nudge.core.ui.theme.greyBorder
import com.nudge.core.ui.theme.languageItemActiveBg
import com.nudge.core.ui.theme.languageItemInActiveBorderBg
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.white

@Composable
fun ButtonNegative(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    horizontalPadding: Dp = 10.dp,
    isArrowRequired: Boolean = true,
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
            .padding(horizontal = horizontalPadding)
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isArrowRequired) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back),
                    contentDescription = "Negative Button",
                    modifier = Modifier
                        .absolutePadding(top = 2.dp),
                    colorFilter = ColorFilter.tint(blueDark)
                )
            }
            Text(
                text = buttonTitle,
                color = blueDark,
                style = buttonTextStyle
            )
        }
    }
}

@Composable
fun ButtonPositive(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    isArrowRequired: Boolean = true,
    isLeftArrow: Boolean = false,
    isActive: Boolean = false,
    textColor: Color = Color.White,
    iconTintColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) blueDark else languageItemActiveBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White
                )

            ) {
                if (isActive) onClick()
            }
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isArrowRequired && isLeftArrow) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Positive Button",
                    tint = if (isActive) iconTintColor else greyBorder,
                    modifier = Modifier
                        .absolutePadding(top = 2.dp, left = 2.dp, right = 10.dp)
                )
            }
            Text(
                text = buttonTitle,
                color = if (isActive) white else greyBorder,
                style = /*buttonTextStyle*/TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
            if (isArrowRequired && !isLeftArrow) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Positive Button",
                    tint = if (isActive) iconTintColor else greyBorder,
                    modifier = Modifier
                        .absolutePadding(top = 2.dp, left = 2.dp)
                )
            }
        }
    }
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    isArrowRequired: Boolean = true,
    isLeftArrow: Boolean = false,
    textColor: Color = Color.White,
    buttonColor: Color = blueDark,
    iconTintColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(buttonColor)
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
                .padding(10.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isArrowRequired && isLeftArrow) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Positive Button",
                    tint = iconTintColor,
                    modifier = Modifier
                        .absolutePadding(top = 2.dp, left = 2.dp, right = 10.dp)
                )
            }
            Text(
                text = buttonTitle,
                color = textColor,
                style = /*buttonTextStyle*/TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
            if (isArrowRequired && !isLeftArrow) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Positive Button",
                    tint = iconTintColor,
                    modifier = Modifier
                        .absolutePadding(top = 2.dp, left = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ButtonOutline(
    modifier: Modifier = Modifier,
    buttonTitle: String = BLANK_STRING,
    icon: ImageVector = Icons.Default.Add,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = {
            onClick()
        },
        border = BorderStroke(1.dp, greyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
            .then(modifier),
        contentPadding = PaddingValues(vertical = 0.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterVertically),/*.padding(vertical = 6.dp)*/
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                icon,
                contentDescription = "Add Button",
                tint = blueDark,
                modifier = Modifier
                    .absolutePadding(top = 0.dp, right = 2.dp)
                    .size(22.dp)
            )
            Text(
                text = buttonTitle,
                color = blueDark,
                style = mediumTextStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absolutePadding(bottom = 3.dp)
            )
        }
    }
}

@Composable
fun ButtonOutlinePreview() {
    ButtonOutline(
        modifier = Modifier.fillMaxWidth(),
    ) {}
}

@Composable
fun BlueButtonWithIcon(
    modifier: Modifier = Modifier,
    buttonText: String,
    icon: ImageVector,
    shouldBeActive: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .background(Color.Transparent)
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
                .padding(vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = "Button Icon",
                tint = if (shouldBeActive) Color.White else languageItemInActiveBorderBg,
                modifier = Modifier
                    .absolutePadding(top = 4.dp)

            )
            Text(
                text = buttonText,
                color = if (shouldBeActive) Color.White else languageItemInActiveBorderBg,
                modifier = Modifier,
                style = newMediumTextStyle
            )
        }
    }
}

@Composable
fun DoubleButtonBox(
    modifier: Modifier = Modifier,
    positiveButtonText: String,
    negativeButtonRequired: Boolean = true,
    negativeButtonText: String = "",
    positiveButtonOnClick: () -> Unit,
    negativeButtonOnClick: () -> Unit,
    isPositiveButtonActive: Boolean = true
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
                    buttonTitle = positiveButtonText,
                    isActive = isPositiveButtonActive
                ) {
                    positiveButtonOnClick()
                }
            }
        }
    }
}


