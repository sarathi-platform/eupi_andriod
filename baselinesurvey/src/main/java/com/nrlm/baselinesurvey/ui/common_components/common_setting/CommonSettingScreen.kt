package com.nrlm.baselinesurvey.ui.common_components.common_setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.model.datamodel.CommonSettingScreenConfig
import com.nrlm.baselinesurvey.ui.common_components.ButtonOutline
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.ToolbarComponent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.greyBorder
import com.nrlm.baselinesurvey.ui.theme.newMediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.textColorDark50
import com.nrlm.baselinesurvey.ui.theme.white
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.ui.commonUi.LastSyncTimeView
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.languageItemInActiveBorderBg

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
                .padding(dimen_20_dp)
                .fillMaxSize()
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
                    ButtonOutline(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        buttonTitle = stringResource(id = R.string.sync),
                        isIconShow = false
                    ) {
                        onSyncDataClick()
                    }
                    LastSyncTimeView(
                        lastSyncTime = settingScreenConfig.lastSyncTime ?: 0L,
                        mobileNumber = settingScreenConfig.mobileNumber,
                        isShowPhoneNumber = false
                    ) { }
                    Spacer(modifier = Modifier.height(dimen_10_dp))
                }
            }
            Column(modifier = Modifier
                .background(Color.White)
                .border(
                    if (settingScreenConfig.isItemCard) 0.dp else
                        dimen_1_dp,
                    color = if (settingScreenConfig.isItemCard) white else greyBorder,
                    RoundedCornerShape(
                        if (settingScreenConfig.isItemCard) 0.dp else dimen_10_dp
                    )
                )
                .fillMaxWidth()
                .constrainAs(mainBox) {
                    start.linkTo(parent.start)
                    top.linkTo(if (settingScreenConfig.isSyncEnable) syncCard.bottom else parent.top)
                    end.linkTo(parent.end)
                }) {

                LazyColumn {
                    itemsIndexed(settingScreenConfig.optionList) { index, item ->
                        SettingOptionCard(
                            title = item.title,
                            subTitle = item.subTitle,
                            icon = item.icon,
                            isItemCard = settingScreenConfig.isItemCard,
                            isShareOption = item.isShareOption,
                            isLastLineShow = index < settingScreenConfig.optionList.size - 1,
                            onClick =
                            {
                                onItemClick(index, item)
                            })
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
     SettingOptionModel(1, "Sync Now", "", "", icon = R.drawable.ic_language),
        SettingOptionModel(2,"Forms","",""),
     SettingOptionModel(3, "Language", "", "", isShareOption = true)
 )
    val commonSettingScreenConfig = CommonSettingScreenConfig(
        isSyncEnable = true,
        mobileNumber = "9862345078",
        lastSyncTime = 1735275558303,
        title = "Setting",
        isScreenHaveLogoutButton = true,
        optionList = list,
        versionText = "Version 978",
        isItemCard = true
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
    SettingOptionCard(
        title = "title",
        subTitle = "",
        isShareOption = false,
        onClick = {},
        icon = R.drawable.ic_language
    )
}

@Composable
fun SettingOptionCard(
    title: String,
    subTitle: String,
    isShareOption: Boolean = false,
    isLastLineShow: Boolean = true,
    isItemCard: Boolean = false,
    onClick: () -> Unit,
    icon: Int?,
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
                    .padding(vertical = dimensionResource(id = R.dimen.dp_15))
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    icon?.let { icon ->
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(dimen_20_dp),
                            tint = textColorDark

                        )
                        Spacer(modifier = Modifier.width(dimen_10_dp))
                    }

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
                    Icon(
                        painter = painterResource(
                            id = if (!isShareOption)
                                R.drawable.ic_arrow_forward_ios_24
                            else R.drawable.ic_share_icon
                        ),
                        contentDescription = null,
                        tint = textColorDark,
                        modifier = Modifier
                            .width(dimen_20_dp)
                            .height(dimen_20_dp)
                    )

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
            if (isLastLineShow) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.dp_1))
                        .background(greyBorder)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 0.dp)
                    .height(dimensionResource(id = R.dimen.height_60dp))
                    .clip(RoundedCornerShape(6.dp))
                    .border(
                        width = 1.dp,
                        color = languageItemInActiveBorderBg,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .background(Color.White)

            ) {
                Text(
                    text = title,
                    color = blueDark,
                    fontSize = 18.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}