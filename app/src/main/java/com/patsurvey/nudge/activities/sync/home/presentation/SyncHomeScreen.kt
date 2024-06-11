package com.patsurvey.nudge.activities.sync.home.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.utils.ConnectionMonitor
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.entities.Events
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.sync.home.viewmodel.SyncHomeViewModel
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.newMediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.utils.IMAGE_STRING
import com.patsurvey.nudge.utils.showCustomToast

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SyncHomeScreen(
    navController: NavController,
    viewModel: SyncHomeViewModel
) {
    val eventList by viewModel.evList.collectAsState()
    val context = LocalContext.current
    val locale=Locale.current
   val lifeCycleOwner= LocalLifecycleOwner.current
    val eventMutableList = remember {
        mutableStateOf((mutableListOf<Events>()))
    }
    val totalDataEventCount = remember {
        derivedStateOf {
            eventMutableList.value.filter {!it.name.toLowerCase(Locale.current).contains(
                IMAGE_STRING
            ) }.size
        }
    }

    val successDataEventCount = remember {
        derivedStateOf {
            eventMutableList.value.filter { !it.name.toLowerCase(Locale.current).contains(
                IMAGE_STRING
            ) && it.status== EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus}.size
        }
    }

    val totalImageEventCount = remember {
        derivedStateOf {
            eventMutableList.value.filter { it.name.toLowerCase(Locale.current).contains(
                IMAGE_STRING
            ) }.size
        }
    }

    val successImageEventCount = remember {
        derivedStateOf {
            eventMutableList.value.filter { it.name.toLowerCase(Locale.current).contains(
                IMAGE_STRING
            ) && it.status== EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus}.size
        }
    }
    DisposableEffect(key1 = Unit) {
        val list = viewModel.syncHomeUseCase.getSyncEventsUseCase.getTotalEvents()
        list.observe(lifeCycleOwner){
            eventMutableList.value.addAll(it)
//            viewModel.totalDataEventCount.value =eventList.filter { !it.name.toLowerCase(locale).contains(IMAGE_STRING) }.size
//            viewModel.successDataEventCount.value =eventList.filter { !it.name.toLowerCase(locale).contains(IMAGE_STRING) && it.status==EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus}.size
//            viewModel.totalImageEventCount.value =eventList.filter { it.name.toLowerCase(locale).contains(IMAGE_STRING) }.size
//            viewModel.successImageEventCount.value =eventList.filter { it.name.toLowerCase(locale).contains(IMAGE_STRING) && it.status==EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus}.size
        }
        onDispose {
        }
    }


    ToolbarWithMenuComponent(
        title = stringResource(id = R.string.sync_all_data),
        modifier = Modifier.fillMaxSize(),
        isMenuIconRequired = false,
        onBackIconClick = { navController.popBackStack() },
        onBottomUI = { }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(start = 10.dp, end = 10.dp, top = 65.dp)
                .fillMaxWidth()

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Button(
                    onClick = {
                        if((context as MainActivity).isOnline.value){
                            viewModel.syncAllPending(
                                ConnectionMonitor.DoesNetworkHaveInternet.getNetworkStrength(),
                            )
                        }else showCustomToast(context,context.getString(R.string.logout_no_internet_error_message))

                    },
                    colors = ButtonDefaults.buttonColors(blueDark),
                    modifier = Modifier.align(
                        Alignment.BottomEnd
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.sync_all_data),
                        color = white,
                        modifier = Modifier,
                        style = newMediumTextStyle
                    )
                }
            }

            EventTypeCard(
                title = stringResource(id = R.string.sync_data),
                totalEventCount = totalDataEventCount.value,
                successEventCount = successDataEventCount.value,
                isRefreshRequired = false,
                onRefreshClick = {},
                onCardClick = {}
            )
            EventTypeCard(
                title = stringResource(id = R.string.sync_images),
                totalEventCount = totalImageEventCount.value,
                successEventCount = successImageEventCount.value,
                isRefreshRequired = false,
                onRefreshClick = {},
                onCardClick = {}
            )

        }
    }

}

@Preview(showBackground = true)
@Composable
fun SyncHomeScreenPreview() {
    SyncHomeScreen(navController = rememberNavController(), viewModel = hiltViewModel())
}
