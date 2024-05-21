package com.patsurvey.nudge.activities.ui.login.dataloadingscreen.screen

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.viewmodel.DataLoadingScreenViewModel
import com.sarathi.missionactivitytask.MatActivity

@Composable
fun DataLoadingScreen(viewModel: DataLoadingScreenViewModel) {
    val context = LocalContext.current
    val loaderState = viewModel.loaderState.value
    LaunchedEffect(key1 = true) {
        viewModel.loaderView(true)
        viewModel.fetchAllData {
            context.startActivity(Intent(context, MatActivity::class.java))
        }
        viewModel.downloadContentData(context = context)
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoaderComponent(visible = loaderState.isLoaderVisible)
    }
}