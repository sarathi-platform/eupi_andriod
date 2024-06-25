package com.patsurvey.nudge.activities.sync.home.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.utils.ConnectionMonitor
import com.nudge.core.EventSyncStatus
import com.nudge.core.json
import com.nudge.navigationmanager.routes.SYNC_HISTORY_ROUTE_NAME
import com.nudge.syncmanager.utils.PRODUCER_WORKER_TAG
import com.nudge.core.utils.SyncType
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.sync.home.viewmodel.SyncHomeViewModel
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.newMediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.syncItemCountStyle
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.utils.IMAGE_STRING
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.SYNC_DATA
import com.patsurvey.nudge.utils.SYNC_IMAGE
import com.patsurvey.nudge.utils.showCustomToast

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SyncHomeScreen(
    navController: NavController,
    viewModel: SyncHomeViewModel
) {
    val context = LocalContext.current
    val workInfo =
        viewModel.workManager.getWorkInfosForUniqueWorkLiveData(PRODUCER_WORKER_TAG).observeAsState().value

    val lifeCycleOwner = LocalLifecycleOwner.current

    val uploadWorkerInfo = remember(key1 = workInfo) {
        Log.d("syncAllPendingDetails", "SyncHomeScreen workInfo: ${workInfo?.json()} ")
         val info =  workInfo?.find { it.id == viewModel.uploadWorkedReqId }
        viewModel.syncWorkerInfo = info
        info
    }

    val totalDataEventCount = remember {
        mutableStateOf(0)
    }

    val successDataEventCount = remember {
        mutableStateOf(0)
    }


    val totalImageEventCount = remember {
        mutableStateOf(0)
    }


    val successImageEventCount = remember {
        mutableStateOf(0)
    }

    DisposableEffect(key1 = lifeCycleOwner) {
        val eventListLive = viewModel.syncHomeUseCase.getSyncEventsUseCase.getTotalEvents()
        eventListLive.observe(lifeCycleOwner) { eventList ->

            totalDataEventCount.value = eventList.filter {
                !it.name.toLowerCase(Locale.current).contains(
                    IMAGE_STRING
                )
            }.size

            successDataEventCount.value = eventList.filter {
                !it.name.toLowerCase(Locale.current).contains(
                    IMAGE_STRING
                ) && it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus
            }.size

            totalImageEventCount.value = eventList.filter {
                it.name.toLowerCase(Locale.current).contains(
                    IMAGE_STRING
                )
            }.size

            successImageEventCount.value = eventList.filter {
                it.name.toLowerCase(Locale.current).contains(
                    IMAGE_STRING
                ) && it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus
            }.size
            NudgeLogger.d(
                "SyncHomeScreen",
                "DisposableEffect-> totalDataEventCount: ${totalDataEventCount.value}, successDataEventCount: ${successDataEventCount.value}, totalImageEventCount: ${totalImageEventCount.value}" +
                        " successImageEventCount: ${successImageEventCount.value}"
            )
        }

        onDispose {
            eventListLive.removeObservers(lifeCycleOwner)
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
                            showCustomToast(context,context.getString(R.string.sync_started))
                            viewModel.syncAllPending(
                                networkSpeed = ConnectionMonitor.DoesNetworkHaveInternet.getNetworkStrength(),
                                syncType = SyncType.SYNC_ALL,
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
                isRefreshRequired = true,
                syncButtonTitle = stringResource(id = R.string.sync_only_data),
                onSyncButtonClick = {},
                onCardClick = {
                        navController.navigate("$SYNC_HISTORY_ROUTE_NAME/$SYNC_DATA")

                }
            )
            EventTypeCard(
                title = stringResource(id = R.string.sync_images),
                totalEventCount = totalImageEventCount.value,
                successEventCount = successImageEventCount.value,
                isRefreshRequired = true,
                onSyncButtonClick = {},
                syncButtonTitle = stringResource(id = R.string.sync_only_images),
                onCardClick = {
                        navController.navigate("$SYNC_HISTORY_ROUTE_NAME/$SYNC_IMAGE")
                }
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            when(uploadWorkerInfo?.state){
                WorkInfo.State.RUNNING -> ShowTextMessage(text = "Running")
                WorkInfo.State.ENQUEUED -> ShowTextMessage(text = "Enqueued")
                WorkInfo.State.SUCCEEDED -> ShowTextMessage(text = "Succeeded")
                WorkInfo.State.FAILED -> ShowTextMessage(text = "Failed")
                WorkInfo.State.BLOCKED -> ShowTextMessage(text = "Blocked")
                WorkInfo.State.CANCELLED -> ShowTextMessage(text = "Cancelled")
                null -> ShowTextMessage(text = "No Initialize")
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun SyncHomeScreenPreview() {
    SyncHomeScreen(navController = rememberNavController(), viewModel = hiltViewModel())
}

@Composable
fun ShowTextMessage(text:String){
    Text(
        text = text,
        style = syncItemCountStyle,
        textAlign = TextAlign.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    )
}
