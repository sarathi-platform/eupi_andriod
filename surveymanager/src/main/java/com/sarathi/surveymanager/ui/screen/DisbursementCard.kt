package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.formatToIndianRupee
import com.nudge.core.ui.theme.black1
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
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.uncheckedTrackColor
import com.nudge.core.ui.theme.white
import com.sarathi.surveymanager.R

@Composable
fun DisbursementCard(
    subTitle1: String = BLANK_STRING,
    subTitle2: String = BLANK_STRING,
    subTitle3: String = BLANK_STRING,
    subTitle4: String = BLANK_STRING,
    subTitle5: String = BLANK_STRING,
    onEditSurvey: () -> Unit,
    onDeleteSurvey: () -> Unit,
    isFormgenerated: Boolean

) {
    Card(
        elevation = dimen_10_dp,
        backgroundColor = white,
        shape = RoundedCornerShape(roundedCornerRadiusDefault)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_10_dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextRow(text1 = stringResource(R.string.date), text2 = subTitle1)
                TextRow(
                    text1 = stringResource(R.string.amount),
                    text2 = formatToIndianRupee(subTitle2)
                )
            }
            if (subTitle3.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimen_10_dp)
                ) {
                    TextRow(text1 = stringResource(R.string.nature), text2 = subTitle3)
                }
            }
            if (subTitle4.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .padding(dimen_10_dp)
                ) {
                    TextRow(text1 = stringResource(R.string.mode), text2 = subTitle4)
                }
            }
            if (subTitle5.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .padding(dimen_10_dp)
                ) {
                    TextRow(text1 = stringResource(R.string.no_of_didi_s), text2 = subTitle5)
                }
            }
            if (isFormgenerated) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        .fillMaxWidth(),
                    thickness = dimen_1_dp,
                    color = uncheckedTrackColor
                )
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
                                style = defaultTextStyle,
                                color = blueDark,
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
                                style = defaultTextStyle,
                                color = blueDark,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }

                    }

                }

            }
        }
    }

}


@Composable
private fun TextRow(text1: String, text2: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (text1.isNotBlank()) {
            Text(
                modifier = Modifier.padding(end = dimen_5_dp),
                text = text1,
                style = defaultTextStyle.copy(color = greyColor)
            )
        }
        if (text2.isNotBlank()) {
            Text(text = text2, style = defaultTextStyle.copy(color = black1))
        }
    }
}