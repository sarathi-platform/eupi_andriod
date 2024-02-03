package com.nrlm.baselinesurvey.ui.question_type_screen.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.CTAButtonComponent
import com.nrlm.baselinesurvey.ui.common_components.DropDownWithTitleComponent
import com.nrlm.baselinesurvey.ui.common_components.EditTextWithTitleComponent
import com.nrlm.baselinesurvey.ui.common_components.SwitchComponent
import com.nrlm.baselinesurvey.ui.theme.borderGrey
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark

@Composable
fun AddHouseholdMemberScreen(navController: NavHostController) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                title = {
                    androidx.compose.material.Text(
                        text = "Add Household Member",
                        color = textColorDark,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        style = largeTextStyle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = textColorDark)
                    }
                },
                backgroundColor = Color.White,
                elevation = 10.dp
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                    .padding(vertical = dimensionResource(id = R.dimen.dp_15))
            ) {
                CTAButtonComponent(tittle = "Add Income Source", Modifier.fillMaxWidth()) {
                }
            }
        }
    ) {
        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(true) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
        ) {
            val (mainBox) = createRefs()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = it.calculateTopPadding() + 20.dp, bottom = 50.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditTextWithTitleComponent(title = "Name")
                RationDropDown()
                EditTextWithTitleComponent(title = "Age")
                EducationDropDown()
                SwitchComponent("Married")
                Divider(
                    color = borderGrey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                )

                SwitchComponent("Disabled")
                Divider(
                    color = borderGrey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                )

                SwitchComponent("Working")
                Divider(
                    color = borderGrey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                )

                SwitchComponent("MIREGA job card")
                Divider(
                    color = borderGrey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                )
                SwitchComponent("Pension")
            }
        }


    }

}

@Composable
fun RationDropDown() {
    val relations = listOf(
        "Husband",
        "Father",
        "Mother",
        "Son",
        "Daughter",
        "Brother",
        "Sister",
        "Father-in-Law",
        "Mother-in-Law"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(relations[0]) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    DropDownWithTitleComponent(
        title = "Relation",
        items = relations,
        modifier = Modifier.fillMaxWidth(),
        mTextFieldSize = textFieldSize,
        expanded = expanded,
        selectedItem = selectedOptionText,
        onExpandedChange = {
            expanded = !it
        },
        onDismissRequest = {
            expanded = false
        },
        onGlobalPositioned = { coordinates ->
            textFieldSize = coordinates.size.toSize()
        },
        onItemSelected = {},
    )
}

@Composable
fun EducationDropDown() {
    val educationStatus = listOf(
        "10th Pass",
        "12th Pass",
        "Graduate",
        "Post-Graduate",
        "Doctorate",
        "Diploma",
        "Uneducated"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(educationStatus[0]) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    DropDownWithTitleComponent(
        title = "Highest education",
        items = educationStatus,
        modifier = Modifier.fillMaxWidth(),
        mTextFieldSize = textFieldSize,
        expanded = expanded,
        selectedItem = selectedOptionText,
        onExpandedChange = {
            expanded = !it
        },
        onDismissRequest = {
            expanded = false
        },
        onGlobalPositioned = { coordinates ->
            textFieldSize = coordinates.size.toSize()
        },
        onItemSelected = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun AddHouseholdMemberScreenPreview() {
    MaterialTheme { AddHouseholdMemberScreen(rememberNavController()) }

}