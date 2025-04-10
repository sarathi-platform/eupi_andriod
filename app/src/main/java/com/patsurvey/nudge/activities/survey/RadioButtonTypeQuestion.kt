package com.patsurvey.nudge.activities.survey

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.patsurvey.nudge.customviews.htmltext.HtmlText
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.ButtonOutlineWithTopIcon
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.visible


@Composable
fun RadioButtonTypeQuestion(
    modifier: Modifier,
    questionNumber: Int,
    question: String,
    isLastIndex:Boolean=false,
    selectedOptionIndex: Int=-1,
    optionList: List<OptionsItem?>?,
    isAnswerSelected:Boolean =false,
    onAnswerSelection: (Int,Boolean) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(selectedOptionIndex) }

    Box {
        ConstraintLayout(
            modifier = modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        ) {
            val (questionBox, submitBox) = createRefs()
            Column(modifier = modifier.
             constrainAs(questionBox){
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
             }) {
                HtmlText(
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
                    }.toString(),
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = textColorDark
                    ),
                    //color = textColorDark
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxWidth(), columns = GridCells.Fixed(2),
                        state = rememberLazyGridState()
                    ) {
                        itemsIndexed(optionList ?: emptyList()) { index, option ->
                            RadioButtonOptionCard(
                                buttonTitle = option?.display ?: BLANK_STRING,
                                index = index,
                                optionValue = option?.optionValue ?: 0,
                                selectedIndex = selectedIndex
                            ) {
                                if(!isAnswerSelected) {
                                    selectedIndex = it
                                    onAnswerSelection(index,false)
                                }
                            }
                        }
                    }
                }


            }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .visible(selectedOptionIndex != -1 && isLastIndex)
                        .padding(horizontal = 5.dp)
                        .padding(top = 20.dp)
                        .padding(bottom = 8.dp)
                        .constrainAs(submitBox) {
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        }
                ) {
                    ButtonPositive(
                        buttonTitle = stringResource(id = R.string.next),
                        isArrowRequired = false,
                        isActive = true,
                        modifier = Modifier.height(45.dp)
                    ) {
                        if(!isAnswerSelected)
                             onAnswerSelection(selectedIndex,true)
                    }
                }


        }
    }
}




@Preview(showBackground = true)
@Composable
fun RadioButtonOptionCardPreview(){
    RadioButtonOptionCard(modifier = Modifier,"Yes",0,1, optionValue = 0,onOptionSelected = {})
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
