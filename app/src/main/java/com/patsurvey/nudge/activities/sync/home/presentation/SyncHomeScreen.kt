package com.patsurvey.nudge.activities.sync.home.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_65_dp
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.utils.ConnectionMonitor
import com.nudge.core.EventSyncStatus
import com.nudge.core.FORM_C_TOPIC
import com.nudge.core.FORM_D_TOPIC
import com.nudge.core.SYNC_VIEW_DATE_TIME_FORMAT
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.isOnline
import com.nudge.core.json
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_50_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.SyncType
import com.nudge.navigationmanager.graphs.SettingScreens
import com.nudge.syncmanager.utils.PRODUCER_WORKER_TAG
import com.nudge.syncmanager.utils.SYNC_WORKER_TAG
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.sync.home.viewmodel.SyncHomeViewModel
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.utils.DATA_PRODUCER_STRING
import com.patsurvey.nudge.utils.DATA_STRING
import com.patsurvey.nudge.utils.IMAGE_PRODUCER_STRING
import com.patsurvey.nudge.utils.IMAGE_STRING
import com.patsurvey.nudge.utils.showCustomToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SyncHomeScreen(
    navController: NavController,
    viewModel: SyncHomeViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val workInfo = viewModel.workManager.getWorkInfosForUniqueWorkLiveData(PRODUCER_WORKER_TAG)
        .observeAsState().value
    val lifeCycleOwner = LocalLifecycleOwner.current

    val uploadWorkerInfo = rememberUploadWorkerInfo(context, workInfo, viewModel)
    val isNetworkAvailable = remember { mutableStateOf(false) }

    ObserveNetworkState(viewModel, isNetworkAvailable, lifeCycleOwner)
    ObserveEventCounts(viewModel, lifeCycleOwner)

    SyncHomeContent(
        navController = navController,
        viewModel = viewModel,
        uploadWorkerInfo = uploadWorkerInfo,
        isNetworkAvailable = isNetworkAvailable,
        scope = scope
    )
}

@Composable
fun rememberUploadWorkerInfo(
    context: Context,
    workInfo: List<WorkInfo>?,
    viewModel: SyncHomeViewModel
): WorkInfo? {
    return remember(key1 = workInfo) {
        CoreLogger.d(context, "SyncHomeScreen", "Sync Worker Info: ${workInfo?.json()}")
        val info = workInfo?.find { it.tags.contains(SYNC_WORKER_TAG) }
        CoreLogger.d(context, "SyncHomeScreen", "Info Details: ${info?.json()}")
        if (info != null) {
            viewModel.syncWorkerInfoState = info.state
        }
        info
    }
}

@Composable
fun ObserveNetworkState(
    viewModel: SyncHomeViewModel,
    isNetworkAvailable: MutableState<Boolean>,
    lifeCycleOwner: LifecycleOwner
) {
    DisposableEffect(key1 = lifeCycleOwner) {
        viewModel.isOnline.observe(lifeCycleOwner) { isOnline ->
            isNetworkAvailable.value = isOnline
            if (isOnline) {
                viewModel.fetchLastSyncDateTimeFromServer(isOnline)
            }
        }
        onDispose {
            viewModel.isOnline.removeObservers(lifeCycleOwner)
        }
    }
}

