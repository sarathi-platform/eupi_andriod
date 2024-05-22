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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarathi.dataloadingmangement.data.entities.MissionActivityEntity
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.BasicContentComponent
import com.sarathi.missionactivitytask.ui.components.ButtonComponent
import com.sarathi.missionactivitytask.ui.components.StepsBoxGrantComponent

@Composable
fun ActivityRowCard(
    contents: List<BasicContent> = listOf(),
    activities: List<MissionActivityEntity>
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
                    boxTitle = activity.activityName,
                    subTitle = "0/5 VOs completed",
                    stepNo = index + 1,
                    index = index,
                    isDividerVisible = index != activities.lastIndex,
                    painter = painterResource(id = R.drawable.ic_mission_inprogress)
                ) {

                }
            }
        }
    }

}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PreviewBasicContentScreen() {
    val basicContent1 = BasicContent("IMAGE", "Content Image")
    val basicContent2 = BasicContent("VIDEO", "Content Video")
    val basicContent3 = BasicContent("FILE", "Content File")
    val basicContent4 = BasicContent("IMAGE", "Content Image")
    val basicContent5 = BasicContent("IMAGE", "Content Image")
    val basicContent6 = BasicContent("IMAGE", "Content Image")
    val basicContent7 = BasicContent("IMAGE", "Content Image")
    val basicContent8 = BasicContent("IMAGE", "Content Image")
    val basicContent9 = BasicContent("IMAGE", "Content Image")
    val basicContent10 = BasicContent("IMAGE", "Content Image")
    val contents = listOf(
        basicContent1,
        basicContent2,
        basicContent3,
        basicContent4,
        basicContent5,
        basicContent6,
        basicContent7,
        basicContent8,
        basicContent9,
        basicContent10
    )
    val grantStep = GrantStep("Receipt of Found-1", "0/5 VOs completed")
    val grantStep1 = GrantStep("Receipt of Found-2", "0/5 VOs completed")
    val grantStep2 = GrantStep("Receipt of Found-3", "0/5 VOs completed")
    val grantSte3 = GrantStep("Receipt of Found-4", "0/5 VOs completed")
    val grantSte4 = GrantStep("Receipt of Found-5", "0/5 VOs completed")
    val grantSte5 = GrantStep("Receipt of Found-6", "0/5 VOs completed")
    val grantSte6 = GrantStep("Receipt of Found-7", "0/5 VOs completed")
    val grantSte7 = GrantStep("Receipt of Found-8", "0/5 VOs completed")
    val grantSte8 = GrantStep("Receipt of Found-9", "0/5 VOs completed")
    val grantSte9 = GrantStep("Receipt of Found-10", "0/5 VOs completed")
    val grantSteps = listOf(
        grantStep,
        grantStep1,
        grantStep2
    )

    // ActivityRowCard(items = contents, grantSteps = grantSteps)
}

data class BasicContent(val contentType: String, val contentTitle: String)
data class GrantStep(val boxTittle: String, val boxSubTitle: String)