package com.sarathi.contentmodule.ui.content_screen.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sarathi.contentmodule.constants.Constants.CONTENT_THRESHOLD_VALUE
import com.sarathi.contentmodule.ui.component.BasicContentComponent
import com.sarathi.contentmodule.ui.content_screen.viewmodel.BaseContentScreenViewModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent

@Composable
fun BaseContentScreen(
    matId: Int,
    contentScreenCategory: Int,
    viewModel: BaseContentScreenViewModel = hiltViewModel(),
    onClick: (contentValue: String, contentKey: String, contentType: String, isLimitContentData: Boolean) -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitContentScreenState(matId, contentScreenCategory))
    }
    if (viewModel.contentList.value.isNotEmpty()) {
        Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)) {
            viewModel.contentList.value.forEachIndexed { index, item ->
                BasicContentComponent(
                    contentType = item.contentType,
                    contentTitle = item.contentName,
                    contentValue = item.contentValue,
                    isLimitContentData = index == CONTENT_THRESHOLD_VALUE,
                    totalContent = viewModel.contentCount.value - CONTENT_THRESHOLD_VALUE,
                    onClick = {
                        onClick(
                            item.contentValue,
                            item.contentKey,
                            item.contentType,
                            index == CONTENT_THRESHOLD_VALUE
                        )
                    }
                )
            }
        }
    }
}
