package com.sarathi.smallgroupmodule.ui.smallGroupSubTab.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.LazyColumnWithVerticalPadding
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.BasicTextWithIconComponent
import com.sarathi.smallgroupmodule.navigation.navigateToAttendanceHistoryScreen
import com.sarathi.smallgroupmodule.ui.didiTab.viewModel.DidiTabViewModel
import com.sarathi.smallgroupmodule.ui.theme.blueDark
import com.sarathi.smallgroupmodule.ui.theme.defaultTextStyle
import com.sarathi.smallgroupmodule.ui.theme.dimen_100_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_16_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_8_dp
import com.sarathi.smallgroupmodule.ui.theme.mediumTextStyle
import com.sarathi.smallgroupmodule.ui.theme.white

@Composable
fun SmallGroupSubTab(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    didiTabViewModel: DidiTabViewModel,
    smallGroupList: List<SmallGroupSubTabUiModel>
) {

    LazyColumnWithVerticalPadding(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimen_8_dp)
    ) {

        itemsIndexed(smallGroupList) { index, smallGroup ->
            SmallGroupItem(smallGroupDetails = smallGroup) { smallGroupId ->
                navHostController.navigateToAttendanceHistoryScreen(smallGroupId)
            }
        }

        item {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(dimen_100_dp))
        }

    }

}

@Composable
fun SmallGroupItem(
    modifier: Modifier = Modifier,
    smallGroupDetails: SmallGroupSubTabUiModel,
    onClick: (smallGroupId: Int) -> Unit
) {

    BasicCardView(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick(smallGroupDetails.smallGroupId)
        }
    ) {
        Row(
            modifier = Modifier
                .background(white)
                .fillMaxWidth()
                .padding(horizontal = dimen_16_dp, vertical = dimen_16_dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = smallGroupDetails.smallGroupName,
                modifier = Modifier,
                color = blueDark,
                style = mediumTextStyle
            )
            BasicTextWithIconComponent(
                modifier = Modifier,
                iconContent = {
                    Icon(
                        painterResource(id = R.drawable.didi_icon),
                        contentDescription = "",
                        tint = blueDark
                    )
                }
            ) {
                Text(
                    text = smallGroupDetails.didiCount.toString(),
                    color = blueDark,
                    style = defaultTextStyle
                )
            }

        }
    }

}