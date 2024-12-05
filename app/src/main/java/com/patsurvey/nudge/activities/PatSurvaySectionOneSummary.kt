package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.borderGrey
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.brownDark
import com.patsurvey.nudge.activities.ui.theme.brownLoght
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.redNoAnswer
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.navigation.selection.BpcDidiListScreens
import com.patsurvey.nudge.navigation.selection.PatScreens
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.EXTENSION_WEBP
import com.patsurvey.nudge.utils.ExclusionType
import com.patsurvey.nudge.utils.ForVOEndorsementType
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
import com.patsurvey.nudge.utils.getImagePath
import com.patsurvey.nudge.utils.showDidiImageDialog
import java.io.File

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PatSurvaySectionSummaryScreen(
    navController: NavHostController,
    modifier: Modifier,
    isOnline: Boolean = true,
    patSectionSummaryViewModel: PatSectionSummaryViewModel,
    didiId: Int
) {

    LaunchedEffect(key1 = true) {
        patSectionSummaryViewModel.updatePATEditAndStepStatus(didiId)
        patSectionSummaryViewModel.setDidiDetailsFromDb(didiId)
        patSectionSummaryViewModel.getQuestionAnswerListForSectionOne(didiId)
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val localDensity = LocalDensity.current

    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }
    val didi = patSectionSummaryViewModel.didiEntity.collectAsState()
    val questionList by patSectionSummaryViewModel.questionList.collectAsState()
    val answerList by patSectionSummaryViewModel.answerList.collectAsState()

    val showPatCompletion = remember {
        mutableStateOf(false)
    }

    if(showPatCompletion.value) {
        BackHandler {
            navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
        }
    }else{
        BackHandler {
            if(patSectionSummaryViewModel.didiEntity.value.section1Status != PatSurveyStatus.COMPLETED.ordinal) {
                if (patSectionSummaryViewModel.patSectionRepository.prefRepo.summaryScreenOpenFrom() == PageFrom.SUMMARY_ONE_PAGE.ordinal) {
                    if (patSectionSummaryViewModel.patSectionRepository.prefRepo.isUserBPC()) {
                        navController.navigate("bpc_yes_no_question_screen/${didi.value.id}/$TYPE_EXCLUSION/0")
                    } else {
                        navController.navigate("yes_no_question_screen/${didi.value.id}/$TYPE_EXCLUSION/0")
                    }
                }
                else navController.popBackStack()
            }else navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
        }
    }

    if(patSectionSummaryViewModel.showDidiImageDialog.value){
        patSectionSummaryViewModel.didiEntity.value?.let {
            showDidiImageDialog(didi = it){
                patSectionSummaryViewModel.showDidiImageDialog.value = false
            }
        }
    }

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
        ) {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                VOAndVillageBoxView(
                    prefRepo = patSectionSummaryViewModel.patSectionRepository.prefRepo,
                    modifier = Modifier.fillMaxWidth(),
                    startPadding = 0.dp
                )
                Text(
                    text = stringResource(id = R.string.pat_survey_section_1),
                    modifier = Modifier
                        .layoutId("sectionText"),
                    color = textColorDark,
                    style = buttonTextStyle.copy(lineHeight = 22.sp)
                )
                Text(
                    text = stringResource(id = R.string.summary_text),
                    modifier = Modifier
                        .layoutId("sectionText"),
                    color = textColorDark,
                    style = buttonTextStyle.copy(lineHeight = 22.sp)
                )

                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                        .fillMaxWidth()
                )

                PatSummeryScreenDidiDetailBoxForSectionOne(
                    modifier = Modifier,
                    screenHeight = screenHeight,
                    didi = didi.value
                ){
                    patSectionSummaryViewModel.showDidiImageDialog.value = true
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                )

                AnimatedVisibility(visible = showPatCompletion.value) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Text(
                            text = stringResource(R.string.pat_survey_completion_text),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                color = textColorDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans
                            )
                        )
                    }

                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(questionList.sortedBy { it.order }) { index, question ->
                        val answer = answerList.find { it.questionId == question.questionId }
                       SectionOneSummeryItem(index = index,
                           quesSummery = answer?.questionId?.let {
                               patSectionSummaryViewModel.getQuestionSummary(
                                   it
                               )
                           } ?: BLANK_STRING,
                           answerValue = answer?.questionId?.let {
                               answer.optionId?.let { it1 ->
                                   patSectionSummaryViewModel.getOptionForLanguage(
                                       it, it1, BLANK_STRING
                                   )
                               }
                           } ?: BLANK_STRING,
                           optionValue =  answer?.optionValue?:0,
                           isArrowVisible = isArrowVisible(patSectionSummaryViewModel,didi) /*if (patSectionSummaryViewModel.prefRepo.questionScreenOpenFrom() == PageFrom.NOT_AVAILABLE_STEP_COMPLETE_SUMMARY_PAGE.ordinal) true else (didi.value.patEdit && (patSectionSummaryViewModel.isPATStepComplete.value == StepStatus.INPROGRESS.ordinal))*/,
                           questionImageUrl =question.questionImageUrl?: BLANK_STRING ){

                           patSectionSummaryViewModel.patSectionRepository.prefRepo.saveQuestionScreenOpenFrom(PageFrom.SUMMARY_ONE_PAGE.ordinal)
                           if(patSectionSummaryViewModel.patSectionRepository.prefRepo.isUserBPC())
                               navController.navigate("bpc_single_question_screen/${didiId}/$TYPE_EXCLUSION/$index")
                           else navController.navigate("single_question_screen/${didiId}/$TYPE_EXCLUSION/$index")
                       }
                    }
                }
            }
        }

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

            positiveButtonText = if (patSectionSummaryViewModel.isYesSelected.value) {
                if (showPatCompletion.value)
                    stringResource(id = R.string.next)
                else
                    stringResource(id = R.string.complete_pat_survey)
            } else stringResource(id = R.string.complete_section_1_text),
            negativeButtonRequired = false,
            positiveButtonOnClick = {
                patSectionSummaryViewModel.setPATSection1Complete(
                    didi.value.id,
                    PatSurveyStatus.COMPLETED.ordinal
                )


                if (patSectionSummaryViewModel.isYesSelected.value) {
                    var exclusionType = ExclusionType.SIMPLE_EXCLUSION.ordinal

                    if(didi.value.section2Status !=0){
                        exclusionType = ExclusionType.EDIT_PAT_EXCLUSION.ordinal
                    }
                    patSectionSummaryViewModel.updateExclusionStatus(didi.value.id,exclusionType,
                        TYPE_EXCLUSION)
                    patSectionSummaryViewModel.updateVOEndorseAfterDidiRejected(didi.value.id,ForVOEndorsementType.REJECTED.ordinal)
                    if (showPatCompletion.value) {
                        patSectionSummaryViewModel.setPATSurveyComplete(
                            didi.value.id,
                            PatSurveyStatus.COMPLETED.ordinal
                        )
                        if(patSectionSummaryViewModel.patSectionRepository.prefRepo.isUserBPC()){
                            navController.popBackStack(BpcDidiListScreens.BPC_DIDI_LIST.route, inclusive = false)
                        }else navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
                    } else {
                        showPatCompletion.value = true
                    }
                    patSectionSummaryViewModel.savePATEvent(isExclusion = true)
                } else {
                    patSectionSummaryViewModel.updateExclusionStatus(didi.value.id,ExclusionType.NO_EXCLUSION.ordinal,
                        TYPE_EXCLUSION)
                    patSectionSummaryViewModel.savePATEvent()
                    if(patSectionSummaryViewModel.patSectionRepository.prefRepo.isUserBPC()){
                        navController.navigate("bpc_yes_no_question_screen/${didi.value.id}/$TYPE_INCLUSION/0")
                    }else navController.navigate("yes_no_question_screen/${didi.value.id}/$TYPE_INCLUSION/0")
                }
            },
            negativeButtonOnClick = {/*Nothing to do here*/ }
        )
    }
}

