package com.sarathi.contentmodule.ui.content_screen.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_5_dp
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
    onClick: (contentValue: String, contentKey: String, contentType: String, isLimitContentData: Boolean, contentTitle: String) -> Unit
) {
    LaunchedEffect(key1 = matId) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitContentScreenState(matId, contentScreenCategory))
    }
    if (viewModel.contentList.value.isNotEmpty()) {
        LazyRow(
            modifier = Modifier
                .padding(start = dimen_16_dp, end = dimen_16_dp, bottom = dimen_5_dp)
        ) {
            itemsIndexed(viewModel.contentList.value.take(4)) { index, item ->
                BasicContentComponent(
                    contentType = item.contentType,
                    contentTitle = item.contentName,
                    isLimitContentData = index == CONTENT_THRESHOLD_VALUE,
                    totalContent = viewModel.contentCount.value - CONTENT_THRESHOLD_VALUE,
                    onClick = {
                        onClick(
                            item.contentValue,
                            item.contentKey,
                            item.contentType,
                            index == CONTENT_THRESHOLD_VALUE,
                            item.contentName
                        )
                    }
                )
            }
        }
    }
}
