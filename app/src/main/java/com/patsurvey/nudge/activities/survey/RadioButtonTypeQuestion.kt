package com.patsurvey.nudge.activities.survey

import android.util.Log
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.ButtonOutlineWithTopIcon
import com.patsurvey.nudge.model.dataModel.AnswerOptionModel
import kotlinx.coroutines.coroutineScope


@Composable
fun RadioButtonTypeQuestion(
    modifier: Modifier,
    questionNumber: Int,
    question: String,
    selectedOptionIndex: Int=-1,
    optionList: List<AnswerOptionModel>,
    onAnswerSelection: (Int) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(selectedOptionIndex) }

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
                    RadioButtonOptionCard(Modifier.disableSplitMotionEvents(),buttonTitle = option.optionText, index = index, optionValue = option.optionValue?:0, selectedIndex = selectedIndex ){
                        selectedIndex=it
                        onAnswerSelection(index)
                    }
                }
            }
        }

    }
}

fun Modifier.disableSplitMotionEvents() =
    pointerInput(Unit) {
        coroutineScope {
            var currentId: Long = -1L
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { pointerInfo ->
                        when {
                            pointerInfo.pressed && currentId == -1L -> currentId = pointerInfo.id.value
                            pointerInfo.pressed.not() && currentId == pointerInfo.id.value -> currentId = -1
                            pointerInfo.id.value != currentId && currentId != -1L -> pointerInfo.consume()
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun RadioButtonOptionCardPreview(){
    RadioButtonOptionCard(modifier = Modifier,"Yes",0,1, onOptionSelected = {}, optionValue = 1)
}

@Composable
fun RadioButtonOptionCard(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    index: Int,
    selectedIndex: Int,
    optionValue:Int,
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
                    if (optionValue == 1)
                        greenActiveIcon
                    else
                        redOffline
                },
                icon = if (optionValue == 1) painterResource(id = R.drawable.icon_check) else painterResource(
                    id = R.drawable.icon_close
                )
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
        selectedOptionIndex = -1,
        question = "This is a sample text. This is an example of adding border to text.",
        optionList = optionList,
        onAnswerSelection = {}
    )
}
