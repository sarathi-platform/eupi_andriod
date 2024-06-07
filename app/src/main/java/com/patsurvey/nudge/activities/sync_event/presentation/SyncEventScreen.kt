package com.patsurvey.nudge.activities.sync_event.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.work.WorkInfo
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.patsurvey.nudge.activities.sync_event.viewmodel.SyncEventViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.mediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.utils.ConnectionMonitor

@SuppressLint("RestrictedApi")
@Composable
fun SyncEventScreen(
    viewModel: SyncEventViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier,
    ) {
    val syncEventList = viewModel.syncEventList.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context= LocalContext.current
    val loaderState = viewModel.loaderState
    LaunchedEffect(key1 = true) {
        viewModel.getAllEvents()
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.sync_your_data),
                        style = mediumTextStyle,
                        color = textColorDark,
                        modifier = Modifier,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = textColorDark)
                    }
                },
                backgroundColor = Color.White,
                elevation = 10.dp
            )
        }
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
        ) {
            Row( modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(top = dimen_18_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)) {
                ButtonPositive(
                    buttonTitle = "Sync Now",
                    isArrowRequired = false,
                    isActive = true,
                    onClick = {
                        viewModel.syncAllPending(
                            ConnectionMonitor.DoesNetworkHaveInternet.getNetworkStrength(),
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                ButtonPositive(
                    buttonTitle = stringResource(id = R.string.retry),
                    isArrowRequired = false,
                    isActive = true,
                    onClick = {
                        viewModel.getAllEvents()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

        }

    }


    if (loaderState.value.isLoaderVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = blueDark,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun WorkStatusScreen(workStatus: LiveData<List<WorkInfo>>) {
    val workInfo = workStatus.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Work Status:", style = mediumTextStyle)
        Spacer(modifier = Modifier.height(16.dp))
        workInfo.value?.let {
            if(it.isNotEmpty()){
                it.forEach { work->
                    when (work.state) {
                        WorkInfo.State.ENQUEUED -> Text(text = "Enqueued")
                        WorkInfo.State.RUNNING -> Text(text = "Running")
                        WorkInfo.State.SUCCEEDED -> Text(text = "Succeeded")
                        WorkInfo.State.FAILED -> Text(text = "Failed")
                        WorkInfo.State.BLOCKED -> Text(text = "Blocked")
                        WorkInfo.State.CANCELLED -> Text(text = "Cancelled")
                    }
                }
            }else {
                Log.d("TAG", "WorkStatusScreen: Work Info List empty ")
            }

        } ?: Text(text = "Unknown")
    }
}
