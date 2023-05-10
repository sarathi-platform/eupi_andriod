package com.patsurvey.nudge.activities.ui.socialmapping

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.NetworkBanner
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.model.dataModel.DidiRankedModel
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION
import com.patsurvey.nudge.utils.MEDIUM_STRING
import com.patsurvey.nudge.utils.POOR_STRING
import com.patsurvey.nudge.utils.RICH_STRING

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SocialMappingScreen(
    navController: NavController,
    viewModel: SocialMappingListViewModel,
    modifier: Modifier
){
    val cards by viewModel.cards.collectAsState()
    val expandedCardIds by viewModel.expandedCardIdsList.collectAsState()
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
            }
            .padding(top = dimensionResource(id = R.dimen.dp_24))
            .padding(horizontal = dimensionResource(id = R.dimen.dp_10))
        ) {

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.social_mapping),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.dp_6))
                )
//                SearchWithFilterView(stringResource(id = R.string.search_didis)){
//                    //OnFilterSelected Clicked
//                }

                Text(
                    text = stringResource(id = R.string.count_didis_pending,0),
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.dp_6))
                )
                Scaffold(backgroundColor = Color.White) { paddingValues ->
                    LazyColumn(Modifier.padding(paddingValues)) {
                        items(cards, DidiRankedModel::id) { card ->
                            ExpandableCard(
                                didiRankedModel = card,
                                onCardArrowClick = { viewModel.onCardArrowClicked(card.id) },
                                expanded = expandedCardIds.contains(card.id),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    didiRankedModel: DidiRankedModel,
    onCardArrowClick: () -> Unit,
    expanded: Boolean,
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = updateTransition(transitionState, label = "transition")
    val cardBgColor by transition.animateColor({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "bgColorTransition") {
        Color.White
    }
    val cardPaddingHorizontal by transition.animateDp({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "paddingTransition") {
        dimensionResource(id = R.dimen.dp_5)
    }
    val cardElevation by transition.animateDp({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "elevationTransition") {
        dimensionResource(id = R.dimen.dp_5)
    }
    val cardRoundedCorners by transition.animateDp({
        tween(
            durationMillis = EXPANSTION_TRANSITION_DURATION,
            easing = FastOutSlowInEasing
        )
    }, label = "cornersTransition") {
        dimensionResource(id = R.dimen.dp_6)
    }
    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (expanded) 0f else 180f
    }
    val context = LocalContext.current
    val contentColour = remember {
        Color(ContextCompat.getColor(context, R.color.placeholder_color))
    }

    Card(
        backgroundColor = cardBgColor,
        contentColor = contentColour,
        elevation = cardElevation,
        shape = RoundedCornerShape(cardRoundedCorners),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = cardPaddingHorizontal,
                vertical = dimensionResource(id = R.dimen.dp_8)
            )
    ) {

        Column(modifier = Modifier
            .fillMaxWidth()) {
            Surface(modifier = Modifier
                .fillMaxWidth(),
                color = Color.White,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.dp_13))
                        .padding(top = dimensionResource(id = R.dimen.dp_10)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,) {
                        Card(modifier = Modifier
                            .width(dimensionResource(id = R.dimen.dp_44))
                            .height(dimensionResource(id = R.dimen.dp_44)),
                            backgroundColor=colorResource(id = R.color.placeholder_color),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_50)),
                        ) {
                            GlideImage(
                                model = didiRankedModel.pic,
                                contentDescription = "Female PlaceHolder Icon",
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.dp_7)),
                            )
                        }

                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.dp_10)))

                        Column{
                            Text(
                                text = didiRankedModel.name,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start,
                            )
                            Row{
                                Text(
                                    text = stringResource(id = R.string.ranking_text),
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontFamily = NotoSans,
                                    textAlign = TextAlign.Start,
                                )
                                Text(
                                    text = didiRankedModel.rank,
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontFamily = NotoSans,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.dp_5))
                                )
                            }
                        }


                    }

                }
             // Arrow Icon
                Column(modifier = Modifier,
                    horizontalAlignment = Alignment.End) {
                    CardArrow(
                        degrees = arrowRotationDegree,
                        onClick = onCardArrowClick
                    )
                }

            }
          //Expandable Content
            Column {
                Box {
                    CardTitle(title = didiRankedModel.name)
                }
                ExpandableContent(visible = expanded, rank = didiRankedModel.rank)
            }
        }

    }
}

