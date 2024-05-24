package com.sarathi.contentmodule.ui.content_screen.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sarathi.contentmodule.ui.component.BasicContentComponent
import com.sarathi.contentmodule.ui.component.ButtonComponent
import com.sarathi.contentmodule.ui.content_screen.viewmodel.BaseContentScreenViewModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent

@Composable
fun BaseContentScreen(
    viewModel: BaseContentScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    if (viewModel.contentList.value.isNotEmpty()) {
        Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)) {
            viewModel.contentList.value.forEachIndexed { index, item ->
                if (index < 3) {
                    BasicContentComponent(
                        contentType = item.contentType,
                        contentTitle = item.contentType,
                        contentValue = item.contentValue
                    ) {
                        //  ExoMediaPlayer()

                    }
                } else if (index == 3) {
                    ButtonComponent(title = "+ ${viewModel.contentList.value.size - index} More Data")
                }
            }
        }
    }

}