//package com.nrlm.baselinesurvey.ui.sync_event.presentation
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.material.CircularProgressIndicator
//import androidx.compose.material.Icon
//import androidx.compose.material.IconButton
//import androidx.compose.material.Scaffold
//import androidx.compose.material.Text
//import androidx.compose.material.TopAppBar
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.nrlm.baselinesurvey.R
//import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
//import com.nrlm.baselinesurvey.ui.common_components.common_setting.SyncEventCard
//import com.nrlm.baselinesurvey.ui.sync_event.viewmodel.SyncEventViewModel
//import com.nudge.syncmanager.ui.sync_event_screen.theme.getBlueDark
//import com.nudge.syncmanager.ui.sync_event_screen.theme.getDimen_10_dp
//import com.nudge.syncmanager.ui.sync_event_screen.theme.getDimen_18_dp
//import com.nrlm.baselinesurvey.ui.theme.mediumTextStyle
//import com.nrlm.baselinesurvey.ui.theme.textColorDark
//import com.nrlm.baselinesurvey.utils.ConnectionMonitor
//
//@Composable
//fun SyncEventScreen(
//    viewModel: SyncEventViewModel = hiltViewModel(),
//    navController: NavController,
//    modifier: Modifier = Modifier,
//    ) {
//    val syncEventList = viewModel.syncEventList.collectAsState()
//
//
//    val loaderState = viewModel.loaderState
//  LaunchedEffect(key1 = true) {
//      viewModel.getAllEvents()
//  }
//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .then(modifier),
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = stringResource(R.string.sync_your_data),
//                        style = mediumTextStyle,
//                        color = textColorDark,
//                        modifier = Modifier,
//                        textAlign = TextAlign.Center
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Filled.ArrowBack, null, tint = textColorDark)
//                    }
//                },
//                backgroundColor = Color.White,
//                elevation = 10.dp
//            )
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .background(Color.White)
//                .padding(top = it.calculateTopPadding())
//                .fillMaxSize()
//        ) {
//            Row( modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 10.dp)
//                .padding(top = dimen_18_dp),
//                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)) {
//                ButtonPositive(
//                    buttonTitle = "Sync Now",
//                    isArrowRequired = false,
//                    isActive = true,
//                    onClick = {
//                        viewModel.syncAllPending(ConnectionMonitor.DoesNetworkHaveInternet.getNetworkStrength())
//                        viewModel.getAllEvents()
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f)
//                )
//
//                ButtonPositive(
//                    buttonTitle = stringResource(id = R.string.retry),
//                    isArrowRequired = false,
//                    isActive = true,
//                    onClick = {
//                         viewModel.syncAllPending(ConnectionMonitor.DoesNetworkHaveInternet.getNetworkStrength())
//                        viewModel.getAllEvents()
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f)
//                )
//            }
//
//
//           LazyColumn {
//                    itemsIndexed(syncEventList.value) { index, item ->
//                        SyncEventCard(
//                            title = item.id,
//                            subTitle = item.name,
//                            status = "OPEN",
//                        ) {
//                        }
//                    }
//                }
//
//        }
//
//    }
//
//
//    if (loaderState.value.isLoaderVisible) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator(
//                color = blueDark,
//                modifier = Modifier
//                    .size(28.dp)
//                    .align(Alignment.Center)
//            )
//        }
//    }
//}
