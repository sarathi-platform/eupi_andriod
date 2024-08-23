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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.lightBlue
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodUiEntity
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LivelihoodPlanningEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.ui.component.ButtonPositive
import com.sarathi.surveymanager.ui.component.LivelihoodPlanningDropDownComponent
import com.sarathi.surveymanager.ui.component.ShowCustomDialog
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent

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
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(R.string.form_alert_dialog_message),
            positiveButtonTitle = stringResource(id = R.string.proceed),
            negativeButtonTitle = stringResource(id = R.string.cancel_text),
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
                        if (viewModel.primaryLivelihoodId.value == viewModel.secondaryLivelihoodId.value && viewModel.secondaryLivelihoodId.value != -1 && viewModel.primaryLivelihoodId.value != -1) {
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
                                    text = stringResource(R.string.primary_and_secondary_value_not_same),
                                    color = Color.Red,
                                    modifier = Modifier
                                        .padding(10.dp)
                                )
                                Spacer(modifier = Modifier.size(dimen_24_dp))
                            }
                        }
                    }

                    ButtonPositive(
                        buttonTitle = stringResource(R.string.submit),
                        isActive = viewModel.isButtonEnable.value,
                        isLeftArrow = false,
                        onClick = {
                            viewModel.saveButtonClicked()
                            navController.navigateUp()
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
                    livelihoodList = viewModel.livelihoodList.value,
                    primaryLivelihoodId = viewModel.primaryLivelihoodId.value,
                    secondaryLivelihoodId = viewModel.secondaryLivelihoodId.value,
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
                    )
            }
        }
    )
}

fun handleBackPress(viewModel: LivelihoodPlaningViewModel, navController: NavController) {

    if ((viewModel.primaryLivelihoodId.value != -1 || viewModel.secondaryLivelihoodId.value != -1) && !viewModel.checkDialogueValidation.value) {
        viewModel.onEvent(DialogEvents.ShowDialogEvent(true))
    } else {
        viewModel.onEvent(DialogEvents.ShowDialogEvent(false))
        navController.popBackStack()
    }
}

@Composable
fun DropdownView(
    livelihoodList: List<LivelihoodUiEntity>,
    primaryLivelihoodId: Int,
    secondaryLivelihoodId: Int,
    onPrimaryLivelihoodSelected: (primaryLivelihoodId: Int) -> Unit,
    onSecondaryLivelihoodSelected: (secondaryLivelihoodId: Int) -> Unit,
) {
    var selectedItem1 by remember { mutableStateOf<Int?>(primaryLivelihoodId) }
    var selectedItem2 by remember { mutableStateOf<Int?>(secondaryLivelihoodId) }

    Column(modifier = Modifier.padding(dimen_10_dp)) {
        val firstDropDownItems = livelihoodList

        LivelihoodPlanningDropDownComponent(
            isEditAllowed = true,
            title = stringResource(R.string.select_first_livelihood_for_didi),
            isMandatory = true,
            enableItem = selectedItem1 ?: -1,
            sources = firstDropDownItems,
            onAnswerSelection = { selectedValue ->
                selectedItem1 = selectedValue.id
                onPrimaryLivelihoodSelected(selectedItem1.value())
            }
        )
        Spacer(modifier = Modifier.height(dimen_10_dp))
        val secondaryDropDownItems = livelihoodList
        LivelihoodPlanningDropDownComponent(title = stringResource(R.string.select_second_livelihood_for_didi),
            isMandatory = true,
            diableItem = selectedItem1 ?: 0,
            enableItem = selectedItem2 ?: -1,
            sources = secondaryDropDownItems,
            onAnswerSelection = { selectedValue ->
                selectedItem2 = selectedValue.id
                onSecondaryLivelihoodSelected(selectedItem2.value())
            }
        )
    }
}