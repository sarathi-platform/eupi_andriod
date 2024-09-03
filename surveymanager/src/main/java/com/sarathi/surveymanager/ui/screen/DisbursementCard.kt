package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.nudge.core.BLANK_STRING
import com.nudge.core.formatToIndianRupee
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.greyColor
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.uncheckedTrackColor
import com.sarathi.dataloadingmangement.ui.component.TextWithReadMoreComponent
import com.sarathi.surveymanager.R

@Composable
fun DisbursementCard(
    subTitle1: String = BLANK_STRING,
    subTitle2: String = BLANK_STRING,
    subTitle3: String = BLANK_STRING,
    subTitle4: String = BLANK_STRING,
    subTitle5: String = BLANK_STRING,
    isActivityCompleted: Boolean = false,
    onEditSurvey: () -> Unit,
    onDeleteSurvey: () -> Unit,
    isFormgenerated: Boolean,
    formGeneratedMessage: String? = BLANK_STRING

) {

    BasicCardView(
        modifier = Modifier
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_10_dp, vertical = dimen_10_dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        modifier = Modifier.padding(end = dimen_5_dp),
                        text = stringResource(R.string.date),
                        style = defaultTextStyle.copy(color = greyColor)
                    )
                    if (subTitle1.isNotBlank()) {
                        Text(
                            text = subTitle1,
                            style = defaultTextStyle.copy(color = blueDark)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        modifier = Modifier.padding(end = dimen_5_dp),
                        text = stringResource(R.string.amount),
                        style = defaultTextStyle.copy(color = greyColor)
                    )
                    if (formatToIndianRupee(subTitle2).isNotBlank()) {
                        Text(
                            text = formatToIndianRupee(subTitle2),
                            style = defaultTextStyle.copy(color = blueDark)
                        )
                    }
                }
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimen_4_dp),
                thickness = 0.5.dp,
                color = uncheckedTrackColor
            )
            if (subTitle3.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = dimen_10_dp, end = dimen_10_dp)
                ) {
                    TextRow(
                        text1 = stringResource(R.string.nature),
                        text2 = subTitle3,
                        isReadMode = true
                    )
                }
            }
            if (subTitle4.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .padding(start = dimen_10_dp, end = dimen_10_dp)
                ) {
                    TextRow(text1 = stringResource(R.string.mode), text2 = subTitle4)
                }
            }
            if (subTitle5.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = dimen_10_dp, vertical = dimen_10_dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(end = dimen_5_dp),
                        text = stringResource(R.string.no_of_didi_s),
                        style = defaultTextStyle.copy(color = greyColor)
                    )
                    if (subTitle1.isNotBlank()) {
                        Text(
                            text = subTitle5,
                            style = defaultTextStyle.copy(color = blueDark)
                        )
                    }
                }
            }
            if (isFormgenerated) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimen_5_dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_green_file),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = dimen_4_dp)
                            .align(Alignment.CenterVertically),
                    )
                    Text(
                        LocalContext.current.getString(R.string.form_e_generated),
                        color = greenOnline,
                        style = newMediumTextStyle
                    )

                }
            } else {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimen_5_dp),
                    thickness = dimen_1_dp,
                    color = uncheckedTrackColor
                )
                if (isActivityCompleted) {
                    Row(
                        modifier = Modifier
                            .height(48.dp)
                            .padding(horizontal = dimen_10_dp)
                            .clickable {
                                onEditSurvey()
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.task_view),
                            modifier = Modifier
                                .padding(horizontal = dimen_5_dp)
                                .absolutePadding(bottom = 3.dp),
                            color = blueDark,
                            style = newMediumTextStyle,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "",
                            tint = blueDark,
                        )
                    }
                } else {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp), horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimen_10_dp)
                            .weight(1f),
                            onClick = {
                                onEditSurvey()
                            }) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_edit_icon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = dimen_4_dp)
                                        .size(dimen_16_dp)
                                        .align(Alignment.CenterVertically),
                                    colorFilter = ColorFilter.tint(blueDark)
                                )
                                Text(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    text = stringResource(R.string.edit),
                                    style = defaultTextStyle.copy(blueDark),
                                )
                            }
                        }

                        Divider(
                            color = uncheckedTrackColor,
                            modifier = Modifier
                                .height(48.dp)
                                .width(1.dp)
                        )
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimen_10_dp)
                                .weight(1f),
                            onClick = {
                                onDeleteSurvey()
                            }) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_delete_icon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = dimen_4_dp)
                                        .size(dimen_16_dp)
                                        .align(Alignment.CenterVertically),
                                    colorFilter = ColorFilter.tint(blueDark)
                                )
                                Text(
                                    text = stringResource(R.string.delete),
                                    style = defaultTextStyle.copy(blueDark),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }

                        }

                    }
                }


            }
        }
    }

}


@Composable
private fun TextRow(
    text1: String,
    text2: String,
    isReadMode: Boolean = false
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (text1Ref, text2Ref) = createRefs()

        if (text1.isNotBlank()) {
            androidx.compose.material3.Text(
                modifier = Modifier.constrainAs(text1Ref) {
                    start.linkTo(parent.start)
                    if (isReadMode) {
                        top.linkTo(parent.top)
                    } else {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    width = Dimension.fillToConstraints
                },
                text = text1,
                style = newMediumTextStyle.copy(color = greyColor)
            )
        }

        if (text2.isNotBlank()) {
            if (isReadMode) {
                TextWithReadMoreComponent(
                    modifier = Modifier
                        .padding(start = dimen_5_dp)
                        .constrainAs(text2Ref) {
                            start.linkTo(text1Ref.end)
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                    title = text1,
                    contentData = text2
                )
            } else {
                androidx.compose.material3.Text(
                    modifier = Modifier
                        .padding(start = dimen_5_dp)
                        .constrainAs(text2Ref) {
                            start.linkTo(text1Ref.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                    text = text2,
                    style = defaultTextStyle.copy(color = blueDark)
                )
            }
        }
    }
}