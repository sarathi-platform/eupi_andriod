package com.sarathi.surveymanager.ui.screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.smallTextStyleMediumWeight2
import com.sarathi.surveymanager.ui.component.DropDownComponent

@Composable
fun LivelihoodDropDownScreen(
    navController: NavController = rememberNavController(),
//    message: String = BLANK_STRING,
//    onNavigateBack: () -> Unit

){

    val screens = listOf(
        "Goatery",
        "Piggery",
        "Agriculture",
        "Small Business",
        "Fishery Farming",
    )
    var casteTextFieldSize by remember { mutableStateOf(Size.Zero) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimen_8_dp, bottom = dimen_10_dp)
            .padding(end = dimen_16_dp, start = dimen_8_dp),
    ) {

        Text(
            text = "1. Select first livelihood for didi",
            color = blueDark,
            modifier = Modifier
                .fillMaxWidth(),
            softWrap = true,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = smallTextStyleMediumWeight2
        )

        DropDownComponent(
            items = screens,
            modifier = Modifier.padding(10.dp),
            mTextFieldSize = casteTextFieldSize,
            onExpandedChange = {},
            onDismissRequest = {},
            onGlobalPositioned = {}
        ) {
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun LivelihoodDropDownScreenPreview() {
//   LivelihoodDropDownScreen {
//
//   }
//}