package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sarathi.missionactivitytask.ui.theme.blueDark
import com.sarathi.missionactivitytask.ui.theme.borderGreyLight
import com.sarathi.missionactivitytask.ui.theme.dimen_1_dp
import com.sarathi.missionactivitytask.ui.theme.dimen_2_dp
import com.sarathi.missionactivitytask.ui.theme.languageItemActiveBg
import com.sarathi.missionactivitytask.ui.theme.roundedCornerRadiusDefault
import com.sarathi.missionactivitytask.ui.theme.smallTextStyleMediumWeight
import com.sarathi.missionactivitytask.ui.theme.smallTextStyleNormalWeight
import com.sarathi.missionactivitytask.ui.theme.white

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    isIcon: Boolean = true
){
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(roundedCornerRadiusDefault),
            colors = ButtonDefaults.buttonColors(
                containerColor = blueDark,
                contentColor = white
            ),
            modifier = modifier
        ) {
            Text(
                text = text,
                style = smallTextStyleMediumWeight
            )
            if(isIcon) {
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = "Proceed",
                    tint = white,
                    modifier = Modifier.absolutePadding(top = dimen_2_dp, left = dimen_2_dp)
                )
            }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
){
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        border = BorderStroke(dimen_1_dp, borderGreyLight),
        colors = ButtonDefaults.buttonColors(
            containerColor = languageItemActiveBg, contentColor = blueDark
        ),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = smallTextStyleNormalWeight
        )
    }
}
