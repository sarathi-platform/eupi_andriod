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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nudge.core.ARG_FROM_SECTION_SCREEN
import com.nudge.core.BLANK_STRING
import com.nudge.core.enums.SurveyFlow
import com.nudge.core.isOnline
import com.nudge.core.ui.commonUi.ButtonComponentWithVisibility
import com.nudge.core.ui.commonUi.CustomButtonVisibilityState
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.ModelBottomSheetDescriptionContentComponent
import com.nudge.core.ui.commonUi.customVerticalSpacer
import com.nudge.core.ui.commonUi.rememberCustomButtonVisibilityState
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_14_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_40_dp
import com.nudge.core.ui.theme.dimen_50_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.smallerTextStyle
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.ui.component.ComplexSearchComponent
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent
import com.sarathi.surveymanager.utils.DescriptionContentState
import com.sarathi.surveymanager.viewmodels.surveyScreen.SectionScreenViewModel
import getColorForComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun SectionScreen(
    modifier: Modifier = Modifier,
    sectionScreenViewModel: SectionScreenViewModel,
    navController: NavController,
    missionId: Int,
    activityId: Int,
    surveyId: Int,
    taskId: Int,
    subjectType: String,
    subjectName: String,
    activityType: String,
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
    onNavigateToMediaScreen: (contentList: ContentList) -> Unit,
    onNavigateToQuestionScreen: (surveyId: Int, sectionId: Int, taskId: Int, sectionName: String, subjectType: String, activityConfigIs: Int, missionId: Int, activityId: Int, activityType: String, surveyFlow: SurveyFlow) -> Unit,
    onNavigateToComplexSearchScreen: (surveyId: Int, sectionId: Int, taskId: Int, activityConfigIs: Int, fromScreen: String, subjectType: String, activityType: String) -> Unit
) {
    val selectedSectionDescription = remember {
        mutableStateOf(DescriptionContentState())
    }

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


    LaunchedEffect(key1 = true) {
        sectionScreenViewModel.setSurveyDetails(
            missionId = missionId,
            activityId = activityId,
            surveyId = surveyId,
            taskId = taskId,
            subjectType = subjectType,
            activityConfigId = activityConfigId
        )
        sectionScreenViewModel.onEvent(InitDataEvent.InitDataStateWithCallBack {

            if (sectionScreenViewModel.sectionList.value.size == 1) {
                val surveyFlow =
                    SurveyFlow.getSurveyFlowFromSectionScreenForActivityType(activityType)
                when (surveyFlow) {
                    SurveyFlow.GrantSurveySummaryScreen -> {
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

                    SurveyFlow.SurveyScreen -> {
                        val sectionId: Int? =
                            sectionScreenViewModel.sectionList.value.firstOrNull()?.sectionId
                        sectionId?.let {
                            onNavigateToQuestionScreen(
                                surveyId,
                                sectionId,
                                taskId,
                                subjectName,
                                subjectType,
                                activityConfigId,
                                missionId,
                                activityId,
                                activityType,
                                surveyFlow
                            )
                        }
                    }

                    SurveyFlow.LivelihoodPopSurveyScreen -> {
                        val sectionId =
                            sectionScreenViewModel.sectionList.value.firstOrNull()?.sectionId

                        sectionId?.let {
                            onNavigateToQuestionScreen(
                                surveyId,
                                sectionId,
                                taskId,
                                subjectName,
                                subjectType,
                                activityConfigId,
                                missionId,
                                activityId,
                                activityType,
                                surveyFlow
                            )
                        }

                    }

                    else -> {
                        /**
                         * Not required for now.
                         * */
                    }
                }
            }

            sectionScreenViewModel.checkButtonValidation()
            sectionScreenViewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
        })

    }

    LaunchedEffect(key1 = sectionScreenViewModel.buttonVisibilityKey) {
        if (sectionScreenViewModel.buttonVisibilityKey.value)
            showBottomButtonState.show()
        else
            showBottomButtonState.hide()
    }

    ModelBottomSheetDescriptionContentComponent(
        modifier = Modifier
            .fillMaxSize(),
        sheetContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                }) {
                    androidx.compose.material3.Icon(
                        painter = painterResource(id = R.drawable.info_icon),
                        contentDescription = "question info button",
                        Modifier.size(dimen_18_dp),
                        tint = blueDark
                    )
                }
                if (sheetState.isVisible) {
                    Divider(
                        thickness = dimen_1_dp,
                        color = lightGray2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                com.sarathi.surveymanager.ui.component.DescriptionContentComponent(
                    buttonClickListener = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    },
                    navigateToMediaPlayerScreen = { contentList ->
                        if (sectionScreenViewModel.isFilePathExists(
                                contentList.contentValue ?: BLANK_STRING
                            )
                        ) {
                            onNavigateToMediaScreen(contentList)
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.file_not_exists),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    descriptionContentState = selectedSectionDescription.value
                )
            }
        },
        sheetState = sheetState,
        sheetElevation = dimen_20_dp,
        sheetBackgroundColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = dimen_10_dp, topEnd = dimen_10_dp)
    ) {

        ToolBarWithMenuComponent(
            title = subjectName,
            modifier = Modifier
                .then(modifier),
            paddingTop = dimen_8_dp,
            onBackIconClick = { navController.navigateUp() },
            onSearchValueChange = { },
            onBottomUI = {
                ButtonComponentWithVisibility(
                    showButtonComponentState = CustomButtonVisibilityState(true),
                    buttonTitle = stringResource(R.string.complete_survey),
                    isActive = sectionScreenViewModel.isButtonEnable.value,
                    onClick = {
                        sectionScreenViewModel.updateMissionFilter()
                        sectionScreenViewModel.updateTaskStatus(taskId)
                        navController.navigateUp()
                        //Change this to proper navigation
//                    onNavigateSuccessScreen("Baseline for $subjectName")
                    }
                )
            },
            onSettingClick = onSettingClick,
            onContentUI = { paddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {
                    PullRefreshIndicator(
                        refreshing = sectionScreenViewModel.loaderState.value.isLoaderVisible,
                        state = pullRefreshState,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .zIndex(1f),
                        contentColor = blueDark,
                    )

                    if (!sectionScreenViewModel.loaderState.value.isLoaderVisible) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(dimen_14_dp),
                            modifier = Modifier
                                .padding(
                                    top = dimen_16_dp,
                                    start = dimen_16_dp,
                                    end = dimen_16_dp,
                                    bottom = dimen_50_dp
                                )
                        ) {

                            item {
                                CustomVerticalSpacer(size = dimen_50_dp)
                            }
                            item {
                                ComplexSearchComponent {
                                    onNavigateToComplexSearchScreen(
                                        surveyId,
                                        NUMBER_ZERO,
                                        taskId,
                                        activityConfigId,
                                        ARG_FROM_SECTION_SCREEN,
                                        subjectType,
                                        activityType
                                    )
                                }
                            }

                            // TODO handle progress correctly.
                            /*item {
                                CustomLinearProgressIndicator(
                                    progressState = linearProgressState
                                )
                            }*/

                            itemsIndexed(sectionScreenViewModel.sectionList.value) { index, section ->

                                SectionItemComponent(
                                    sectionId = section.sectionId,
                                    sectionUiEntity = section,
                                    sectionStatus = sectionScreenViewModel.sectionStatusMap.value[section.sectionId]
                                        ?: SurveyStatusEnum.INPROGRESS.name,
                                    onDetailIconClicked = { sectionId ->
                                        coroutineScope.launch {
                                            selectedSectionDescription.value =
                                                selectedSectionDescription.value.copy(
                                                    contentDescription = section.contentEntities
                                                )

                                            delay(100)
                                            if (!sheetState.isVisible) {
                                                sheetState.show()
                                            } else {
                                                sheetState.hide()
                                            }
                                        }

                                    },
                                    onSectionItemClicked = { sectionId ->
                                        onNavigateToQuestionScreen(
                                            surveyId,
                                            sectionId,
                                            taskId,
                                            section.sectionName,
                                            subjectType,
                                            activityConfigId,
                                            missionId,
                                            activityId,
                                            activityType,
                                            SurveyFlow.SurveyScreen
                                        )
                                    }
                                )

                            }

                            customVerticalSpacer(size = dimen_50_dp)
                        }
                    }

                }
            })


    }

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

                val (textContainer, buttonContainer, iconContainer, infoIconContainer) = createRefs()
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
                        .constrainAs(textContainer) {
                            top.linkTo(iconContainer.top)
                            start.linkTo(iconContainer.end)
                            bottom.linkTo(iconContainer.bottom)
                            end.linkTo(buttonContainer.start)
                            width = Dimension.fillToConstraints
                        }
                        .fillMaxWidth()
                        .padding(start = dimen_16_dp, end = dimen_8_dp)
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
                        text = "${sectionUiEntity.questionSize} Questions",
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

                if (sectionUiEntity.contentEntities.isNotEmpty()) {

                    IconButton(
                        onClick = { onDetailIconClicked(sectionId) },
                        modifier = Modifier
                            .constrainAs(infoIconContainer) {
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

                }

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

