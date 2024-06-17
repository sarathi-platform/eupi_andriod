package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGreyLight
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.greyColor
import com.nudge.core.ui.theme.languageItemActiveBg
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.smallTextStyleMediumWeight
import com.nudge.core.ui.theme.smallTextStyleNormalWeight
import com.nudge.core.ui.theme.smallerTextStyle
import com.nudge.core.ui.theme.white

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    color: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = blueDark,
        contentColor = white
    ),
    isIcon: Boolean = true
){
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(roundedCornerRadiusDefault),
            colors = color,
            modifier = modifier
        ) {
            Text(
                text = text,
                style = smallTextStyleMediumWeight
            )
            if(isIcon) {
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = "Proceed",
                    tint = white,
                    modifier = Modifier.absolutePadding(top = dimen_2_dp, left = dimen_2_dp)
                )
            }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
){
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        border = BorderStroke(dimen_1_dp, borderGreyLight),
        colors = ButtonDefaults.buttonColors(
            containerColor = languageItemActiveBg, contentColor = blueDark
        ),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = smallTextStyleNormalWeight
        )
    }
}

@Composable
fun ButtonComponent(title: String = "") {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(end = 16.dp, top = 2.dp)
                .size(60.dp)
                .clip(shape = CircleShape)
                .border(
                    1.dp, color = lightGray2, RoundedCornerShape(100.dp)
                )
                .background(color = Color.Transparent)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material.Text(
                text = title,
                textAlign = TextAlign.Center,
                style = smallerTextStyle.copy(fontSize = 10.sp),
                modifier = Modifier.padding(10.dp),
            )

        }
    }
}

@Composable
fun ButtonPositiveComponent(
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
                androidx.compose.material.Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Positive Button",
                    tint = if (isActive) iconTintColor else greyColor,
                    modifier = Modifier
                        .absolutePadding(top = 2.dp, left = 2.dp, right = 10.dp)
                )
            }
            androidx.compose.material.Text(
                text = buttonTitle,
                color = if (isActive) white else greyColor,
                style = /*buttonTextStyle*/TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
            if (isArrowRequired && !isLeftArrow) {
                androidx.compose.material.Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Positive Button",
                    tint = if (isActive) iconTintColor else greyColor,
                    modifier = Modifier
                        .absolutePadding(top = 2.dp, left = 2.dp)
                )
            }
        }
    }
}
