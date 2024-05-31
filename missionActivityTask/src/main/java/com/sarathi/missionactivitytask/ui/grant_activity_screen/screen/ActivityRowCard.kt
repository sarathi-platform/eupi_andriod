package com.sarathi.missionactivitytask.ui.grant_activity_screen.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sarathi.contentmodule.ui.content_screen.screen.BaseContentScreen
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.navigation.navigateToContentDetailScreen
import com.sarathi.missionactivitytask.navigation.navigateToMediaPlayerScreen
import com.sarathi.missionactivitytask.navigation.navigateToTaskScreen
import com.sarathi.missionactivitytask.ui.components.StepsBoxGrantComponent

@Composable
fun ActivityRowCard(
    navController: NavController,
    contents: List<BasicContent> = listOf(),
    activities: List<ActivityUiModel>,

) {
    Column {
        BaseContentScreen { contentKey, contentType, isLimitContentData ->
            if (!isLimitContentData) {
                navigateToMediaPlayerScreen(navController, contentKey, contentType)
            } else {
                navigateToContentDetailScreen(navController)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(
        ) {
            itemsIndexed(
                items = activities
            ) { index, activity ->

                StepsBoxGrantComponent(
                    modifier = Modifier.clickable {
                        navigateToTaskScreen(
                            navController,
                            missionId = activity.missionId,
                            activityId = activity.activityId,
                            activityName = activity.description
                        )
                    },
                    boxTitle = activity.description,
                    subTitle = "${activity.pendingTaskCount}/${activity.taskCount}",
                    stepNo = index + 1,
                    pendingCount = activity.pendingTaskCount,
                    totalCount = activity.taskCount,
                    index = index,
                    isDividerVisible = index != activities.lastIndex,
                    painter = painterResource(id = R.drawable.ic_mission_inprogress)
                ) {

                }
            }
        }
    }

}



data class BasicContent(val contentType: String, val contentTitle: String)
data class GrantStep(val boxTittle: String, val boxSubTitle: String)