@Composable
fun ObserveEventCounts(
    viewModel: SyncHomeViewModel,
    lifeCycleOwner: LifecycleOwner
) {
    DisposableEffect(key1 = lifeCycleOwner) {
        val eventListLive = viewModel.syncEventDetailUseCase.getSyncEventsUseCase.getTotalEvents()
        eventListLive.observe(lifeCycleOwner) { eventList ->

            val (totalDataCount, successDataCount) = eventList.filterAndCountEvents {
                !it.name.lowercase(Locale.ENGLISH)
                    .contains(IMAGE_STRING) && it.name != FORM_C_TOPIC && it.name != FORM_D_TOPIC
            }
            val (totalImageCount, successImageCount) = eventList.filterAndCountEvents {
                it.name.lowercase(Locale.ENGLISH)
                    .contains(IMAGE_STRING) || it.name == FORM_C_TOPIC || it.name == FORM_D_TOPIC
            }
            val (totalProducerDataCount, producerSuccessDataCount) = eventList.filterAndCountProducerEvents {
                !it.name.lowercase(Locale.ENGLISH)
                    .contains(IMAGE_STRING) && it.name != FORM_C_TOPIC && it.name != FORM_D_TOPIC
            }

            val (totalProducerImageCount, producerSuccessImageCount) = eventList.filterAndCountProducerEvents {
                it.name.lowercase(Locale.ENGLISH)
                    .contains(IMAGE_STRING) || it.name == FORM_C_TOPIC || it.name == FORM_D_TOPIC
            }
            viewModel.totalImageEventCount.intValue = totalImageCount
            //Producer Event Progress
            viewModel.dataProducerEventProgress.floatValue = viewModel.calculateBarProgress(
                totalProducerDataCount,
                producerSuccessDataCount,
                DATA_PRODUCER_STRING
            )
            viewModel.imageProducerEventProgress.floatValue = viewModel.calculateBarProgress(
                totalProducerImageCount, producerSuccessImageCount,
                IMAGE_PRODUCER_STRING
            )

            //Consumer Event Progress
            viewModel.imageEventProgress.floatValue =
                viewModel.calculateBarProgress(totalImageCount, successImageCount, IMAGE_STRING)
            viewModel.dataEventProgress.floatValue =
                viewModel.calculateBarProgress(totalDataCount, successDataCount, DATA_STRING)
            viewModel.isSyncImageActive.value =
                !eventList.filter { it.status != EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus }
                    .any { it.name == EventName.ADD_DIDI.name || it.name == EventName.UPDATE_DIDI.name || it.name == EventName.DELETE_DIDI.name }
            CoreLogger.d(
                CoreAppDetails.getApplicationContext().applicationContext,
                "SyncHomeScreen",
                "Event counts updated: totalDataCount=$totalDataCount, successDataCount=$successDataCount, totalImageCount=$totalImageCount, successImageCount=$successImageCount"
            )
        }

        onDispose {
            eventListLive.removeObservers(lifeCycleOwner)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SyncHomeContent(
    navController: NavController,
    viewModel: SyncHomeViewModel,
    uploadWorkerInfo: WorkInfo?,
    isNetworkAvailable: MutableState<Boolean>,
    scope: CoroutineScope
) {
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(
        viewModel.loaderState.value.isLoaderVisible,
        {
            if (isOnline(context)) {
                viewModel.loaderState.value.isLoaderVisible = true
                viewModel.refreshConsumerStatus()
            } else {
                Toast.makeText(
                    context,
                    context.getString(com.sarathi.missionactivitytask.R.string.refresh_failed_please_try_again),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    ToolbarWithMenuComponent(
        title = stringResource(id = R.string.sync_all_data),
        modifier = Modifier.fillMaxSize(),
        isMenuIconRequired = false,
        onBackIconClick = { navController.popBackStack() },
        onBottomUI = {
            BottomContent(
                context = context,
                viewModel = viewModel,
                isNetworkAvailable = isNetworkAvailable
            )
        }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        )
        {
            PullRefreshIndicator(
                refreshing = viewModel.loaderState.value.isLoaderVisible,
                state = pullRefreshState,
                modifier = Modifier
                    .padding(top = dimen_50_dp)
                    .align(Alignment.TopCenter)
                    .zIndex(1f),
                contentColor = blueDark,
            )
            LazyColumn(
                modifier = Modifier
                    .background(Color.White)
                    .padding(start = dimen_10_dp, end = dimen_10_dp, top = dimen_65_dp)
                    .fillMaxSize()
            ) {
                item {
                    LastSyncTime(viewModel) {
                        CoreLogger.d(
                            context,
                            "SyncHomeScreen",
                            "LastSyncTime Click: Worker Cancel ${viewModel.isSyncStarted.value}"
                        )
                        if (viewModel.isSyncStarted.value)
                            viewModel.cancelSyncUploadWorker()
                    }
                }
                item {
                    SyncDataCard(
                        viewModel = viewModel,
                        context = context,
                        isNetworkAvailable = isNetworkAvailable
                    ) {
                        navController.navigate(SettingScreens.SYNC_HISTORY_SCREEN.route)
                    }
                }
                item {
                    SyncImageCard(
                        viewModel = viewModel,
                        context = context,
                        isNetworkAvailable = isNetworkAvailable,
                        totalImageEventCount = viewModel.totalImageEventCount
                    ) {
                        navController.navigate(SettingScreens.SYNC_HISTORY_SCREEN.route)
                    }
                }
                item {
                    HandleWorkerState(uploadWorkerInfo, viewModel, context, scope)
                }
            }
        }
    }
}

@Composable
fun BottomContent(
    context: Context,
    viewModel: SyncHomeViewModel,
    isNetworkAvailable: MutableState<Boolean>
) {
    Box(
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
            .padding(vertical = dimensionResource(id = R.dimen.dp_15))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (viewModel.failedEventList.value.isNotEmpty()) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimen_5_dp),
                                text = stringResource(id = R.string.sync_failed_message),
                                style = smallTextStyle,
                                color = textColorDark
                            )
                            ButtonPositive(
                                modifier = Modifier.wrapContentHeight(),
                                buttonTitle = stringResource(id = R.string.export_failed_event),
                                isActive = true,
                                isArrowRequired = false,
                                textColor = white,
                            ) {
                                viewModel.findFailedEventAndWriteIntoFile()
                            }
                        }
                    }
                }
            }
            ButtonPositive(
                buttonTitle = stringResource(id = R.string.sync_all_data),
                isArrowRequired = false,
                isActive = true
            ) {
                viewModel.selectedSyncType.intValue = SyncType.SYNC_ALL.ordinal
                CoreLogger.d(
                    context,
                    "SyncHomeScreen",
                    "Sync All Data Click: ${viewModel.selectedSyncType.intValue}"
                )
                startSyncProcess(context, viewModel, isNetworkAvailable.value)
            }
        }
    }
}

