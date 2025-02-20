package com.sarathi.surveymanager.ui.screen.livelihood


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_LIVELIHOOD_ID
import com.nudge.core.NOT_DECIDED_LIVELIHOOD_ID
import com.nudge.core.helper.TranslationHelper
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.lightBlue
import com.nudge.core.value
import com.sarathi.dataloadingmangement.R
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodUiEntity
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LivelihoodPlanningEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.surveymanager.ui.component.ButtonPositive
import com.sarathi.surveymanager.ui.component.LivelihoodPlanningDropDownComponent
import com.sarathi.surveymanager.ui.component.ShowCustomDialog
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LivelihoodDropDownScreen(
    navController: NavController = rememberNavController(),
    viewModel: LivelihoodPlaningViewModel,
    taskId: Int,
    activityId: Int,
    missionId: Int,
    subjectName: String,
    onSettingClicked: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.setPreviousScreenData(taskId, activityId, missionId, subjectName)
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    BackHandler {
        handleBackPress(viewModel = viewModel, navController = navController)
    }
    if (viewModel.showCustomDialog.value.isDialogVisible ) {
        ShowCustomDialog(
            title = viewModel.stringResource(R.string.are_you_sure),
            message = viewModel.stringResource(R.string.form_alert_dialog_message),
            positiveButtonTitle = viewModel.stringResource(R.string.proceed_txt),
            negativeButtonTitle = viewModel.stringResource(R.string.cancel_txt),
            onPositiveButtonClick = {
                viewModel.onEvent(DialogEvents.ShowDialogEvent(false))
                navController.popBackStack()
            }, onNegativeButtonClick = {
                viewModel.onEvent(DialogEvents.ShowDialogEvent(false))
            }
        )
    }
    ToolBarWithMenuComponent(
        title = subjectName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = {
            handleBackPress(viewModel = viewModel, navController = navController)
        },
        isSearch = false,
        onSearchValueChange = {},
        onSettingClick = {
            onSettingClicked()
        },
        onBottomUI = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_10_dp)
            ) {
                Column {
                    if (!viewModel.isButtonEnable.value) {
                        if (viewModel.primaryLivelihoodId.value == viewModel.secondaryLivelihoodId.value && viewModel.secondaryLivelihoodId.value != DEFAULT_LIVELIHOOD_ID && viewModel.primaryLivelihoodId.value !=DEFAULT_LIVELIHOOD_ID) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .background(
                                        lightBlue, shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        border = ButtonDefaults.outlinedBorder,
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                androidx.compose.material.Text(
                                    text = viewModel.stringResource(
                                        R.string.primary_and_secondary_value_not_same
                                    ),
                                    color = Color.Red,
                                    modifier = Modifier
                                        .padding(10.dp)
                                )
                                Spacer(modifier = Modifier.size(dimen_24_dp))
                            }
                        }
                    }

                    ButtonPositive(
                        buttonTitle = viewModel.stringResource(R.string.submit),
                        isActive = viewModel.isButtonEnable.value && !viewModel.isActivityCompleted.value,
                        isLeftArrow = false,
                        onClick = {
                            viewModel.saveButtonClicked() {
                                withContext(Dispatchers.Main) {
                                    navController.navigateUp()
                                }
                            }
                        }
                    )
                }
            }
        },
        onContentUI = {

            if (viewModel.loaderState.value.isLoaderVisible) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                DropdownView(
                    isEditAllowed = !viewModel.isActivityCompleted.value,
                    translationHelper = viewModel.translationHelper,
                    primaryLivelihoodList = viewModel.primaryLivelihoodList.value,
                    secondaryLivelihoodList = viewModel.secondaryLivelihoodList.value,
                    primaryLivelihoodId = viewModel.primaryLivelihoodId.value,
                    secondaryLivelihoodId = viewModel.secondaryLivelihoodId.value,
                    livelihoodTypeList = viewModel.livelihoodType.value,
                    secondarylivelihoodTypeList = viewModel.seondarylivelihoodTypeList.value,
                    onPrimaryLivelihoodSelected = {
                        viewModel.checkDialogueValidation.value=false
                        viewModel.onEvent(
                            LivelihoodPlanningEvent.PrimaryLivelihoodPlanningEvent(
                                it
                            )
                        )
                    },
                    onSecondaryLivelihoodSelected = {
                        viewModel.checkDialogueValidation.value=false

                        viewModel.onEvent(
                            LivelihoodPlanningEvent.SecondaryLivelihoodPlanningEvent(
                                it
                            )
                        )
                    },
                    onPrimaryLivelihoodTypeSelected = {
                        viewModel.onEvent(
                            LivelihoodPlanningEvent.PrimaryLivelihoodTypePlanningEvent(
                                it
                            )
                        )
                    },
                    onSecondaryLivelihoodTypeSelected = {
                        viewModel.onEvent(
                            LivelihoodPlanningEvent.SecondaryLivelihoodTypePlanningEvent(
                                it
                            )
                        )
                    }
                    )
            }
        }
    )
}

