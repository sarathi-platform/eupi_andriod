package com.nrlm.baselinesurvey.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.ui.common_components.CircularImageViewComponent
import com.nrlm.baselinesurvey.ui.theme.bgGreyLight
import com.nrlm.baselinesurvey.ui.theme.borderGrey
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.sectionIconNotStartedBg
import com.nrlm.baselinesurvey.ui.theme.white

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreen() {

    Card(elevation = CardDefaults.cardElevation(
        defaultElevation = defaultCardElevation
    ),
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(10.dp)
            .clickable {

            }) {
        Column(modifier = Modifier.background(white)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextComponent(
                    modifier = Modifier
                        .weight(0.9F)
                        .padding(horizontal = 10.dp),
                    text = "Today",
                    textAlign = TextAlign.Start
                )
                Icon(
                    modifier = Modifier
                        .weight(0.10F)
                        .padding(10.dp),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Person Icon"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .border(
                        dimen_1_dp, borderGreyLight, RoundedCornerShape(roundedCornerRadiusDefault)
                    )
                    .background(bgGreyLight, RoundedCornerShape(roundedCornerRadiusDefault)),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                TextComponent(
                    modifier = Modifier
                        .weight(0.75F)
                        .padding(10.dp),
                    textAlign = TextAlign.Start,
                    text = "CSG Training for Didis"
                )

                TextComponent(
                    modifier = Modifier
                        .weight(0.25F)
                        .padding(10.dp)
                        .border(
                            dimen_1_dp, borderGrey, RectangleShape
                        )
                        .background(sectionIconNotStartedBg, RectangleShape),
                    textAlign = TextAlign.Center,
                    text = "10 Didis",
                )
            }
            TextComponent(
                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                textAlign = TextAlign.Start,
                text = "Tola 1 small group",
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                TextComponent(
                    modifier = Modifier
                        .weight(0.90F)
                        .padding(horizontal = 10.dp),
                    textAlign = TextAlign.Start,
                    text = "Demand request by didi",
                )
                Icon(
                    modifier = Modifier
                        .weight(0.10F)
                        .padding(10.dp),
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Person Icon"
                )

            }
        }
    }
}

@Preview
@Composable
fun EntityRowComponent() {
    Card(elevation = CardDefaults.cardElevation(
        defaultElevation = defaultCardElevation
    ),
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(10.dp)
            .clickable {

            }) {
        Column(modifier = Modifier.background(white)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(0.30F)
                ) {
                    CircularImageViewComponent(modifier = Modifier, BLANK_STRING)
                }
                Column(modifier = Modifier.weight(0.70f)) {
                    TextComponent(modifier = Modifier, text = "Form A Details Collection")
                    TextComponent(modifier = Modifier, text = "Shanti Devi")
                    Row {
                        TextComponent(
                            modifier = Modifier.weight(.80f),
                            textAlign = TextAlign.Start,
                            text = "Demand request by didi"
                        )
                        Icon(
                            modifier = Modifier.weight(0.10F),
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Person Icon"
                        )
                    }

                }

            }

        }
    }
}

@Composable
fun TextComponent(modifier: Modifier, textAlign: TextAlign = TextAlign.Center, text: String) {
    Text(
        modifier = modifier, textAlign = textAlign, text = text, color = Color.Black
    )
}