@Composable
fun LastSyncTime(viewModel: SyncHomeViewModel, onCancelWorker: () -> Unit) {
    if (viewModel.lastSyncTime.longValue != 0L) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimen_10_dp)
                .clickable {
                    onCancelWorker()
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.last_sync_date_time),
                style = mediumTextStyle,
                color = textColorDark
            )

            Text(
                text = SimpleDateFormat(SYNC_VIEW_DATE_TIME_FORMAT).format(viewModel.lastSyncTime.longValue),
                style = mediumTextStyle,
                color = textColorDark
            )
        }
    }
}

fun List<Events>.filterAndCountEvents(predicate: (Events) -> Boolean): Pair<Int, Int> {
    val totalCount = filter(predicate).size
    val successCount =
        filter { predicate(it) && it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus }.size
    return totalCount to successCount
}

fun List<Events>.filterAndCountProducerEvents(predicate: (Events) -> Boolean): Pair<Int, Int> {
    val totalCount = filter(predicate).size
    val successCount =
        filter {
            predicate(it) && it.status != EventSyncStatus.PRODUCER_FAILED.eventSyncStatus
                    && it.status != EventSyncStatus.OPEN.eventSyncStatus
                    && it.status != EventSyncStatus.IMAGE_NOT_EXIST.eventSyncStatus
        }.size
    return totalCount to successCount
}

fun List<Events>.filterBeneficiaryEvent(predicate: (Events) -> Boolean): Pair<Int, Int> {
    val totalCount = filter(predicate).size
    val successCount =
        filter { predicate(it) && it.name == EventName.ADD_DIDI.name || it.name == EventName.UPDATE_DIDI.name || it.name == EventName.DELETE_DIDI.name }.size
    return totalCount to successCount
}

