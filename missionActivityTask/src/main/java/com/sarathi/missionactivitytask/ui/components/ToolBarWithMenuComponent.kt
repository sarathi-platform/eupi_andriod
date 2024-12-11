package com.sarathi.missionactivitytask.ui.components

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.grayColor
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.missionactivitytask.R


@SuppressLint("UnrememberedMutableState")
@Composable
fun ToolBarWithMenuComponent(
    title: String,
    subTitle: String = BLANK_STRING,
    modifier: Modifier,
    isSearch: Boolean = false,
    iconResId: Int = R.drawable.arrow_left,
    navController: NavController? = rememberNavController(),
    dataNotLoadMsg: String = stringResource(R.string.not_able_to_load),
    onBackIconClick: () -> Unit,
    onSearchValueChange: (String) -> Unit,
    isDataNotAvailable: Boolean = false,
    onBottomUI: @Composable () -> Unit,
    onContentUI: @Composable (PaddingValues, Boolean, (String) -> Unit) -> Unit,
    onSettingClick: () -> Unit,
    onRetry: () -> Unit
) {
    val dataNotAvailableState = mutableStateOf(isDataNotAvailable)

    Scaffold(
        modifier = modifier,
        containerColor = white,
        topBar = {
            TopAppBar(

                title = {
                    Row(modifier = Modifier) {
                        IconButton(
                            onClick = { onBackIconClick() },
                            modifier = Modifier
                        ) {
                            Icon(
                                painter = painterResource(id = iconResId),
                                contentDescription = "Back Button",
                                tint = blueDark
                            )
                        }
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(bottom = 5.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = title,
                                style = mediumTextStyle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = blueDark,
                                textAlign = TextAlign.Center
                            )
                            if (!TextUtils.isEmpty(subTitle)) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = subTitle,
                                    style = defaultTextStyle.copy(color = grayColor)
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSettingClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.more_icon),
                            contentDescription = "more action button",
                            tint = blueDark,
                            modifier = Modifier
                                .padding(10.dp)
                        )
                    }
                },
                backgroundColor = Color.White,
                elevation = 10.dp
            )
        },
        bottomBar = {
            onBottomUI()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 75.dp),
            verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
        ) {
            if (dataNotAvailableState.value) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        dataNotLoadMsg,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = defaultTextStyle,
                        color = textColorDark
                    )
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    ButtonPositiveComponent(
                        buttonTitle = stringResource(id = R.string.retry),
                        isActive = true,
                        isArrowRequired = false,
                        onClick = {
                            onRetry()
                        })
                }
            } else {
                onContentUI(it, isSearch, onSearchValueChange)
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMatToolBarWithMenuComponent() {
//    ToolBarWithMenuComponent(title = "Mission Summary", modifier = Modifier, onBackIconClick = {
//
//    }, onContentUI = {
//
//    }, onBottomUI = {})
}