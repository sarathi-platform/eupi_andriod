package com.sarathi.contentmodule.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.greyBorder
import com.nudge.core.ui.theme.languageItemActiveBg
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.white


@Composable
fun ButtonPositive(
    buttonTitle: String,
    modifier: Modifier = Modifier,
    isArrowRequired: Boolean = true,
    isLeftArrow: Boolean = false,
    isActive: Boolean = false,
    iconTintColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(dimen_6_dp))
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
                .padding(dimen_10_dp)
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
                        .absolutePadding(top = dimen_2_dp, left = dimen_2_dp, right = dimen_10_dp)
                )
            }
            TextViewWithIcon(
                buttonTitle = buttonTitle,
                isActive = isActive,
                isArrowRequired = isArrowRequired,
                isLeftArrow = isLeftArrow,
                iconTintColor = iconTintColor
            )
        }
    }
}

@Composable
private fun TextViewWithIcon(
    buttonTitle: String,
    isActive: Boolean,
    isArrowRequired: Boolean,
    isLeftArrow: Boolean,
    iconTintColor: Color
) {
    Text(
        text = buttonTitle,
        color = if (isActive) white else greyBorder,
        style = TextStyle(
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