fun HandleWorkerState(
    uploadWorkerInfo: WorkInfo?, viewModel: SyncHomeViewModel, context: Context,
    scope: CoroutineScope
) {
    when (uploadWorkerInfo?.state) {
        WorkInfo.State.RUNNING -> {
            if (viewModel.isSyncStarted.value) {
                when (viewModel.selectedSyncType.intValue) {
                    SyncType.SYNC_ONLY_DATA.ordinal -> viewModel.isDataPBVisible.value = true
                    SyncType.SYNC_ONLY_IMAGES.ordinal -> viewModel.isImagePBVisible.value = true
                    SyncType.SYNC_ALL.ordinal -> viewModel.isDataPBVisible.value = true
                }
            }
            viewModel.checkSyncProgressBarStatus(isWorkerRunning = true)
            viewModel.findFailedEventList()
            CoreLogger.d(
                context,
                "SyncHomeScreen",
                "Worker Status: RUNNING :: ${System.currentTimeMillis()}"
            )
        }

        WorkInfo.State.ENQUEUED -> {
            viewModel.isSyncStarted.value = true
            scope.launch { delay(300) }
            viewModel.checkSyncProgressBarStatus(isWorkerRunning = false)
            viewModel.findFailedEventList()
            CoreLogger.d(
                context,
                "SyncHomeScreen",
                "Worker Status: ENQUEUED :: ${System.currentTimeMillis()}"
            )
        }

        WorkInfo.State.SUCCEEDED -> CoreLogger.d(
            context,
            "SyncHomeScreen",
            "Worker Status: SUCCEEDED :: ${System.currentTimeMillis()}"
        )

        WorkInfo.State.FAILED -> {
            viewModel.isDataStatusVisible.value = false
            viewModel.isImageStatusVisible.value = false
            CoreLogger.d(
                context,
                "SyncHomeScreen",
                "Worker Status: FAILED :: ${System.currentTimeMillis()}"
            )
        }

        WorkInfo.State.BLOCKED -> {
            viewModel.isDataStatusVisible.value = false
            viewModel.isImageStatusVisible.value = false
            CoreLogger.d(
                context,
                "SyncHomeScreen",
                "Worker Status: BLOCKED :: ${System.currentTimeMillis()}"
            )
        }

        WorkInfo.State.CANCELLED -> {
            viewModel.isDataStatusVisible.value = false
            viewModel.isImageStatusVisible.value = false
            CoreLogger.d(
                context,
                "SyncHomeScreen",
                "Worker Status: CANCELLED :: ${System.currentTimeMillis()}"
            )
        }

        null -> CoreLogger.d(
            context,
            "SyncHomeScreen",
            "Worker Status: Null :: ${System.currentTimeMillis()}"
        )
    }
}


@Composable
private fun SyncDataCard(
    viewModel: SyncHomeViewModel,
    context: Context,
    isNetworkAvailable: MutableState<Boolean>,
    onViewProcessClick: () -> Unit
) {
    EventTypeCard(
        title = stringResource(id = R.string.sync_data),
        progress = viewModel.dataEventProgress.floatValue,
        producerProgress = viewModel.dataProducerEventProgress.floatValue,
        isProgressBarVisible = viewModel.isDataPBVisible.value,
        syncButtonTitle = stringResource(id = R.string.sync_only_data),
        isImageSyncCard = false,
        onSyncButtonClick = {
            viewModel.selectedSyncType.intValue = SyncType.SYNC_ONLY_DATA.ordinal
            CoreLogger.d(
                context,
                "SyncHomeScreen",
                "Sync Only Data Click: ${viewModel.selectedSyncType.intValue}"
            )
            startSyncProcess(context, viewModel, isNetworkAvailable.value)
        },
        isStatusVisible = viewModel.isDataStatusVisible.value,
        onCardClick = {

        },
        onViewProcessClick = {
            onViewProcessClick()
        }
    )
}

@Composable
private fun SyncImageCard(
    totalImageEventCount: MutableState<Int>,
    viewModel: SyncHomeViewModel,
    context: Context,
    isNetworkAvailable: MutableState<Boolean>,
    onViewProcessClick: () -> Unit
) {
    if (totalImageEventCount.value > 0) {
        EventTypeCard(
            title = stringResource(id = R.string.sync_images),
            progress = viewModel.imageEventProgress.floatValue,
            producerProgress = viewModel.imageProducerEventProgress.floatValue,
            isProgressBarVisible = viewModel.isImagePBVisible.value,
            isImageSyncCard = true,
            onSyncButtonClick = {
                if (viewModel.isSyncImageActive.value) {

                    viewModel.selectedSyncType.intValue = SyncType.SYNC_ONLY_IMAGES.ordinal
                    CoreLogger.d(
                        context,
                        "SyncHomeScreen",
                        "Sync Only Images Click: ${viewModel.selectedSyncType.intValue}"
                    )
                    startSyncProcess(context, viewModel, isNetworkAvailable.value)
                } else {
                    showCustomToast(context, context.getString(R.string.sync_data_first_message))
                }
            },
            syncButtonTitle = stringResource(id = R.string.sync_only_images),
            isStatusVisible = viewModel.isImageStatusVisible.value,
            onCardClick = {
            },
            onViewProcessClick = {
                onViewProcessClick()
            }
        )
    }
}


private fun startSyncProcess(
    context: Context,
    viewModel: SyncHomeViewModel,
    isNetworkAvailable: Boolean
) {
    if (isNetworkAvailable) {
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