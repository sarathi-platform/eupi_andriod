package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.white
import com.sarathi.surveymanager.R

@Composable
fun OutlineButtonWithIconComponent(
    modifier: Modifier = Modifier,
    buttonTitle: String = "Yes",
    textColor: Color,
    iconTintColor: Color,
    buttonBackgroundColor: Color = white,
    buttonBorderColor: Color = lightGray2,
    icon: Painter? = painterResource(id = R.drawable.icon_check),
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
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = "Button Icon",
                    tint = iconTintColor,
                    modifier = Modifier.padding(bottom = 18.dp)
                )
            }
            Text(
                text = buttonTitle,
                color = textColor,
                style = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun OutlineButtonWithIconComponentPreview() {
   OutlineButtonWithIconComponent(textColor = lightGray2, iconTintColor = lightGray2) {

   }
}