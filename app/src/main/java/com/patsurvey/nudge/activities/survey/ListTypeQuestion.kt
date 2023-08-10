package com.patsurvey.nudge.activities.survey

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.lightGray2
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.htmltext.HtmlText
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.utils.BLANK_STRING


@Composable
fun ListTypeQuestion(
    modifier: Modifier,
    questionNumber: Int,
    question: String,
    index: Int=-1,
    selectedIndex: Int,
    optionList: List<OptionsItem?>?,
    isAnswerSelected:Boolean =false,
    onAnswerSelection: (Int) -> Unit
) {
    Column(modifier = modifier) {
        HtmlText(
            modifier = Modifier
                .border(
                    BorderStroke(1.dp, lightGray2),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(14.dp)
                .fillMaxWidth(),
            text =
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = textColorDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        )
                    ) {
                        append("$questionNumber.")
                    }
                    append(" $question")

            }.toString(),

            style = TextStyle(
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = textColorDark
            ),

        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()){
                itemsIndexed(optionList?.sortedBy { it?.optionValue } ?: emptyList()){ index, option ->
                  OptionCard(buttonTitle = option?.display?: BLANK_STRING, index = index, selectedIndex = selectedIndex ){
//                      selectedIndex=it
                      if(!isAnswerSelected)
                            onAnswerSelection(index)
                  }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 100.dp))
                }
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun ListTypeQuestionPreview() {
    val optionList= mutableListOf<OptionsItem>()
    for (i in 1..5){
        optionList.add(OptionsItem("Option Value $i",i+1,i,1,"Summery"))
    }
    ListTypeQuestion(
       modifier = Modifier.padding(16.dp),
        questionNumber = 1,
        question = "This is a sample text. This is an example of adding border to text.",
        optionList= optionList,
        selectedIndex = 0,
        onAnswerSelection = {},
    )
}

@Composable
fun OptionCard(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    index: Int,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(6.dp))
        .background(if (selectedIndex == index) blueDark else languageItemActiveBg)
        .clickable {
            onOptionSelected(index)
        }
        .padding(horizontal = 10.dp)
        .then(modifier)) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopStart,
        ) {
            Row(
                Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HtmlText(
                    text = buttonTitle,
                    style = TextStyle(
                        color = if(selectedIndex == index) Color.White else Color.Black,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                )
            }
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(5.dp))
    }

}

@Preview(showBackground = true)
@Composable
fun OptionCardPreview(){
    OptionCard(modifier = Modifier,"Option", index = 0, onOptionSelected = {}, selectedIndex = 0)
}
