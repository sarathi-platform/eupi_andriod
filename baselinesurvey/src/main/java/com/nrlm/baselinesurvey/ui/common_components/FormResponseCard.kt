package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.model.FormResponseObjectDto
import com.nrlm.baselinesurvey.ui.question_screen.viewmodel.QuestionScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_14_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_56_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.greyLightColor
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun FormResponseCard(
    modifier: Modifier = Modifier,
    householdMemberDto: FormResponseObjectDto,
    viewModel: BaseViewModel,
    isPictureRequired: Boolean = true,
    onDelete: (referenceId: String) -> Unit,
    onUpdate: (referenceId: String) -> Unit
) {

    val questionScreenViewModel = viewModel as QuestionScreenViewModel

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

        val dividerHeight = remember {
            mutableStateOf(0.dp)
        }

        Column(modifier = Modifier
            .background(white)
            .padding(vertical = dimen_8_dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_8_dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPictureRequired) {
                    CircularImageViewComponent(
                        modifier = Modifier
                            .height(dimen_56_dp)
                            .width(dimen_56_dp)
                    )
                }
                Spacer(modifier = Modifier.width(dimen_14_dp))
                Column {
                    Text(text = householdMemberDto.memberDetailsMap[questionScreenViewModel.optionItemEntityList.find { it.display?.contains("Name", ignoreCase = true)!! }?.optionId] ?: BLANK_STRING)
                    Text(text = buildString {
                        this.append(householdMemberDto.memberDetailsMap[questionScreenViewModel.optionItemEntityList.find { it.display?.contains("Relationship", ignoreCase = true)!! }?.optionId] ?: BLANK_STRING)
                        this.append(" | ")
                        this.append(householdMemberDto.memberDetailsMap[questionScreenViewModel.optionItemEntityList.find { it.display?.contains("Age", ignoreCase = true)!! }?.optionId] ?: BLANK_STRING)
                    })
                }
            }
            Spacer(modifier = Modifier.height(dimen_8_dp))
            Divider(thickness = dimen_1_dp, modifier = Modifier.fillMaxWidth(), color = borderGreyLight)
            Row(modifier = Modifier
                .fillMaxWidth()
            ) {
                TextButton(onClick = { onUpdate(householdMemberDto.referenceId) }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = blueDark)
                ) {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit Button", tint = blueDark)
                }
                Divider(
                    color = borderGreyLight,
                    modifier = Modifier
                        .fillMaxHeight()  //fill the max height
                        .width(1.dp)
                )
                TextButton(onClick = { onDelete(householdMemberDto.referenceId) }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = blueDark)
                ) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete Button", tint = blueDark)
                }
            }
        }
    }
}

/*
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun FormResponseCardPreview() {

}*/
