package com.patsurvey.nudge.activities.ui.bpc.bpc_didi_list_screens


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.navigation.home.BpcDidiListScreens
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SURVEY
import com.patsurvey.nudge.utils.DidiItemCardForPat
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.ShowDidisFromTola
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.showDidiImageDialog
import kotlinx.coroutines.delay

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BpcDidiListScreen(
    modifier: Modifier = Modifier,
    bpcDidiListViewModel: BpcDidiListViewModel,
    navController: NavHostController,
    villageId: Int,
    stepId: Int
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    LaunchedEffect(key1 = Unit) {
        bpcDidiListViewModel.showLoader.value = true
        bpcDidiListViewModel.isStepComplete { _, isComplete ->
            if (isComplete) {
                navController.navigate(BpcDidiListScreens.BPC_SCORE_COMPARISION_SCREEN.route)

            }
        }
        delay(250)
        bpcDidiListViewModel.showLoader.value = false
    }

    val didis by bpcDidiListViewModel.selectedDidiList.collectAsState()

    val newFilteredTolaDidiList = bpcDidiListViewModel.filterTolaMapList
    val newFilteredDidiList = bpcDidiListViewModel.filterDidiList

    val localDensity = LocalDensity.current

    val focusManager = LocalFocusManager.current

    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    var filterSelected by remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        bpcDidiListViewModel.fetchDidiListForBPC()
        delay(100)
    }

    if(bpcDidiListViewModel.showDidiImageDialog.value){
        bpcDidiListViewModel.dialogDidiEntity.value?.let {
            showDidiImageDialog(didi = it){
                bpcDidiListViewModel.showDidiImageDialog.value = false
            }
        }
    }

    if (bpcDidiListViewModel.showLoader.value) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 30.dp)
        ) {
            CircularProgressIndicator(
                color = blueDark,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Center)
            )
        }

    } else {
        if (bpcDidiListViewModel.selectedDidiList.value.isEmpty()) {
            ConstraintLayout(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize().padding(top = 16.dp)
                    .border(
                        width = 0.dp,
                        color = Color.Transparent,
                    )
            ) {
                Column(modifier = Modifier) {
                    VOAndVillageBoxView(
                        prefRepo = bpcDidiListViewModel.prefRepo,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = (screenHeight / 4).dp)
                                .align(
                                    Alignment.TopCenter
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = textColorDark,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontFamily = NotoSans
                                        )
                                    ) {
                                        append(stringResource(R.string.no_didis_availble_for_bpc_verification))
                                    }
                                },
                                modifier = Modifier.padding(top = 32.dp)
                            )
                        }
                    }
                }
            }
        }else {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .then(modifier)
            ) {
                val (bottomActionBox, mainBox) = createRefs()

                Box(modifier = Modifier
                    .constrainAs(mainBox) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(bottomActionBox.top)
                        height = Dimension.fillToConstraints
                    }
                    .padding(top = 14.dp)
                    .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    ) {

                        Text(
                            text = stringResource(id = R.string.bpc_didi_screen_title),
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(
                                    vertical = dimensionResource(id = R.dimen.dp_6),
                                    horizontal = 32.dp
                                )
                                .fillMaxWidth()
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = white)
                                .pointerInput(true) {
                                    detectTapGestures(onTap = {
                                        focusManager.clearFocus()
                                    })
                                }
                                .weight(1f),
                            state = listState
                        ) {

                            item {
                                Spacer(
                                    modifier = Modifier
                                        .height(14.dp)
                                        .fillMaxWidth()
                                )
                            }

                            item {
                                SearchWithFilterView(
                                    placeholderString = stringResource(id = R.string.search_didis),
                                    filterSelected = filterSelected,
                                    onFilterSelected = {
                                        if (didis.isNotEmpty()) {
                                            filterSelected = !it
                                            bpcDidiListViewModel.filterList()
                                        }
                                    },
                                    onSearchValueChange = {
                                        bpcDidiListViewModel.performQuery(it, filterSelected)
                                    }
                                )
                            }

                            item {
                                Spacer(
                                    modifier = Modifier
                                        .height(14.dp)
                                        .fillMaxWidth()
                                )
                            }

                            item {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val count = bpcDidiListViewModel.pendingDidiCount.value
                                    Text(
                                        text = stringResource(
                                            id = if (count > 1) R.string.count_didis_pending_plural else R.string.count_didis_pending_singular,
                                            count
                                        ),
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        fontFamily = NotoSans,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier
                                            .padding(vertical = dimensionResource(id = R.dimen.dp_6))
                                            .padding(start = 4.dp)
                                            .weight(1f)
                                    )
                                    Spacer(modifier = Modifier.padding(14.dp))
                                }
                            }

                            item {
                                Spacer(
                                    modifier = Modifier
                                        .height(14.dp)
                                        .fillMaxWidth()
                                )
                            }

                            if (filterSelected) {
                                itemsIndexed(
                                    newFilteredTolaDidiList.keys.toList().reversed()
                                ) { index, didiKey ->

                                    ShowDidisFromTola(navController = navController,
                                        prefRepo = bpcDidiListViewModel.prefRepo,
                                        didiTola = didiKey,
                                        answerDao = bpcDidiListViewModel.answerDao,
                                        questionListDao = bpcDidiListViewModel.questionListDao,
                                        didiList = if (bpcDidiListViewModel.prefRepo.getFromPage()
                                                .equals(ARG_FROM_PAT_SURVEY, true)
                                        )
                                            newFilteredTolaDidiList[didiKey]?.filter { it.wealth_ranking == WealthRank.POOR.rank }
                                                ?: emptyList()
                                        else newFilteredTolaDidiList[didiKey] ?: emptyList(),
                                        modifier = modifier,
                                        expandedIds = listOf(),
                                        onExpendClick = { _, _ ->

                                    },
                                    onNavigate = {
                                    },
                                    onDeleteClicked = {
                                    },
                                    onCircularImageClick = {
                                        bpcDidiListViewModel.showDidiImageDialog.value=true
                                        bpcDidiListViewModel.dialogDidiEntity.value=it
                                    }
                                )
                                if (index < newFilteredTolaDidiList.keys.size - 1) {
                                    Divider(
                                        color = borderGreyLight,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 22.dp,
                                            bottom = 1.dp
                                        )
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(newFilteredDidiList.sortedByDescending { it.id }) { _, didi ->
                                DidiItemCardForPat(
                                    navController = navController,
                                    didi = didi,
                                    expanded = true,
                                    modifier = modifier,
                                    answerDao = bpcDidiListViewModel.answerDao,
                                    questionListDao = bpcDidiListViewModel.questionListDao,
                                    onExpendClick = { _, _ ->

                                        },
                                        prefRepo = bpcDidiListViewModel.prefRepo,
                                        onNotAvailableClick = { didiEntity ->
                                            bpcDidiListViewModel.setDidiAsUnavailable(didiEntity.id)
                                        },
                                        onItemClick = {
                                        },
                                     onCircularImageClick = {
                                         bpcDidiListViewModel.showDidiImageDialog.value=true
                                         bpcDidiListViewModel.dialogDidiEntity.value=it
                                     }
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
                if (didis.isNotEmpty() && bpcDidiListViewModel.pendingDidiCount.value == 0) {
                    DoubleButtonBox(
                        modifier = Modifier
                            .constrainAs(bottomActionBox) {
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                            }
                            .onGloballyPositioned { coordinates ->
                                bottomPadding = with(localDensity) {
                                    coordinates.size.height.toDp()
                                }
                            },

                        positiveButtonText = stringResource(id = R.string.review_and_submit_button_text),
                        negativeButtonRequired = false,
                        positiveButtonOnClick = {
                            bpcDidiListViewModel.getPatStepStatus(stepId = stepId) {
                                navController.navigate("bpc_pat_survey_summary/$stepId/$it")
                            }
                        },
                        negativeButtonOnClick = {/*Nothing to do here*/ }
                    )
                }
            }
        }
    }
}