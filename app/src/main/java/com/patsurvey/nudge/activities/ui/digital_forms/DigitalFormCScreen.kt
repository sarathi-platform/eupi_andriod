package com.patsurvey.nudge.activities.ui.digital_forms

import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.*
import java.io.File


@Composable
fun DigitalFormCScreen(
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
        mutableStateOf(
            File(
                "${
                    context.getExternalFilesDir(
                        Environment.DIRECTORY_DOCUMENTS
                    )?.absolutePath
                }"
            )
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
                    text = "Digital Form C",
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
                                    PREF_PAT_COMPLETION_DATE,
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
                                text = didiList.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }.size.toString(),
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
                    ConstraintLayout() {
                        val (listBox, bottomBox) = createRefs()
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .constrainAs(listBox) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    bottom.linkTo(bottomBox.top)
                                    height = Dimension.fillToConstraints
                                }
                        ) {
                            items(didiList.filter { it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal }) { card ->
                                DidiVillageItem(card)
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(bottomBox) {
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                            }
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Column() {
                                Divider(
                                    color = borderGreyLight,
                                    thickness = 1.dp,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.endorsement_badge),
                                        contentDescription = null
                                    )
                                    Column() {
                                        Text(
                                            text = "Link Form C",
                                            style = TextStyle(
                                                fontFamily = NotoSans,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp,
                                                textDecoration = TextDecoration.Underline
                                            ),
                                            color = textColorDark,
                                            modifier = Modifier.clickable {
                                                navController.navigate("image_viewer/$FORM_C")
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Link Form D",
                                            style = TextStyle(
                                                fontFamily = NotoSans,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp,
                                                textDecoration = TextDecoration.Underline
                                            ),
                                            color = textColorDark,
                                            modifier = Modifier.clickable {
                                                navController.navigate("image_viewer/$FORM_D")
                                            }
                                        )
                                    }
                                }

                            }

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
                            }", "digital_form_c_${viewModel.prefRepo.getSelectedVillage().name}.pdf"
                        )
                        viewModel.generateFormCPdf(context) { formGenerated, formPath ->
                            Log.d("DigitalFormBScreen", "Digital Form C Downloaded")
                            val fileUri = uriFromFile(context, pdfFile)
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "application/pdf"
                            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
                            ContextCompat.startActivity(
                                context,
                                Intent.createChooser(shareIntent, "Share Form C"),
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
                        },
                        showLoader = showLoader.value,
                    ) {
                        if (formPathState.value.isFile) {
                            navController.navigate("pdf_viewer/digital_form_c_${viewModel.prefRepo.getSelectedVillage().name}.pdf")
                        } else {
                            showLoader.value = true
                            viewModel.generateFormCPdf(context) { formGenerated, formPath ->
                                if (formGenerated) {
                                    showToast(context, "Digital Form C Downloaded")
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
                    text = stringResource(id = if (fromScreen == "") R.string.finish else R.string.done_text),
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
