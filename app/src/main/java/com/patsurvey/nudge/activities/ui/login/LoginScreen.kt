package com.patsurvey.nudge.activities.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.navigation.ScreenRoutes

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier
){
    val mobileNumber = remember { mutableStateOf(TextFieldValue()) }
    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .then(modifier)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(id = R.string.verification_text),
                color = textColorDark,
                fontSize = 24.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )

        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = stringResource(id = R.string.mobile_text),
                fontSize = 18.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(vertical = 6.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(
            ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color.Transparent)
                            .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp)),
                        value = mobileNumber.value,
                        onValueChange ={mobileNumber.value=it},
                        colors =TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent,
                            textColor = blueDark),
                        placeholder = { Text(text = stringResource(id = R.string.enter_mobile_number)) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            autoCorrect = true,
                            keyboardType = KeyboardType.Number,
                        ),

                        )
                }
            
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { navController.navigate(ScreenRoutes.OTP_VERIFICATION_SCREEN.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                colors = ButtonDefaults.buttonColors(blueDark),
                shape = RoundedCornerShape(6.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.get_otp),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(blueDark)
                )

            }
        }


    }
}