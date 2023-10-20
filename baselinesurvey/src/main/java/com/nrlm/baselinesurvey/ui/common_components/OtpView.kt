package com.nrlm.baselinesurvey.ui.common_components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun OtpView(){

    var otpValue by remember {
        mutableStateOf("")
    }

    val isEnableButton = derivedStateOf {
        otpValue.length == 6
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(text = "Login with your pin", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        val currentShape = MaterialTheme.shapes.copy(small = RoundedCornerShape(50))
        val typography = MaterialTheme.typography.copy(
            bodyMedium = TextStyle(
                fontSize = 12.sp
            )
        )

        val currentColor = MaterialTheme.colorScheme.copy(primary = Color.Magenta)

        MaterialTheme(
            shapes = currentShape,
            colorScheme = currentColor,
            typography = typography
        ) {
            val otp = remember {
                mutableStateOf("")
            }
            OtpInputField(otpLength = 6, otp,onOtpChanged = { otp ->
                otpValue = otp
            })
        }



        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = {

        }, enabled = isEnableButton.value) {
            Text("Login")
        }
    }

}