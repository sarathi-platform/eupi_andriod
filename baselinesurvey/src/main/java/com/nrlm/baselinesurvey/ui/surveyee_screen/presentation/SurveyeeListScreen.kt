package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.SearchWithFilterViewComponent
import com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel.SurveyeeScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.progressIndicatorColor
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.trackColor
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.FilterListState

@Composable
fun SurveyeeListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SurveyeeScreenViewModel
) {

    LaunchedEffect(key1 = true) {
        viewModel.init()
    }

    val loaderState = viewModel.loaderState.value
    val didiList = viewModel.surveyeeListState.value

    val isFilterApplied = remember {
        mutableStateOf(FilterListState())
    }

    Surface(color = white) {

        LoaderComponent(visible = loaderState.isLoaderVisible)

        if (!loaderState.isLoaderVisible){
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(dimen_8_dp),
                modifier = Modifier.padding(horizontal = dimen_16_dp, vertical = dimen_16_dp)
            ) {
                item {
                    SearchWithFilterViewComponent(
                        placeholderString = stringResource(id = R.string.search_didis),
                        filterSelected = isFilterApplied.value.isFilterApplied,
                        onFilterSelected = {
                                           if (didiList.isNotEmpty()) {
                                               isFilterApplied.value =
                                                   FilterListState(isFilterApplied = !it)
                                           }
                        },
                        onSearchValueChange = {
                            /*TODO*/
                        }
                    )
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .weight(1f)
                                .height(dimen_8_dp)
                                .padding(top = 1.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            color = progressIndicatorColor,
                            trackColor = trackColor,
                            progress = 0.2f
                        )
                        Spacer(modifier = Modifier.width(dimen_8_dp))
                        Text(text = "2/4", color = textColorDark, style = smallTextStyle)
                    }

                }

                itemsIndexed(items = didiList) { index, item ->
                    SurveyeeCardComponent(surveyeeState = item) {buttonName ->
                        when (buttonName) {
                            is ButtonName.START_BUTTON -> {
                                navController.navigate(
                                    "home_graph"
                                )
                            }
                            is ButtonName.NEGATIVE_BUTTON -> {

                            }
                            is ButtonName.SHOW_BUTTON -> {

                            }
                        }
                    }
                }
            }
        }

    }


}