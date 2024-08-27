package com.nrlm.baselinesurvey.ui.common_components.common_setting


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nrlm.baselinesurvey.EXPANSTION_TRANSITION_DURATION
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.ToolbarComponent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.newMediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.textColorDark50
import com.nrlm.baselinesurvey.ui.theme.white
import com.nudge.core.UPCM_USER
import com.nudge.core.model.SettingOptionModel
import com.sarathi.dataloadingmangement.model.uiModel.ActivityFormUIModel

@Composable
fun CommonSettingScreen(
    userType: String,
    title:String,
    versionText:String,
    optionList:List<SettingOptionModel>,
    expanded: Boolean,
    onBackClick:()->Unit,
    activityForm: List<ActivityFormUIModel>,
    isLoaderVisible:Boolean=false,
    onItemClick:(Int,SettingOptionModel)->Unit,
    isScreenHaveLogoutButton:Boolean=true,
    onParticularFormClick: (Int) -> Unit,
    onLogoutClick:()->Unit
){

    val formList = mutableListOf<String>()
    if (userType != UPCM_USER) {
        formList.add(stringResource(R.string.digital_form_a_title))
        formList.add(stringResource(R.string.digital_form_b_title))
        formList.add(stringResource(R.string.digital_form_c_title))
    } else {
        if (activityForm.isNotEmpty()) {
            activityForm.forEach {
                formList.add("${it.missionName} - ${stringResource(R.string.form_e)}")
            }
        }
    }


    Scaffold(
        backgroundColor = white,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ToolbarComponent(
                title = title,
                modifier = Modifier
            ) {
                onBackClick()

            }
        },
        bottomBar = {
            if(isScreenHaveLogoutButton) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                        .padding(vertical = dimensionResource(id = R.dimen.dp_15))
                ) {
                    Column {

                        Text(
                            text = versionText,
                            color = textColorDark50,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 0.dp, 0.dp, 10.dp),
                            textAlign = TextAlign.Center,
                            style = newMediumTextStyle
                        )
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.logout),
                            isArrowRequired = false,
                            isActive = true
                        ) {
                            onLogoutClick()
                        }
                    }

                }
            }
        }){
        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
        ) {
            val (mainBox, logoutButton, versionBox,circularLoader) = createRefs()

            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .constrainAs(mainBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }) {
                LazyColumn {
                    itemsIndexed(optionList) { index, item ->
                        CommonSettingCard(
                            title = item.title,
                            subTitle = item.subTitle,
                            formList = formList,
                            expanded = item.title == stringResource(id = R.string.forms) && expanded,
                            showArrow = item.title == stringResource(id = R.string.forms),
                            onClick = {
                                onItemClick(index,item)
                            },
                            onParticularFormClick = {formIndex->
                                onParticularFormClick(formIndex)
                            }

                        )
                    }
                }
            }

            if (isLoaderVisible) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clickable {

                    }
                    .constrainAs(circularLoader) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    }) {
                    CircularProgressIndicator(
                        color = blueDark,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommonSettingScreenPreview(){
 val list= listOf(
        SettingOptionModel(1,"Sync Now","new Datta",""),
        SettingOptionModel(2,"Forms","",""),
        SettingOptionModel(3,"Language","",""))
    CommonSettingScreen(
        userType = UPCM_USER,
        title = "Setting",
        versionText = "Version 978",
        list,
        onBackClick = {},
        onItemClick = { index, item -> },
        expanded = true,
        onLogoutClick = {},
        activityForm = listOf(),
        onParticularFormClick = { index -> })
}

@Preview(showBackground = true)
@Composable
fun CommonSettingCardPreview(){
    val formList = mutableListOf<String>()
    formList.add(stringResource(R.string.digital_form_a_title))
    formList.add(stringResource(R.string.digital_form_b_title))
    formList.add(stringResource(R.string.digital_form_c_title))
    CommonSettingCard(
        title = "title",
        subTitle = "subtitle",
        formList = formList,
        expanded = false,
        onParticularFormClick = {index->},
        onClick = {}
    )
}
@Composable
fun CommonSettingCard(
    title: String,
    subTitle: String,
    expanded: Boolean,
    formList: List<String>,
    showArrow: Boolean = false,
    onClick: () -> Unit,
    onParticularFormClick: (Int) -> Unit
) {

    val transition = updateTransition(expanded, label = "transition")

    val animateInt by transition.animateInt({
        tween(durationMillis = 10)
    }, label = "animate float") {
        if (it) 1 else 0
    }

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (it) 0f else -90f
    }

    Column(modifier = Modifier
        .background(Color.White)
        .fillMaxWidth()
        .clickable {
            onClick()
        }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.dp_20))
                .padding(vertical = dimensionResource(id = R.dimen.dp_15))
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = title,
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                if (showArrow) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down),
                        contentDescription = null,
                        tint = textColorDark,
                        modifier = Modifier.rotate(arrowRotationDegree)
                    )
                }
            }
            if (!subTitle.isNullOrEmpty()) {
                Text(
                    text = subTitle,
                    textAlign = TextAlign.Start,
                    fontSize = 13.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Normal,
                    color = black100Percent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        ExpandedSettingsList(
            modifier = Modifier,
            expanded = animateInt == 1,
            formList = formList
        ){
            onParticularFormClick(it)
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.dp_2))
                .background(borderGreyLight)
        )

    }
}

@Composable
fun ExpandedSettingsList(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    formList: List<String>,
    onParticularFormClick:(Int)->Unit
) {

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

    val interactionSource = remember { MutableInteractionSource() }

    AnimatedVisibility(
        visible = expanded,
        enter = enterTransition,
        exit = exitTransition,
        modifier = Modifier.then(modifier)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
        ) {
            if (formList.isNotEmpty()) {
                formList.forEachIndexed { index, name ->

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = name,
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            color = textColorDark,
                            modifier = Modifier
                                .padding(horizontal = 26.dp)
                                .padding(top = if (index == 0) 0.dp else 8.dp, bottom = 8.dp)
                                .fillMaxWidth()
                                .indication(
                                    interactionSource = interactionSource,
                                    indication = rememberRipple(
                                        bounded = true,
                                        color = Color.Black
                                    )
                                )
                                .clickable {
                                    onParticularFormClick(index)
                                })

                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                            contentDescription = null,
                            tint = textColorDark,
                            modifier = Modifier.rotate(-90f)
                        )
                    }
                    if (index < formList.size - 1)
                        Divider(
                            color = borderGreyLight,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 26.dp)
                        )
                }
            } else {
                Text(
                    text = stringResource(R.string.no_form_available_yet_text),
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorDark,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}