@Composable
fun CardArrow(
    degrees: Float,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        content = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                contentDescription = "Expandable Arrow",
                modifier = Modifier.rotate(degrees),
                tint=blueDark

            )
        }
    )
}

@Composable
fun CardTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun ExpandableContent(rank:String,
    visible: Boolean = true,
) {
    val isPoorSelected= remember {
     mutableStateOf((rank.equals(POOR_STRING,true)))
    }
    val isMediumSelected= remember {
        mutableStateOf((rank.equals(MEDIUM_STRING,true)))
    }
    val isRichSelected= remember {
        mutableStateOf((rank.equals(RICH_STRING,true)))
    }
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.dp_8))) {
            ConstraintLayout(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
            ) {
                val (main_card) = createRefs()
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(main_card) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                    .padding(
                        start = dimensionResource(id = R.dimen.dp_10),
                        end = dimensionResource(id = R.dimen.dp_10),
                        bottom = dimensionResource(id = R.dimen.dp_10)
                    ),
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.height_50dp))
                        .border(
                            width = 0.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
                        )
                        .clickable {
                            isPoorSelected.value = true
                            isMediumSelected.value = false
                            isRichSelected.value = false
                        },
                        backgroundColor=if(isPoorSelected.value) blueDark else colorResource(id = R.color.grey_lighter),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.background(Color.Transparent)) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = POOR_STRING,
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.dp_7)),
                                colorFilter = ColorFilter.tint(if(isPoorSelected.value) Color.White else Color.Black)
                                )
                            Text(
                                text = stringResource(id = R.string.poor_text),
                                color = if(isPoorSelected.value) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.dp_5))
                            )

                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.height_50dp))
                        .border(
                            width = 0.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
                        )
                        .clickable {
                            isPoorSelected.value = false
                            isMediumSelected.value = true
                            isRichSelected.value = false
                        },
                        backgroundColor= if(isMediumSelected.value) blueDark else colorResource(id = R.color.grey_lighter),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.background(Color.Transparent)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = stringResource(id = R.string.medium_text),
                                colorFilter = ColorFilter.tint(if(isMediumSelected.value) Color.White else Color.Black)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = stringResource(id = R.string.medium_text),
                                colorFilter = ColorFilter.tint(if(isMediumSelected.value) Color.White else Color.Black)
                            )
                            Text(
                                text = stringResource(id = R.string.medium_text),
                                color = if(isMediumSelected.value) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )

                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.height_50dp))
                            .border(
                                width = 0.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                isPoorSelected.value = false
                                isMediumSelected.value = false
                                isRichSelected.value = true
                            },
                        backgroundColor = if (isRichSelected.value) blueDark else colorResource(id = R.color.grey_lighter),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.background(Color.Transparent)
                           ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = stringResource(id = R.string.rich_text),
                                colorFilter = ColorFilter.tint(if(isRichSelected.value) Color.White else Color.Black)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = stringResource(id = R.string.rich_text),
                                colorFilter = ColorFilter.tint(if(isRichSelected.value) Color.White else Color.Black))
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = stringResource(id = R.string.rich_text),
                                colorFilter = ColorFilter.tint(if(isRichSelected.value) Color.White else Color.Black)
                            )
                            Text(
                                text = stringResource(id = R.string.rich_text),
                                color = if(isRichSelected.value) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )

                        }
                    }
                }
            }
        }
    }
}