package com.sarathi.missionactivitytask.ui.activities.select

import android.net.Uri
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.EXPANSTION_TRANSITION_DURATION
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CardArrow
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.buttonTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_3_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.languageItemActiveBg
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardSlots
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.basic_content.component.ImageViewer
import com.sarathi.missionactivitytask.ui.basic_content.component.SubContainerView
import com.sarathi.missionactivitytask.ui.components.CircularImageViewComponent
import com.sarathi.missionactivitytask.ui.grantTask.screen.TaskScreen
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.TaskScreenViewModel
import com.sarathi.missionactivitytask.utils.StatusEnum
import com.sarathi.missionactivitytask.utils.event.InitDataEvent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivitySelectTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: ActivitySelectTaskViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    onSettingClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.onEvent(InitDataEvent.InitActivitySelectTaskScreenState(missionId, activityId))
    }
    TaskScreen(
        missionId = missionId,
        activityId = activityId,
        activityName = activityName,
        onSettingClick = onSettingClick,
        viewModel = viewModel,
        onSecondaryButtonClick = {

        },
        isSecondaryButtonEnable = false,
        secondaryButtonText = stringResource(id = R.string.generate_form_e),
        isSecondaryButtonVisible = false,
        taskList = emptyList(),//viewModel.taskUiList.value,
        navController = navController,
        taskScreenContent = { vm, mNavController ->
            SelectActivityTaskScreenContent(viewModel = viewModel)
        }
    )
}

fun LazyListScope.SelectActivityTaskScreenContent(viewModel: TaskScreenViewModel) {

    itemsIndexed(
        items = viewModel.filterList.value.entries.toList()
    ) { _, task ->

        ExpandableTaskCardRow(viewModel = viewModel, task = task)

        CustomVerticalSpacer()
    }
    item {
        CustomVerticalSpacer(size = dimen_20_dp)
    }

}

@Composable
fun ExpandableTaskCardRow(
    viewModel: TaskScreenViewModel,
    task: MutableMap.MutableEntry<Int, HashMap<String, TaskCardModel>>
) {
    val options = TaskCardModel(label = "option1, option2 ", value = "subTitle 1", icon = null)


    ExpandableTaskCard(
        title = task.value[TaskCardSlots.TASK_TITLE.name],
        subTitle1 = task.value[TaskCardSlots.TASK_SUBTITLE.name],
        options = options,
        status = task.value[TaskCardSlots.TASK_STATUS.name],
        imagePath = viewModel.getFilePathUri(
            task.value[TaskCardSlots.TASK_IMAGE.name]?.value ?: BLANK_STRING
        ) ?: Uri.EMPTY,
        modifier = Modifier,
        questionUiModel = null,
        expanded = false
    ) { _, _ ->

    }
}

@Composable
fun ExpandableTaskCard(
    title: TaskCardModel?,
    subTitle1: TaskCardModel?,
    options: TaskCardModel?,
    status: TaskCardModel?,
    imagePath: Uri,
    modifier: Modifier,
    questionUiModel: QuestionUiModel?,
    expanded: Boolean,
    onExpendClick: (Boolean, TaskCardModel) -> Unit,
) {
    val taskStatus = remember(status?.value) {
        mutableStateOf(status?.value)
    }

    val transition = updateTransition(expanded, label = "transition")


    val animateColor by transition.animateColor({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "animate color") {
        if (it) {
            greenOnline
        } else {
            textColorDark
        }
    }


    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (it) 180f else 0f
    }
    BasicCardView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_16_dp)
            .border(
                width = dimen_1_dp,
                color = greenOnline,
                shape = RoundedCornerShape(dimen_6_dp)
            )
            .background(Color.Transparent)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(white)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_16_dp, vertical = dimen_5_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (imagePath != null) {
                    CircularImageViewComponent(modifier = Modifier, imagePath = imagePath)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        title?.value?.let {
                            if (it.contains("vo", true)) {
                                title.icon?.let {
                                    ImageViewer(it)
                                    Spacer(modifier = Modifier.width(dimen_5_dp))
                                }
                                Spacer(modifier = Modifier.width(dimen_3_dp))
                            }
                            Text(
                                text = title.value,
                                style = buttonTextStyle,
                                color = blueDark
                            )
                        }
                    }
                    SubContainerView(subTitle1)
                }

                CardArrow(
                    modifier = Modifier,
                    degrees = arrowRotationDegree,
                    iconColor = animateColor,
                    arrowIcon = R.drawable.ic_baseline_keyboard_arrow_down_24,
                    onClick = {
                        if (title != null) {
                            onExpendClick(expanded, title)
                        }
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LazyVerticalGrid(
                userScrollEnabled = false,
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = dimen_16_dp)
                    .heightIn(
                        min = 110.dp,
                        max = 140.dp
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                itemsIndexed(questionUiModel?.options?.sortedBy { it.optionId }
                    ?: emptyList()) { _index, optionItem ->
                    if (optionItem.optionType?.equals(
                            QuestionType.MultiSelect.name,
                            true
                        ) == true
                    ) {
                        GridOptionCard(
                            optionItem = optionItem,
                            index = _index,
                            isEnabled = true,
                            selectedIndex = emptyList()
                        ) { _ ->

                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

            }
        }

        if (taskStatus?.value == StatusEnum.INPROGRESS.name) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimen_5_dp)
            ) {
                Divider(
                    modifier = Modifier
                        .height(dimen_1_dp)
                        .weight(1f)

                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimen_16_dp,
                        end = dimen_16_dp,
                        bottom = dimen_8_dp,
                        top = dimen_8_dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
            ) {
                SubContainerView(options)
            }
        }
    }
}

@Composable
fun GridOptionCard(
    modifier: Modifier = Modifier,
    optionItem: OptionsUiModel,
    index: Int,
    isEnabled: Boolean = true,
    selectedIndex: List<Int>,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_5_dp, vertical = dimen_5_dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (selectedIndex.contains(optionItem.optionId)) blueDark else languageItemActiveBg)
            .clickable {
                onOptionSelected(optionItem.optionId ?: -1)
            }
            .then(modifier)) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = optionItem.originalValue.toString() ?: BLANK_STRING,
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = if (selectedIndex.contains(optionItem.optionId)) white else textColorDark.copy(
                        alpha = if (isEnabled) 1f else 0.5f
                    )
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
    }

}