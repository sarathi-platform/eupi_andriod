package com.sarathi.surveymanager.search.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nudge.core.ALL_TAB
import com.nudge.core.ARG_FROM_SECTION_SCREEN
import com.nudge.core.QUESTION_DATA_TAB
import com.nudge.core.SECTION_INFORMATION_TAB
import com.nudge.core.SearchTab
import com.nudge.core.model.uiModel.ComplexSearchState
import com.nudge.core.ui.events.SearchEvent
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.mediumSpanStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.searchSectionTitleColor
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.smallTextStyleWithNormalWeight
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.unselectedTabColor
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.search.viewmodel.SearchScreenViewModel
import com.sarathi.surveymanager.ui.component.CustomOutlineTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreens(
    modifier: Modifier = Modifier,
    viewModel: SearchScreenViewModel = hiltViewModel(),
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    activityConfigId: Int,
    fromScreen: String = ARG_FROM_SECTION_SCREEN,
    subjectType: String,
    activityType: String,
    navController: NavController,
    onSearchItemClicked: (surveyId: Int, activityConfigId: Int, subjectType: String, activityType: String, complexSearchState: ComplexSearchState, taskEntity: ActivityTaskEntity) -> Unit
) {
    val searchText = viewModel.searchText.collectAsState()
    val isSearching = viewModel.isSearching.collectAsState()
    val searchItems = viewModel.complexSearchStateList.value

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.setPreviousScreenData(
            surveyId,
            sectionId,
            taskId = taskId,
            activityConfigId = activityConfigId,
            fromScreen = fromScreen
        )
        viewModel.initSearch()
    }

    var selectedTabIndex = remember { mutableIntStateOf(0) }

    val tabs = listOf(ALL_TAB, QUESTION_DATA_TAB, SECTION_INFORMATION_TAB)

    Scaffold(
        topBar = {
            CustomOutlineTextField(
                value = searchText.value,//text showed on SearchBar
                onValueChange = {
                    viewModel.onEvent(
                        SearchEvent.PerformComplexSearch(
                            it,
                            viewModel.searchTabFilter.value
                        )
                    )
                },
                placeholder = {
                    Text(
                        text = "Search Question", style = TextStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ), color = placeholderGrey
                    )
                },
                textStyle = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                singleLine = true,
                maxLines = 1,
                colors = textFieldColors(
                    textColor = textColorDark,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = borderGrey,
                    unfocusedIndicatorColor = borderGrey,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(40.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = blueDark,
                        contentDescription = "search icon",
                        modifier = Modifier
                            .absolutePadding(top = 3.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                },
                trailingIcon = {
                    if (searchText.value.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = textColorDark,
                            modifier = Modifier
                                .absolutePadding(top = 2.dp)
                                .clickable {
                                    viewModel.onEvent(
                                        SearchEvent.PerformComplexSearch(
                                            "",
                                            viewModel.searchTabFilter.value
                                        )
                                    )
                                }
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        containerColor = Color.White.copy(alpha = 0.7f)

    ) {
        AnimatedVisibility(
            visible = searchText.value.isNotBlank(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                Modifier
                    .padding(it)
                    .padding(horizontal = 16.dp)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex.intValue,
                    containerColor = Color.White,
                    contentColor = textColorDark,
                ) {
                    tabs.forEachIndexed { tabIndex, tab ->
                        Tab(
                            selected = tabIndex == selectedTabIndex.intValue,
                            onClick = {
                                selectedTabIndex.intValue = tabIndex

                                if (selectedTabIndex.intValue == tabs.indexOf(
                                        SECTION_INFORMATION_TAB
                                    )
                                ) {
                                    viewModel.onEvent(
                                        SearchEvent.PerformComplexSearch(
                                            searchText.value,
                                            SearchTab.SECTION_INFORMATION_TAB
                                        )
                                    )
                                    viewModel.onEvent(SearchEvent.SearchTabChanged(SearchTab.SECTION_INFORMATION_TAB))
                                }
                                if (selectedTabIndex.intValue == tabs.indexOf(QUESTION_DATA_TAB)) {
                                    viewModel.onEvent(SearchEvent.SearchTabChanged(SearchTab.QUESTION_DATA_TAB))
                                    viewModel.onEvent(
                                        SearchEvent.PerformComplexSearch(
                                            searchText.value,
                                            SearchTab.QUESTION_DATA_TAB
                                        )
                                    )
                                }
                                if (selectedTabIndex.intValue == tabs.indexOf(ALL_TAB)) {
                                    viewModel.onEvent(
                                        SearchEvent.PerformComplexSearch(
                                            searchText.value,
                                            SearchTab.ALL_TAB
                                        )
                                    )
                                    viewModel.onEvent(SearchEvent.SearchTabChanged(SearchTab.ALL_TAB))
                                }
                            },
                            text = {
                                Text(
                                    text = if (tab == ALL_TAB) stringResource(id = R.string.all_tab_title) else if (tab == QUESTION_DATA_TAB) stringResource(
                                        R.string.questions_data_tab_title
                                    ) else stringResource(R.string.section_information_tab_title),
                                    style = if (tabIndex == selectedTabIndex.intValue) smallTextStyle else smallTextStyleWithNormalWeight
                                )
                            },
                            selectedContentColor = textColorDark,
                            unselectedContentColor = unselectedTabColor
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_18_dp)
                )

                Text(
                    text = if (searchText.value.isNotBlank() && searchItems.isEmpty()) "No Data Found" else searchText.value,
                    style = smallTextStyle,
                    color = textColorDark
                )

                LazyColumn(modifier = Modifier.background(white)) {
                    itemsIndexed(viewModel.filteredComplexSearchStateList) { index, item ->
                        Text(text = buildAnnotatedString {
                            withStyle(
                                style = mediumSpanStyle.copy(color = searchSectionTitleColor)
                            ) {
                                append(item.sectionName)
                            }
                            if (!item.isSectionSearchOnly) {
                                withStyle(
                                    style = mediumSpanStyle.copy(color = textColorDark)
                                ) {
                                    append(" >> ")
                                }
                                withStyle(
                                    style = mediumSpanStyle.copy(color = textColorDark)
                                ) {
                                    append(item.questionTitle)
                                }
                            }
                        }, modifier = Modifier.clickable {
                            if (fromScreen == ARG_FROM_SECTION_SCREEN) {
                                viewModel.taskEntity?.let { it1 ->
                                    onSearchItemClicked(
                                        surveyId,
                                        activityConfigId,
                                        subjectType,
                                        activityType,
                                        item,
                                        it1
                                    )
                                }
                            } else {
                                navController.navigateUp()
                            }
                        })
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimen_10_dp)
                        )
                    }
                }
            }
        }
    }


}
