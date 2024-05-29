package com.sarathi.missionactivitytask.ui.grantTask.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.BLANK_STRING
import com.sarathi.missionactivitytask.ui.basic_content.component.GrantTaskCard
import com.sarathi.missionactivitytask.ui.components.BasicContentComponent
import com.sarathi.missionactivitytask.ui.components.ButtonComponent
import com.sarathi.missionactivitytask.ui.grantTask.model.GrantTaskCardSlots
import com.sarathi.missionactivitytask.ui.grant_activity_screen.screen.BasicContent

@Composable
fun GrantTaskList(
    taskList: HashMap<Int, HashMap<String, String>>,
    contents: List<BasicContent> = listOf(),
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
        LazyColumn {
            itemsIndexed(
                items = taskList.entries.toList()
            ) { index, task ->

                GrantTaskCard(
                    title = task.value[GrantTaskCardSlots.GRANT_TASK_TITLE.name] ?: BLANK_STRING,
                    subTitle = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE.name]
                        ?: BLANK_STRING,
                    primaryButtonText = task.value[GrantTaskCardSlots.GRANT_TASK_PRIMARY_BUTTON.name]
                        ?: BLANK_STRING,
                    secondaryButtonText = task.value[GrantTaskCardSlots.GRANT_TASK_TITLE.name]
                        ?: BLANK_STRING,
                    status = task.value[GrantTaskCardSlots.GRANT_TASK_STATUS.name]
                        ?: BLANK_STRING,
                )


            }
        }
    }

}