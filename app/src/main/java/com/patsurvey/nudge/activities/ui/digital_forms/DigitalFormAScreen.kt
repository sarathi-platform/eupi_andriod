package com.patsurvey.nudge.activities.ui.digital_forms

import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.*
import java.io.File


@Composable
fun DigitalFormAScreen(
    navController: NavController,
    viewModel: DigitalFormViewModel,
    modifier: Modifier = Modifier,
    fromScreen: String = ""
) {
    val context = LocalContext.current
    val didiList by viewModel.didiDetailList.collectAsState()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    BackHandler() {
        if (fromScreen == ARG_FROM_SETTING)
            navController.popBackStack()
        else {
            navController.navigate(Graph.HOME) {
                popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                    inclusive = true
                }
            }
        }
    }

    val formPathState = remember {
        mutableStateOf(File(
            "${
                context.getExternalFilesDir(
                    Environment.DIRECTORY_DOCUMENTS
                )?.absolutePath
            }")
        )
    }


    val showLoader = remember {
        mutableStateOf(false)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {
        val (mainCard, buttonCard) = createRefs()
        Box(modifier = Modifier
            .constrainAs(mainCard) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(buttonCard.top)
                height = Dimension.fillToConstraints
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = "Digital Form A",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen.dp_10))
                )
                Card(
                    backgroundColor = Color.White,
                    contentColor = Color(
                        ContextCompat.getColor(
                            context,
                            R.color.placeholder_color
                        )
                    ),
                    elevation = dimensionResource(id = R.dimen.dp_5),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.dp_16))
                        .padding(bottom = dimensionResource(id = R.dimen.dp_10))

                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                            .padding(bottom = dimensionResource(id = R.dimen.dp_15))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(id = R.string.village_text) + ":",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.dp_10))
                            )
                            Text(
                                text = viewModel.prefRepo.getSelectedVillage().name,
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = dimensionResource(id = R.dimen.dp_10),
                                        start = dimensionResource(id = R.dimen.dp_5)
                                    )
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(id = R.string.participation_date_wealth_ranking) + ":",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.dp_5))
                            )
                            Text(
                                text = viewModel.prefRepo.getPref(
                                    PREF_WEALTH_RANKING_COMPLETION_DATE,
                                    ""
                                ) ?: "",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = dimensionResource(id = R.dimen.dp_5),
                                        start = dimensionResource(id = R.dimen.dp_5)
                                    )
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(id = R.string.vo_name) + ":",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.dp_5))
                            )
                            Text(
                                text = viewModel.prefRepo.getSelectedVillage().federationName,
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = dimensionResource(id = R.dimen.dp_5),
                                        start = dimensionResource(id = R.dimen.dp_5)
                                    )
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(id = R.string.total) + ":",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.dp_5))
                            )
                            Text(
                                text = didiList.size.toString(),
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = dimensionResource(id = R.dimen.dp_5),
                                        start = dimensionResource(id = R.dimen.dp_5)
                                    )
                            )
                        }

                    }

                }

                Card(
                    backgroundColor = Color.White,
                    contentColor = Color(
                        ContextCompat.getColor(
                            context,
                            R.color.placeholder_color
                        )
                    ),
                    elevation = dimensionResource(id = R.dimen.dp_5),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(id = R.dimen.dp_16),
                        )
                        .padding(bottom = 14.dp)
                        .height((screenHeight / 2).dp)
                ) {
                    // List of Didis with Details
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(didiList) { card ->
                            DidiVillageItem(card)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
//                    .padding(bottom = 70.dp)
                ) {
                    ButtonNegative(
                        buttonTitle = stringResource(id = R.string.share_button_text),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        isArrowRequired = false
                    ) {
                        val pdfFile = File(
                            "${
                                context.getExternalFilesDir(
                                    Environment.DIRECTORY_DOCUMENTS
                                )?.absolutePath
                            }", "${FORM_A_PDF_NAME}_${viewModel.prefRepo.getSelectedVillage().name}.pdf"
                        )
                        viewModel.generateFormAPdf(context) { formGenerated, formPath ->
                            Log.d("DigitalFormAScreen", "Digital Form A Downloaded")
                            val fileUri = uriFromFile(context, pdfFile)
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "application/pdf"
                            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
                            ContextCompat.startActivity(
                                context,
                                Intent.createChooser(shareIntent, "Share Form A"),
                                null
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlineButtonCustom(
                        modifier = Modifier
                            .background(white)
                            .weight(1f),
                        buttonTitle = if (formPathState.value.isFile) stringResource(id = R.string.view) else {
                            if (showLoader.value)
                                stringResource(id = R.string.downloading_button_text)
                            else
                                stringResource(R.string.download_button_text)
                        }, showLoader = showLoader.value,
                    ) {
                        if (formPathState.value.isFile) {
                            navController.navigate("pdf_viewer/${FORM_A_PDF_NAME}_${viewModel.prefRepo.getSelectedVillage().name}.pdf")
                        } else {
                            showLoader.value = true
                            viewModel.generateFormAPdf(context) { formGenerated, formPath ->
                                if (formGenerated) {
                                    showToast(context, "Digital Form A Downloaded")
                                    formPath?.let {
                                        formPathState.value = it
                                    }
                                    showLoader.value = false
                                } else {
                                    showToast(
                                        context,
                                        "Something went wrong, unable to download form."
                                    )
                                    showLoader.value = false
                                }
                            }
                        }
                    }
                }

            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .constrainAs(buttonCard) {
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Button(
                onClick = {
                    if (fromScreen == ARG_FROM_SETTING)
                        navController.popBackStack()
                    else {
                        navController.navigate(Graph.HOME) {
                            popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                                inclusive = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.button_height))
                    .background(Color.Transparent)
                    .padding(horizontal = dimensionResource(id = R.dimen.dp_16)),
                colors = ButtonDefaults.buttonColors(blueDark),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
            ) {
                Text(
                    text = stringResource(id = if (fromScreen == "") R.string.continue_text else R.string.done_text),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen.dp_6))
                )
            }
        }
    }
}

@Composable
fun DidiVillageItem(didiDetailsModel: DidiEntity) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = dimensionResource(id = R.dimen.dp_20))
            .padding(end = dimensionResource(id = R.dimen.dp_15))
            .padding(vertical = dimensionResource(id = R.dimen.dp_5))
    ) {

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = didiDetailsModel.name,
                    color = colorResource(id = R.color.text_didi_name_color),
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                )
                Image(
                    painter = painterResource(R.drawable.ic_completed_tick),
                    contentDescription = "completed",
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.dp_5))
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_home_24),
                    contentDescription = "Get Location",
                    modifier = Modifier,
                    tint = blueDark,

                    )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = didiDetailsModel.cohortName,
                    textAlign = TextAlign.Center,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = blueDark
                )
            }
        }
    }
}