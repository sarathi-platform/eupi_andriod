package com.nrlm.baselinesurvey.ui.sync_event.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.common_setting.SyncEventCard
import com.nrlm.baselinesurvey.ui.sync_event.viewmodel.SyncEventViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.mediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.utils.ConnectionMonitor
import com.nudge.syncmanager.utils.PRODUCER_WORKER_TAG

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
    val workManager = WorkManager.getInstance(context)
    var workInfo by remember { mutableStateOf<WorkInfo?>(null) }
    
    val loaderState = viewModel.loaderState
    LaunchedEffect(key1 = true) {
        viewModel.getAllEvents()
    }
    LaunchedEffect(Unit) {
        val info = workManager.getWorkInfosForUniqueWork(PRODUCER_WORKER_TAG).await()
        if(info.isNotEmpty()){
            workInfo=info[0]
        }
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
            Column(modifier = Modifier.wrapContentSize()) {
                Text(text = "WorkManager Status:")
                Text(text = workInfo?.state?.name ?: "No WorkInfo Available")
            }
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
//                         viewModel.syncAllPending(ConnectionMonitor.DoesNetworkHaveInternet.getNetworkStrength(),lifecycleOwner)
                        viewModel.getAllEvents()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }


           LazyColumn {
                    itemsIndexed(syncEventList.value) { index, item ->
                        SyncEventCard(
                            title = item.id,
                            subTitle = item.name,
                            status = "OPEN",
                        ) {
                        }
                    }
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
