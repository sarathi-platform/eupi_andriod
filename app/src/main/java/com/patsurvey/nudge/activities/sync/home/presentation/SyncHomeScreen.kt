package com.patsurvey.nudge.activities.sync.home.presentation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_65_dp
import com.nrlm.baselinesurvey.utils.ConnectionMonitor
import com.nudge.core.EventSyncStatus
import com.nudge.core.json
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.SyncType
import com.nudge.syncmanager.utils.PRODUCER_WORKER_TAG
import com.nudge.syncmanager.utils.SYNC_WORKER_TAG
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.sync.home.viewmodel.SyncHomeViewModel
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.utils.IMAGE_STRING
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
        CoreLogger.d(context, "SyncHomeScreen", "Sync Worker Info: ${workInfo?.json()}")
        val info = workInfo?.find { it.tags.contains(SYNC_WORKER_TAG) }
        CoreLogger.d(context, "SyncHomeScreen", "Info Details: ${info?.json()}")
        if (info != null) {
            viewModel.syncWorkerInfoState = info.state
        }
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
        val eventListLive = viewModel.syncEventDetailUseCase.getSyncEventsUseCase.getTotalEvents()
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

            viewModel.imageEventProgress.floatValue = viewModel.calculateBarProgress(
                totalEventCount = totalImageEventCount.value,
                successEventCount = successImageEventCount.value
            )

            viewModel.dataEventProgress.floatValue = viewModel.calculateBarProgress(
                totalEventCount = totalDataEventCount.value,
                successEventCount = successDataEventCount.value
            )
            CoreLogger.d(
                context,
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
        onBottomUI = {
            Box(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                    .padding(vertical = dimensionResource(id = R.dimen.dp_15))
            ) {
                ButtonPositive(
                    buttonTitle = stringResource(id = R.string.sync_all_data),
                    isArrowRequired = false,
                    isActive = true
                ) {
                    CoreLogger.d(
                        context,
                        "SyncHomeScreen",
                        "Sync All Data Click: ${viewModel.selectedSyncType.intValue}"
                    )
                    startSyncProcess(context, viewModel)
                }
            }
        }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(start = dimen_10_dp, end = dimen_10_dp, top = dimen_65_dp)
                .fillMaxSize()

        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_10_dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Last Sync Time: ",
                    style = mediumTextStyle,
                    color = textColorDark
                )

                Text(
                    text = "${System.currentTimeMillis()}",
                    style = mediumTextStyle,
                    color = textColorDark
                )
            }


            EventTypeCard(
                title = stringResource(id = R.string.sync_data),
                progress = viewModel.dataEventProgress.floatValue,
                isProgressBarVisible = (viewModel.syncWorkerInfoState == WorkInfo.State.RUNNING || viewModel.syncWorkerInfoState == WorkInfo.State.ENQUEUED) && (viewModel.dataEventProgress.floatValue < 100),
                syncButtonTitle = stringResource(id = R.string.sync_only_data),
                onSyncButtonClick = {
                    viewModel.selectedSyncType.intValue = SyncType.SYNC_ONLY_DATA.ordinal
                    CoreLogger.d(
                        context,
                        "SyncHomeScreen",
                        "Sync Only Data Click: ${viewModel.selectedSyncType.intValue}"
                    )
                    startSyncProcess(context, viewModel)
                },
                onCardClick = {

                }
            )
            EventTypeCard(
                title = stringResource(id = R.string.sync_images),
                progress = viewModel.imageEventProgress.floatValue,
                isProgressBarVisible = (viewModel.syncWorkerInfoState == WorkInfo.State.RUNNING || viewModel.syncWorkerInfoState == WorkInfo.State.ENQUEUED) && (viewModel.imageEventProgress.floatValue < 100),
                onSyncButtonClick = {
                    viewModel.selectedSyncType.intValue = SyncType.SYNC_ONLY_IMAGES.ordinal
                    CoreLogger.d(
                        context,
                        "SyncHomeScreen",
                        "Sync Only Images Click: ${viewModel.selectedSyncType.intValue}"
                    )
                    startSyncProcess(context, viewModel)
                },
                syncButtonTitle = stringResource(id = R.string.sync_only_images),
                onCardClick = {
                }
            )
            when(uploadWorkerInfo?.state){
                WorkInfo.State.RUNNING -> CoreLogger.d(
                    context,
                    "SyncHomeScreen",
                    "Worker Status: RUNNING :: ${System.currentTimeMillis()}"
                )

                WorkInfo.State.ENQUEUED -> CoreLogger.d(
                    context,
                    "SyncHomeScreen",
                    "Worker Status: ENQUEUED :: ${System.currentTimeMillis()}"
                )

                WorkInfo.State.SUCCEEDED -> CoreLogger.d(
                    context,
                    "SyncHomeScreen",
                    "Worker Status: SUCCEEDED :: ${System.currentTimeMillis()}"
                )

                WorkInfo.State.FAILED -> CoreLogger.d(
                    context,
                    "SyncHomeScreen",
                    "Worker Status: FAILED :: ${System.currentTimeMillis()}"
                )

                WorkInfo.State.BLOCKED -> CoreLogger.d(
                    context,
                    "SyncHomeScreen",
                    "Worker Status: BLOCKED :: ${System.currentTimeMillis()}"
                )

                WorkInfo.State.CANCELLED -> CoreLogger.d(
                    context,
                    "SyncHomeScreen",
                    "Worker Status: CANCELLED :: ${System.currentTimeMillis()}"
                )

                null -> CoreLogger.d(
                    context,
                    "SyncHomeScreen",
                    "Worker Status: Null :: ${System.currentTimeMillis()}"
                )
            }
        }
    }

}


private fun startSyncProcess(
    context: Context,
    viewModel: SyncHomeViewModel
) {
    if ((context as MainActivity).isOnline.value) {
        showCustomToast(context, context.getString(R.string.sync_started))
        viewModel.syncAllPending(
            networkSpeed = ConnectionMonitor.DoesNetworkHaveInternet.getNetworkStrength()
        )
    } else showCustomToast(context, context.getString(R.string.logout_no_internet_error_message))
}

@Preview(showBackground = true)
@Composable
fun SyncHomeScreenPreview() {
    SyncHomeScreen(navController = rememberNavController(), viewModel = hiltViewModel())
}