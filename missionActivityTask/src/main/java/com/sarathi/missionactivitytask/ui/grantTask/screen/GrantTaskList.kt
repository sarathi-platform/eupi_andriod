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
import com.sarathi.missionactivitytask.ui.basic_content.component.GrantTaskCard
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.grantTask.model.GrantTaskCardSlots

@Composable
fun GrantTaskList(
    taskList: HashMap<Int, HashMap<String, String>>,
    isSearch: Boolean = true,
    searchPlaceholder: String = "Search",
    onSearchValueChange: (String) -> Unit,
    navController: NavController,
    onPrimaryButtonClick: (Int, String) -> Unit,
    onContentData: (contentValue: String, contentKey: String, contentType: String) -> Unit,
) {
    Column {
//        BaseContentScreen { contentValue, contentKey, contentType, isLimitContentData ->
//            if (!isLimitContentData) {
//                onContentData(contentValue, contentKey, contentType)
//                navigateToMediaPlayerScreen(navController, contentKey, contentType)
//            } else {
//              //  navigateToContentDetailScreen(navController)
//            }
//        }
        if (isSearch) {
            SearchWithFilterViewComponent(
                placeholderString = searchPlaceholder,
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
                        onPrimaryButtonClick(
                            task.key,
                            task.value[GrantTaskCardSlots.GRANT_TASK_TITLE.name] ?: BLANK_STRING
                        )
                    },
                    title = task.value[GrantTaskCardSlots.GRANT_TASK_TITLE.name] ?: BLANK_STRING,
                    subTitle1 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE.name]
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
                    subtitle4 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_4.name]
                        ?: BLANK_STRING,
                    subtitle5 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_5.name]
                        ?: BLANK_STRING
                )

            }
        }
    }

}