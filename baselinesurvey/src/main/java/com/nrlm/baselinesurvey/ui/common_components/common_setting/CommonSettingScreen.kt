package com.nrlm.baselinesurvey.ui.common_components.common_setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.model.datamodel.CommonSettingScreenConfig
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.ToolbarComponent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.newMediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.syncButtonBorderColor
import com.nrlm.baselinesurvey.ui.theme.syncButtonColor
import com.nrlm.baselinesurvey.ui.theme.syncCardBorderColor
import com.nrlm.baselinesurvey.ui.theme.textColorDark50
import com.nrlm.baselinesurvey.ui.theme.text_size_16_sp
import com.nrlm.baselinesurvey.ui.theme.white
import com.nudge.core.BLANK_STRING
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.ui.commonUi.LastSyncTimeView
import com.nudge.core.ui.theme.alpha_6
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_15_dp
import com.nudge.core.ui.theme.dimen_16_sp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_28_dp
import com.nudge.core.ui.theme.dimen_30_dp
import com.nudge.core.ui.theme.dimen_44_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.syncMediumTextStyle
import com.nudge.core.ui.theme.text_size_13
import com.nudge.core.ui.theme.uncheckedTrackColor

@Composable
fun CommonSettingScreen(
    settingScreenConfig: CommonSettingScreenConfig,
    isLoaderVisible: Boolean = false,
    onBackClick:()->Unit,
    onItemClick:(Int,SettingOptionModel)->Unit,
    onSyncDataClick: () -> Unit,
    onLogoutClick:()->Unit
){
    Scaffold(
        backgroundColor = white,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ToolbarComponent(
                title = settingScreenConfig.title,
                modifier = Modifier
            ) {
                onBackClick()

            }
        },
        bottomBar = {
            if (settingScreenConfig.isScreenHaveLogoutButton) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                        .padding(vertical = dimensionResource(id = R.dimen.dp_15))
                ) {
                    Column {

                        Text(
                            text = settingScreenConfig.versionText,
                            color = textColorDark50,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = dimen_10_dp),
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
                .padding(dimen_20_dp)
        ) {
            val (mainBox, syncCard, circularLoader) = createRefs()
            if (settingScreenConfig.isSyncEnable) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .constrainAs(syncCard) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(dimen_6_dp))
                            .border(width = dimen_1_dp, color = syncButtonBorderColor)
                            .background(syncButtonColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = true,
                                    color = Color.White
                                )

                            ) {
                                onSyncDataClick()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            Modifier
                                .padding(dimen_10_dp)
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.sync),
                                color = blueDark,
                                style = TextStyle(
                                    fontFamily = NotoSans,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = text_size_16_sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    LastSyncTimeView(
                        lastSyncTime = settingScreenConfig.lastSyncTime ?: 0L,
                        mobileNumber = settingScreenConfig.mobileNumber,
                        isShowPhoneNumber = false
                    ) { }
                    Spacer(modifier = Modifier.height(dimen_10_dp))
                }
            }
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .border(
                        if (settingScreenConfig.isItemCard) dimen_0_dp else
                            dimen_1_dp,
                        color = if (settingScreenConfig.isItemCard) white else syncCardBorderColor,
                        RoundedCornerShape(
                            if (settingScreenConfig.isItemCard) dimen_0_dp else dimen_10_dp
                        )
                    )
                    .constrainAs(mainBox) {
                        start.linkTo(parent.start)
                        top.linkTo(if (settingScreenConfig.isSyncEnable) syncCard.bottom else parent.top)
                        end.linkTo(parent.end)
                    },
            ) {
                if (settingScreenConfig.optionList.isNotEmpty()) {
                    LazyColumn {
                        itemsIndexed(settingScreenConfig.optionList) { index, item ->
                            SettingOptionCard(
                                settingOptionModel = item,
                                isItemCard = settingScreenConfig.isItemCard,
                                isLastLineShow = index < settingScreenConfig.optionList.size - 1,
                                onClick =
                                {
                                    onItemClick(index, item)
                                })
                        }
                    }
                } else {
                    if (settingScreenConfig.errorMessage.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(white),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(modifier = Modifier.align(Alignment.Center)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_no_forms),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(dimen_30_dp)
                                        .alpha(alpha_6),
                                    tint = blueDark
                                )
                                Spacer(modifier = Modifier.width(dimen_10_dp))

                                Text(
                                    text = settingScreenConfig.errorMessage,
                                    style = syncMediumTextStyle,
                                    color = blueDark,
                                    modifier = Modifier.alpha(alpha_6)
                                )
                            }

                        }
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
                            .size(dimen_28_dp)
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
     SettingOptionModel(
         1,
         "Sync Now",
         "",
         "",
         leadingIcon = R.drawable.ic_language,
         trailingIcon = R.drawable.ic_arrow_forward_ios_24
     ),
     SettingOptionModel(
         2,
         "Forms",
         "",
         "",
         leadingIcon = R.drawable.ic_language,
         trailingIcon = R.drawable.ic_share_icon
     ),
     SettingOptionModel(
         3,
         "Language",
         "",
         "",
         leadingIcon = R.drawable.ic_language,
         trailingIcon = R.drawable.ic_share_icon
     )
 )
    val commonSettingScreenConfig = CommonSettingScreenConfig(
        isSyncEnable = false,
        mobileNumber = "9862345078",
        lastSyncTime = 1735275558303,
        title = "Setting",
        isScreenHaveLogoutButton = false,
        optionList = emptyList(),
        versionText = "Version 978",
        isItemCard = true,
        errorMessage = "No Data available"
    )
    CommonSettingScreen(
        settingScreenConfig = commonSettingScreenConfig,
        onBackClick = {},
        onItemClick = { index, item -> },
        onLogoutClick = {},
        onSyncDataClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun CommonSettingCardPreview(){
    val settingOptionModel = SettingOptionModel(
        title = "Language",
        leadingIcon = R.drawable.ic_language,
        subTitle = BLANK_STRING,
        tag = BLANK_STRING,
        id = 0
    )
    SettingOptionCard(
        settingOptionModel = settingOptionModel,
        onClick = {},

    )
}

@Composable
fun SettingOptionCard(
    settingOptionModel: SettingOptionModel,
    isLastLineShow: Boolean = true,
    isItemCard: Boolean = false,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier
        .background(Color.White)
        .fillMaxWidth()
        .clickable {
            onClick()
        }) {
        if (!isItemCard) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.dp_10))
                    .padding(vertical = dimensionResource(id = R.dimen.dp_13))
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    settingOptionModel.leadingIcon?.let { icon ->
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(dimen_20_dp),
                            tint = blueDark

                        )
                        Spacer(modifier = Modifier.width(dimen_10_dp))
                    }

                    Text(
                        text = settingOptionModel.title,
                        textAlign = TextAlign.Start,
                        fontSize = dimen_16_sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        color = blueDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    Icon(
                        painter = painterResource(
                            id = settingOptionModel.trailingIcon
                        ),
                        contentDescription = null,
                        tint = blueDark,
                        modifier = Modifier
                            .size(dimen_15_dp)
                            .align(Alignment.CenterVertically)
                    )



                }
                if (!settingOptionModel.subTitle.isNullOrEmpty()) {
                    Text(
                        text = settingOptionModel.subTitle,
                        textAlign = TextAlign.Start,
                        fontSize = text_size_13,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Normal,
                        color = black100Percent,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (isLastLineShow) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.dp_1))
                        .background(syncCardBorderColor)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimen_4_dp)
                    .height(dimen_44_dp)
                    .clip(RoundedCornerShape(dimen_6_dp))
                    .border(
                        width = dimen_1_dp,
                        color = uncheckedTrackColor,
                        shape = RoundedCornerShape(dimen_6_dp)
                    )
                    .background(Color.White)

            ) {
                Text(
                    text = settingOptionModel.title,
                    color = blueDark,
                    fontSize = dimen_16_sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}