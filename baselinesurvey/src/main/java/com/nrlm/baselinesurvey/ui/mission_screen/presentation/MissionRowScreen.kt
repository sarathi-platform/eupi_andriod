package com.nrlm.baselinesurvey.ui.mission_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.CTAButtonComponent
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.greenDark
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark50

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@Preview(showBackground = true)
fun MissionRowScreen() {
    Card(elevation = 8.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(text = "Active", style = defaultTextStyle)
            Spacer(modifier = Modifier.padding(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person Icon",
                    modifier = Modifier.weight(.2f)
                )
                Column(
                    modifier = Modifier
                        .weight(.8f)
                        .fillMaxWidth(),
                ) {
                    Text(text = "CSG Demand by Request by didi", style = smallTextStyle)
                    Spacer(modifier = Modifier.height(10.dp))
                    RectangleShapeText("Due in 2 Days", horizontal = 15.dp, vertical = 10.dp)
                }

            }
            VOList()
            Divider(
                color = Color.Black,
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                    .padding(vertical = dimensionResource(id = R.dimen.dp_15))
            ) {
                CTAButtonComponent(tittle = "Continue", Modifier.fillMaxWidth()) {}
            }
        }

    }

}


@Composable
fun RectangleShapeText(
    title: String,
    horizontal: Dp = 10.dp,
    vertical: Dp = 5.dp,
    roundedCornerShape: Dp = 5.dp

) {
    Box(
        modifier = Modifier.border(
            width = 1.dp, color = Color.Black, shape = RoundedCornerShape(roundedCornerShape)
        )

    ) {
        Text(
            text = title,
            color = textColorDark50,
            fontWeight = FontWeight.W800,
            modifier = Modifier.padding(horizontal = horizontal, vertical = vertical),
            style = smallerTextStyle
        )
    }
}

@Composable
fun VOList() {
    val items1 = listOf(
        VO("VO(A)", "Didi 20/20", "$ 15,000/15,000"),
        VO("VO(B)", "Didi 20/20", "$ 15,000/15,000"),
        VO("VO(C)", "Didi 20/20", "$ 15,000/15,000"),
        //VO("VO(D)", "Didi 20/20", "$ 15,000/15,000"),
        //  VO("VO(E)", "Didi 20/20", "$ 15,000/15,000"),
        //  VO("VO(F)", "Didi 20/20", "$ 15,000/15,000"),
    )
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(items1) {
            VORow(title = it.title, didi = it.didi, amount = it.amount)
        }
    }
}

@Composable
fun VORow(title: String, didi: String, amount: String) {
    Divider(
        color = Color.Black,
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = smallerTextStyle)
        RectangleShapeText(didi)
        RectangleShapeText(amount)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 1..5) {
            val startCornerDp = if (i == 1) 5.dp else 0.dp
            val endCornerDp = if (i == 5) 5.dp else 0.dp
            Box(
                modifier = Modifier.background(
                    greenDark, shape = RoundedCornerShape(
                        startCornerDp, endCornerDp, endCornerDp, startCornerDp
                    )
                )
            ) {
                Text(
                    text = "$i",
                    color = Color.White,
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 3.dp)
                )
            }

        }

    }
}

data class VO(val title: String, val didi: String, val amount: String)
