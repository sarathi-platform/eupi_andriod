package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.NO_TOLA_TITLE
import com.nudge.core.SHG_VERIFICATION_STATUS_NOT_VERIFIED
import com.nudge.core.getShgMemberNameWithId
import com.nudge.core.helper.LocalTranslationHelper
import com.nudge.core.ui.commonUi.AlertDialogComponent
import com.nudge.core.ui.commonUi.ContentWithImage
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.DropDownComponent
import com.nudge.core.ui.commonUi.ImageProperties
import com.nudge.core.ui.commonUi.ToolBarWithMenuComponent
import com.nudge.core.ui.commonUi.componet_.component.ButtonPositive
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_45_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_72_dp
import com.nudge.core.ui.theme.lokosInfoCardBgColor
import com.nudge.core.ui.theme.lokosInfoCardBorderColor
import com.nudge.core.ui.theme.smallTextStyleMediumWeight
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.smallgroupmodule.R
import com.sarathi.smallgroupmodule.ui.didiTab.viewModel.DidiShgVerificationViewModel
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.DidiVerificationEvent
import com.sarathi.smallgroupmodule.ui.theme.blueDark
import com.sarathi.smallgroupmodule.ui.theme.dimen_1_dp
import com.sarathi.smallgroupmodule.ui.theme.greyColor
import com.sarathi.smallgroupmodule.ui.theme.mediumTextStyle
import com.sarathi.smallgroupmodule.ui.theme.textColorDark

