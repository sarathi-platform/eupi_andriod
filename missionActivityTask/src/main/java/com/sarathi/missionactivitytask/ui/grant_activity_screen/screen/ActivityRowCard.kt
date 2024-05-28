package com.sarathi.missionactivitytask.ui.grant_activity_screen.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.BasicContentComponent
import com.sarathi.missionactivitytask.ui.components.ButtonComponent
import com.sarathi.missionactivitytask.ui.components.StepsBoxGrantComponent

@Composable
fun ActivityRowCard(
    contents: List<BasicContent> = listOf(),
    activities: List<ActivityUiModel>
) {
    Column {
        Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)) {
            contents.forEachIndexed { index, item ->
                if (index < 3) {
                    BasicContentComponent(
                        contentType = item.contentType,
                        contentTitle = item.contentTitle
                    )
                } else if (index == 3) {
                    ButtonComponent(title = "+ ${contents.size - index} More Data")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(
        ) {
            itemsIndexed(
                items = activities
            ) { index, activity ->
                StepsBoxGrantComponent(
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