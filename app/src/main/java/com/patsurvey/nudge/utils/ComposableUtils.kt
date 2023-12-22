package com.patsurvey.nudge.utils

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.patsurvey.nudge.R
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.AddDidiViewModel
import com.patsurvey.nudge.activities.CircularDidiImage
import com.patsurvey.nudge.activities.DidiItemCard
import com.patsurvey.nudge.activities.MainTitle
import com.patsurvey.nudge.activities.decoupledConstraintsForPatCard
import com.patsurvey.nudge.activities.navigateSocialToSummeryPage
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.black100Percent
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.greyBorder
import com.patsurvey.nudge.activities.ui.theme.greyTransparentColor
import com.patsurvey.nudge.activities.ui.theme.inprogressYellow
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.lightGrayTranslucent
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.textColorBlueLight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.activities.ui.theme.yellowBg
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun DidiItemCardForPat(
    navController: NavHostController,
    prefRepo: PrefRepo,
    didi: DidiEntity,
    expanded: Boolean,
    modifier: Modifier,
    answerDao: AnswerDao,
    isFromNotAvailableCard:Boolean?=false,
    isVoEndorsementComplete: Boolean?=false,
    questionListDao: QuestionListDao,
    onExpendClick: (Boolean, DidiEntity) -> Unit,
    onNotAvailableClick: (DidiEntity) -> Unit,
    onItemClick: (DidiEntity) -> Unit,
    onCircularImageClick: (DidiEntity) -> Unit
) {
    Log.d("TAG", "DidiItemCardForPatDetails: $isVoEndorsementComplete")


    val didiMarkedNotAvailable = remember {
        mutableStateOf(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal)
    }

    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (prefRepo
                        .getFromPage()
                        .equals(
                            ARG_FROM_PAT_SURVEY,
                            true
                        ) && didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal
                ) {
                    if (prefRepo.isUserBPC())
                        navController.navigate("bpc_pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_DIDI_LIST_SCREEN}")
                    else
                        navController.navigate("pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_DIDI_LIST_SCREEN}")
                } else {
                    onExpendClick(expanded, didi)
                }
            }
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints {
                val constraintSet = decoupledConstraintsForPatCard()
                ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
                    CircularDidiImage(
                        didi = didi,
                        modifier = Modifier.layoutId("didiImage")
                    ){
                        onCircularImageClick(didi)
                    }
                    Row(
                        modifier = Modifier
                            .layoutId("didiRow")
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = didi.name,
                            style = TextStyle(
                                color = textColorDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start
                            ),
                        )

                        if (prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)) {
                            if (didi.patSurveyStatus.equals(PatSurveyStatus.COMPLETED.ordinal)) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_completed_tick),
                                    contentDescription = "home image",
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(30.dp)
                                        .padding(5.dp)
                                        .layoutId("successImage")
                                )
                            }

                            if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                Text(
                                    text = stringResource(R.string.pat_inprogresee_status_text),
                                    style = smallTextStyle,
                                    color = inprogressYellow,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .layoutId("successImage")
                                )
                            }

                            if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                Text(
                                    text = stringResource(R.string.not_avaliable),
                                    style = smallTextStyle,
                                    color = textColorBlueLight,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .layoutId("successImage")
                                )
                            }
                        }
                    }

                    Text(
                        text = didi.guardianName,
                        style = TextStyle(
                            color = textColorBlueLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("homeImage")
                    )

                    Text(
                        text = didi.address,
                        style = TextStyle(
                            color = textColorBlueLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("houseNumber_1")
                    )
                }
            }

            if (prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)
            ) {
                Divider(
                    color = borderGreyLight,
                    thickness = 1.dp,
                    modifier = Modifier
                        .layoutId("divider")
                        .padding(vertical = 4.dp)
                )

                if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal ||
                    didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal ||
                    didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||
                    didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                ) {


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 16.dp)
                    ) {
                        ButtonNegativeForPAT(
                            buttonTitle = stringResource(id = R.string.not_avaliable),
                            isArrowRequired = false,
                            color = if (didiMarkedNotAvailable.value) blueDark else languageItemActiveBg,
                            textColor = if (didiMarkedNotAvailable.value) white else blueDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                                .weight(1f)
                                .background(
                                    if (didiMarkedNotAvailable.value
                                    ) blueDark else languageItemActiveBg
                                )
                        ) {
                            didiMarkedNotAvailable.value = true
                            onNotAvailableClick(didi)
//                            didiViewModel.setDidiAsUnavailable(didi.id)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        ButtonPositiveForPAT(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                                .weight(1f)
                                .background(
                                    if (didiMarkedNotAvailable.value
                                    ) languageItemActiveBg else blueDark
                                ),
                            buttonTitle = if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                                || didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal
                            )
                                stringResource(id = R.string.start_pat)
                            else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal
                                || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                            )
                                stringResource(id = R.string.continue_pat)
                            else "",
                            true,
                            color = if (!didiMarkedNotAvailable.value) blueDark else languageItemActiveBg,
                            textColor = if (!didiMarkedNotAvailable.value) white else blueDark,
                            iconTintColor = if (!didiMarkedNotAvailable.value) white else blueDark
                        ) {

                            validateDidiToNavigate(
                                didiId = didi.id,
                                prefRepo = prefRepo,
                                answerDao = answerDao,
                                questionListDao = questionListDao
                            ) { navigationValue ->
                                Log.d("TAG", "DidiItemCardForPat: $navigationValue ")
                                if (navigationValue == SummaryNavigation.SECTION_1_PAGE.ordinal) {
                                    prefRepo.saveSummaryScreenOpenFrom(PageFrom.SUMMARY_ONE_PAGE.ordinal)
                                    navigateSocialToSummeryPage(navController, 1, didi.id, prefRepo)

                                } else if (navigationValue == SummaryNavigation.SECTION_2_PAGE.ordinal) {
                                    prefRepo.saveSummaryScreenOpenFrom(PageFrom.SUMMARY_TWO_PAGE.ordinal)
                                    navigateSocialToSummeryPage(navController, 2, didi.id, prefRepo)

                                } else {
                                    if (didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal
                                        || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                                    ) {
                                        if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal) {
                                            didiMarkedNotAvailable.value = false
                                        }
                                        if (prefRepo.isUserBPC())
                                            navController.navigate("bcp_didi_pat_summary/${didi.id}")
                                        else
                                            navController.navigate("didi_pat_summary/${didi.id}")

                                    } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                        val quesIndex = 0
                                        prefRepo.saveQuestionScreenOpenFrom(PageFrom.DIDI_LIST_PAGE.ordinal)
                                        prefRepo.saveSummaryScreenOpenFrom(PageFrom.DIDI_LIST_PAGE.ordinal)
                                        if (didi.section1Status == 0 || didi.section1Status == 1) {
                                            if (prefRepo.isUserBPC())
                                                navController.navigate("bpc_yes_no_question_screen/${didi.id}/$TYPE_EXCLUSION/$quesIndex")
                                            else
                                                navController.navigate("yes_no_question_screen/${didi.id}/$TYPE_EXCLUSION/$quesIndex")
                                        }
                                        else if ((didi.section2Status == 0 || didi.section2Status == 1) && didi.patExclusionStatus == 0) {
                                            if (prefRepo.isUserBPC())
                                                navController.navigate("bpc_yes_no_question_screen/${didi.id}/$TYPE_INCLUSION/$quesIndex")
                                            else
                                                navController.navigate("yes_no_question_screen/${didi.id}/$TYPE_INCLUSION/$quesIndex")
                                        }
                                        else if (didi.section1Status == 2 && didi.patExclusionStatus == ExclusionType.SIMPLE_EXCLUSION.ordinal) {
                                            if (prefRepo.isUserBPC())
                                                navController.navigate("bpc_yes_no_question_screen/${didi.id}/$TYPE_EXCLUSION/$quesIndex")
                                            else
                                                navController.navigate(
                                                    "yes_no_question_screen/${didi.id}/$TYPE_EXCLUSION/$quesIndex"
                                                )
                                        }
                                        else if (didi.section1Status == 2 && didi.patExclusionStatus == ExclusionType.EDIT_PAT_EXCLUSION.ordinal) {
                                            if (prefRepo.isUserBPC())
                                                navController.navigate("bpc_yes_no_question_screen/${didi.id}/$TYPE_INCLUSION/$quesIndex")
                                            else
                                                navController.navigate(
                                                    "yes_no_question_screen/${didi.id}/$TYPE_INCLUSION/$quesIndex"
                                                )
                                        }
                                    }
                                }

                            }

                        }
                    }
                } else {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 20.dp)
                        .clickable {
                            if (prefRepo.isUserBPC())
                                navController.navigate("bpc_pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_DIDI_LIST_SCREEN}")
                            else
                                navController.navigate("pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_DIDI_LIST_SCREEN}")
                        }
                        .then(modifier),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.show),
                            style = smallTextStyleMediumWeight,
                            color = textColorDark,
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = blueDark,
                            modifier = Modifier
                                .absolutePadding(top = 4.dp, left = 2.dp)
                                .size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

