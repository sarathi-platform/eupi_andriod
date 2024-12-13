package com.nrlm.baselinesurvey.ui.common_components

import android.text.TextUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.grayColor
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph


@Composable
fun ToolbarWithMenuComponent(
    title: String,
    subTitle: String = BLANK_STRING,
    subTitleColorId: Color = grayColor,
    modifier: Modifier,
    navController: NavController? = rememberNavController(),
    isMenuIconRequired: Boolean? = true,
    actions: @Composable () -> Unit = {
        if (isMenuIconRequired == true) {
            IconButton(onClick = {
                navController?.navigate(NudgeNavigationGraph.SETTING_GRAPH)
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
    onBackIconClick: () -> Unit,
    onBottomUI: @Composable () -> Unit,
    onContentUI: @Composable (PaddingValues) -> Unit
) {
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
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back Button",
                                tint = Color.Black
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
                                color = com.nudge.core.ui.theme.blueDark,
                                textAlign = TextAlign.Center
                            )
                            if (!TextUtils.isEmpty(subTitle)) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = subTitle,
                                    style = defaultTextStyle.copy(color = subTitleColorId),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis  // This will add an ellipsis if the text exceeds the width
                                )
                            }
                        }

                    }
                },
                actions = {
                    actions()
                },
                backgroundColor = Color.White,
                elevation = 10.dp
            )
        },
        bottomBar = {
            onBottomUI()
        }
    ) {
        onContentUI(it)
    }
}

@Preview(showBackground = true)
@Composable
fun ToolbarWithMenuComponent() {
    ToolbarWithMenuComponent(
        title = "Mission Summary",
        isMenuIconRequired = false,
        modifier = Modifier,
        onBackIconClick = {

        },
        onContentUI = {

        },
        onBottomUI = {})
}