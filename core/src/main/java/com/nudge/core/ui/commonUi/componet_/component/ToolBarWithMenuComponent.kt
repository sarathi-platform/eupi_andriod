package com.nudge.core.ui.commonUi.componet_.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.R
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white


@SuppressLint("UnrememberedMutableState")
@Composable
fun ToolBarWithMenuComponent(
    title: String,
    modifier: Modifier,
    isSearch: Boolean = false,
    iconResId: Int = R.drawable.arrow_left,
    navController: NavController? = rememberNavController(),
    onBackIconClick: () -> Unit,
    onSearchValueChange: (String) -> Unit,
    showSettingsButton: Boolean = true,
    isDataNotAvailable: Boolean = false,
    onBottomUI: @Composable () -> Unit,
    onContentUI: @Composable (PaddingValues) -> Unit,
    onSettingClick: () -> Unit

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
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(bottom = 5.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = title,
                                style = mediumTextStyle,
                                color = blueDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                actions = {
                    if (showSettingsButton) {
                        IconButton(onClick = {
                            onSettingClick()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.more_icon),
                                contentDescription = "more action button",
                                tint = blueDark,
                                modifier = Modifier
                                    .padding(10.dp)
                            )
                        }
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
                .padding(top = 75.dp)
        ) {
            if (isSearch) {
                SearchWithFilterViewComponent(placeholderString = "Search",
                    filterSelected = false,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    showFilter = false,
                    onFilterSelected = {},
                    onSearchValueChange = { queryTerm ->
                        onSearchValueChange(queryTerm)

                    })
            }
            if (dataNotAvailableState.value) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        stringResource(R.string.not_able_to_load),
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
                        })
                }
            } else {
                onContentUI(it)
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