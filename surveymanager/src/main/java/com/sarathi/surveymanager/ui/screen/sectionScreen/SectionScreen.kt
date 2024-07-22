package com.sarathi.surveymanager.ui.screen.sectionScreen

import ComponentName
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.isOnline
import com.nudge.core.ui.commonUi.ButtonComponentWithVisibility
import com.nudge.core.ui.commonUi.CustomLinearProgressIndicator
import com.nudge.core.ui.commonUi.customVerticalSpacer
import com.nudge.core.ui.commonUi.rememberCustomButtonVisibilityState
import com.nudge.core.ui.commonUi.rememberCustomProgressState
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_14_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.smallerTextStyle
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.ui.component.ComplexSearchComponent
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent
import com.sarathi.surveymanager.ui.description_component.presentation.DescriptionContentComponent
import com.sarathi.surveymanager.ui.description_component.presentation.ModelBottomSheetDescriptionContentComponent
import com.sarathi.surveymanager.viewmodels.surveyScreen.SectionScreenViewModel
import getColorForComponent
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun SectionScreen(
    modifier: Modifier = Modifier,
    sectionScreenViewModel: SectionScreenViewModel,
    navController: NavController,
    surveyId: Int,
    taskId: Int,
    subjectType: String,
    subjectName: String,
    activityName: String,
    activityConfigId: Int,
    sanctionedAmount: Int,
    onNavigateToGrantSurveySummaryScreen: (
        navController: NavController,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        subjectType: String,
        subjectName: String,
        activityConfigId: Int,
        sanctionedAmount: Int?,
    ) -> Unit,
    onNavigateSuccessScreen: (msg: String) -> Unit,
    onSettingClick: () -> Unit,
    onNavigateToMediaScreen: (
        navController: NavController, contentKey: String,
        contentType: String,
        contentTitle: String
    ) -> Unit
) {

    val context = LocalContext.current

    val showBottomButtonState = rememberCustomButtonVisibilityState(false)

    val pullRefreshState = rememberPullRefreshState(
        sectionScreenViewModel.loaderState.value.isLoaderVisible,
        {
            if (isOnline(context)) {
                sectionScreenViewModel.refreshData()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.refresh_failed_please_try_again),
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    )

    val sheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)

    val coroutineScope = rememberCoroutineScope()

    val buttonVisibilityKey: State<Boolean> = remember {
        derivedStateOf {
            true
        }
    }

    val linearProgressState = rememberCustomProgressState()

    LaunchedEffect(key1 = true) {
        //TODO fetch section from db
        sectionScreenViewModel
        sectionScreenViewModel.setSurveyDetails(surveyId, taskId, subjectType, activityConfigId)
        sectionScreenViewModel.onEvent(InitDataEvent.InitDataStateWithCallBack {
            // Navigate to Grant Survey Summary Screen if it is grant type activity
            if (activityName.toLowerCase() != ActivityTypeEnum.SURVEY.name.toLowerCase() && sectionScreenViewModel.sectionList.value.size == 1) {
                val sectionId: Int? =
                    sectionScreenViewModel.sectionList.value.firstOrNull()?.sectionId
                sectionId?.let {
                    onNavigateToGrantSurveySummaryScreen(
                        navController,
                        surveyId,
                        sectionId,
                        taskId,
                        subjectType,
                        subjectName,
                        activityConfigId,
                        sanctionedAmount
                    )
                }
            }
        })

    }

    LaunchedEffect(key1 = buttonVisibilityKey) {
        if (buttonVisibilityKey.value)
            showBottomButtonState.show()
        else
            showBottomButtonState.hide()
    }


    ToolBarWithMenuComponent(
        title = "",
        modifier = Modifier
            .then(modifier),
        onBackIconClick = { /*TODO*/ },
        onSearchValueChange = { },
        onBottomUI = {
            ButtonComponentWithVisibility(
                showButtonComponentState = showBottomButtonState,
                buttonTitle = "Complete Survey", isActive = true,
                onClick = {
                    onNavigateSuccessScreen("Baseline for $subjectName")
                }
            )
        },
        onSettingClick = onSettingClick,
        onContentUI = { paddingValues ->

            Column {
                Box {

                    PullRefreshIndicator(
                        refreshing = sectionScreenViewModel.loaderState.value.isLoaderVisible,
                        state = pullRefreshState,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .zIndex(1f),
                        contentColor = blueDark,
                    )


                }

                ModelBottomSheetDescriptionContentComponent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .pullRefresh(pullRefreshState),
                    sheetContent = {

                        DescriptionContentComponent(
                            contentList = sectionScreenViewModel.contentList.value,
                            onMediaContentClick = { contentKey ->
                                coroutineScope.launch {
                                    sheetState.hide()
                                }
                                sectionScreenViewModel.handleMediaContentClick(contentKey) { contentType, contentTitle ->
                                    onNavigateToMediaScreen(
                                        navController,
                                        contentKey,
                                        contentType,
                                        contentTitle
                                    )
                                }

                            },
                            onCloseListener = {
                                coroutineScope.launch {
                                    sheetState.hide()
                                }

                            }
                        )
                    },
                    sheetState = sheetState,
                    sheetElevation = dimen_20_dp,
                    sheetBackgroundColor = Color.White,
                    sheetShape = RoundedCornerShape(topStart = dimen_10_dp, topEnd = dimen_10_dp)
                ) {

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimen_14_dp),
                        modifier = Modifier
                            .padding(
                                horizontal = dimen_16_dp
                            )
                            .padding(top = dimen_16_dp)
                    ) {

                        item {
                            ComplexSearchComponent {
//                                navController.navigateToSearchScreen(surveyId, surveyeeId = didiId, fromScreen = ARG_FROM_SECTION_SCREEN)
                            }
                        }

                        item {
                            CustomLinearProgressIndicator(
                                progressState = linearProgressState
                            )
                        }

                        itemsIndexed(sectionScreenViewModel.sectionList.value) { index, section ->

                            SectionItemComponent(
                                sectionId = section.sectionId,
                                sectionUiEntity = section,
                                sectionStatus = SurveyStatusEnum.INPROGRESS.name, //TODO Fetch Dynamically From Db
                                onDetailIconClicked = { sectionId ->
                                    coroutineScope.launch {
                                        sheetState.show()
                                    }
                                },
                                onSectionItemClicked = { sectionId ->
//                                    onNavigateSurveyScreen()
                                }
                            )

                        }

                        customVerticalSpacer()
                    }

                }

            }

        }
    )

}

