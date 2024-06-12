package com.sarathi.contentmodule.ui.content_detail_screen.screen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.capitalizeFirstLetter
import com.nudge.core.ui.events.theme.blueDark
import com.nudge.core.ui.events.theme.dimen_100_dp
import com.nudge.core.ui.events.theme.dimen_10_dp
import com.nudge.core.ui.events.theme.dimen_16_dp
import com.nudge.core.ui.events.theme.dimen_20_dp
import com.nudge.core.ui.events.theme.dimen_5_dp
import com.nudge.core.ui.events.theme.mediumTextStyle
import com.nudge.core.ui.events.theme.white
import com.sarathi.contentmodule.R
import com.sarathi.contentmodule.ui.component.BasicContentComponent
import com.sarathi.contentmodule.ui.component.ButtonPositive
import com.sarathi.contentmodule.ui.component.SearchWithFilterViewComponent
import com.sarathi.contentmodule.ui.content_detail_screen.viewmodel.ContentDetailViewModel
import com.sarathi.contentmodule.utils.event.SearchEvent
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ContentDetailScreen(
    navController: NavController = rememberNavController(),
    viewModel: ContentDetailViewModel = hiltViewModel(),
    outerState: LazyListState = rememberLazyListState(),
    innerState: LazyGridState = rememberLazyGridState(),
    queLazyState: LazyListState = rememberLazyListState(),
    matId: Int,
    contentType: Int,
    onNavigateToMediaScreen: (fileType: String, key: String) -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(
            InitDataEvent.InitContentScreenState(
                matId = matId,
                contentCategory = contentType
            )
        )

    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val innerFirstVisibleItemIndex by remember {
        derivedStateOf {
            innerState.firstVisibleItemIndex
        }
    }

    Scaffold(containerColor = white, topBar = {
        TopAppBar(title = {
            Row(modifier = Modifier) {
                IconButton(
                    onClick = { }, modifier = Modifier
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sarathi_logo),
                        contentDescription = "Back Button",
                        tint = Color.Black
                    )
                }
            }
        }, actions = {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.more_icon),
                    contentDescription = "more action button",
                    tint = blueDark,
                    modifier = Modifier.padding(dimen_10_dp)
                )
            }
        }, backgroundColor = Color.White, elevation = dimen_10_dp
        )
    }, bottomBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            ButtonPositive(
                buttonTitle = stringResource(R.string.go_back), isActive = true, isLeftArrow = false

            ) {
                navController.popBackStack()
            }
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 75.dp)
        ) {
            SearchWithFilterViewComponent(placeholderString = "Search",
                filterSelected = viewModel.filterSelected.value,
                modifier = Modifier.padding(horizontal = dimen_10_dp),
                showFilter = true,
                onFilterSelected = {
                    if (viewModel.filterContentList.value.isNotEmpty()) {
                        viewModel.filterSelected.value = !it
                    }
                },
                onSearchValueChange = { queryTerm ->
                    viewModel.onEvent(
                        SearchEvent.PerformSearch(
                            queryTerm,
                            false,
                            ""
                        )
                    )
                })
            Spacer(modifier = Modifier.height(dimen_16_dp))
            if (viewModel.filterContentList.value.isNotEmpty()) {
                BoxWithConstraints(
                    modifier = Modifier
                        .padding(top = dimen_20_dp)
                        .scrollable(
                            state = rememberScrollableState {
                                scope.launch {
                                    val toDown = it <= 0
                                    if (toDown) {
                                        if (outerState.run { firstVisibleItemIndex == layoutInfo.totalItemsCount - 1 }) {
                                            innerState.scrollBy(-it)
                                        } else {
                                            outerState.scrollBy(-it)
                                        }
                                    } else {
                                        if (innerFirstVisibleItemIndex == 0 && innerState.firstVisibleItemScrollOffset == 0) {
                                            outerState.scrollBy(-it)
                                        } else {
                                            innerState.scrollBy(-it)
                                        }
                                    }
                                }
                                it
                            },
                            Orientation.Vertical,
                        )
                ) {
                    if (viewModel.filterContentMap.values.isNotEmpty() && viewModel.filterSelected.value) {
                        LazyColumn(
                            state = outerState,
                            modifier = Modifier.padding(bottom = 50.dp)
                        ) {
                            viewModel.filterContentMap.forEach { (category, itemsInCategory) ->
                                item {
                                    Text(
                                        text = category.capitalizeFirstLetter(),
                                        style = mediumTextStyle,
                                        color = Color.Black,
                                        modifier = Modifier.padding(
                                            horizontal = dimen_16_dp,
                                            vertical = dimen_5_dp
                                        )
                                    )
                                }
                                item {
                                    LazyVerticalGrid(
                                        state = innerState,
                                        modifier = Modifier.heightIn(
                                            min = dimen_100_dp,
                                            max = maxHeight
                                        ),
                                        columns = GridCells.Fixed(4),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        itemsIndexed(
                                            items = itemsInCategory
                                        ) { _, item ->
                                            ContentRowView(
                                                item,
                                                viewModel,
                                                onNavigateToMediaScreen,
                                                context
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            itemsIndexed(
                                items = viewModel.filterContentList.value
                            ) { index, item ->
                                ContentRowView(
                                    item,
                                    viewModel,
                                    onNavigateToMediaScreen,
                                    context
                                )
                            }
                        }
                    }

                }
            }
        }
    }


}

@Composable
private fun ContentRowView(
    item: Content,
    viewModel: ContentDetailViewModel,
    onNavigateToMediaScreen: (fileType: String, key: String) -> Unit,
    context: Context
) {
    BasicContentComponent(contentType = item.contentType,
        contentTitle = item.contentName,
        contentValue = item.contentValue,
        onClick = {
            if (viewModel.isFilePathExists(item.contentValue)) {
                onNavigateToMediaScreen(
                    item.contentType,
                    item.contentKey
                )
            } else {
                Toast.makeText(
                    context,
                    "file not Exists ",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
}