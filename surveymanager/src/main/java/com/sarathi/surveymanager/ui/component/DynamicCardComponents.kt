package com.sarathi.surveymanager.ui.component

import android.net.Uri
import android.text.TextUtils
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nudge.core.formatToIndianRupee
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.sarathi.dataloadingmangement.model.uiModel.SurveyCardModel

@Composable
fun SubContainerView(
    surveyCard: SurveyCardModel?,
    isNumberFormattingRequired: Boolean = false,
    labelStyle: TextStyle = newMediumTextStyle,
    valueStyle: TextStyle = newMediumTextStyle,
    labelColor: Color = blueDark,
    valueColor: Color = blueDark
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        if (!TextUtils.isEmpty(surveyCard?.label)) {
            Text(
                text = surveyCard?.label!! + ": ",
                color = labelColor,
                style = labelStyle
            )
        }
        if (!TextUtils.isEmpty(surveyCard?.value)) {
            Text(
                text = if (isNumberFormattingRequired) formatToIndianRupee(surveyCard?.value!!) else surveyCard?.value!!,
                color = valueColor,
                style = valueStyle
            )
        }
    }
}

@Composable
fun ImageViewer(uri: Uri) {
    AsyncImage(
        model = uri,
        contentDescription = "Loaded Image",
        modifier = Modifier
            .size(15.dp)
            .padding(vertical = dimen_0_dp)
    )

}