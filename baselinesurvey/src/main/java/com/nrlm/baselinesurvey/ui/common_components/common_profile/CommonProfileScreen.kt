package com.nrlm.baselinesurvey.ui.common_components.common_profile


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.ToolbarComponent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun CommonProfileScreen(
    title:String,
    userName:String,
    userEmail:String,
    userMobile:String,
    userIdentity:String,
    onBackClick:()->Unit
){
    Scaffold(
        backgroundColor = white,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ToolbarComponent(
                title = title,
                modifier = Modifier
            ) {
                onBackClick()

            }
        },){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = it.calculateTopPadding() + 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = buildAnnotatedString
                {
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("${stringResource(id = R.string.profile_name)}: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append(userName)
                    }
                }
            )
            Text(
                text = buildAnnotatedString
                {
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("${stringResource(id = R.string.profile_email)}: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append(userEmail)
                    }
                }
            )
            Text(
                text = buildAnnotatedString
                {
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("${stringResource(id = R.string.profile_phone)}: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append(userMobile)
                    }
                }
            )
            Text(
                text = buildAnnotatedString
                {
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("${stringResource(id = R.string.profile_identity_num)}: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append(userIdentity)
                    }
                }
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommonProfileScreenPreview(){
    CommonProfileScreen(title = "Profile", userName = "Naren", userEmail = "anas.mansoori@tothenew.com", userMobile = "7619983037", userIdentity = "78456",onBackClick = {})
}