fun isArrowVisible(viewModel: PatSectionSummaryViewModel, didi: State<DidiEntity>):Boolean{
    Log.d("TAG", "isArrowVisible: ${viewModel.patSectionRepository.prefRepo.questionScreenOpenFrom()}  ::${didi.value.id} ::${didi.value.name} :: ${viewModel.isPATStepComplete.value}")
    return if (viewModel.patSectionRepository.prefRepo.questionScreenOpenFrom() == PageFrom.NOT_AVAILABLE_STEP_COMPLETE_SUMMARY_PAGE.ordinal)
        true
    else if(viewModel.patSectionRepository.prefRepo.isUserBPC() && viewModel.isBPCVerificationStepComplete.value == StepStatus.INPROGRESS.ordinal){
        true
    }else didi.value.patEdit && (viewModel.isPATStepComplete.value == StepStatus.INPROGRESS.ordinal)
}


@Composable
fun PatSummeryScreenDidiDetailBoxForSectionOne(
    modifier: Modifier = Modifier,
    screenHeight: Int,
    didi: DidiEntity?,
    onCircularImageClick:(DidiEntity) ->Unit
) {
    Box(
        modifier = Modifier
            .size((screenHeight / 4).dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                color = brownLoght,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 1.dp,
                color = borderGreyLight,
                shape = RoundedCornerShape(6.dp)
            )
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = if (didi?.localPath?.isNotEmpty()==true) rememberImagePainter(
                    Uri.fromFile(
                        File(
                            didi.localPath.split("|")[0]
                        )
                    )
                ) else painterResource(id = R.drawable.didi_icon),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height((screenHeight / 10).dp)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .border(
                        width = 5.dp,
                        color = brownDark,
                        shape = CircleShape

                    )
                    .clip(CircleShape)
                    .background(languageItemActiveBg)
                    .clickable {
                        if (didi != null) {
                            onCircularImageClick(didi)
                        }
                    }
            )
            Spacer(modifier = Modifier.height(4.dp))
            didi?.name?.let {
                Text(
                    text = it,
                    color = textColorDark,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                didi?.guardianName?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        ),
                        textAlign = TextAlign.Start,
                    )
                }
                didi?.address?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        ),
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PatSummeryScreenDidiDetailBoxPreview(){
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
   val didi=DidiEntity(0,"",0,"Didi1","Hno 123", "Dada1","Husband", castId = 0,
       castName = "OBC", cohortId = 0, cohortName = "Tola1", createdDate = 457874, localPath = BLANK_STRING, villageId = 40,
       wealth_ranking = "POOR", needsToPost = false, modifiedDate = 654789, needsToPostRanking = false, patSurveyStatus = 0, shgFlag = SHGFlag.NOT_MARKED.value, ableBodiedFlag = AbleBodiedFlag.NOT_MARKED.value)
    PatSummeryScreenDidiDetailBoxForSectionOne(modifier = Modifier,screenHeight,didi, onCircularImageClick = {})
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SectionOneSummeryItem(
    modifier: Modifier = Modifier,
    index: Int,
    questionImageUrl:String,
    quesSummery:String,
    answerValue: String,
    optionValue:Int,
    isArrowVisible:Boolean = false,
    onCardClick:(Int)->Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    if (isArrowVisible)
                        onCardClick(index)
                }, verticalAlignment = Alignment.CenterVertically) {
            if (questionImageUrl.isNotEmpty()){
            val quesImage: File? =
                questionImageUrl?.let { it1 ->
                    getImagePath(
                        LocalContext.current,
                        it1
                    )
                }
            if (quesImage?.extension.equals(EXTENSION_WEBP, true)) {
                GlideImage(
                    model = quesImage,
                    contentDescription = "Question Image",
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp),
                )
            } else {
                var imgBitmap: Bitmap? = null
                if (quesImage?.exists() == true) {
                    imgBitmap = BitmapFactory.decodeFile(quesImage.absolutePath)
                }
                if (quesImage?.exists() == true) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imgBitmap),
                        contentDescription = "home image",
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp),
                        colorFilter = ColorFilter.tint(textColorDark)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.white_background),
                        contentDescription = "home image",
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                    )
                }
            }
        }else {
                Image(
                    painter = painterResource(id = R.drawable.white_background),
                    contentDescription = "home image",
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                )
        }
            de.charlex.compose.material.HtmlText(
                text = "${index+1}. ${quesSummery}.",
                style = TextStyle(
                    color = textColorDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans,
                    textAlign = TextAlign.Start
                ),
                //textAlign = TextAlign.Start,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            )
            de.charlex.compose.material.HtmlText(
                text = "${answerValue}",
                style = TextStyle(
                    color = if (optionValue == 1) greenOnline else redNoAnswer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans,
                    textAlign = TextAlign.Start
                ),
               // textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start=2.dp)
            )

            if(isArrowVisible) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Forward Arrow",
                        tint = textColorDark,
                        modifier = Modifier.padding(5.dp)
                    )
            }
        }
        Divider(
            color = borderGrey,
            thickness = 1.dp,
            modifier = Modifier
                .padding(
                    vertical = 10.dp
                )
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SectionOneSummeryItemPreview(){
    SectionOneSummeryItem(
        index = 1,
        questionImageUrl = BLANK_STRING,
        quesSummery = "Question Summary",
        answerValue = "1",
        optionValue = 1,
        isArrowVisible = true,
        onCardClick = {}
    )
}
