package com.nrlm.baselinesurvey.ui.common_components

import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.QuestionEntity
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.descriptionBoxBackgroundLightBlue
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.patsurvey.nudge.customviews.htmltext.HtmlText
import dagger.Lazy
import kotlinx.coroutines.launch

@Composable
fun RadioQuestionBoxComponent(
    modifier: Modifier = Modifier,
    index: Int,
    question: QuestionEntity,
    selectedOptionIndex: Int = -1,
    questionDetailExpanded: (index: Int) -> Unit
) {

    var selectedIndex by remember { mutableIntStateOf(selectedOptionIndex) }

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = defaultCardElevation
        ),
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .clickable {

            }
            .then(modifier)
    ) {
        Column(modifier = Modifier.background(white)) {

            Column(
                Modifier.padding(vertical = dimen_16_dp, horizontal = dimen_16_dp),
                verticalArrangement = Arrangement.spacedBy(dimen_18_dp)
            ) {
                Row {
                    Text(
                        text = "${index + 1}. ", style = defaultTextStyle,
                        color = textColorDark
                    )
                    HtmlText(
                        text = "${question.questionDisplay}",
                        style = defaultTextStyle,
                        color = textColorDark,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true
                    )
                }
                if (question.options?.isNotEmpty() == true) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        (question.options?.sortedBy { it.optionValue }
                            ?: emptyList()).forEachIndexed { index, optionsItem ->
                            RadioButtonOptionComponent(
                                modifier = Modifier.weight(1f),
                                index = index,
                                optionsItem = optionsItem,
                                selectedIndex = selectedIndex
                            ) {
                                selectedIndex = index
                            }
                        }
                        /*LazyVerticalGrid(
                            modifier = Modifier.fillMaxWidth(),
                            columns = GridCells.Fixed(2),
                            state = rememberLazyGridState(),
                        ) {

                            itemsIndexed(question.options ?: emptyList()) { index: Int, optionsItem: OptionsItem ->

                                RadioButtonOptionComponent(
                                    index = index,
                                    optionsItem = optionsItem,
                                    selectedIndex = selectedIndex
                                ) {

                                }

                            }
                        }*/
                    }
                } else {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen_10_dp)
                    )
                }
            }
            Divider(thickness = dimen_1_dp, color = lightGray2, modifier = Modifier.fillMaxWidth())
            InfoComponent(questionDetailExpanded, index, question)
        }
    }

}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RadioQuestionBoxComponentPreview(
    modifier: Modifier = Modifier,

    ) {
    val question = QuestionEntity(
        id = 1,
        questionId = 1,
        questionDisplay = "Did everyone in your family have at least 2 meals per day in the last 1 month?",
        questionSummary = "Please check if the family is getting ration through the public distribution system (PDS) of the government or not? \n\nPlease check the granary/ where they store their grain and also check with neighbors also to understand the food security of the family",
        order = 1,
        type = "RadioButton",
        gotoQuestionId = 2,
        options = listOf(
            OptionsItem(
                optionId = 1,
                display = "YES",
                weight = 1,
                summary = "YES",
                optionValue = 1,
                optionImage = R.drawable.icon_check,
                optionType = ""
            ),
            OptionsItem(
                optionId = 2,
                display = "NO",
                weight = 0,
                summary = "NO",
                optionValue = 0,
                optionImage = R.drawable.icon_close,
                optionType = ""
            )
        ),
        questionImageUrl = "Section1_GovtService.webp",
    )
    Surface {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            RadioQuestionBoxComponent(index = 0, question = question) {

            }
        }
    }
}