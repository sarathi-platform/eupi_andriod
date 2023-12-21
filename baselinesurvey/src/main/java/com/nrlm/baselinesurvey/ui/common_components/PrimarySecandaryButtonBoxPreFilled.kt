package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
import com.nrlm.baselinesurvey.ui.theme.redLight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun PrimarySecandaryButtonBoxPreFilled(
    modifier: Modifier = Modifier,
    primaryButtonText: String,
    secandaryButtonRequired: Boolean = true,
    secandaryButtonText: String = "",
    primaryButtonOnClick: () -> Unit,
    secandaryButtonOnClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth()
            .background(Color.White)
            .then(modifier),
        elevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                if (secandaryButtonRequired) {

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(languageItemActiveBg)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = true,
                                    color = redLight
                                )
                            ) {
                                secandaryButtonOnClick()
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier.padding(all = 10.dp),
                            text = secandaryButtonText,
                            color =  textColorDark,
                            style = TextStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(blueDark)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = true,
                                color = redLight
                            )
                        ) {
                            primaryButtonOnClick()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(all = 10.dp),
                        text = primaryButtonText,
                        color= white,
                        style = TextStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AcceptRejectButtonBoxPreFilledPreview(){
    PrimarySecandaryButtonBoxPreFilled(
        modifier = Modifier.shadow(10.dp),
        secandaryButtonRequired = true,
        primaryButtonText = "Cancel",
        secandaryButtonText = "More",
        primaryButtonOnClick = {},
        secandaryButtonOnClick = {}
    )
}

