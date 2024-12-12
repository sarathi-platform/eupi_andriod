package com.sarathi.missionactivitytask.ui.grant_activity_screen.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.nudge.auditTrail.domain.usecase.AuditTrailUseCase
import com.nudge.core.BLANK_STRING
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_72_dp
import com.sarathi.contentmodule.ui.content_screen.screen.BaseContentScreen
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.model.uiModel.ContentCategoryEnum
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.navigation.navigateToActivitySelectTaskScreen
import com.sarathi.missionactivitytask.navigation.navigateToContentDetailScreen
import com.sarathi.missionactivitytask.navigation.navigateToGrantTaskScreen
import com.sarathi.missionactivitytask.navigation.navigateToLivelihoodTaskScreen
import com.sarathi.missionactivitytask.navigation.navigateToSurveyTaskScreen
import com.sarathi.missionactivitytask.ui.components.StepsBoxGrantComponent
import com.sarathi.missionactivitytask.ui.mission_screen.screen.auditTailDetail
import com.sarathi.missionactivitytask.utils.getFilePathUri
import java.util.Locale

@Composable
fun ActivityRowCard(
    programId: Int,
    missionId: Int,
    navController: NavController,
    contents: List<BasicContent> = listOf(),
    activities: List<ActivityUiModel>,
    auditTrail:AuditTrailUseCase,
    onContentData: (contentValue: String, contentKey: String, contentType: String, contentTitle: String) -> Unit
) {
    val context = LocalContext.current
    Column {onContentData
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
                    when (activity.activityType.lowercase()) {
                        ActivityTypeEnum.GRANT.name.lowercase(
                            Locale.ENGLISH
                        ) -> {
                            navigateToGrantTaskScreen(
                                navController,
                                missionId = activity.missionId,
                                activityId = activity.activityId,
                                activityName = activity.description,
                                programId = programId

                            )
                            auditTailDetail(auditTrail,context.getString(R.string.audit_trail_action,activity.description))

                        }

                        ActivityTypeEnum.LIVELIHOOD.name.lowercase(
                            Locale.ENGLISH
                        ) -> {
                            navigateToLivelihoodTaskScreen(
                                navController,
                                missionId = activity.missionId,
                                activityId = activity.activityId,
                                activityName = activity.description,
                                programId = programId
                            )
                            auditTailDetail(auditTrail,context.getString(R.string.audit_trail_action,activity.description))
                        }

                        ActivityTypeEnum.SURVEY.name.lowercase(
                            Locale.ENGLISH
                        ), ActivityTypeEnum.BASIC.name.lowercase(
                            Locale.ENGLISH
                        ), ActivityTypeEnum.LIVELIHOOD_PoP.name.lowercase(
                            Locale.ENGLISH
                        ) -> {
                            navigateToSurveyTaskScreen(
                                navController,
                                missionId = activity.missionId,
                                activityId = activity.activityId,
                                activityName = activity.description,
                                programId = programId
                            )
                            auditTailDetail(auditTrail,context.getString(R.string.audit_trail_action,activity.description))
                        }

                        ActivityTypeEnum.SELECT.name.lowercase(
                            Locale.ENGLISH
                        ),
                        ActivityTypeEnum.TRAINING.name.lowercase(
                            Locale.ENGLISH
                        ) -> {
                            navigateToActivitySelectTaskScreen(
                                navController,
                                missionId = activity.missionId,
                                activityId = activity.activityId,
                                activityName = activity.description,
                                programId = programId
                            )
                            auditTailDetail(auditTrail,context.getString(R.string.audit_trail_action,activity.description))
                        }
                    }
                }
            }
            item {
                CustomVerticalSpacer(size = dimen_72_dp)
            }
        }
    }
}

data class BasicContent(val contentType: String, val contentTitle: String)
