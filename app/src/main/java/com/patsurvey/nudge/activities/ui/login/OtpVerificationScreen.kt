package com.patsurvey.nudge.activities.ui.login


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.patsurvey.nudge.activities.ui.theme.*

@Composable
fun OtpVerificationScreen(
    navController: NavController,
    modifier:Modifier=Modifier
) {
    var otpValue by remember {
        mutableStateOf("")
    }
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
                text = "Verification",
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
                text = "OTP",
                fontSize = 18.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(vertical = 6.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            val currentShape = MaterialTheme.shapes.copy(small = RoundedCornerShape(8))
            val typography = MaterialTheme.typography.copy(
                h4 = TextStyle(
                    fontSize = 12.sp
                )
            )

            val currentColor = MaterialTheme.colors.copy(primary = Color.Magenta)

            MaterialTheme(
                shapes = currentShape,
                colors = currentColor,
                typography = typography
            ) {
                OtpInputField(otpLength = 6, onOtpChanged = { otp ->
                    otpValue = otp
                })
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent),
                    colors = ButtonDefaults.buttonColors(greyButtonColor),
                    shape = RoundedCornerShape(6.dp)
                ) {

                    Text(
                        text = "Resend Otp",
                        color = textColorDark,
                        fontSize = 18.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .background(Color.Transparent)
                    )

                }

                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .background(Color.Transparent)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(blueDark),
                    shape = RoundedCornerShape(6.dp)
                ) {

                    Text(
                        text = "Submit",
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
}
@Composable
 fun InputOTPView(){
    val focusManager = LocalFocusManager.current
    var textOtp1 by remember { mutableStateOf("") }
    var textOtp2 by remember { mutableStateOf("") }
    var textOtp3 by remember { mutableStateOf("") }
    var textOtp4 by remember { mutableStateOf("") }
    var textOtp5 by remember { mutableStateOf("") }
    var textOtp6 by remember { mutableStateOf("") }
    Row(horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()) {

        TextField(
            modifier = Modifier
                .width(48.dp)
                .height(50.dp)
                .background(Color.Transparent)
                .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp)),
            value = textOtp1,
            onValueChange ={textOtp1=it},
            colors =TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Number,
            ),
            )

        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            modifier = Modifier
                .width(48.dp)
                .height(50.dp)
                .background(Color.Transparent)
                .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp)),
            value = textOtp2,
            onValueChange ={textOtp2=it},
            colors =TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Number,
            ),
        )

        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            modifier = Modifier
                .width(48.dp)
                .height(50.dp)
                .background(Color.Transparent)
                .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp)),
            value = textOtp3,
            onValueChange ={textOtp3=it},
            colors =TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Number,
            ),
        )

        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            modifier = Modifier
                .width(48.dp)
                .height(50.dp)
                .background(Color.Transparent)
                .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp)),
            value = textOtp4,
            onValueChange ={textOtp4=it},
            colors =TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Number,
            ),
        )

        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            modifier = Modifier
                .width(48.dp)
                .height(50.dp)
                .background(Color.Transparent)
                .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp)),
            value = textOtp5,
            onValueChange ={textOtp5=it},
            colors =TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Number,
            ),
        )

        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            modifier = Modifier
                .width(48.dp)
                .height(50.dp)
                .background(Color.Transparent)
                .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp)),
            value = textOtp6,
            onValueChange ={textOtp6=it},
            colors =TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Number,
            ),
        )
    }
}
