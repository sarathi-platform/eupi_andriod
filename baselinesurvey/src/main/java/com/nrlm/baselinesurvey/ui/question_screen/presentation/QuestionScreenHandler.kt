package com.nrlm.baselinesurvey.ui.question_screen.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.navigation.home.navigateBackToDidiScreen
import com.nrlm.baselinesurvey.ui.question_screen.viewmodel.QuestionScreenViewModel

@Composable
fun QuestionScreenHandler(
    navController: NavController,
    viewModel: QuestionScreenViewModel,
    didiId: Int,
    sectionId: Int,
    surveyId: Int
) {

    val sectionsList = viewModel.sectionsList.value

    val selectedSectionId = mutableStateOf(sectionId)

    LaunchedEffect(key1 = Unit) {
        viewModel.initQuestionScreenHandler(surveyId)
    }

    Scaffold(modifier = Modifier.fillMaxSize(), backgroundColor = Color.White) {
        QuestionScreen(
            navController = navController,
            viewModel = viewModel,
            surveyeeId = didiId,
            sectionId = selectedSectionId.value
        ) { currentSectionId ->
            if (sectionsList.size == 1 && sectionsList[0].sectionName.equals(NO_SECTION, true)) {
                navigateBackToDidiScreen(navController)
            } else {
                try {
                    // TODO @Anupam Update this when order number is received from backend
                    /*val currentSection = sectionsList[sectionsList.getSectionIndexById(selectedSectionId.value)]
                    selectedSectionId.value = sectionsList[sectionsList.getSectionIndexByOrder(currentSection.sectionOrder + 1)].sectionId*/

                    // TODO @Anupam Temp Solution only until order number is not received from backend
                    navigateBackToDidiScreen(navController)
                } catch (ex: Exception) {
                    navigateBackToDidiScreen(navController)
                }
            }
        }
    }
}