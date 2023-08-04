package com.patsurvey.nudge.activities.ui.digital_forms

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
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
import com.patsurvey.nudge.activities.ui.socialmapping.ShowDialog
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.ARG_FROM_SETTING
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_B_PDF_NAME
import com.patsurvey.nudge.utils.OutlineButtonCustom
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.changeMilliDateToDate
import com.patsurvey.nudge.utils.openSettings
import com.patsurvey.nudge.utils.showToast
import com.patsurvey.nudge.utils.uriFromFile
import java.io.File

@Composable
fun DigitalFormBScreen(
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
                    saveState = false
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.generateFormBPdf(context) { formGenerated, formPath -> }
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

    val shouldRequestPermission = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = context) {
        viewModel.requestStoragePermission(context as Activity, viewModel) {
            shouldRequestPermission.value = true
        }
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

                if (shouldRequestPermission.value) {
                    ShowDialog(
                        title = stringResource(R.string.permission_required_prompt_title),
                        message = stringResource(R.string.storage_permission_dialog_prompt_message),
                        setShowDialog = {
                            shouldRequestPermission.value = it
                        }
                    ) {
                        openSettings(context)
                    }
                }

                Text(
                    text = stringResource(id = R.string.digital_form_b_title),
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
                                text = changeMilliDateToDate(viewModel.prefRepo.getPref(
                                    PREF_PAT_COMPLETION_DATE_ +viewModel.prefRepo.getSelectedVillage().id,0L)) ?: BLANK_STRING,
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
                                text = stringResource(id = R.string.total_ultra_poor_didis) + ":",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.dp_5))
                            )
                            Text(
                                text = didiList.filter { it.forVoEndorsement == 1 && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal  && !it.patEdit }.size.toString(),
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
                        items( didiList.filter { it.forVoEndorsement == 1 && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal  && !it.patEdit }) { card ->
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
                    OutlineButtonCustom(
                        buttonTitle = stringResource(id = R.string.share_button_text),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        val pdfFile = File(
                            "${
                                context.getExternalFilesDir(
                                    Environment.DIRECTORY_DOCUMENTS
                                )?.absolutePath
                            }", "${FORM_B_PDF_NAME}_${viewModel.prefRepo.getSelectedVillage().id}.pdf"
                        )
                        viewModel.generateFormBPdf(context) { formGenerated, formPath ->
                            Log.d("DigitalFormBScreen", "Digital Form B Downloaded")
                            val fileUri = uriFromFile(context, pdfFile)
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "application/pdf"
                            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
                            ContextCompat.startActivity(
                                context,
                                Intent.createChooser(shareIntent, "Share Form B"),
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
                            navController.navigate("pdf_viewer/${FORM_B_PDF_NAME}_${viewModel.prefRepo.getSelectedVillage().id}.pdf")
                        } else {
                            showLoader.value = true
                            viewModel.generateFormBPdf(context) { formGenerated, formPath ->
                                if (formGenerated) {
//                                    showToast(context, context.getString(R.string.digital_form_b_downloded))
                                    formPath?.let {
                                        formPathState.value = it
                                    }
                                    showLoader.value = false
                                } else {
                                    showToast(
                                        context,
                                        context.getString(R.string.something_went_wrong_unable_to_download_form)
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
                                saveState = false
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
                        .padding(bottom = dimensionResource(id = R.dimen.dp_6), top = 3.5.dp)
                )
            }
        }
    }
}
