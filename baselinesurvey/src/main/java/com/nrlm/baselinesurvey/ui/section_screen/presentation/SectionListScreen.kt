package com.nrlm.baselinesurvey.ui.section_screen.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.navigation.home.HomeScreens
import com.nrlm.baselinesurvey.navigation.home.QUESTION_SCREEN_ROUTE_NAME
import com.nrlm.baselinesurvey.navigation.home.VIDEO_PLAYER_SCREEN_ROUTE_NAME
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.SearchWithFilterViewComponent
import com.nrlm.baselinesurvey.ui.common_components.SectionItemComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.DescriptionContentComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.ImageExpanderDialogComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.ModelBottomSheetDescriptionContentComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.descriptionContentStateSample
import com.nrlm.baselinesurvey.ui.section_screen.viewmode.SectionListScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_14_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.greyBorder
import com.nrlm.baselinesurvey.ui.theme.lightBlue
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.states.DescriptionContentState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SectionListScreen(
    navController: NavController,
    viewModel: SectionListScreenViewModel,
    modifier: Modifier = Modifier,
    didiId: Int
) {

    val loaderState = viewModel.loaderState.value

    LaunchedEffect(key1 = true) {
        viewModel.init(didiId)
    }

    val sectionsList = viewModel.sectionItemStateList.value

    val selectedSectionDescription = remember {
        mutableStateOf(DescriptionContentState())
    }

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current

    val showExpandedImage = remember {
        mutableStateOf(false)
    }
    val expandedImagePath = remember {
        mutableStateOf("")
    }

    BackHandler {
        navController.popBackStack()
    }

    Surface(color = white) {
        
        LoaderComponent(visible = loaderState.isLoaderVisible)

        if (showExpandedImage.value) {
            ImageExpanderDialogComponent(
                expandedImagePath.value
            ) {
                showExpandedImage.value = false
            }
        }

        if (!loaderState.isLoaderVisible) {
            if (sectionsList.size == 1 && sectionsList[0].section.sectionName.equals(NO_SECTION, true)) {
                navController.navigate("$QUESTION_SCREEN_ROUTE_NAME/${sectionsList[0].section.sectionId}/$didiId")
            } else {
                ModelBottomSheetDescriptionContentComponent(
                    sheetContent = {
                        DescriptionContentComponent(
                            buttonClickListener = {
                                scope.launch {
                                    scaffoldState.hide()
                                }
                            },
                            imageClickListener = {
                                expandedImagePath.value = it
                                showExpandedImage.value = true
                            },
                            videoLinkClicked = {
                                navController.navigate("$VIDEO_PLAYER_SCREEN_ROUTE_NAME/$it")
                            },
                            descriptionContentState = selectedSectionDescription.value
                        )
                    },
                    sheetState = scaffoldState,
                    sheetElevation = 20.dp,
                    sheetBackgroundColor = Color.White,
                    sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimen_14_dp),
                        modifier = Modifier.padding(
                            horizontal = dimen_16_dp,
                            vertical = dimen_16_dp
                        )
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .background(
                                        lightBlue,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        navController.navigate("$VIDEO_PLAYER_SCREEN_ROUTE_NAME/https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6ParticipatoryWealthRanking.mp4")
                                    }
                                    .border(
                                        border = ButtonDefaults.outlinedBorder,
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Image(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clickable {
                                        },
                                    painter = painterResource(id = R.drawable.ic_ionic_close),
                                    contentDescription = ""
                                )
                                Text(text = "Quick refresher on baseline survey")
                                Image(
                                    modifier = Modifier.padding(10.dp),
                                    painter = painterResource(id = R.drawable.info_icon),
                                    contentDescription = ""
                                )
                            }
                        }

                        item {
                            SearchWithFilterViewComponent(
                                placeholderString = "Search Question",
                                showFilter = false,
                                onFilterSelected = {

                                },
                                onSearchValueChange = {

                                }
                            )
                        }

                        item {
                            Text(
                                text = "Choose Section",
                                style = smallerTextStyle,
                                color = textColorDark
                            )
                        }

                        itemsIndexed(items = sectionsList) { index, sectionStateItem ->
                            SectionItemComponent(
                                sectionStateItem = sectionStateItem,
                                onclick = {
                                    navController.navigate("$QUESTION_SCREEN_ROUTE_NAME/${sectionStateItem.section.sectionId}/$didiId")
                                },
                                onDetailIconClicked = {
                                    scope.launch {
                                        //TODO Modify code to handle contentList.
                                        selectedSectionDescription.value =
                                            selectedSectionDescription.value.copy(
                                                textTypeDescriptionContent = sectionStateItem.section.sectionDetails
                                            )

                                        delay(100)
                                        if (!scaffoldState.isVisible) {
                                            scaffoldState.show()
                                        } else {
                                            scaffoldState.hide()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}