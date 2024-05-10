package com.nrlm.baselinesurvey.ui.mission_summary_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.navigation.home.navigateBackToMissionScreen
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.StepsBox
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.mission_summary_screen.viewModel.MissionSummaryViewModel
import com.nrlm.baselinesurvey.ui.theme.inprogressYellow
import com.nrlm.baselinesurvey.utils.numberInEnglishFormat
import com.nrlm.baselinesurvey.utils.states.SectionStatus


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MissionSummaryScreen(
    navController: NavController = rememberNavController(),
    missionId: Int = 0,
    missionName: String,
    missionDate: String,
    viewModel: MissionSummaryViewModel = hiltViewModel()
) {
    val activities =
        viewModel.activities.value

    LaunchedEffect(key1 = true) {
        viewModel.init(missionId)
    }
    ToolbarWithMenuComponent(
        title = missionName,
        modifier = Modifier.fillMaxSize(),
        navController=navController,
        onBackIconClick = { navController.popBackStack() },
        onBottomUI = {
            if (activities.filter { it.status != SectionStatus.COMPLETED.name }
                    .isEmpty() && viewModel.mission.value?.status != SectionStatus.COMPLETED.name) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    ButtonPositive(
                        buttonTitle = stringResource(R.string.complete) + " " + missionName,
                        isActive = true,
                        isLeftArrow = true

                    ) {
                        viewModel.onEvent(
                            EventWriterEvents.UpdateMissionStatusEvent(
                                missionId = missionId,
                                status = SectionStatus.COMPLETED
                            )
                        )
                        navigateBackToMissionScreen(navController)
                    }
                }
            }

        },
        onContentUI = {
            if (activities.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 55.dp)) {
                    //TODO in future in uncomment whenever get correct data from backend

//                    Text(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 10.dp),
//                        text = stringResource(id = R.string.due_by_x, missionDate),
//                        style = newMediumTextStyle,
//                        color = black100Percent
//
//                    )
                    LazyColumn(
                    ) {
                        itemsIndexed(
                            items = activities
                        ) { index, activity ->


                            val pendingTaskCount =
                                viewModel.getPendingDidiCountLive(activity.activityId)
                                    .observeAsState().value ?: 0
                            val pendingTasks = numberInEnglishFormat(pendingTaskCount, null)

                            var subTitle = if (activity.activityId == 1) {
                                if (pendingTaskCount > 1) {
                                    stringResource(id = R.string.didis_item_text_plural)
                                } else {
                                    stringResource(id = R.string.didis_item_text_singular)
                                }
                            } else {
                                if (pendingTaskCount > 1) {
                                    stringResource(R.string.hamlets_item_text_plural)
                                } else {
                                    stringResource(R.string.hamlets_item_text_singular)
                                }

                            }

                            StepsBox(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                boxTitle = activity.activityName,
                                subTitle = stringResource(
                                    id = R.string.x_didi_pending,
                                    pendingTasks,
                                    subTitle,
                                ),
                                stepNo = index + 1,
                                index = index,
                                isCompleted = activity.status == SectionStatus.COMPLETED.name,
                                iconResourceId = R.drawable.ic_mission_inprogress,
                                backgroundColor = inprogressYellow,
                                onclick = {
                                    navController.navigate("add_didi_graph/${activity.activityName}/${missionId}/${activity.endDate}/${activity.activityId}")
                                })
                        }

                    }
                }

            }
        }
    )
}