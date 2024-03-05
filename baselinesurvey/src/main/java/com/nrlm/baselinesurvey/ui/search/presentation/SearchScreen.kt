package com.nrlm.baselinesurvey.ui.search.presentation

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.ALL_TAB
import com.nrlm.baselinesurvey.ARG_FROM_QUESTION_SCREEN
import com.nrlm.baselinesurvey.ARG_FROM_SECTION_SCREEN
import com.nrlm.baselinesurvey.QUESTION_DATA_TAB
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.SECTION_INFORMATION_TAB
import com.nrlm.baselinesurvey.navigation.home.navigateToSelectedSectionFromSearch
import com.nrlm.baselinesurvey.ui.common_components.CustomOutlineTextField
import com.nrlm.baselinesurvey.ui.common_components.SearchTab
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.search.viewmodel.SearchScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGrey
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.placeholderGrey
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleWithNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.unselectedTabColor
import com.nrlm.baselinesurvey.ui.theme.white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreens(
    modifier: Modifier = Modifier,
    viewModel: SearchScreenViewModel,
    surveyId: Int,
    surveyeeId: Int,
    fromScreen: String = ARG_FROM_SECTION_SCREEN,
    navController: NavController
) {
    val searchText = viewModel.searchText.collectAsState()
    val isSearching = viewModel.isSearching.collectAsState()
    val searchItems = viewModel.complexSearchStateList.value

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.initSearch(surveyId)
    }

    var selectedTabIndex = remember { mutableIntStateOf(0) }

    val tabs = listOf(ALL_TAB, QUESTION_DATA_TAB, SECTION_INFORMATION_TAB)

    Scaffold(
        topBar = {
            CustomOutlineTextField(
                value = searchText.value,//text showed on SearchBar
                onValueChange = {
                    viewModel.onSearchTextChange(it, viewModel.searchTabFilter.value)
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
                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
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
                                    viewModel.onSearchTextChange(
                                        "",
                                        viewModel.searchTabFilter.value
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
                    .padding(horizontal = 16.dp)) {
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

                                if (selectedTabIndex.intValue == tabs.indexOf(SECTION_INFORMATION_TAB))
                                    viewModel.onSearchTextChange(searchText.value, SearchTab.SECTION_INFORMATION_TAB)
                                if (selectedTabIndex.intValue == tabs.indexOf(QUESTION_DATA_TAB))
                                    viewModel.onSearchTextChange(searchText.value, SearchTab.QUESTION_DATA_TAB)
                                if (selectedTabIndex.intValue == tabs.indexOf(ALL_TAB))
                                    viewModel.onSearchTextChange(searchText.value, SearchTab.ALL_TAB)
                                viewModel.onEvent(SearchEvent.SearchTabChanged)
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
                                style = SpanStyle(
                                    Color(0xFF7D7572),
                                    fontFamily = NotoSans,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            ) {
                                append(item.sectionName)
                            }
                            if (!item.isSectionSearchOnly) {
                                withStyle(
                                    style = SpanStyle(
                                        textColorDark,
                                        fontFamily = NotoSans,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                ) {
                                    append(" >> ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        textColorDark,
                                        fontFamily = NotoSans,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                ) {
                                    append(item.questionTitle)
                                }
                            }
                        }, modifier = Modifier.clickable {
                            navController.navigateToSelectedSectionFromSearch(surveyId = surveyId, didiId = surveyeeId,
                                sectionId = if (item.itemParentId != -1) item.itemParentId else item.itemId, isFromQuestionSearch = fromScreen == ARG_FROM_QUESTION_SCREEN)
                           /*showCustomToast(context, "item-> sectionName${item.sectionName}," +
                                    " questionTitle: ${item.questionTitle}")*/
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SearchScreensPreview() {

}