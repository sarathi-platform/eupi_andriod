package com.patsurvey.nudge.activities.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg

@Composable
fun CustomFloatingButton(modifier: Modifier = Modifier,buttonTitle:String, isNext:Boolean,onClick: () -> Unit){

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(languageItemActiveBg)
            .shadow(elevation = 20.dp)
            .pointerInput(true) {
                detectTapGestures(onTap = {
                    onClick()
                },
                    onPress = {},
                    onLongPress = {},
                    onDoubleTap = {})
            }
            .then(modifier)
    ) {
        Surface(
            modifier = Modifier
                .width(100.dp)
                .align(Alignment.Center)
                .background(languageItemActiveBg),
            elevation = 10.dp
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(languageItemActiveBg),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(!isNext){
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_back),
                        contentDescription = "Negative Button",
                        modifier = Modifier
                            .height(20.dp)
                            .absolutePadding(top = 2.dp),
                        colorFilter = ColorFilter.tint(blueDark)
                    )
                }
                Text(
                    text = buttonTitle,
                    color = blueDark,
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start
                    )
                )
                if(isNext) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
                        contentDescription = "Negative Button",
                        modifier = Modifier
                            .height(20.dp)
                            .absolutePadding(top = 2.dp),
                        colorFilter = ColorFilter.tint(blueDark)
                    )
                }
            }

        }


        //            Spacer(modifier = Modifier.height(100.dp))
        //        }
    }
}

@Preview(showBackground = true)
@Composable
fun prevFloatButtonPreview(){
    Box(modifier = Modifier.fillMaxSize()
        .padding(bottom = 40.dp)) {
        CustomFloatingButton(Modifier, "Q12",true) {}
    }
}