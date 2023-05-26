package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
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
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.home.PatScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.*
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

    LaunchedEffect(key1 = Unit){
        questionList.forEach {
            val answer = answerList.find { it.questionId == it.questionId }
        }

    }

    BackHandler() {
        navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
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
                    prefRepo = patSectionSummaryViewModel.prefRepo,
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
                    text = stringResource(id = R.string.result_summary),
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

                PatSummeryScreenDidiDetailBox(
                    modifier = Modifier,
                    screenHeight = screenHeight,
                    didi = didi.value
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                )

                Text(
                    text = stringResource(R.string.summary_text),
                    style = TextStyle(
                        color = textColorDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = NotoSans
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(questionList.sortedBy { it.order }) { index, question ->
                        val answer = answerList.find { it.questionId == question.questionId }
                       SectionOneSummeryItem(index = index+1, question = question, answer = answer!!)
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

            positiveButtonText = if(patSectionSummaryViewModel.isYesSelected.value) stringResource(id = R.string.complete_pat_survey) else stringResource(id = R.string.complete_section_1_text),
            negativeButtonRequired = false,
            positiveButtonOnClick = {
                patSectionSummaryViewModel.setPATSection1Complete(didi.value.id,PatSurveyStatus.COMPLETED.ordinal)
                if(patSectionSummaryViewModel.isYesSelected.value){
                    patSectionSummaryViewModel.setPATSurveyComplete(didi.value.id,PatSurveyStatus.COMPLETED.ordinal)
                    navController.popBackStack("pat_screens/${patSectionSummaryViewModel.prefRepo.getSelectedVillage().id}/${patSectionSummaryViewModel.prefRepo.getStepId()}", inclusive = false)
                }else{
                    patSectionSummaryViewModel.setPATSection2Complete(didi.value.id,PatSurveyStatus.INPROGRESS.ordinal)
                    patSectionSummaryViewModel.setPATSurveyComplete(didi.value.id,PatSurveyStatus.INPROGRESS.ordinal)
                    navController.navigate("yes_no_question_screen/${didi.value.id}/$TYPE_INCLUSION")
                }
            },
            negativeButtonOnClick = {/*Nothing to do here*/ }
        )
    }
}

@Composable
fun PatSummeryScreenDidiDetailBox(
    modifier: Modifier = Modifier,
    screenHeight: Int,
    didi: DidiEntity
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
                painter = if (didi.localPath.isNotEmpty()) rememberImagePainter(
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
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = didi.name,
                color = textColorDark,
                fontFamily = NotoSans,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home_icn),
                    contentDescription = "home image",
                    tint = Color.Black
                )
                Text(
                    text = didi.cohortName,
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

@Preview(showBackground = true)
@Composable
fun PatSummeryScreenDidiDetailBoxPreview(){
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
   val didi=DidiEntity(0,"Didi1","Hno 123", BLANK_STRING,"Husband", castId = 0,
       castName = "OBC", cohortId = 0, cohortName = "Tola1", createdDate = 457874, localPath = BLANK_STRING, villageId = 40,
       wealth_ranking = "POOR", needsToPost = false, modifiedDate = 654789, needsToPostRanking = false, patSurveyProgress = 0, shgFlag = SHGFlag.NOT_MARKED.value)
    PatSummeryScreenDidiDetailBox(modifier = Modifier,screenHeight,didi)
}

@Composable
fun SectionOneSummeryItem(
    modifier: Modifier = Modifier,
    index: Int,
    question: QuestionEntity,
    answer: SectionAnswerEntity
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Icon(
                painter = painterResource(id = R.drawable.home_icn),
                contentDescription = null,
                tint = textColorDark,
                modifier = Modifier
                    .size(25.dp)
            )
            Text(
                text = "$index. ${question.questionDisplay}.",
                style = TextStyle(
                    color = textColorDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans
                ),
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = "${answer.answerValue}",
                style = TextStyle(
                    color = if (answer.optionValue == 1) greenOnline else redNoAnswer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans
                ),
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
            )
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
