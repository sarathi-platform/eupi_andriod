package com.patsurvey.nudge.utils

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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



@Preview(showBackground = true)
@Composable
fun BlueButtonWithIconPreview() {
    BlueButtonWithIcon(
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
                modifier = Modifier.absolutePadding(top = 4.dp, right = 2.dp).size(22.dp)
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
    icon: ImageVector = Icons.Default.Check,
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
                style = mediumTextStyle,
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
        iconTintColor = greenActiveIcon,
        icon = Icons.Default.Close
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