fun validateDidiToNavigate(
    didiId: Int,
    answerDao: AnswerDao,
    prefRepo: PrefRepo,
    questionListDao: QuestionListDao,
    onNavigateToSummary: (Int) -> Unit
) {
    CoroutineScope(Dispatchers.IO + RetryHelper.exceptionHandler).launch {
        val questionExclusionAnswered =
            answerDao.getAnswerForDidi(didiId = didiId, actionType = TYPE_EXCLUSION)
        val questionInclusionAnswered =
            answerDao.getAnswerForDidi(didiId = didiId, actionType = TYPE_INCLUSION)
        val quesList = questionListDao.getAllQuestionsForLanguage(prefRepo.getAppLanguageId() ?: 2)
        val yesQuesCount = answerDao.fetchOptionYesCount(
            didiId = didiId,
            QuestionType.RadioButton.name,
            TYPE_EXCLUSION
        )
        val exclusiveQuesCount = quesList.filter { it.actionType == TYPE_EXCLUSION }.size
        val inclusiveQuesCount = quesList.filter { it.actionType == TYPE_INCLUSION }.size
        if (questionInclusionAnswered.isNotEmpty()) {
            if (inclusiveQuesCount == questionInclusionAnswered.size) {
                withContext(Dispatchers.Main) {
                    if (yesQuesCount > 0) {
                        onNavigateToSummary(SummaryNavigation.SECTION_1_PAGE.ordinal)
                    } else onNavigateToSummary(SummaryNavigation.SECTION_2_PAGE.ordinal)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onNavigateToSummary(SummaryNavigation.DIDI_CAMERA_PAGE.ordinal)
                }
            }
        } else {
            if (questionExclusionAnswered.isNotEmpty()) {
                if (exclusiveQuesCount == questionExclusionAnswered.size) {
                    withContext(Dispatchers.Main) {
                        onNavigateToSummary(SummaryNavigation.SECTION_1_PAGE.ordinal)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onNavigateToSummary(SummaryNavigation.DIDI_CAMERA_PAGE.ordinal)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    onNavigateToSummary(SummaryNavigation.DIDI_CAMERA_PAGE.ordinal)
                }
            }
        }
    }
}

@Composable
fun ShowDidisFromTola(
    navController:NavHostController,
    prefRepo: PrefRepo,
    didiTola: String,
    didiList: List<DidiEntity>,
    modifier: Modifier,
    expandedIds: List<Int>,
    answerDao: AnswerDao,
    questionListDao: QuestionListDao,
    addDidiViewModel: AddDidiViewModel?=null,
    onExpendClick: (Boolean, DidiEntity) -> Unit,
    onNavigate: (DidiEntity) -> Unit,
    onDeleteClicked: (DidiEntity) -> Unit,
    onCircularImageClick:(DidiEntity) ->Unit
) {
    Column(modifier = Modifier) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 16.dp, bottom = 10.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_icn),
                contentDescription = "home image",
                modifier = Modifier
                    .size(18.dp),
                colorFilter = ColorFilter.tint(textColorBlueLight)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = didiTola,
                style = TextStyle(
                    color = textColorDark,
                    fontSize = 16.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(end = 10.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = yellowBg,
                        shape = CircleShape
                    )
                    .background(
                        yellowBg,
                        shape = CircleShape
                    )
                    .padding(6.dp)
                    .size(24.dp)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${didiList.size}",
                    color = greenOnline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .absolutePadding(bottom = 3.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            didiList.forEachIndexed { index, didi ->
                if (prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)) {
                    DidiItemCardForPat(
                        navController = navController,
                        didi = didi,
                        expanded = expandedIds.contains(didi.id),
                        modifier = modifier,
                        answerDao = answerDao,
                        questionListDao = questionListDao,
                        onExpendClick = { _, _ ->

                        },
                        prefRepo = prefRepo,
                        onNotAvailableClick = { didi->
//                            didiViewModel.setDidiAsUnavailable(didi.id)
                        },
                        onItemClick = { didi ->
                            onNavigate(didi)
                        },
                        onCircularImageClick = { didiEntity ->
                            onCircularImageClick(didiEntity)
                        }
                    )
                } else {
                    if (addDidiViewModel != null) {
                        DidiItemCard(navController,
                            addDidiViewModel,
                            didi,
                            expandedIds.contains(didi.id),
                            modifier,
                            onExpendClick = { expand, didiDetailModel ->
                                onExpendClick(expand, didiDetailModel)
                            },
                            onItemClick = { didi ->
                                onNavigate(didi)
                            },
                            onDeleteClicked = { didi ->
                                onDeleteClicked(didi)
                            },
                            onCircularImageClick = { didiEntity ->
                                onCircularImageClick(didiEntity)
                            }
                            )
                    }
                }
            }
        }
    }
}

