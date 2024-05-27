package com.sarathi.contentmodule.ui.content_detail_screen.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.contentmodule.R
import com.sarathi.contentmodule.ui.component.BasicContentComponent
import com.sarathi.contentmodule.ui.component.ButtonPositive
import com.sarathi.contentmodule.ui.component.SearchWithFilterViewComponent
import com.sarathi.contentmodule.ui.content_detail_screen.viewmodel.ContentDetailViewModel
import com.sarathi.contentmodule.ui.theme.blueDark
import com.sarathi.contentmodule.ui.theme.white
import com.sarathi.contentmodule.utils.event.SearchEvent
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ContentDetailScreen(
    navController: NavController = rememberNavController(),
    viewModel: ContentDetailViewModel = hiltViewModel(),
    onNavigateToMediaScreen: (fileType: String, key: String) -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
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
                    modifier = Modifier.padding(10.dp)
                )
            }
        }, backgroundColor = Color.White, elevation = 10.dp
        )
    }, bottomBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            ButtonPositive(
                buttonTitle = "Go Back", isActive = true, isLeftArrow = false

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
                filterSelected = false,
                modifier = Modifier.padding(horizontal = 10.dp),
                showFilter = true,
                onFilterSelected = {},
                onSearchValueChange = { queryTerm ->
                    viewModel.onEvent(
                        SearchEvent.PerformSearch(
                            queryTerm,
                            false,
                            ""
                        )
                    )
                })
            Spacer(modifier = Modifier.height(16.dp))
            if (viewModel.contentList.value.isNotEmpty()) {
                Column {
                    LazyVerticalGrid(
                        userScrollEnabled = false,
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        itemsIndexed(
                            items = viewModel.contentList.value
                        ) { index, item ->
                            BasicContentComponent(contentType = item.contentType,
                                contentTitle = item.contentName,
                                contentValue = item.contentValue,
                                onClick = {
                                    onNavigateToMediaScreen(item.contentType, item.contentKey)
                                })
                        }
                    }
                }
            }
        }
    }


}