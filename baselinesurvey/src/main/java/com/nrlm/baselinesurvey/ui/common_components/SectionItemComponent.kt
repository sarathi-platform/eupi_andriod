package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_30_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_40_dp
import com.nrlm.baselinesurvey.ui.theme.greenLight
import com.nrlm.baselinesurvey.ui.theme.greenOnline
import com.nrlm.baselinesurvey.ui.theme.greyBorder
import com.nrlm.baselinesurvey.ui.theme.sectionIconCompletedBg
import com.nrlm.baselinesurvey.ui.theme.sectionIconNotStartedBg
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.textColorDark50
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.states.SectionState
import com.nrlm.baselinesurvey.utils.states.SectionStatus

@Composable
fun SectionItemComponent(
    index: Int,
    modifier: Modifier = Modifier,
    sectionStateItem: SectionState,
    onclick: (Int) -> Unit,
    onDetailIconClicked: (Int) -> Unit
) {

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .border(
                width = 0.dp,
                color = Color.Transparent,
            )
            .then(modifier)
    ) {
        val (stepNo, stepBox) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name)) greenOnline else greyBorder,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(Color.White)
                .clickable {
                    if (!sectionStateItem.sectionStatus.name.equals(SectionStatus.NOT_STARTED.name)) {
                        onclick(sectionStateItem.section.sectionId)
                    }
                }
                .constrainAs(stepBox) {
                    start.linkTo(parent.start)
                    top.linkTo(stepNo.bottom, margin = -16.dp)
                }

        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name)) greenLight else white //TODO Temporary change do not remove the commented part
                        /*if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name)) greenLight else if (sectionStateItem.sectionStatus.name.equals(
                                SectionStatus.INPROGRESS.name
                            )
                        ) stepBoxActiveColor else white*/
                    )
                    .padding(vertical = /*if (isCompleted) 10.dp else */14.dp)
                    .padding(end = 16.dp, start = 8.dp),
            ) {
                val (textContainer, buttonContainer, iconContainer) = createRefs()
                if (sectionStateItem.section.sectionIcon != null) {

                    Box(modifier = Modifier
                        .constrainAs(iconContainer) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name))
                                sectionIconCompletedBg else sectionIconNotStartedBg //TODO Temporary change do not remove the commented part
                            /*if (sectionStateItem.sectionStatus.name.equals(
                                    SectionStatus.COMPLETED.name
                                )
                            ) sectionIconCompletedBg else if (sectionStateItem.sectionStatus.name.equals(
                                    SectionStatus.INPROGRESS.name
                                )
                            ) sectionIconInProgressBg else sectionIconNotStartedBg*/,
                            shape = CircleShape
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = getIcon(sectionStateItem.section.sectionIcon)),
                            contentDescription = null,
//                            colorFilter = ColorFilter.tint(
//                                color = if (sectionStateItem.sectionStatus.name.equals(SectionStatus.INPROGRESS.name) or sectionStateItem.sectionStatus.name.equals(
//                                        SectionStatus.COMPLETED.name
//                                    )
//                                ) {
//                                    if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name)) stepIconCompleted else stepIconEnableColor
//                                } else stepIconDisableColor,blendMode = BlendMode.Modulate
//                            ),
                            modifier = Modifier
                        )
//                        Icon(
//                            painter = painterResource(id = getIcon(index)),
//                            contentDescription = null,
//                            tint = if (sectionStateItem.sectionStatus.name.equals(SectionStatus.INPROGRESS.name) or sectionStateItem.sectionStatus.name.equals(
//                                    SectionStatus.COMPLETED.name
//                                )
//                            ) {
//                                if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name)) stepIconCompleted else stepIconEnableColor
//                            } else stepIconDisableColor,
//                            modifier = Modifier
//                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .constrainAs(textContainer) {
                            top.linkTo(iconContainer.top)
                            start.linkTo(iconContainer.end)
                            bottom.linkTo(iconContainer.bottom)
                            end.linkTo(buttonContainer.start)
                            width = Dimension.fillToConstraints
                        }
                        .fillMaxWidth()
                ) {
                    Text(
                        text = sectionStateItem.section.sectionName,
                        color = if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name)) greenOnline else textColorDark //TODO Temporary change do not remove the commented part
                        /*if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name)) greenOnline else if(sectionStateItem.sectionStatus.name.equals(
                                SectionStatus.INPROGRESS.name)) textColorDark else textColorDark50*/,
                        modifier = Modifier
                            .fillMaxWidth(),
                        softWrap = true,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = defaultTextStyle
                    )
                    Text(
                        text = "${sectionStateItem.section.questionSize} Questions",
                        color = if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name)) greenOnline else textColorDark //TODO Temporary change do not remove the commented part
                        /*if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name)) greenOnline else if (sectionStateItem.sectionStatus.name.equals(
                                SectionStatus.INPROGRESS.name
                            )
                        ) textColorDark else textColorDark50*/,
                        modifier = Modifier
                            .fillMaxWidth(),
                        softWrap = true,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = smallerTextStyle
                    )


                }
