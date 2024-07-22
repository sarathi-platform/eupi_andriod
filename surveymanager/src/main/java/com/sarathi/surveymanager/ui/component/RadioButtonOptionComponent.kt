package com.sarathi.surveymanager.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.greenActiveIcon
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.redOffline
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.surveymanager.R

@SuppressLint("SuspiciousIndentation")
@Composable
fun RadioButtonOptionComponent(
    modifier: Modifier = Modifier,
    index: Int,
    selectedIndex: Int,
    optionsItem: OptionsUiModel,
    onOptionSelected: (OptionsUiModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp)
            .then(modifier)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopStart,
        ) {
            OutlineButtonWithIconComponent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                buttonTitle = optionsItem.description ?: "",
                textColor = if (selectedIndex == index) Color.White else blueDark,
                buttonBackgroundColor = if (selectedIndex == index) blueDark else Color.White,
                buttonBorderColor = if (selectedIndex == index) {
                    blueDark
                } else {
                    lightGray2
                },
                iconTintColor = if (selectedIndex == index) {
                    white
                } else {
                    //TODO need to remove this condition when get image from backend
                    if (index == 0)
                        greenActiveIcon
                    else
                        redOffline
                },
                //TODO need to remove this condition when get image from backend
                icon = if (index == 0)
                    painterResource(id = R.drawable.icon_check)
                else
                    painterResource(id = R.drawable.icon_close)
            ) {
                onOptionSelected(optionsItem)
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
    }

}


