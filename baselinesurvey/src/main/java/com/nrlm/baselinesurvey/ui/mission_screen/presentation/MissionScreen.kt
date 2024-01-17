package com.nrlm.baselinesurvey.ui.mission_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.navigation.home.HomeScreens
import com.nrlm.baselinesurvey.ui.MissionListRowScreen
import com.nrlm.baselinesurvey.ui.common_components.SearchWithFilterViewComponent
import com.nrlm.baselinesurvey.ui.mission_screen.viewmodel.MissionViewModel
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MissionScreen(
    navController: NavController,
    viewModel: MissionViewModel,
) {
    LaunchedEffect(key1 = true) {
        viewModel.init()
    }

    val listModifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
    Scaffold(topBar = {
        TopAppBar(
            elevation = 0.dp,
            title = { Text("My Tasks", color = black100Percent, style = largeTextStyle) },
            backgroundColor = Color.White
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)
        ) {
            SearchWithFilterViewComponent(placeholderString = stringResource(id = R.string.search),
                filterSelected = false,
                modifier = Modifier.padding(horizontal = 10.dp),
                showFilter = false,
                onFilterSelected = {},
                onSearchValueChange = {})
            LazyColumn(modifier = listModifier) {
                items(viewModel.sectionsList.value) { section ->
                    MissionListRowScreen(section.surveyName) {
                        navController.navigate(HomeScreens.DIDI_SCREEN.route)
                    }
                }
            }
        }
    }

}