//                Spacer(modifier = Modifier.size(dimen_40_dp))

                if (sectionStateItem.section.contentData?.isNotEmpty() == true) {
                    IconButton(
                        onClick = { onDetailIconClicked(sectionStateItem.section.sectionId) },
                        modifier = Modifier
                            .constrainAs(buttonContainer) {
                                bottom.linkTo(textContainer.bottom)
                                top.linkTo(textContainer.top)
                                end.linkTo(parent.end)
                            }
                            .size(dimen_40_dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.info_icon),
                            contentDescription = "section info screen",
                            tint = if (sectionStateItem.sectionStatus.name.equals(SectionStatus.NOT_STARTED.name)) textColorDark50 else blueDark
                        )

                    }
                } else {
                    Spacer(modifier = Modifier.size(dimen_30_dp))
                }
            }
        }

        if (sectionStateItem.sectionStatus.name.equals(SectionStatus.COMPLETED.name)) {
            Image(
                painter = painterResource(id = R.drawable.icon_check_circle_green),
                contentDescription = null,
                modifier = modifier
                    .border(
                        width = 2.dp,
                        color = Color.Transparent,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .constrainAs(stepNo) {
                        start.linkTo(parent.start, margin = 16.dp)
                        top.linkTo(parent.top)
                    }
            )
        }
    }

}

fun getIcon(index: String): Int {
    when (index) {
        "baseline_household_information" -> {
            return R.drawable.baseline_household_information
        }

        "baseline_food_security_sutritional_diversity" -> {
            return R.drawable.baseline_food_security_sutritional_diversity
        }

        "baseline_social_inclusion" -> {
            return R.drawable.baseline_social_inclusion
        }

        "baseline_financial_inclusion" -> {
            return R.drawable.baseline_financial_inclusion
        }

        "baseline_household_entitlements" -> {
            return R.drawable.baseline_household_entitlements
        }

        "baseline_livelihood_sources" -> {
            return R.drawable.baseline_livelihood_sources
        }

        "demographic" -> {
            return R.drawable.demographic
        }

        "key_gov_program" -> {
            return R.drawable.key_gov_program
        }

        "cimate_change" -> {
            return R.drawable.cimate_change
        }

        "governance" -> {
            return R.drawable.governance
        }

        "health_community" -> {
            return R.drawable.health_community
        }

        "small_business" -> {
            return R.drawable.small_business
        }

        "alternative_livelihood" -> {
            return R.drawable.alternative_livelihood
        }

        "infrastructure" -> {
            return R.drawable.infrastructure
        }

        else -> {
            return R.drawable.house_hold_icon
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SectionItemComponentPreview(
    modifier: Modifier = Modifier
) {
    /* val sectionStateItem1 = SectionState(sampleSetcion1, SectionStatus.INPROGRESS)
     val sectionStateItem2 = SectionState(sampleSection2, SectionStatus.COMPLETED)
     val sectionStateItem3 = SectionState(sampleSection2, SectionStatus.NOT_STARTED)

     Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(
         dimen_10_dp)) {
         SectionItemComponent(
             sectionStateItem = sectionStateItem1,
             onclick = {},
             onDetailIconClicked = {})
         SectionItemComponent(
             sectionStateItem = sectionStateItem2,
             onclick = {},
             onDetailIconClicked = {})
         SectionItemComponent(
             sectionStateItem = sectionStateItem3,
             onclick = {},
             onDetailIconClicked = {})
     }*/
}