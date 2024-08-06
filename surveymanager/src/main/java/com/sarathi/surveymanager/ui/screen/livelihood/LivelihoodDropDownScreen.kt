package com.sarathi.surveymanager.ui.screen.livelihood


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodUiEntity
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LivelihoodPlanningEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.ui.component.ButtonPositive
import com.sarathi.surveymanager.ui.component.LivelihoodPlanningDropDownComponent
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

    ToolBarWithMenuComponent(
        title = subjectName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
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
                        viewModel.onEvent(LivelihoodPlanningEvent.PrimaryLivelihoodPlanningEvent(it))
                    },
                    onSecondaryLivelihoodSelected = {
                        viewModel.onEvent(
                            LivelihoodPlanningEvent.SecondaryLivelihoodPlanningEvent(
                                it
                            )
                        )
                    }
                )
            }
        }
    )
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
            title = "Select first livelihood for didi",
            isMandatory = true,
            diableItem = selectedItem2 ?: 0,
            enableItem = selectedItem1 ?: -1,
            sources = firstDropDownItems,
            onAnswerSelection = { selectedValue ->
                selectedItem1 = selectedValue.id
                onPrimaryLivelihoodSelected(selectedItem1.value())
            }
        )
        Spacer(modifier = Modifier.height(dimen_10_dp))
        val secondaryDropDownItems = livelihoodList
        LivelihoodPlanningDropDownComponent(title = "Select second livelihood for didi",
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