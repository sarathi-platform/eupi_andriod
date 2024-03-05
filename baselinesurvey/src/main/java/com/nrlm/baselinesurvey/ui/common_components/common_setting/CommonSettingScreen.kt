package com.nrlm.baselinesurvey.ui.common_components.common_setting


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.ToolbarComponent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.newMediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.textColorDark50
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nudge.core.model.SettingOptionModel

@Composable
fun CommonSettingScreen(
    title:String,
    versionText:String,
    optionList:List<SettingOptionModel>,
    onBackClick:()->Unit,
    onItemClick:(Int,SettingOptionModel)->Unit,
    onLogoutClick:()->Unit
){
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
        }){
        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
        ) {
            val (mainBox, logoutButton, versionBox) = createRefs()

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
                            expanded = item.title == stringResource(id = R.string.forms),
                            showArrow = item.title == stringResource(id = R.string.forms),
                        ) {
                            onItemClick(index,item)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommonSettingScreenPreview(){
 val list=   listOf(
        SettingOptionModel(1,"Sync Now","new Datta",""),
        SettingOptionModel(2,"Sync Now","",""),
        SettingOptionModel(3,"Sync Now","",""))
CommonSettingScreen(title = "Setting", versionText = "Version 978", list ,onBackClick = {}, onItemClick = {index,item->}, onLogoutClick = {})
}

@Preview(showBackground = true)
@Composable
fun CommonSettingCardPreview(){
    CommonSettingCard(title = "title", subTitle = "subtitle", expanded = false) {

    }
}
@Composable
fun CommonSettingCard(
    title: String,
    subTitle: String,
    expanded: Boolean,
    showArrow: Boolean = false,
    onClick: () -> Unit
) {

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
//                        modifier = Modifier.rotate(arrowRotationDegree)
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

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.dp_2))
                .background(borderGreyLight)
        )

    }
}