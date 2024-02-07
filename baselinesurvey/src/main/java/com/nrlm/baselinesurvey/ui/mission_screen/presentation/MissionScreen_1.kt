package com.nrlm.baselinesurvey.ui.mission_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.navigation.home.MISSION_SUMMARY_SCREEN_ROUTE_NAME
import com.nrlm.baselinesurvey.ui.common_components.SearchWithFilterViewComponent
import com.nrlm.baselinesurvey.ui.mission_screen.viewmodel.MissionViewModel
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.textColorDark

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun MissionScreen_1(
    navController: NavController = rememberNavController(),
    viewModel: MissionViewModel = hiltViewModel()
) {
    val list = listOf(
        1, 2, 3, 4
    )
    LaunchedEffect(key1 = true) {
        viewModel.init()
    }
    Scaffold(modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sarathi_logo),
                            contentDescription = "more action button",
                            tint = blueDark,
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                        )
                        Text(
                            text = "SARATHI",
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp, color = textColorDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                    }) {
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
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
        ) {
            SearchWithFilterViewComponent(placeholderString = stringResource(id = R.string.search),
                filterSelected = false,
                modifier = Modifier.padding(horizontal = 10.dp),
                showFilter = false,
                onFilterSelected = {},
                onSearchValueChange = { queryTerm ->
                })

            LazyColumn(
            ) {
                items(viewModel.filterMissionList.value) { mission ->
                    MissonRowScreen_1(
                        missionName = mission.missionName,
                        missionDueDate = mission.startDate,
                        onViewStatusClick = {},
                        onStartClick = { navController.navigate("${MISSION_SUMMARY_SCREEN_ROUTE_NAME}/${mission.missionId}") })
                }
            }
        }
    }

}

@Preview
@Composable
fun MissionScreen_1Perview() {
    MissionScreen_1()
}