@Composable
fun showDidiImageDialog(didi: DidiEntity,onCloseClick:()->Unit){
    Dialog(onDismissRequest = { }, properties = DialogProperties(
        dismissOnClickOutside = true
    )) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
        ) {
                Column(
                    modifier = Modifier
                        .background(color = greyTransparentColor,
                            shape = RoundedCornerShape(6.dp)),
                    verticalArrangement = Arrangement.Center
                ) {

                    Box{
                        if (didi.localPath.isNotEmpty()) {
                            Image(
                                painter = rememberImagePainter(
                                    Uri.fromFile(
                                        File(
                                            didi.localPath.split("|")[0]
                                        )
                                    )
                                ),
                                contentDescription = "didi image",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                    .fillMaxSize()
                            )
                        } else {
                            Box(modifier = Modifier
                                .background(white)
                                .padding(10.dp)
                                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                .fillMaxSize()){
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                        .fillMaxSize()
                                        .background(color = yellowBg),
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.didi_icon),
                                        contentDescription = "Placeholder didi image",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .padding(30.dp)
                                            .align(Alignment.Center)
                                            .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                            .fillMaxSize()
                                    )
                                }
                            }

                        }
                        ConstraintLayout(modifier = Modifier
                            .fillMaxWidth()
                            .background(lightGrayTranslucent)) {
                            val (titleText, closeButton) = createRefs()
                            Text(
                                text = didi.name,
                                style = TextStyle(
                                    color = white,
                                    fontSize = 16.sp,
                                    fontFamily = NotoSans,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(horizontal = 5.dp)
                                    .wrapContentWidth()
                                    .constrainAs(titleText) {
                                        start.linkTo(parent.start)
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                    }
                            )

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "close camera",
                                tint = white,
                                modifier = Modifier
                                    .constrainAs(closeButton) {
                                        end.linkTo(parent.end)
                                        top.linkTo(parent.top)
                                    }
                                    .width(28.dp)
                                    .height(28.dp)
                                    .padding(3.dp)
                                    .clickable {
                                        onCloseClick()
                                    }
                            )


                        }

                    }
                }


        }
    }
}

