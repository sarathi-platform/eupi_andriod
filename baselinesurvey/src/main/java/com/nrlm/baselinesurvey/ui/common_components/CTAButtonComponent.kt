package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.greyBorder
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.weight_10_percent
import com.nrlm.baselinesurvey.ui.theme.weight_60_percent
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun CTAButtonComponent(
    tittle: String? = BLANK_STRING,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
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
        tittle?.let {
            Text(
                text = it,
                color = if (isActive) white else greyBorder,
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

@Composable
fun OutlinedCTAButtonComponent(
    tittle: String? = BLANK_STRING,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {

    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        onClick = { if (isActive) onClick() },
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        border = BorderStroke(dimen_1_dp, lightGray2),
        elevation = ButtonDefaults.elevation(defaultElevation = defaultCardElevation)
    ) {
        tittle?.let {
            Text(
                text = it,
                color = blueDark.copy(alpha = if (isActive) 1f else 0.5f),
                style = /*buttonTextStyle*/TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    /*Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clip(RoundedCornerShape(6.dp))
//            .border(
//                dimen_1_dp,
//                color = lightGray2,
//                shape = RoundedCornerShape(roundedCornerRadiusDefault)
//            )
            .background(Color(0xFFEBEBEB))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White
                )
            ) {
                if (isActive) onClick()
            }
            .zIndex(1f)
            .then(modifier),
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        border = BorderStroke(dimen_1_dp, lightGray2),
        elevation = defaultCardElevation,
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            tittle?.let {
                Text(
                    text = it,
                    color = blueDark,
                    style = *//*buttonTextStyle*//*TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }*/
}

@Composable
fun CTAButtonComponent60PercentWidth(
    modifier: Modifier = Modifier,
    title: String? = BLANK_STRING,
    isActive: Boolean = true,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(modifier)) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight_10_percent)
        )
        OutlinedCTAButtonComponent(
            tittle = title,
            isActive = isActive,
            textColor = textColor,
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight_60_percent)
        ) {
            onClick()
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight_10_percent)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CTAButtonComponentPreview() {
    CTAButtonComponent(tittle = "Add Income", modifier = Modifier.fillMaxWidth()) {

    }
}
