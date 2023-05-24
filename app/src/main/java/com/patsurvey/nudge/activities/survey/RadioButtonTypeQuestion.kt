package com.patsurvey.nudge.activities.survey

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.ButtonOutlineWithTopIcon
import com.patsurvey.nudge.model.dataModel.AnswerOptionModel


@Composable
fun RadioButtonTypeQuestion(
    modifier: Modifier,
    questionNumber: Int,
    question: String,
    optionList: List<AnswerOptionModel>,
    onAnswerSelection: (Int) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(-1) }
    /*button width will be the half size of device width, after remove padding(start, end, between)*/
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val buttonWidth = (screenWidth - ((16.dp) * 3)) / 2

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
                        color = textColorBlueLight,
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
            LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Fixed(2),
             state = rememberLazyGridState()){
                itemsIndexed(optionList){ index, option ->
                    RadioButtonOptionCard(buttonTitle = option.optionText, index = index, selectedIndex = selectedIndex ){
                        selectedIndex=it
                        onAnswerSelection(index)
                    }
                }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun RadioButtonOptionCardPreview(){
    RadioButtonOptionCard(modifier = Modifier,"Yes",0,1, onOptionSelected = {})
}

@Composable
fun RadioButtonOptionCard(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    index: Int,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(6.dp))
        .padding(horizontal = 10.dp)
        .then(modifier)) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopStart,
        ) {
            ButtonOutlineWithTopIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                buttonTitle = buttonTitle,
                textColor = if(selectedIndex == index) Color.White else blueDark,
                buttonBackgroundColor = if(selectedIndex == index) blueDark else Color.White,
                buttonBorderColor = if (selectedIndex == index) {
                    blueDark
                } else {
                    lightGray2
                },
                iconTintColor = if (selectedIndex == index) {
                    white
                } else {
                    greenActiveIcon
                },
                icon = Icons.Default.Check
            ) {
                onOptionSelected(index)
            }
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(5.dp))
    }

}
@Preview(showBackground = true)
@Composable
fun RadioButtonTypeQuestionPreview() {
    val optionList= mutableListOf<AnswerOptionModel>()
    for (i in 1..5){
        optionList.add(AnswerOptionModel(i,"Option Value $i",false))
    }
    RadioButtonTypeQuestion(
       modifier = Modifier.padding(16.dp),
        questionNumber = 1,
        question = "This is a sample text. This is an example of adding border to text.",
        optionList,
        onAnswerSelection = {}
    )
}
