package com.sarathi.missionactivitytask.ui.disbursement_summary_screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ui.theme.black20
import com.nudge.core.ui.theme.borderGreyLight
import com.nudge.core.ui.theme.brownDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_100_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_30_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.greyTransparentColor
import com.nudge.core.ui.theme.quesOptionTextStyle
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.uiModel.DisbursementFormSummaryUiModel
import com.sarathi.missionactivitytask.ui.components.CircularImageViewComponent
import com.sarathi.missionactivitytask.ui.disbursement_summary_screen.viewmodel.DisbursementFormSummaryScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import kotlinx.coroutines.launch


@Composable
fun DisbursementFormSummaryScreen(
    navController: NavController = rememberNavController(),
    viewModel: DisbursementFormSummaryScreenViewModel
) {
    val outerState = rememberLazyListState()
    val innerState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    if (viewModel.formList.value.isNotEmpty()) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = dimen_30_dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimen_16_dp)
                .clip(RoundedCornerShape(dimen_6_dp))
                .border(
                    width = dimen_1_dp,
                    color = black20,
                    shape = RoundedCornerShape(dimen_6_dp)
                )
                .background(Color.Transparent)
        ) {
            Column(modifier = Modifier.background(white)) {
                BoxWithConstraints(
                    modifier = Modifier
                        .scrollable(
                            state = rememberScrollableState {
                                scope.launch {
                                    val toDown = it <= 0
                                    if (toDown) {
                                        if (outerState.run { firstVisibleItemIndex == layoutInfo.totalItemsCount - 1 }) {
                                            innerState.scrollBy(-it)
                                        } else {
                                            outerState.scrollBy(-it)
                                        }
                                    }
                                }
                                it
                            },
                            Orientation.Vertical,
                        )
                ) {
                    LazyColumn(
                        state = outerState,
                        verticalArrangement = Arrangement.spacedBy(dimen_8_dp)
                    ) {
                        item {
                            Column(modifier = Modifier.background(greyTransparentColor)) {
                                // val villageName = viewModel.formList.value.keys.toString()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimen_6_dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "VO - ${
                                            viewModel.formList.value.keys.toString()
                                                .replace("[", "").replace("]", "")
                                        }", style = defaultTextStyle
                                    )
                                    Text("", style = quesOptionTextStyle)
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimen_6_dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("CSG Disbursed", style = quesOptionTextStyle)
                                    Text("", style = quesOptionTextStyle)
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimen_6_dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Number of Didis", style = quesOptionTextStyle)
                                    Text(
                                        viewModel.formList.value.size.toString(),
                                        style = quesOptionTextStyle
                                    )
                                }
                                Divider(
                                    color = borderGreyLight,
                                    thickness = dimen_1_dp,
                                    modifier = Modifier.padding(vertical = dimen_8_dp)
                                )
                            }
                        }
                        itemsIndexed(
                            items = viewModel.formList.value.entries.toList()
                        ) { _, form ->
                            BoxWithConstraints(
                                modifier = Modifier
                                    .scrollable(
                                        state = outerState,
                                        Orientation.Vertical,
                                    )
                                    .heightIn(dimen_100_dp, maxHeight)
                            ) {
                                LazyColumn(modifier = Modifier.padding(bottom = 50.dp)) {
                                    itemsIndexed(
                                        items = form.value
                                    ) { _, disbursementFormSummaryUiModel ->
                                        MakeTaskCard(
                                            disbursementFormSummaryUiModel,
                                            viewModel.getFilePathUri(disbursementFormSummaryUiModel.didiImage)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}


@Composable
private fun MakeTextRow(text1: String, text2: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_6_dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text1, style = quesOptionTextStyle)
        Text(text2, style = smallTextStyle)
    }
}


@Composable
private fun MakeTaskCard(
    disbursementFormSummaryUiModel: DisbursementFormSummaryUiModel,
    imageUri: Uri?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(dimen_10_dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_4_dp),
            horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularImageViewComponent(modifier = Modifier, imageUri ?: Uri.EMPTY)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = disbursementFormSummaryUiModel.subjectName,
                    style = defaultTextStyle,
                    color = brownDark
                )
                Text(
                    text = disbursementFormSummaryUiModel.villageName,
                    style = smallTextStyle,
                    color = brownDark
                )
            }

        }
        Column(modifier = Modifier.padding(start = dimen_10_dp, end = dimen_10_dp)) {
            MakeTextRow(
                text1 = "Mode: ${disbursementFormSummaryUiModel.mode}",
                text2 = "Nature: ${disbursementFormSummaryUiModel.nature}"
            )
            MakeTextRow(text1 = "Amount: ₹${disbursementFormSummaryUiModel.amount}", text2 = "")
        }

    }

}