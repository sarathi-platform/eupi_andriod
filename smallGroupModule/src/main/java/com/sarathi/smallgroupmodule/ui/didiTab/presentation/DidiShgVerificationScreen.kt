package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.model.uiModel.ValuesDto
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.ToolBarWithMenuComponent
import com.nudge.core.ui.commonUi.componet_.component.ButtonPositive
import com.nudge.core.ui.commonUi.componet_.component.TypeDropDownComponent
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_72_dp
import com.nudge.core.ui.theme.lokosInfoCardBgColor
import com.nudge.core.ui.theme.smallTextStyleMediumWeight
import com.nudge.core.ui.theme.white
import com.sarathi.smallgroupmodule.R
import com.sarathi.smallgroupmodule.ui.didiTab.viewModel.DidiShgVerificationViewModel
import com.sarathi.smallgroupmodule.ui.theme.greyColor

@Composable
fun DidiShgVerificationScreen(
    subjectId: Int,
    didiName: String,
    villageName: String,
    navController: NavHostController = rememberNavController(),
    viewModel: DidiShgVerificationViewModel = hiltViewModel(),
    onSettingClick: () -> Unit
) {

    ToolBarWithMenuComponent(
        title = didiName,
        subTitle = villageName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onSearchValueChange = {},
        onBottomUI = {
            BottomAppBar(
                modifier = Modifier.height(dimen_72_dp),
                backgroundColor = white
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_10_dp),
                ) {


                    ButtonPositive(
                        modifier = Modifier.weight(0.5f),
                        buttonTitle = viewModel.getString(R.string.submit),
                        isActive = viewModel.isSubmitButtonEnable.value,
                        isArrowRequired = false,
                        onClick = {

                        }
                    )

                }
            }
        },
        onSettingClick = { onSettingClick() },
        onContentUI = { paddingValues, b, function ->

            Column(modifier = Modifier.padding(horizontal = dimen_16_dp)) {
                TypeDropDownComponent(
                    isEditAllowed = true,
                    title = viewModel.stringResource(
                        R.string.select_shg
                    ),
                    isMandatory = true,
                    sources = listOf(ValuesDto(1, "Seema Devi", isSelected = false)),
                    selectedValue = "Seema Devi",
                    onAnswerSelection = { selectedValue ->

                    }
                )
                CustomVerticalSpacer()
                TypeDropDownComponent(
                    isEditAllowed = true,
                    title = viewModel.stringResource(
                        R.string.select_didi
                    ),
                    isMandatory = true,
                    sources = listOf(ValuesDto(1, "Seema Devi", isSelected = false)),
                    selectedValue = "Seema Devi",
                    onAnswerSelection = { selectedValue ->

                    }

                )
                CustomVerticalSpacer()

                Text(
                    viewModel.getString(R.string.lokos_data),
                    style = smallTextStyleMediumWeight.copy(
                        greyColor
                    )
                )
                CustomVerticalSpacer()
                Column(
                    modifier = Modifier
                        .background(
                            color = lokosInfoCardBgColor, shape = RoundedCornerShape(
                                dimen_5_dp
                            )
                        )
                        .fillMaxWidth()
                        .padding(dimen_10_dp)
                ) {
                    LokosDataSection("Husband's Name: Shubham Kumar")
                    LokosDataSection("Caste: Ahir")
                    LokosDataSection("House No.: 114")
                }
            }
        },
        onBackIconClick = { navController.navigateUp() },
        onRetry = {}
    )
}

@Composable
fun LokosDataSection(info: String) {


    Column() {
        Text(info, style = smallTextStyleMediumWeight.copy(color = Color.Black))
        CustomVerticalSpacer(size = dimen_5_dp)

    }


}


@Preview
@Composable
fun previewDidiShgVerificationScreen() {
    DidiShgVerificationScreen(
        didiName = "Seema Devi",
        villageName = "Sundar Pahari",
        onSettingClick = {},
        subjectId = 77
    )
}