@Composable
fun SectionItemComponent(
    modifier: Modifier = Modifier,
    sectionId: Int,
    sectionUiEntity: SectionUiModel,
    sectionStatus: String,
    onDetailIconClicked: (sectionId: Int) -> Unit,
    onSectionItemClicked: (sectionId: Int) -> Unit
) {

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .border(
                width = 0.dp,
                color = Color.Transparent,
            )
            .then(modifier)
    ) {

        val (stepNo, stepBox) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = getColorForComponent(
                        sectionStatus,
                        ComponentName.SECTION_BOX_BORDER_COLOR
                    ),
                    shape = RoundedCornerShape(6.dp)
                )
                .background(Color.White)
                .clickable {
                    onSectionItemClicked(sectionId)
                }
                .constrainAs(stepBox) {
                    start.linkTo(parent.start)
                    top.linkTo(stepNo.bottom, margin = -16.dp)
                }

        ) {

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = getColorForComponent(
                            sectionStatus,
                            ComponentName.SECTION_BOX_CONTAINER_COLOR
                        )
                    )
                    .padding(vertical = dimen_14_dp)
                    .padding(end = dimen_16_dp, start = dimen_8_dp),
            ) {

                val (textContainer, buttonContainer, iconContainer) = createRefs()
                if (!TextUtils.isEmpty(sectionUiEntity.sectionIcon)) {
                    //Make icon dynamic from Server
                    AsyncImage(
                        model = painterResource(id = R.drawable.baseline_household_information),
                        contentDescription = "Section Icon",
                        modifier = Modifier
                            .constrainAs(iconContainer) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                color = getColorForComponent(
                                    sectionStatus,
                                    ComponentName.SECTION_BOX_ICON_CONTAINER_COLOR
                                ),
                                shape = CircleShape
                            )
                    )
                }

                Column(
                    modifier
                        .padding(horizontal = 14.dp)
                        .constrainAs(textContainer) {
                            top.linkTo(iconContainer.top)
                            start.linkTo(iconContainer.end)
                            bottom.linkTo(iconContainer.bottom)
                            end.linkTo(buttonContainer.start)
                            width = Dimension.fillToConstraints
                        }
                        .fillMaxWidth()
                ) {

                    Text(
                        text = sectionUiEntity.sectionName,
                        color = getColorForComponent(
                            sectionStatus,
                            ComponentName.SECTION_BOX_TEXT_COLOR
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        softWrap = true,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = defaultTextStyle
                    )

                    Text(
                        text = "1 Questions",
                        color = getColorForComponent(
                            sectionStatus,
                            ComponentName.SECTION_BOX_TEXT_COLOR
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        softWrap = true,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = smallerTextStyle
                    )

                }

                /*if (sectionUiEntity.contentEntities.isNotEmpty()) {

                    IconButton(
                        onClick = { onDetailIconClicked(sectionId) },
                        modifier = Modifier
                            .constrainAs(buttonContainer) {
                                bottom.linkTo(textContainer.bottom)
                                top.linkTo(textContainer.top)
                                end.linkTo(parent.end)
                            }
                            .size(dimen_40_dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.info_icon),
                            contentDescription = "section info screen",
                            tint = blueDark
                        )

                    }

                } else {

                    CustomSpacer(size = dimen_30_dp)

                }*/

            }

        }

        if (TextUtils.equals(
                sectionStatus.toLowerCase(),
                SurveyStatusEnum.COMPLETED.name.toLowerCase()
            )
        ) {

            Image(
                painter = painterResource(id = R.drawable.icon_check_circle_green),
                contentDescription = null,
                modifier = modifier
                    .border(
                        width = 2.dp,
                        color = Color.Transparent,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .constrainAs(stepNo) {
                        start.linkTo(parent.start, margin = 16.dp)
                        top.linkTo(parent.top)
                    }
            )

        }

    }

}