fun handleBackPress(viewModel: LivelihoodPlaningViewModel, navController: NavController) {

    if ((viewModel.primaryLivelihoodId.value != DEFAULT_LIVELIHOOD_ID || viewModel.secondaryLivelihoodId.value !=DEFAULT_LIVELIHOOD_ID) && !viewModel.checkDialogueValidation.value) {
        viewModel.onEvent(DialogEvents.ShowDialogEvent(true))
    } else {
        viewModel.onEvent(DialogEvents.ShowDialogEvent(false))
        navController.popBackStack()
    }
}

@Composable
fun DropdownView(
    translationHelper: TranslationHelper,
    primaryLivelihoodList: List<LivelihoodUiEntity>,
    secondaryLivelihoodList: List<LivelihoodUiEntity>,
    livelihoodTypeList: List<LivelihoodUiEntity>,
    secondarylivelihoodTypeList: List<LivelihoodUiEntity>,
    primaryLivelihoodId: Int,
    secondaryLivelihoodId: Int,
    isEditAllowed: Boolean,
    onPrimaryLivelihoodSelected: (primaryLivelihoodId: Int) -> Unit,
    onSecondaryLivelihoodSelected: (secondaryLivelihoodId: Int) -> Unit,
    onPrimaryLivelihoodTypeSelected: (livelihoodType: String) -> Unit,
    onSecondaryLivelihoodTypeSelected: (livelihoodType: String) -> Unit,
) {

    var selectedItem1 = primaryLivelihoodId
    var selectedItem2 = secondaryLivelihoodId

    val context = LocalContext.current
    Column(modifier = Modifier.padding(dimen_10_dp)) {
        LivelihoodPlanningDropDownComponent(
            isEditAllowed = isEditAllowed,
            title = translationHelper.stringResource(
                R.string.select_first_livelihood_for_didi
            ),
            isMandatory = true,
            enableItem = selectedItem1 ?: DEFAULT_LIVELIHOOD_ID,
            diableItem = if (selectedItem2 == NOT_DECIDED_LIVELIHOOD_ID) NOT_DECIDED_LIVELIHOOD_ID else 0
                ?: 0,
            sources = livelihoodTypeList,
            onAnswerSelection = { selectedValue ->
                onPrimaryLivelihoodTypeSelected(selectedValue.livelihoodEntity.type)
            }
        )
        Spacer(modifier = Modifier.height(dimen_10_dp))
        LivelihoodPlanningDropDownComponent(
            isEditAllowed = isEditAllowed,
            title = BLANK_STRING,
            isMandatory = true,
            enableItem = selectedItem1 ?: DEFAULT_LIVELIHOOD_ID,
            diableItem = selectedItem2 ?: 0,
            sources = primaryLivelihoodList,
            onAnswerSelection = { selectedValue ->
                selectedItem1 = selectedValue.id
                onPrimaryLivelihoodSelected(selectedItem1.value())
            }
        )
        Spacer(modifier = Modifier.height(dimen_10_dp))
        LivelihoodPlanningDropDownComponent(
            title = translationHelper.stringResource(
                R.string.select_second_livelihood_for_didi
            ),
            isEditAllowed = isEditAllowed,
            isMandatory = true,
            diableItem = if (selectedItem1 == NOT_DECIDED_LIVELIHOOD_ID) NOT_DECIDED_LIVELIHOOD_ID else 0,
            enableItem = selectedItem2 ?: DEFAULT_LIVELIHOOD_ID,
            sources = secondarylivelihoodTypeList,
            onAnswerSelection = { selectedValue ->
                onSecondaryLivelihoodTypeSelected(selectedValue.livelihoodEntity.type)
            }
        )
        Spacer(modifier = Modifier.height(dimen_10_dp))

        LivelihoodPlanningDropDownComponent(
            title = BLANK_STRING,
            isEditAllowed = isEditAllowed,
            isMandatory = true,
            diableItem = selectedItem1 ?: 0,
            enableItem = selectedItem2 ?: DEFAULT_LIVELIHOOD_ID,
            sources = secondaryLivelihoodList,
            onAnswerSelection = { selectedValue ->
                selectedItem2 = selectedValue.id
                onSecondaryLivelihoodSelected(selectedItem2.value())
            }
        )
    }
}