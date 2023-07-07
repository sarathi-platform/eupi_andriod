package com.patsurvey.nudge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.utils.PREF_KEY_EMAIL
import com.patsurvey.nudge.utils.PREF_KEY_IDENTITY_NUMBER
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_KEY_USER_NAME

@Composable
fun ProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    profileScreenVideModel: ProfileScreenViewModel
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "User Profile",
                        color = textColorDark,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        style = largeTextStyle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = textColorDark)
                    }
                },
                backgroundColor = Color.White,
                elevation = 10.dp
            )
        }
    ) {

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
                        append("Name: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append(profileScreenVideModel.prefRepo.getPref(PREF_KEY_NAME, ""))
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
                        append("Email: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append(profileScreenVideModel.prefRepo.getPref(PREF_KEY_EMAIL, ""))
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
                        append("Phone Number: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append(profileScreenVideModel.prefRepo.getPref(PREF_KEY_USER_NAME, ""))
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
                        append("Identity Number: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append(
                            profileScreenVideModel.prefRepo.getPref(
                                PREF_KEY_IDENTITY_NUMBER,
                                ""
                            )
                        )
                    }
                }
            )

        }
    }
}