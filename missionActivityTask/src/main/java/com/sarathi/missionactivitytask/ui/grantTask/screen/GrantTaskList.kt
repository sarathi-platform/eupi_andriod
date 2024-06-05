package com.sarathi.missionactivitytask.ui.grantTask.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nudge.core.BLANK_STRING
import com.sarathi.contentmodule.ui.content_screen.screen.BaseContentScreen
import com.sarathi.missionactivitytask.navigation.navigateToContentDetailScreen
import com.sarathi.missionactivitytask.navigation.navigateToMediaPlayerScreen
import com.sarathi.missionactivitytask.ui.basic_content.component.GrantTaskCard
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.grantTask.model.GrantTaskCardSlots
import com.sarathi.missionactivitytask.ui.grant_activity_screen.screen.BasicContent

@Composable
fun GrantTaskList(
    taskList: HashMap<Int, HashMap<String, String>>,
    contents: List<BasicContent> = listOf(),
    isSearch: Boolean = true,
    onSearchValueChange: (String) -> Unit,
    navController: NavController,
    onPrimaryButtonClick: (Int) -> Unit,
    onContentData: (contentValue: String, contentKey: String, contentType: String) -> Unit,
) {
    Column {
        BaseContentScreen { contentValue, contentKey, contentType, isLimitContentData ->
            if (!isLimitContentData) {
                onContentData(contentValue, contentKey, contentType)
                navigateToMediaPlayerScreen(navController, contentKey, contentType)
            } else {
                navigateToContentDetailScreen(navController)
            }
        }
        if (isSearch) {
            SearchWithFilterViewComponent(placeholderString = "Search",
                filterSelected = false,
                modifier = Modifier.padding(horizontal = 10.dp),
                showFilter = false,
                onFilterSelected = {},
                onSearchValueChange = { queryTerm ->
                    onSearchValueChange(queryTerm)

                })
        }
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn {
            itemsIndexed(
                items = taskList.entries.toList()
            ) { index, task ->

                GrantTaskCard(
                    onPrimaryButtonClick = {
                        onPrimaryButtonClick(task.key)
                    },
                    title = task.value[GrantTaskCardSlots.GRANT_TASK_TITLE.name] ?: BLANK_STRING,
                    subTitle = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE.name]
                        ?: BLANK_STRING,
                    primaryButtonText = task.value[GrantTaskCardSlots.GRANT_TASK_PRIMARY_BUTTON.name]
                        ?: BLANK_STRING,
                    secondaryButtonText = task.value[GrantTaskCardSlots.GRANT_TASK_SECONDARY_BUTTON.name]
                        ?: BLANK_STRING,
                    status = task.value[GrantTaskCardSlots.GRANT_TASK_STATUS.name]
                        ?: BLANK_STRING,

                    subtitle2 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_2.name]
                        ?: BLANK_STRING,
                    subtitle3 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_3.name]
                        ?: BLANK_STRING,
                )


            }
        }
    }

}