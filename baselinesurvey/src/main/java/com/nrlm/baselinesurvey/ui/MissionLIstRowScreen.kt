package com.nrlm.baselinesurvey.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.model.datamodel.MissionActivityModel
import com.nrlm.baselinesurvey.ui.common_components.CircularImageViewComponent
import com.nrlm.baselinesurvey.ui.theme.bgGreyLight
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.borderGrey
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.brownDark
import com.nrlm.baselinesurvey.ui.theme.didiDetailItemStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_3_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_5_dp
import com.nrlm.baselinesurvey.ui.theme.progressIndicatorColor
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.sectionIconNotStartedBg
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white


@Composable
fun MissionListRowScreen(mission: MissionEntity, clickListener: () -> Unit) {
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .background(white)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextComponent(
                modifier = Modifier
                    .weight(0.85F)
                    .padding(horizontal = 10.dp),
                text = mission.missionName,
                textAlign = TextAlign.Start,
                style = didiDetailItemStyle
            )

            IconButton(modifier = Modifier
                .weight(0.15f)
                .padding(10.dp),
                onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (expanded) {
                        "show less"
                    } else {
                        "show more"
                    }
                )
            }
        }
        Column(modifier = Modifier.background(white)) {
            if (expanded) {
                for (activity in mission.activities) {
                    MissionActivityRow(missionActivityModel = activity) {
                        clickListener()
                    }
                }
            }
        }

    }

}

@Composable
private fun MissionActivityRow(
    missionActivityModel: MissionActivityModel,
    clickListener: () -> Unit
) {
    Column {
        Card(elevation = CardDefaults.cardElevation(
            defaultElevation = dimen_5_dp
        ),
            shape = RoundedCornerShape(roundedCornerRadiusDefault),
            modifier = Modifier
                .fillMaxWidth()
                .background(white)
                .padding(10.dp)
                .clickable {

                }) {
            Column(modifier = Modifier
                .background(white)
                .clickable {
                    clickListener()
                }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .border(
                            dimen_1_dp,
                            borderGreyLight,
                            RoundedCornerShape(roundedCornerRadiusDefault)
                        )
                        .background(
                            bgGreyLight,
                            RoundedCornerShape(roundedCornerRadiusDefault)
                        ),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    TextComponent(
                        modifier = Modifier
                            .weight(0.75F)
                            .padding(10.dp),
                        textAlign = TextAlign.Start,
                        text = "${missionActivityModel.activityName}",
                        color = brownDark,
                        style = didiDetailItemStyle
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
                        color = progressIndicatorColor
                    )
                }
                TextComponent(
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    textAlign = TextAlign.Start,
                    text = "Tola 1 small group",
                    color = textColorDark,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    TextComponent(
                        modifier = Modifier
                            .weight(0.85F)
                            .padding(horizontal = 10.dp),
                        textAlign = TextAlign.Start,
                        text = "Demand request by didi",
                        color = textColorDark
                    )
                    Icon(
                        modifier = Modifier
                            .weight(0.15F)
                            .padding(10.dp),
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Person Icon"
                    )
                }
            }
        }

        for (task in missionActivityModel.tasks) {
            EntityRowComponent()
        }
    }
}

@Preview
@Composable
fun EntityRowComponent() {
    Card(elevation = CardDefaults.cardElevation(
        defaultElevation = dimen_3_dp
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
                    modifier = Modifier
                        .weight(0.30F)
                        .padding(10.dp)
                ) {
                    CircularImageViewComponent(
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp),
                        BLANK_STRING
                    )
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
fun TextComponent(
    modifier: Modifier,
    textAlign: TextAlign = TextAlign.Center,
    text: String,
    color: Color = black100Percent,
    style: TextStyle = smallerTextStyleNormalWeight
) {
    Text(
        modifier = modifier, textAlign = textAlign, text = text, color = color, style = style
    )
}