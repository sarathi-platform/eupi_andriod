package com.patsurvey.nudge.activities.survey

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.transect_walk.VillageDetailView
import com.patsurvey.nudge.utils.BLANK_STRING
import kotlinx.coroutines.delay

@Composable
fun PatSuccessScreen(
    modifier: Modifier = Modifier,
    viewModel: PatSuccessScreenViewModel,
    navController: NavHostController,
    messages: String
) {

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        delay(3000L)
        navController.navigate(
            "pat_step_completion_screen/${
                context.getString(R.string.pat_survey_completed_message)
                    .replace(
                        "{VILLAGE_NAME}",
                        viewModel.prefRepo.getSelectedVillage().name ?: BLANK_STRING
                    )
            }"
        )
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(top = 14.dp)) {

        VillageDetailView(
            villageName = viewModel.prefRepo.getSelectedVillage().name ?: "",
            voName = (viewModel.prefRepo.getSelectedVillage().federationName)
                ?: "",
            modifier = Modifier
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_check_green_without_border),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = modifier.padding(horizontal = 10.dp)) {
                    Text(
                        text = messages,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = textColorDark,
                        style = largeTextStyle,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

}