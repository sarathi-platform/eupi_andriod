package com.nrlm.baselinesurvey.ui.section_screen.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.navigation.home.VIDEO_PLAYER_SCREEN_ROUTE_NAME
import com.nrlm.baselinesurvey.navigation.home.navigateBackToSurveyeeListScreen
import com.nrlm.baselinesurvey.navigation.home.navigateToQuestionScreen
import com.nrlm.baselinesurvey.navigation.home.navigateToSearchScreen
import com.nrlm.baselinesurvey.ui.common_components.SectionItemComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.DescriptionContentComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.ImageExpanderDialogComponent
import com.nrlm.baselinesurvey.ui.description_component.presentation.ModelBottomSheetDescriptionContentComponent
import com.nrlm.baselinesurvey.ui.section_screen.viewmode.SectionListScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.borderGrey
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_14_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_30_dp
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.lightBlue
import com.nrlm.baselinesurvey.ui.theme.placeholderGrey
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.states.DescriptionContentState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SectionListScreen(
    navController: NavController,
    viewModel: SectionListScreenViewModel,
    modifier: Modifier = Modifier,
    didiId: Int,
    surveyId: Int
) {

    val loaderState = viewModel.loaderState.value
    lateinit var didiDetail: SurveyeeEntity

    LaunchedEffect(key1 = true) {
        viewModel.init(didiId, surveyId)
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

    val isBannerExpanded = remember {
        mutableStateOf(false)
    }

    BackHandler {
        navigateBackToSurveyeeListScreen(navController)
    }

    Scaffold(
        backgroundColor = white,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                backgroundColor = white,
            ) {
                IconButton(
                    onClick = { navigateBackToSurveyeeListScreen(navController) },
                    modifier = Modifier
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back Button")
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart),
                        text = viewModel.didiName.value,
                        style = largeTextStyle
                    )
                    Box(
                        Modifier
                            .padding(dimen_1_dp)
                            .padding(end = dimen_10_dp)
                            .background(
                                lightBlue, shape = RoundedCornerShape(dimen_30_dp)
                            )
                            .align(Alignment.CenterEnd)
                            .zIndex(1f)
                    ) {
//                        Image(
//                            modifier = Modifier
//                                .padding(5.dp)
//                                .clickable {
//                                    if (!isBannerExpanded.value)
//                                        isBannerExpanded.value = true
//                                },
//                            painter = painterResource(id = R.drawable.info_icon),
//                            contentDescription = ""
//                        )
                    }
                    this@TopAppBar.AnimatedVisibility(
                        visible = isBannerExpanded.value,
                        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                        exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .background(
                                    lightBlue, shape = RoundedCornerShape(6.dp)
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
                                        isBannerExpanded.value = false
                                    },
                                painter = painterResource(id = R.drawable.ic_ionic_close),
                                contentDescription = ""
                            )
                            Text(text = "Quick refresher on baseline survey")
                            Spacer(modifier = Modifier.size(dimen_24_dp))
                            /*Image(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clickable {
                                        //Keeping empty to avoid crash.
                                    },
                                painter = painterResource(id = R.drawable.info_icon),
                                contentDescription = ""
                            )*/
                        }
                    }
                }
            }
        }
    ) {
        it
//        LoaderComponent(visible = loaderState.isLoaderVisible)

        if (showExpandedImage.value) {
            ImageExpanderDialogComponent(
                expandedImagePath.value
            ) {
                showExpandedImage.value = false
            }
        }

        if (!loaderState.isLoaderVisible) {
            if (sectionsList.size == 1 && sectionsList[0].section.sectionName.equals(NO_SECTION, true)) {
                navigateToQuestionScreen(didiId, sectionsList[0].section.sectionId, surveyId = sectionsList[0].section.surveyId, navController)
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
                            OutlinedButton(modifier = Modifier.background(color = white, shape = RoundedCornerShape(6.dp)),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, borderGrey),
                                onClick = {
                                    navigateToSearchScreen(navController)
                                }
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_search),
                                        tint = placeholderGrey,
                                        contentDescription = "seach icon",
                                        modifier = Modifier.absolutePadding(top = 3.dp)
                                    )
                                    Spacer(modifier = Modifier
                                        .fillMaxHeight()
                                        .width(dimen_10_dp))
                                    Text(
                                        text = "Search Question",
                                        style = TextStyle(
                                            fontFamily = NotoSans,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        ), color = placeholderGrey
                                    )
                                }
                            }
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
                                index,
                                sectionStateItem = sectionStateItem,
                                onclick = {
                                    navigateToQuestionScreen(
                                        didiId = didiId,
                                        sectionId = sectionStateItem.section.sectionId,
                                        sectionStateItem.section.surveyId,
                                        navController
                                    )
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