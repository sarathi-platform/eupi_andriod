package com.nrlm.baselinesurvey.ui.section_screen.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.SearchWithFilterViewComponent
import com.nrlm.baselinesurvey.ui.common_components.SectionItemComponent
import com.nrlm.baselinesurvey.ui.section_screen.viewmode.SectionListScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_14_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.greyBorder
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.SectionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SectionListScreen(
    navController: NavController,
    viewModel: SectionListScreenViewModel,
    modifier: Modifier = Modifier
) {

    val loaderState = viewModel.loaderState.value

    LaunchedEffect(key1 = true) {
        viewModel.init()
    }

    val sectionsList = viewModel.sectionItemStateList.toList()

    val selectedSectionDescription = remember {
        mutableStateOf<String>(BLANK_STRING)
    }

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    BackHandler {
        navController.popBackStack()
    }

    Surface(color = white) {
        
        LoaderComponent(visible = loaderState.isLoaderVisible)

        if (!loaderState.isLoaderVisible) {
            if (sectionsList.size == 1 && sectionsList[0].section.sectionName.equals(NO_SECTION, true)) {
                navController.navigate("question_screen/${sectionsList[0].section.sectionId}")
            } else {
                ModalBottomSheetLayout(
                    sheetContent = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                            /*.height(((2 * screenHeight) / 3).dp)*/
                        ) {

                            Column() {
//                        BaselineLogger.d("ProgressScreen","BottomSheet : $villages :: size ${villages.size}")
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = dimen_10_dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.info_icon),
                                        contentDescription = "info icon"
                                    )
                                }
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp), color = greyBorder
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = dimen_16_dp, horizontal = dimen_18_dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = selectedSectionDescription.value,
                                        color = textColorDark,
                                        style = smallerTextStyleNormalWeight,
                                        modifier = Modifier.padding(horizontal = dimen_10_dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = dimen_16_dp, horizontal = dimen_24_dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                scaffoldState.hide()
                                            }
                                        }, shape = RoundedCornerShape(
                                            roundedCornerRadiusDefault
                                        ), colors = ButtonDefaults.buttonColors(
                                            backgroundColor = blueDark,
                                            contentColor = white
                                        )
                                    ) {
                                        Text(text = "Ok", color = white, style = smallerTextStyle)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    },
                    sheetState = scaffoldState,
                    sheetElevation = 20.dp,
                    sheetBackgroundColor = Color.White,
                    sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimen_14_dp),
                        modifier = Modifier.padding(
                            horizontal = dimen_16_dp,
                            vertical = dimen_16_dp
                        )
                    ) {
                        item {
                            SearchWithFilterViewComponent(
                                placeholderString = "Search Question",
                                showFilter = false,
                                onFilterSelected = {

                                },
                                onSearchValueChange = {

                                }
                            )
                        }

                        item {
                            Text(
                                text = "Chose Section",
                                style = smallerTextStyle,
                                color = textColorDark
                            )
                        }

                        itemsIndexed(items = sectionsList) { index, sectionStateItem ->
                            SectionItemComponent(
                                sectionStateItem = sectionStateItem,
                                onclick = {
                                    navController.navigate("question_screen/${sectionStateItem.section.sectionId}")
                                },
                                onDetailIconClicked = {
                                    scope.launch {
                                        selectedSectionDescription.value =
                                            sectionStateItem.section.sectionDetails
                                        delay(100)
                                        if (!scaffoldState.isVisible) {
                                            scaffoldState.show()
                                        } else {
                                            scaffoldState.hide()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}