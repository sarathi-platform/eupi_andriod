package com.patsurvey.nudge.customviews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark

@Composable
fun ModuleAddedSuccessView(completeAdditionClicked:Boolean,message:String, modifier: Modifier = Modifier){
    AnimatedVisibility(visible = completeAdditionClicked) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.then(modifier),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_check_green_without_border),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = textColorDark,
                    style = largeTextStyle,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModuleAddedSuccessView() {
    ModuleAddedSuccessView(completeAdditionClicked = true,"Added Succssfully")
}