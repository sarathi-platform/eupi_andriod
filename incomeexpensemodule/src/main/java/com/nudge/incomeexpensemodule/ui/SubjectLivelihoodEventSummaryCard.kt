package com.nudge.incomeexpensemodule.ui

import android.net.Uri
import android.text.TextUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.incomeexpensemodule.R
import com.nudge.core.BLANK_STRING
import com.nudge.core.getFileNameFromURL
import com.nudge.core.getFirstAndLastInitials
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CircularImageViewComponent
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.brownDark
import com.nudge.core.ui.theme.buttonTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.incomeCardTopViewColor
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.smallTextStyleWithNormalWeight
import com.nudge.core.ui.theme.smallerTextStyle
import com.nudge.core.ui.theme.white
import com.nudge.core.ui.theme.yellowBg
import com.nudge.core.utils.FileUtils
import com.nudge.core.utils.FileUtils.findImageFile
import com.nudge.core.value
import com.nudge.incomeexpensemodule.ui.component.TotalIncomeExpenseAssetSummaryView
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel

@Composable
fun SubjectLivelihoodEventSummaryCard(
    subjectId: Int,
    name: String,
    imageFileName: String?,
    dadaName: String,
    location: String,
    lastUpdated: String,
    incomeExpenseSummaryUiModel: IncomeExpenseSummaryUiModel?,
    subjectLivelihoodMapping: List<SubjectEntityWithLivelihoodMappingUiModel>,
    onAssetCountClicked: (subjectId: Int) -> Unit,
    onSummaryCardClicked: () -> Unit
) {
    val context = LocalContext.current
    BasicCardView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimen_8_dp)
    ) {
        Column(
            modifier = Modifier
                .clickable {
                    onSummaryCardClicked()
                }
        ) {
            if (!TextUtils.isEmpty(lastUpdated)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(incomeCardTopViewColor),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.last_updated_days_ago, lastUpdated),
                        style = getTextColor(textColor = smallerTextStyle),
                        modifier = Modifier.padding(vertical = dimen_2_dp, horizontal = dimen_10_dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_8_dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    val imageFile =
                        findImageFile(context, getFileNameFromURL(imageFileName ?: BLANK_STRING))
                    val imageUri =
                        imageFileName?.let {
                            FileUtils.getImageUri(
                                context = context,
                                fileName = getFileNameFromURL(imageFileName)
                            )
                        } ?: Uri.EMPTY

                    if (!TextUtils.isEmpty(imageFileName) && imageFile.isFile && imageFile.exists() && imageUri != Uri.EMPTY) {
                        CircularImageViewComponent(
                            modifier = Modifier,
                            imagePath = imageUri
                        )
                    } else if (name != BLANK_STRING) {
                        Box(
                            modifier = Modifier
                                .border(width = dimen_2_dp, shape = CircleShape, color = brownDark)
                                .clip(CircleShape)
                                .width(dimen_56_dp)
                                .height(dimen_56_dp)
                                .background(color = yellowBg)
                        ) {
                            Text(
                                getFirstAndLastInitials(name),
                                modifier = Modifier.align(Alignment.Center),
                                style = mediumTextStyle.copy(color = brownDark)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(dimen_8_dp))
                    Column {
                        Text(text = name, style = getTextColor(buttonTextStyle))
                        Text(text = dadaName, style = getTextColor(smallTextStyleWithNormalWeight))
                    }
                }

                Row {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "ArrowForward Icon",
                        modifier = Modifier.size(dimen_24_dp),
                        tint = blueDark
                    )
                }
            }

            Row(
                modifier = Modifier.padding(start = dimen_8_dp, bottom = dimen_8_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home_icn),
                    contentDescription = "Location",
                    modifier = Modifier.size(dimen_16_dp),
                    tint = blueDark
                )
                Spacer(modifier = Modifier.width(dimen_5_dp))
                Text(text = location, style = getTextColor(smallerTextStyle))
            }
            Divider()

            Column(modifier = Modifier.padding(dimen_8_dp)) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        imageVector = Icons.Default.DateRange,
//                        contentDescription = "Calendar Icon",
//                        modifier = Modifier.size(dimen_16_dp),
//                        tint = blueDark
//                    )
//                    Spacer(modifier = Modifier.width(dimen_4_dp))
//                    Text(
//                        text = stringResource(R.string.last_1_month),
//                        style = getTextColor(smallTextStyleMediumWeight2)
//                    )
//                }
//                Spacer(modifier = Modifier.height(dimen_8_dp))
                IncomeExpenseAssetAmountView(
                    incomeExpenseSummaryUiModel = incomeExpenseSummaryUiModel,
                    subjectLivelihoodMapping = subjectLivelihoodMapping,
                    onAssetCountClicked = {
                        onAssetCountClicked(it)
                    }
                )
            }
        }
    }
}

@Composable
fun IncomeExpenseAssetAmountView(
    incomeExpenseSummaryUiModel: IncomeExpenseSummaryUiModel?,
    subjectLivelihoodMapping: List<SubjectEntityWithLivelihoodMappingUiModel>,
    onAssetCountClicked: (subjectId: Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(horizontal = dimen_8_dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TotalIncomeExpenseAssetSummaryView(
            incomeExpenseSummaryUiModel = incomeExpenseSummaryUiModel,
            subjectLivelihoodMapping
        ) {
            onAssetCountClicked(incomeExpenseSummaryUiModel?.subjectId.value())
        }
    }
}

private fun getTextColor(textColor: TextStyle, color: Color = blueDark): TextStyle =
    textColor.copy(color)

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun UserProfileCardList() {
    Column {
        SubjectLivelihoodEventSummaryCard(
            subjectId = 123,
            name = "Shanti Devi",
            imageFileName = null,
            dadaName = "Killu dada",
            location = "Sundar Pahari",
            lastUpdated = "10 days ago",
            incomeExpenseSummaryUiModel = IncomeExpenseSummaryUiModel.getDefaultIncomeExpenseSummaryUiModel(
                123
            ),
            subjectLivelihoodMapping = listOf(),
            onAssetCountClicked = {

            }
        ) {

        }
    }
}
