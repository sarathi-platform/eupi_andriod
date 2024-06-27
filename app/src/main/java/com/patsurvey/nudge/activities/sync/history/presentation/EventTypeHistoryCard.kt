package com.patsurvey.nudge.activities.sync.history.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_5_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle

@Composable
fun EventTypeHistoryCard(
    eventDateTime: String,
    eventStatusList:List<Pair<String,Int>>,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_10_dp)
            .clickable { onCardClick() },
        elevation = dimen_10_dp
    ) {
        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(dimen_8_dp)
        ) {
            val (titleText,countText) = createRefs()
            Text(
                text = eventDateTime,
                style = smallTextStyle,
                color = textColorDark,
                modifier = Modifier
                    .constrainAs(titleText) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                    .padding(top = dimen_5_dp, bottom = dimen_5_dp)
            )
            LazyColumn(modifier = Modifier.fillMaxWidth().constrainAs(countText){
                top.linkTo(titleText.bottom)
                start.linkTo(parent.start)
            }) {
                itemsIndexed(eventStatusList){ _, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.first,
                            color = textColorDark,
                            style = smallerTextStyle,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .weight(1f)
                                .padding(dimen_5_dp)

                        )

                        Text(
                            text = "${item.second}",
                            color = textColorDark,
                            style = smallerTextStyle,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .weight(1f)
                                .padding(dimen_5_dp)

                        )
                    }

                }
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun CommonSyncScreenPreview() {
    val list = listOf(
        Pair("Count",2)
    )
    EventTypeHistoryCard(
        eventDateTime = "16 Jan 2021, 08:12:00",
        eventStatusList = list,
        onCardClick = {}
    )
}