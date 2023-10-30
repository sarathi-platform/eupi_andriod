package com.nrlm.baselinesurvey.ui.language.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBorderBg
import com.nrlm.baselinesurvey.ui.theme.languageItemInActiveBorderBg

@Composable
fun LanguageItemComponent(
    modifier: Modifier = Modifier,
    languageState: LanguagesState,
    itemIndex: Int,
    onClick: (Int) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 0.dp)
            .height(dimensionResource(id = R.dimen.height_60dp))
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = if (itemIndex == languageState.selectedLanguageId) languageItemActiveBorderBg else languageItemInActiveBorderBg,
                shape = RoundedCornerShape(6.dp)
            )
            .background(if (itemIndex == languageState.selectedLanguageId) languageItemActiveBg else Color.White)
            .clickable {
                onClick(itemIndex)
            }
    ) {
        Text(
            text = languageState.languageList[itemIndex].localName ?: languageState.languageList[itemIndex].language,
            color = blueDark,
            fontSize = 18.sp,
            fontFamily = NotoSans,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }

}