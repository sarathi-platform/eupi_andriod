package com.patsurvey.nudge.activities.survey

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.gson.Gson
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.model.dataModel.AnswerOptionModel


@Composable
fun ListTypeQuestion(
    modifier: Modifier,
    questionNumber: Int,
    question: String,
    index: Int=-1,
    optionList: List<AnswerOptionModel>,
    onAnswerSelection: (Int) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(index) }
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .border(
                    BorderStroke(1.dp, lightGray2),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(14.dp)
                .fillMaxWidth(),
            text = buildAnnotatedString {
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
            },
            style = TextStyle(
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            color = textColorDark
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()){
                itemsIndexed(optionList){ index, option ->
                  OptionCard(buttonTitle = option.optionText, index = index, selectedIndex = selectedIndex ){
                      Log.d("TAG", "ListTypeQuestion: $index :: ${Gson().toJson(option)}")
                      selectedIndex=it
                      onAnswerSelection(index)
                  }
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
    val optionList= mutableListOf<AnswerOptionModel>()
    for (i in 1..5){
        optionList.add(AnswerOptionModel(i,"Option Value $i",false))
    }
    ListTypeQuestion(
       modifier = Modifier.padding(16.dp),
        questionNumber = 1,
        question = "This is a sample text. This is an example of adding border to text.",
       optionList= optionList,
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
                Text(
                    text = buttonTitle,
                    color = if(selectedIndex == index) Color.White else Color.Black,
                    style = quesOptionTextStyle
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
