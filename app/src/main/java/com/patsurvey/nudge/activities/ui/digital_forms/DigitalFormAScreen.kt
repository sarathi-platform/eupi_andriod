package com.patsurvey.nudge.activities.ui.digital_forms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.NetworkBanner
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel


@Composable
fun DigitalFormAScreen(
    navController: NavController,
    viewModel:DigitalFormAViewModel,
    modifier:Modifier= Modifier
){
    val context = LocalContext.current
    val didiList by viewModel.didiDetailList.collectAsState()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .then(modifier)
    ){
        Column(modifier = Modifier
            .fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                NetworkBanner()
            }
            Text(
                text = "Digital Form A",
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
                contentColor = Color(ContextCompat.getColor(context, R.color.placeholder_color)),
                elevation = dimensionResource(id = R.dimen.dp_5),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.dp_16)
                    )
                    .padding(
                        top = dimensionResource(id = R.dimen.dp_10),
                        bottom = dimensionResource(id = R.dimen.dp_10)
                    )

            ) {
                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                        .padding(bottom = dimensionResource(id = R.dimen.dp_15))) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.village_text) +":",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(top = dimensionResource(id = R.dimen.dp_10))
                        )
                        Text(
                            text = "Ghaghara",
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
                            text = stringResource(id = R.string.participation_date_wealth_ranking) +":",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(top = dimensionResource(id = R.dimen.dp_5))
                        )
                        Text(
                            text = "Ghaghara",
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
                            text = stringResource(id = R.string.vo_name) +":",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(top = dimensionResource(id = R.dimen.dp_5))
                        )
                        Text(
                            text = "Ghaghara",
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
                            text = stringResource(id = R.string.total) +":",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(top = dimensionResource(id = R.dimen.dp_5))
                        )
                        Text(
                            text = "Ghaghara",
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
        contentColor = Color(ContextCompat.getColor(context, R.color.placeholder_color)),
        elevation = dimensionResource(id = R.dimen.dp_5),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.dp_16)
            )
            .padding(
                top = dimensionResource(id = R.dimen.dp_10),
                bottom = dimensionResource(id = R.dimen.dp_20)
            )
    ) {
           // List of Didis with Details
           Scaffold(backgroundColor = Color.White) { paddingValues ->
               LazyColumn(Modifier.padding(paddingValues)) {
                   items(didiList, DidiDetailsModel::id) { card ->
                       DidiVillageItem(card)
                   }
               }
           }
       }
        }
    }
}

@Composable
fun DidiVillageItem(didiDetailsModel: DidiDetailsModel){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = dimensionResource(id = R.dimen.dp_20))
                .padding(end = dimensionResource(id = R.dimen.dp_15))
                .padding(vertical = dimensionResource(id = R.dimen.dp_10))
        ) {

            Column{
                Row(horizontalArrangement = Arrangement.SpaceBetween
                    ,verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()) {
                    Text(
                        text = didiDetailsModel.name,
                        color = colorResource(id = R.color.text_didi_name_color),
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.dp_5))
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_completed_tick),
                        contentDescription = "completed",
                        modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.dp_5))
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_home_24),
                        contentDescription = "Get Location",
                        modifier = Modifier,
                        tint = blueDark,

                        )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = didiDetailsModel.village,
                        textAlign = TextAlign.Center,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = blueDark
                    )
                }
            }
    }
}