@Composable
fun DidiShgVerificationScreen(
    subjectId: Int,
    didiName: String,
    villageName: String,
    navController: NavHostController = rememberNavController(),
    viewModel: DidiShgVerificationViewModel = hiltViewModel(),
    onSettingClick: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.setPreviousScreenValue(subjectId)
        viewModel.onEvent(InitDataEvent.InitDataState)
    }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val isShgListExpanded = remember { mutableStateOf(false) }

    val isShgMemberListExpanded = remember { mutableStateOf(false) }

    val showAlertDialog = remember { mutableStateOf(false) }

    BackHandler {
        if (viewModel.subjectEntity.value?.shgVerificationStatus == SHG_VERIFICATION_STATUS_NOT_VERIFIED)
            showAlertDialog.value = true
        else
            navController.navigateUp()
    }

    if (viewModel.showLoader.value) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ), onDismissRequest = {}
        ) {
            Box(
                modifier = Modifier
                    .background(white)
                    .padding(dimen_16_dp)
                    .size(dimen_45_dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimen_24_dp),
                    color = com.nudge.core.ui.theme.textColorDark,
                    backgroundColor = Color.Transparent
                )
            }
        }
    }

    if (showAlertDialog.value) {
        AlertDialogComponent(
            onDismissRequest = { showAlertDialog.value = false },
            onConfirmation = {
                showAlertDialog.value = false
                navController.navigateUp()
            },
            dialogTitle = stringResource(R.string.alert_dialog_title_text),
            dialogText = "Data is not saved, are you sure you want to continue?",
            confirmButtonText = stringResource(R.string.confirm),
            dismissButtonText = stringResource(R.string.cancel_text)
        )
    }

    ToolBarWithMenuComponent(
        title = "Verify SHG",
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onSearchValueChange = {},
        onBackIconClick = {
            if (viewModel.subjectEntity.value?.shgVerificationStatus == SHG_VERIFICATION_STATUS_NOT_VERIFIED)
                showAlertDialog.value = true
            else
                navController.navigateUp()
        },
        onBottomUI = {
            if (viewModel.subjectEntity.value?.shgVerificationStatus == SHG_VERIFICATION_STATUS_NOT_VERIFIED) {
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
                                viewModel.onEvent(DidiVerificationEvent.SaveShgVerificationStatus {
                                    navController.navigateUp()
                                })
                            }
                        )

                    }
                }
            }
        },
        onSettingClick = { onSettingClick() },
        onContentUI = { paddingValues, b, function ->

            Column(modifier = Modifier.padding(horizontal = dimen_16_dp)) {

                ContentWithImage(
                    imageProperties = ImageProperties(
                        path = viewModel.subjectEntity.value?.crpImageLocalPath.value(),
                        viewModel.subjectEntity.value?.subjectName.value(),
                        contentDescription = null
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = viewModel.subjectEntity.value?.subjectName.value(),
                            style = mediumTextStyle,
                            color = blueDark
                        )

                        TextWithIconComponent(
                            iconProperties = IconProperties(
                                icon = painterResource(id = com.sarathi.missionactivitytask.R.drawable.home_icn),
                                "home icon"
                            ),
                            textProperties = TextProperties(
                                text = if (!viewModel.subjectEntity.value?.cohortName.equals(
                                        NO_TOLA_TITLE,
                                        true
                                    )
                                ) viewModel.subjectEntity.value?.cohortName else viewModel.subjectEntity.value?.villageName,
                                style = com.sarathi.smallgroupmodule.ui.theme.smallTextStyleMediumWeight,
                                color = textColorDark
                            )
                        )
                        CustomVerticalSpacer()
                    }
                }

                DropDownComponent(
                    modifier = Modifier,
                    title = "Select SHG",
                    isMandatory = true,
                    items = viewModel.shgList,
                    selectedItem = viewModel.selectedShg.value?.cboName.value(),
                    mTextFieldSize = textFieldSize,
                    expanded = isShgListExpanded.value,
                    onExpandedChange = {
                        isShgListExpanded.value = !isShgListExpanded.value
                    },
                    onDismissRequest = {
                        isShgListExpanded.value = false
                    },
                    onGlobalPositioned = { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
                    onItemSelected = { item ->
                        isShgListExpanded.value = false
                        viewModel.onEvent(DidiVerificationEvent.OnShgSelected(item) {})
                        viewModel.checkSubmitButtonValidation()
                    }
                )

                if (viewModel.showShgMemberListDropDown.value) {

                    CustomVerticalSpacer()
                    DropDownComponent(
                        modifier = Modifier,
                        title = "Select Didi",
                        isMandatory = true,
                        items = viewModel.shgMemberList,
                        selectedItem = getShgMemberNameWithId(viewModel.selectedShgMember.value),
                        mTextFieldSize = textFieldSize,
                        expanded = isShgMemberListExpanded.value,
                        onExpandedChange = {
                            isShgMemberListExpanded.value = !isShgMemberListExpanded.value
                        },
                        onDismissRequest = {
                            isShgMemberListExpanded.value = false
                        },
                        onGlobalPositioned = { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                        onItemSelected = { item ->
                            isShgMemberListExpanded.value = false
                            viewModel.onEvent(DidiVerificationEvent.OnShgMemberSelected(item) {})
                            viewModel.checkSubmitButtonValidation()
                        }
                    )
                }

                if (viewModel.showLokOsData.value) {
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
                            .border(
                                dimen_1_dp, lokosInfoCardBorderColor, RoundedCornerShape(
                                    dimen_5_dp
                                )
                            )
                            .fillMaxWidth()
                            .padding(dimen_10_dp)
                    ) {
                        LokosDataSection(
                            "Member Code: ",
                            viewModel.lokOsDataModel.value?.memberId.value()
                        )
                        LokosDataSection(
                            "SHG Name: ",
                            viewModel.lokOsDataModel.value?.shgName.value()
                        )
                        LokosDataSection(
                            "Didi Name: ",
                            viewModel.lokOsDataModel.value?.memberName.value()
                        )
                        LokosDataSection(
                            getRelationLabel(viewModel.lokOsDataModel.value?.relationName.value()),
                            viewModel.lokOsDataModel.value?.husbandName.value()
                        )
                        LokosDataSection(
                            "Caste: ",
                            viewModel.lokOsDataModel.value?.caste.value()
                        )

                        LokosDataSection(
                            "Village: ",
                            viewModel.lokOsDataModel.value?.village.value()
                        )
                        LokosDataSection(
                            "Panchayat: ",
                            viewModel.lokOsDataModel.value?.panchayatName.value()
                        )
                    }
                }
            }
        },
        onRetry = {}
    )
}

@Composable
fun LokosDataSection(label: String, info: String) {

    Column() {
        Row {
            Text(label, style = smallTextStyleMediumWeight.copy(color = Color.Black))
            Text(info, style = smallTextStyleMediumWeight.copy(color = Color.Black))
        }
//        Text(info, style = smallTextStyleMediumWeight.copy(color = Color.Black))
        CustomVerticalSpacer(size = dimen_5_dp)

    }
}

@Composable
fun getRelationLabel(relationType: String): String {
    val translationHelper = LocalTranslationHelper.current
    return when (relationType.lowercase()) {
        "SPOUSE".lowercase() -> "Husband Name: "
        "FATHER".lowercase() -> "Father Name: "
        else -> "Husband/Father Name: "
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