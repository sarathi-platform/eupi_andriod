package com.sarathi.missionactivitytask.ui.add_image_screen.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ui.theme.dimen_100_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_80_dp
import com.nudge.core.ui.theme.largeTextStyle
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.navigation.navigateToActivityCompletionScreen
import com.sarathi.surveymanager.ui.component.AddImageComponent
import com.sarathi.surveymanager.ui.component.ButtonNegative
import com.sarathi.surveymanager.ui.component.ButtonPositive
import com.sarathi.surveymanager.ui.screen.commaSeparatedStringToList
import com.sarathi.surveymanager.ui.screen.listToCommaSeparatedString

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SubmitPhysicalFormScreen(
    navController: NavController = rememberNavController(),
    viewModel: SubmitPhysicalFormScreenViewModel,
    activityId: Int,
    missionId: Int,
    taskIdList: String,
    activityType: String
) {
    val outerState = rememberLazyListState()
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.setTranslationConfig()
        viewModel.setTotalDidi(activityId = activityId, missionId)
    }
    Scaffold(modifier = Modifier.fillMaxWidth(),
        containerColor = white,
        topBar = {},
        bottomBar = {
            BottomAppBar(
                backgroundColor = white, modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(dimen_80_dp, dimen_100_dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimen_10_dp)
                ) {
                    ButtonNegative(
                        modifier = Modifier.weight(0.4f),
                        buttonTitle = viewModel.stringResource(context, R.string.go_back),
                        isArrowRequired = true,
                        onClick = {
                            navController.popBackStack()
                        })
                    Spacer(modifier = Modifier.width(10.dp))
                    ButtonPositive(modifier = Modifier.weight(0.4f),
                        buttonTitle = viewModel.submitPhysicalFormButtonText.value,
                        isActive = viewModel.isButtonEnable.value,
                        isArrowRequired = false,
                        onClick = {
                            viewModel.saveMultiImage(activityId)
                            viewModel.updateFormTable(
                                missionId = missionId,
                                activityId = activityId,
                                taskIdList,
                                onCompleted = { message ->
                                    navigateToActivityCompletionScreen(
                                        navController,
                                        message.replace(
                                            "%1s",
                                            viewModel.totalDidi.value.toString()
                                        ),
                                        activityRoutePath = activityType
                                    )
                                })

                        })
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_16_dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimen_5_dp),
                    text = viewModel.attachPhyicalFormTitle.value,
                    style = largeTextStyle
                )
                BoxWithConstraints(
                    modifier = Modifier.scrollable(
                        state = outerState,
                        Orientation.Vertical,
                    )
                ) {
                    AddImageComponent(
                        maxCustomHeight = maxHeight,
                        fileNamePrefix = viewModel.getPrefixFileName(),
                    ) { selectedValue, isDeleted ->
                        saveMultiImage(
                            filePath = selectedValue,
                            isDeleted = isDeleted,
                            values = viewModel.documentValues.value
                        )
                        viewModel.checkButtonValidation()
                    }
                }

            }
        })
}

fun saveMultiImage(filePath: String, values: ArrayList<DocumentUiModel>?, isDeleted: Boolean) {
    val savedOptions =
        commaSeparatedStringToList(values?.firstOrNull()?.filePath ?: BLANK_STRING)
    val list: ArrayList<String> = ArrayList()
    list.addAll(savedOptions)

    if (isDeleted) {
        list.remove(filePath)
    } else {
        list.add(filePath)

    }
    values?.clear()
    if (list.isNotEmpty()) {
        values?.add(
            DocumentUiModel(
                isSelected = false,
                filePath = listToCommaSeparatedString(list)
            )
        )
    }

    values?.firstOrNull()?.isSelected = list.isNotEmpty()
}

