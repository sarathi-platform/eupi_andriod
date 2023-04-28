package com.patsurvey.nudge.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.progress.ProgressScreenViewModel
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.navigation.ScreenRoutes

@Composable
fun VillageSelectionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ProgressScreenViewModel
) {

    val villages by viewModel.villageList.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .then(modifier)
    ) {
        Text(text = "Select Village & VO", style = largeTextStyle, color = textColorDark)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)){
            itemsIndexed(villages) { index, village ->
                VillageAndVoBox(
                    tolaName = village.villageName,
                    voName = village.voName,
                    index = index,
                    viewModel.villageSelected.value,
                    ScreenRoutes.VILLAGE_SELECTION_SCREEN
                ) {
                    viewModel.villageSelected.value = it
                    navController.navigate(ScreenRoutes.HOME_SCREEN.route)
                }
            }
        }
    }

}

@Composable
fun VillageAndVoBox(
    tolaName: String = "",
    voName: String = "",
    index: Int,
    selectedIndex: Int,
    screenName: ScreenRoutes,
    modifier: Modifier = Modifier,
    onVillageSeleted: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (index == selectedIndex && screenName == ScreenRoutes.VILLAGE_SELECTION_SCREEN) blueDark else if (index == selectedIndex && screenName == ScreenRoutes.PROGRESS_SCREEN) greenLight else greyBorder,
                shape = RoundedCornerShape(6.dp)
            )
            .shadow(
                elevation = 10.dp,
                ambientColor = White,
                spotColor = Black,
                shape = RoundedCornerShape(6.dp),
            )
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Black
                )
            ) {
                onVillageSeleted(index)
            }
            .background(if (index == selectedIndex && screenName == ScreenRoutes.VILLAGE_SELECTION_SCREEN) blueDark else if (index == selectedIndex && screenName == ScreenRoutes.PROGRESS_SCREEN) greenLight else White)
            .then(modifier),
        elevation = 10.dp
    ) {
        Column(modifier = Modifier
            .background(if (index == selectedIndex && screenName == ScreenRoutes.VILLAGE_SELECTION_SCREEN) blueDark else if (index == selectedIndex && screenName == ScreenRoutes.PROGRESS_SCREEN) greenLight else White)) {
            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.home_icn),
                    contentDescription = null,
                    tint = if (index == selectedIndex && screenName == ScreenRoutes.VILLAGE_SELECTION_SCREEN) White else textColorDark,
                )
                Text(
                    text = tolaName,
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = if (index == selectedIndex && screenName == ScreenRoutes.VILLAGE_SELECTION_SCREEN) White else textColorDark,
                    style = smallTextStyle
                )
            }
            Row(
                modifier = Modifier
                    .absolutePadding(left = 4.dp)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "VO:",
                    modifier = Modifier,
                    color = if (index == selectedIndex && screenName == ScreenRoutes.VILLAGE_SELECTION_SCREEN) White else textColorDark,
                    style = smallTextStyle
                )
                Text(
                    text = voName,
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = if (index == selectedIndex && screenName == ScreenRoutes.VILLAGE_SELECTION_SCREEN) White else textColorDark,
                    style = smallTextStyle
                )
            }
        }
    }
}