@Composable
fun showCustomDialog(
    title:String,
    message:String,
    positiveButtonTitle : String ?=EMPTY_STRING,
    negativeButtonTitle : String ?=EMPTY_STRING,
    onPositiveButtonClick:()->Unit,
    onNegativeButtonClick:()->Unit){
    Dialog(onDismissRequest = {  }, properties = DialogProperties(
        dismissOnClickOutside = false
    )) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .background(color = white, shape = RoundedCornerShape(6.dp)),
                ) {
                    Column(Modifier.padding(vertical = 16.dp, horizontal = 16.dp),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if(!title.isNullOrEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                            ) {
                                MainTitle(
                                    title,
                                    Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    align = TextAlign.Center
                                )
                            }
                            Divider(thickness = 1.dp, color = greyBorder)
                        }
                        Text(
                            text = message,
                            style = TextStyle(
                                color = black100Percent,
                                fontSize = 16.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Normal,
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .wrapContentWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {

                                    if(!negativeButtonTitle.isNullOrEmpty()) {
                                        ButtonNegative(
                                            buttonTitle = stringResource(id = R.string.cancel_tola_text),
                                            isArrowRequired = false,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            onNegativeButtonClick()
                                        }

                                }else{
                                    Spacer(modifier = Modifier.weight(2f))
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                positiveButtonTitle?.let {
                                    if(!it.isNullOrEmpty()) {
                                        ButtonPositive(
                                            buttonTitle = it,
                                            isArrowRequired = false,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(vertical = 2.dp)
                                        ) {
                                            onPositiveButtonClick()
                                        }
                                    }
                                }

                            }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DidiImagePreview(){
   
      val didi=  DidiEntity(
            id = 0,
            name = "Didi",
            address = "",
            guardianName = "",
            relationship = "",
            castId = 0,
            castName = "",
            cohortId = 0,
            cohortName = "",
            villageId = 0,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis(),
            shgFlag = SHGFlag.NOT_MARKED.value,
            ableBodiedFlag = AbleBodiedFlag.NOT_MARKED.value
        )
    showDidiImageDialog(didi = didi, onCloseClick = {})
    
}

@Preview(showBackground = true)
@Composable
fun showCustomDialogPreview(){
    showCustomDialog(
        "Main Title",
        message = "New Message You are submitting the wealth ranking forYou are submitting the wealth ranking for",
        negativeButtonTitle = "Cancel",
        positiveButtonTitle = "Exit",
        onNegativeButtonClick = {},
        onPositiveButtonClick = {}
    )
}


