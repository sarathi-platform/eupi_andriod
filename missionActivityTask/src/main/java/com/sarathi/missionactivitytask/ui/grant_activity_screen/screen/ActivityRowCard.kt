package com.sarathi.missionactivitytask.ui.grant_activity_screen.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.sarathi.contentmodule.ui.content_screen.screen.BaseContentScreen
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.model.uiModel.ContentCategoryEnum
import com.sarathi.missionactivitytask.navigation.navigateToContentDetailScreen
import com.sarathi.missionactivitytask.navigation.navigateToGrantTaskScreen
import com.sarathi.missionactivitytask.ui.components.StepsBoxGrantComponent
import java.util.Locale
import com.sarathi.missionactivitytask.utils.getFilePathUri

@Composable
fun ActivityRowCard(
    missionId: Int,
    navController: NavController,
    contents: List<BasicContent> = listOf(),
    activities: List<ActivityUiModel>,
    onContentData: (contentValue: String, contentKey: String, contentType: String, contentTitle: String) -> Unit
) {
    Column {
        BaseContentScreen(
            matId = missionId,
            contentScreenCategory = ContentCategoryEnum.MISSION.ordinal
        ) { contentValue, contentKey, contentType, isLimitContentData, contentTitle ->
            if (!isLimitContentData) {
                onContentData(contentValue, contentKey, contentType, contentTitle)
            } else {
                navigateToContentDetailScreen(
                    navController,
                    matId = missionId,
                    contentScreenCategory = ContentCategoryEnum.MISSION.ordinal
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_16_dp)
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
                    imageUri = getFilePathUri(activity.icon ?: BLANK_STRING)
                ) {
                    if (activity.activityType.lowercase() == ActivityTypeEnum.GRANT.name.lowercase(
                            Locale.ENGLISH
                        )
                    ) {
                        navigateToGrantTaskScreen(
                        navController,
                        missionId = activity.missionId,
                        activityId = activity.activityId,
                        activityName = activity.description
                    )
                }
                }
            }
            item {
                CustomVerticalSpacer(size = dimen_20_dp)
            }
        }
    }

}

data class BasicContent(val contentType: String, val contentTitle: String)
