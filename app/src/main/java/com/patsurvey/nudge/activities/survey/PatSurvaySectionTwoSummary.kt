package com.patsurvey.nudge.activities.survey

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import com.patsurvey.nudge.activities.PatSectionSummaryViewModel
import com.patsurvey.nudge.activities.ui.socialmapping.ShowDialog
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.navigation.home.BpcDidiListScreens
import com.patsurvey.nudge.navigation.home.PatScreens
import com.patsurvey.nudge.utils.*
import java.io.File

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PatSurvaySectionTwoSummaryScreen(
    navController: NavHostController,
    modifier: Modifier,
    patSectionSummaryViewModel: PatSectionSummaryViewModel,
    didiId: Int
) {

    LaunchedEffect(key1 = true) {
        patSectionSummaryViewModel.sectionType.value= TYPE_INCLUSION
        patSectionSummaryViewModel.setDidiDetailsFromDb(didiId)
    }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val localDensity = LocalDensity.current
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }
    val didi = patSectionSummaryViewModel.didiEntity.collectAsState()
    val inclusionQuesList = patSectionSummaryViewModel.inclusionQuestionList.collectAsState()
    val answerSummeryList by patSectionSummaryViewModel.answerSummeryList.collectAsState()

    val showDialog = remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {
        val (bottomActionBox, mainBox) = createRefs()

        if (showDialog.value){
            ShowDialog(title = stringResource(R.string.confirmation_dialog_titile), message = stringResource(R.string.didi_pat_comption_dialog).replace("{DIDI_NAME}", didi.value.name), setShowDialog = {
                showDialog.value = it
            }, positiveButtonClicked = {

                patSectionSummaryViewModel.setPATSection2Complete(didi.value.id,PatSurveyStatus.COMPLETED.ordinal)
                patSectionSummaryViewModel.setPATSurveyComplete(didi.value.id,PatSurveyStatus.COMPLETED.ordinal)
                if(patSectionSummaryViewModel.prefRepo.isUserBPC()){

                    navController.popBackStack(BpcDidiListScreens.BPC_DIDI_LIST.route, inclusive = false)
                }else navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
            })
        }


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
                    text = stringResource(id = R.string.pat_survey_section_2),
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

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(answerSummeryList) { index, answer ->
                        val question = inclusionQuesList.value.find { it.questionId == answer.questionId }
                      SectionTwoSummeryItem(index = index, quesSummery = answer.questionId.let {
                          patSectionSummaryViewModel.getQuestionSummary(
                              it
                          )
                      } ,
                          answerValue = answer.questionId?.let {
                              answer.optionId?.let { it1 ->
                                  patSectionSummaryViewModel.getOptionForLanguage(
                                      it, it1
                                  )
                              }
                          } ?: BLANK_STRING,
                          questionType =  answer.type,
                          questionImageUrl=question?.questionImageUrl?: BLANK_STRING,
                          questionFlag = answer.questionFlag?: QUESTION_FLAG_WEIGHT)
                      {
                          patSectionSummaryViewModel.prefRepo.saveQuestionScreenOpenFrom(PageFrom.SUMMARY_TWO_PAGE.ordinal)
                          navController.navigate("yes_no_question_screen/${didiId}/$TYPE_INCLUSION/$it")
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

            positiveButtonText =stringResource(id = R.string.complete_pat_survey),
            negativeButtonRequired = false,
            positiveButtonOnClick = {
                                    showDialog.value = true
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
                    painter = painterResource(id = R.drawable.pat_sample_icon),
                    contentDescription = "home image",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
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
   val didi=DidiEntity(0,0,"Didi1","Hno 123", BLANK_STRING,"Husband", castId = 0,
       castName = "OBC", cohortId = 0, cohortName = "Tola1", createdDate = 457874, localPath = BLANK_STRING, villageId = 40,
       wealth_ranking = "POOR", needsToPost = false, modifiedDate = 654789, needsToPostRanking = false, patSurveyStatus = 0, shgFlag = SHGFlag.NOT_MARKED.value)
    PatSummeryScreenDidiDetailBox(modifier = Modifier,screenHeight,didi)
}
@Preview(showBackground = true)
@Composable
fun SectionTwoSummeryItemPreview(){
    SectionTwoSummeryItem(modifier = Modifier,0,"New Summery","New Answer Value",QuestionType.Numeric_Field.name,
        BLANK_STRING,QUESTION_FLAG_WEIGHT, onCardClick = {})
}



@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SectionTwoSummeryItem(
    modifier: Modifier = Modifier,
    index: Int,
    quesSummery:String,
    answerValue:String,
    questionType: String,
    questionImageUrl: String,
    questionFlag:String,
    onCardClick:(Int)->Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Row(Modifier.fillMaxWidth().clickable {
//           onCardClick(index)
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
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.white_background),
                    contentDescription = "home image",
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                )
            }

            var summaryText = "$answerValue."
            if(questionType.equals(QuestionType.Numeric_Field.name,true)){
                summaryText = if(questionFlag.equals(QUESTION_FLAG_WEIGHT,true)){
                    LocalContext.current.getString(R.string.total_productive_asset_value,answerValue)
                }else answerValue
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("${index + 1}. $quesSummery:  ")
                    }
                    withStyle(
                        SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("$summaryText")
                    }
                },
                style = TextStyle(
                    color = textColorDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans
                ),
                textAlign = TextAlign.Start,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .weight(1f)
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
