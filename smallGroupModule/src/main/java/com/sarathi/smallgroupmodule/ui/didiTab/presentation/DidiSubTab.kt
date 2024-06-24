package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.LazyColumnWithVerticalPadding
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.smallgroupmodule.ui.didiTab.viewModel.DidiTabViewModel
import com.sarathi.smallgroupmodule.ui.theme.blueDark
import com.sarathi.smallgroupmodule.ui.theme.defaultTextStyle
import com.sarathi.smallgroupmodule.ui.theme.dimen_100_dp

@Composable
fun DidiSubTab(
    modifier: Modifier = Modifier,
    didiTabViewModel: DidiTabViewModel,
    didiList: List<SubjectEntity>
) {

    Row(Modifier.fillMaxWidth()) {

        TextWithIconComponent(
            iconProperties = IconProperties(
                painterResource(id = R.drawable.didi_icon),
                contentDescription = "",
                blueDark,
            ), textProperties = TextProperties(
                text = "Total Didis - ${didiTabViewModel.totalCount.value}",
                color = blueDark,
                style = defaultTextStyle
            )
        )
    }

    LazyColumnWithVerticalPadding(modifier = Modifier.fillMaxSize()) {

        itemsIndexed(didiList) { index, item ->

            DidiTabCard(subjectEntity = item) {

            }
        }
        item {
            Spacer(modifier = modifier
                .fillMaxWidth()
                .height(dimen_100_dp))
        }

    }

}