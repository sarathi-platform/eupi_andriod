package com.patsurvey.nudge.utils

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.CustomOutlineTextField
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
fun BlueButtonWithRightArrow(
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
//            .padding(vertical = 14.dp)
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
                .width(160.dp),
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
fun BlueButtonWithIconWithFixedWidth(
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
                .padding(vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = "Forward arrow",
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


@Preview(showBackground = true)
@Composable
fun BlueButtonWithIconPreview() {
    BlueButtonWithIconWithFixedWidth(
        modifier = Modifier,
        buttonText = stringResource(id = R.string.add_didi),
        icon = Icons.Default.Add
    ){}
}

@Composable
fun ButtonPositive(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    isArrowRequired: Boolean = true,
    isActive: Boolean = true,
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
            if (isArrowRequired) {
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
fun ButtonPositiveForPAT(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    isArrowRequired: Boolean = true,
    isActive: Boolean = true,
    color: Color = languageItemActiveBg,
    textColor: Color = Color.White,
    iconTintColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
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
            if (isArrowRequired) {
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
fun ButtonPositiveForVo(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    isArrowRequired: Boolean = true,
    isActive: Boolean = true,
    color: Color = languageItemActiveBg,
    textColor: Color = Color.White,
    iconTintColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
            if (isArrowRequired) {
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

@Preview(showBackground = true)
@Composable
fun ButtonPositivePreview() {
    ButtonPositive(
        buttonTitle = stringResource(id = R.string.save_tola_text),
        isArrowRequired = true,
        modifier = Modifier.fillMaxWidth()
    ) {}
}

@Composable
fun ButtonNegative(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    horizontalPadding : Dp = 10.dp,
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
fun ButtonNegativeForPAT(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    color: Color = languageItemActiveBg,
    textColor: Color = blueDark,
    horizontalPadding : Dp = 10.dp,
    isArrowRequired: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
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
            Modifier.padding(10.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
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
                color = textColor,
                style = /*buttonTextStyle*/TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonNegativePreview() {
    ButtonNegative(
        buttonTitle = stringResource(id = R.string.save_tola_text),
        modifier = Modifier.fillMaxWidth()
    ) {}
}


@Composable
fun ButtonOutline(
    modifier: Modifier = Modifier,
    buttonTitle: String = "Add Tola",
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
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically)/*.padding(vertical = 6.dp)*/,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = "Add Button",
                tint = blueDark,
                modifier = Modifier
                    .absolutePadding(top = 4.dp, right = 2.dp)
                    .size(22.dp)
            )
            Text(
                text = buttonTitle,
                color = blueDark,
                style = mediumTextStyle,
                textAlign = TextAlign.Center,
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ButtonOutlinePreview() {
    ButtonOutline(
        modifier = Modifier.fillMaxWidth(),
    ) {}
}

@Composable
fun ButtonOutline(
    modifier: Modifier = Modifier,
    buttonTitle: String = "Add Tola",
    outlineColor: Color = greyBorder,
    textColor: Color = blueDark,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = {
            onClick()
        },
        border = BorderStroke(1.dp, outlineColor),
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = buttonTitle,
                color = textColor,
                style = /*mediumTextStyle*/TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonOutline2Preview() {
    ButtonOutline(
        buttonTitle = stringResource(id = R.string.delete_tola_text),
        outlineColor = redDark,
        textColor = redDark,
        modifier = Modifier.fillMaxWidth()
    ) {}
}

@Composable
fun DoubleButtonBox(
    modifier: Modifier = Modifier,
    positiveButtonText: String,
    negativeButtonRequired: Boolean = true,
    negativeButtonText: String = "",
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
fun BottomButtonBox(
    modifier: Modifier = Modifier,
    positiveButtonText: String,
    isArrowRequired: Boolean = true,
    positiveButtonOnClick: () -> Unit,
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
                ButtonPositive(
                    modifier = Modifier.weight(1.25f),
                    buttonTitle = positiveButtonText,
                    isArrowRequired = isArrowRequired
                ) {
                    positiveButtonOnClick()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoubleButtonBoxPreview() {
    DoubleButtonBox(
        modifier = Modifier.shadow(10.dp),
        negativeButtonRequired = true,
        positiveButtonText = stringResource(id = R.string.add_didi),
        negativeButtonText = "Back",
        positiveButtonOnClick = {},
        negativeButtonOnClick = {}
    )
}

@Composable
fun OutlineButtonWithIcon(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    icon: ImageVector,
    contentColor: Color,
    borderColor: Color = greyBorder,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = {
            onClick()
        },
        border = BorderStroke(1.dp, borderColor),
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
                imageVector = icon,
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

@Preview(showBackground = true)
@Composable
fun SimpleActionButtonPreview() {
    SimpleActionButton(
        buttonTitle = stringResource(id = R.string.mark_as_completed),
        textColor = white,
        backgroundColor = greenOnline,
        modifier = Modifier.fillMaxWidth()
    ) {}
}

@Composable
fun IconButtonForward(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(blueDark)
            .clickable {
                onClick()
            }
            .indication(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White
                )
            )
            .then(modifier)
    ) {
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
    }
}
@Preview(showBackground = true)
@Composable
fun IconButtonForwardPreview() {
    IconButtonForward(
        modifier = Modifier
            .size(80.dp)
    ) {}
}

@Composable
fun BlueButtonWithDrawableIcon(
    modifier: Modifier = Modifier,
    buttonText: String,
    icon: ImageVector,
    imageIcon:Int,
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
            .width(210.dp)
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
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(id = imageIcon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(27.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )

            Icon(
                icon,
                contentDescription = "Forward arrow",
                tint = if (shouldBeActive) Color.White else languageItemInActiveBorderBg,
                modifier = Modifier
                    .absolutePadding(top = 4.dp)
                    .padding(start = 10.dp)

            )
            Text(
                text = buttonText,
                color = if (shouldBeActive) Color.White else languageItemInActiveBorderBg,
                modifier = Modifier,
                style = mediumTextStyle
            )
        }
    }
}

@Composable
fun ButtonOutlineWithTopIcon(
    modifier: Modifier = Modifier,
    buttonTitle: String = "Yes",
    textColor: Color,
    iconTintColor: Color,
    buttonBackgroundColor: Color = white,
    buttonBorderColor: Color = lightGray2,
    icon: Painter = painterResource(id = R.drawable.icon_check),
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = {
            onClick()
        },
        border = BorderStroke(1.dp, buttonBorderColor),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonBackgroundColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = "Button Icon",
                tint = iconTintColor,
                modifier = Modifier.padding(bottom = 18.dp)
            )
            Text(
                text = buttonTitle,
                color = textColor,
                style = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonOutlineWithTopIconPreview() {
    ButtonOutlineWithTopIcon(
        modifier = Modifier.size(155.dp, 110.dp),
        buttonTitle = "Yes",
        textColor = textColorDark,
        iconTintColor = greenActiveIcon
    ) {}
}

@Preview(showBackground = true)
@Composable
fun BlueButtonWithDrawableIconView(){
    BlueButtonWithDrawableIcon(buttonText = stringResource(id = R.string.add_didi), icon = Icons.Default.Add,
        imageIcon = R.drawable.didi_icon) {

    }
}

@Composable
fun TextButtonWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(modifier = Modifier
        .clickable {
            onClick()
        }
        .indication(
            interactionSource = interactionSource,
            indication = rememberRipple(
                bounded = true,
                color = Color.White
            )
        )
        .then(modifier)
    ) {
        Text(
            text = "Show",
            style = smallTextStyleMediumWeight,
            color = textColorDark,
        )
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = blueDark,
            modifier = Modifier
                .absolutePadding(top = 4.dp, left = 2.dp)
                .size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TextButtonWithIconPreview(){
    TextButtonWithIcon(modifier = Modifier, onClick = {})
}

@Composable
fun OutlineButtonCustom(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    horizontalPadding : Dp = 10.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(white, shape = RoundedCornerShape(6.dp))
            .border(1.dp, color = borderGreyShare, shape = RoundedCornerShape(6.dp))
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
            Text(
                text = buttonTitle,
                color = blueDark,
                style = buttonTextStyle
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OutlineButtonCustomPreview(){
    OutlineButtonCustom(buttonTitle = "Download") {

    }
}

@Composable
fun IncrementDecrementView(modifier: Modifier,optionText:String,
                           currentValue: Int=0,
                           onDecrementClick: (Int)->Unit,
                           onIncrementClick: (Int)->Unit,
                           onValueChange: (String) -> Unit){
    var currentCount by remember {
        mutableStateOf(currentValue.toString())
    }
    Log.d(TAG, "IncrementDecrementView: ")
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)) {
        Row(
            modifier = Modifier
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = optionText,
                color = Color.Black,
                modifier = Modifier,
                style = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            )
        }
    Box(modifier = modifier
        .fillMaxWidth()
        .background(Color.White)
        .border(width = 1.dp, shape = RoundedCornerShape(6.dp), color = Color.Black)){

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)) {
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                ,contentAlignment = Alignment.Center){
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        currentCount = incDecValue(0, currentCount)
                        onDecrementClick(currentCount.toInt())
                    }, horizontalArrangement = Arrangement.Center){
                    Spacer(modifier = Modifier
                        .width(14.dp)
                        .background(Color.Black)
                        .height(1.5.dp)
                        .align(Alignment.CenterVertically))
                }

            }
           Spacer(modifier = Modifier
               .width(1.dp)
               .fillMaxHeight()
               .background(Color.Black))
            Column(modifier = Modifier
                .fillMaxHeight()
                .weight(1f)){
                CustomOutlineTextField(
                    value = currentCount,
                    readOnly = true,
                    onValueChange = {
                        currentCount = if(it.isEmpty())
                            "0"
                        else
                            it
                        onValueChange(it)
                    },
                    placeholder = {
                        Text(
                            text = "0", style = TextStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            ), color = placeholderGrey
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true,
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = textColorDark,
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Number,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
            }
            Spacer(modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.Black))
            Box(modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable {
                    currentCount= incDecValue(1,currentCount)
                    onIncrementClick(currentCount.toInt())
                },
                contentAlignment = Alignment.Center){
                Text(
                    text = "+",
                    color = Color.Black,
                    fontFamily = NotoSans,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            }
        }
        }

    }

}

fun incDecValue(operation:Int,value:String):String{
    var intValue=0
    if(value.isNotEmpty()){
        intValue=value.toInt()
    }

    if(operation==0){
        if(intValue>0)
            intValue--
    }else{
        intValue++
    }
    return intValue.toString()
}

@Preview(showBackground = true)
@Composable
fun IncrementDecrementViewPreview(){
    IncrementDecrementView(modifier = Modifier,"Goat",0, onDecrementClick = {}, onIncrementClick = {}, onValueChange = {})
}

@Preview(showBackground = true)
@Composable
fun DidiPATSurveyCompleteViewPreview(){
    DidiPATSurveyCompleteView(modifier = Modifier, onClick = {})
}


@Composable
fun DidiPATSurveyCompleteView(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(modifier = Modifier.
    fillMaxWidth()
        .padding(vertical = 10.dp)
        .padding(horizontal = 10.dp)
        .clickable {
            onClick()
        }
        .then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Show",
            style = smallTextStyleMediumWeight,
            color = textColorDark,
        )
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = blueDark,
            modifier = Modifier
                .absolutePadding(top = 4.dp, left = 2.dp)
                .size(24.dp)
        )
    }
}

@Composable
fun AcceptRejectButtonBox(
    modifier: Modifier = Modifier,
    positiveButtonText: String,
    negativeButtonRequired: Boolean = true,
    negativeButtonText: String = "",
    positiveButtonOnClick: () -> Unit,
    negativeButtonOnClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth()
            .background(Color.White)
            .then(modifier)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                if (negativeButtonRequired) {

                    Row(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp))
                            .background(rejectColor).clickable( interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = true,
                                    color = redLight
                                )) {
                            negativeButtonOnClick()
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier =Modifier.padding(all = 10.dp),
                            text = negativeButtonText,
                            color = redDark,
                            style = /*mediumTextStyle*/TextStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.padding(10.dp))
                }
                ButtonPositive(
                    modifier = Modifier.weight(1.5f),
                    buttonTitle = positiveButtonText,
                    isArrowRequired = false
                ) {
                    positiveButtonOnClick()
                }
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun AcceptRejectButtonBoxPreview(){
    AcceptRejectButtonBox(
        modifier = Modifier.shadow(10.dp),
        negativeButtonRequired = true,
        positiveButtonText = "Accept",
        negativeButtonText = "Reject",
        positiveButtonOnClick = {},
        negativeButtonOnClick = {}
    )
}




