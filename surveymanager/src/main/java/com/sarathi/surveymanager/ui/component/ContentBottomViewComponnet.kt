package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.lightGray2
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.survey.response.ContentList

@Composable
fun ContentBottomViewComponent(
    questionIndex: Int,
    contents: List<ContentList?>? = emptyList(),
    showCardView: Boolean = false,
    questionDetailExpanded: (index: Int) -> Unit,
    imageClickListener: (Content) -> Unit = {},
    videoLinkClicked: (Content) -> Unit = {}
) {
    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )

        if (contents?.isNotEmpty() == true && showCardView) {
            Divider(
                thickness = dimen_1_dp,
                color = lightGray2,
                modifier = Modifier.fillMaxWidth()
            )

            ExpandableDescriptionContentComponent(
                questionDetailExpanded = questionDetailExpanded,
                index = questionIndex,
                contents = contents,
                subTitle = BLANK_STRING,
                imageClickListener = { imageClickListener ->

                },
                videoLinkClicked = { videoLinkClicked ->

                }
            )
        }
    }
}
