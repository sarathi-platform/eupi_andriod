package com.sarathi.surveymanager.ui.screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ui.theme.dimen_10_dp
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEntity
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.ui.component.ButtonPositive
import com.sarathi.surveymanager.ui.component.LivelihoodPlanningDropDownComponent
import com.sarathi.surveymanager.ui.component.LivelihoodUIEntity
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent

@Composable
fun LivelihoodDropDownScreen(
    navController: NavController = rememberNavController(),
//    message: String = BLANK_STRING,
//    onNavigateBack: () -> Unit

) {

    ToolBarWithMenuComponent(title = "Didi_Name",
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = false,
        onSearchValueChange = {},
        onSettingClick = {},
        onBottomUI = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_10_dp)
            ) {
                ButtonPositive(buttonTitle = stringResource(R.string.submit),
                    isActive = false,
                    isLeftArrow = false,
                    onClick = {
                        //navController.popBackStack()
                    })
            }
        },
        onContentUI = {
            DropdownSample()
        })
}

@Composable
fun DropdownSample() {
    var selectedItem1 by remember { mutableStateOf<Int?>(null) }
    var selectedItem2 by remember { mutableStateOf<Int?>(null) }

    val items = listOf(
        LivelihoodUIEntity(
            isSelected = false,
            livelihoodEntity = LivelihoodEntity(id = 1, userId = "", name = "Item 1", status = 1)
        ),
        LivelihoodUIEntity(
            isSelected = false,
            livelihoodEntity = LivelihoodEntity(id = 2, userId = "", name = "Item 2", status = 1)
        ),
        LivelihoodUIEntity(
            isSelected = false,
            livelihoodEntity = LivelihoodEntity(id = 3, userId = "", name = "Item 3", status = 1)
        ),
        LivelihoodUIEntity(
            isSelected = false,
            livelihoodEntity = LivelihoodEntity(id = 4, userId = "", name = "Item 4", status = 1)
        ),
        LivelihoodUIEntity(
            isSelected = false,
            livelihoodEntity = LivelihoodEntity(id = 5, userId = "", name = "Item 5", status = 1)
        ),
    )

    Column(modifier = Modifier.padding(dimen_10_dp)) {
        val firstDropDownItems = items
        LivelihoodPlanningDropDownComponent(isEditAllowed = true,
            title = "Select first livelihood for didi",
            isMandatory = true,
            diableItem = selectedItem2 ?: 0,
            sources = firstDropDownItems,
            onAnswerSelection = { selectedValue ->
                selectedItem1 = selectedValue.livelihoodEntity.id

            })
        Spacer(modifier = Modifier.height(dimen_10_dp))
        val SeacndDropDownItems = items
        LivelihoodPlanningDropDownComponent(title = "Select second livelihood for didi",
            isMandatory = true,
            diableItem = selectedItem1 ?: 0,
            sources = SeacndDropDownItems,
            onAnswerSelection = { selectedValue ->
                selectedItem2 = selectedValue.livelihoodEntity.id

            })
    }
}