package com.patsurvey.nudge.activities.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.DropDownWithTitle
import com.patsurvey.nudge.activities.ui.theme.bgGreyLight
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonPositive

@Composable
fun BugLogggingMechanismScreen(navController: NavHostController) {

    var topPadding by remember { mutableStateOf(5.dp) }
    var startPadding by remember { mutableStateOf(16.dp) }
    var endPadding by remember { mutableStateOf(16.dp) }
    val focusManager = LocalFocusManager.current

    val txt: MutableState<String> = remember {
        mutableStateOf(BLANK_STRING)
    }


    var text by remember {
        mutableStateOf(String)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.user_bug_report_text),
                        color = textColorDark,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        style = largeTextStyle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = textColorDark)
                    }
                },
                backgroundColor = Color.White,
                elevation = 10.dp
            )
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(true) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            val (bottomActionBox, mainBox) = createRefs()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = it.calculateTopPadding() + 20.dp)
                    .constrainAs(mainBox) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    },
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DropDownView()
                Text(
                    text = "Add Description",
                    color = textColorDark,
                    style = mediumTextStyle,
                    fontSize = 14.sp,
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    value = txt.value,
                    onValueChange = {
                        txt.value = it
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        textColor = textColorDark

                    )
                )
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.background(bgGreyLight),
                    border = BorderStroke(
                        0.5.dp,
                        Color.Black
                    ),
                    shape = RectangleShape
                ) {
                    Text(
                        text = "Attach Screenshot",
                        style = mediumTextStyle,
                        color = Color.Black,
                        fontSize = 14.sp,
                    )
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                .padding(vertical = dimensionResource(id = R.dimen.dp_15))
                .constrainAs(bottomActionBox) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }) {
                ButtonPositive(
                    buttonTitle = stringResource(id = R.string.submit),
                    isArrowRequired = false,
                    isActive = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)

                ) {
                }
            }
        }
    }
}

@Composable
fun DropDownView() {
    val screens = listOf(
        "Setting",
        "Question",
        "SingleQuestion",
        "DigitalFormA",
        "DigitalFormB",
        "DigitalFormC",
        "Login",
        "Other"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(screens[0]) }
    var casteTextFieldSize by remember { mutableStateOf(Size.Zero) }
    DropDownWithTitle(
        title = "Select Issue Screen",
        items = screens,
        modifier = Modifier
            .fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = {
            expanded = !it
        },
        onDismissRequest = {
            expanded = false
        },
        mTextFieldSize = casteTextFieldSize,
        onGlobalPositioned = { coordinates ->
            casteTextFieldSize = coordinates.size.toSize()
        },
        selectedItem = selectedOptionText
    ) {
        selectedOptionText = it
        // selectedCast?.value=Pair(it.id,it.casteName)
        //  didiViewModel?.validateDidiDetails()
        expanded = false
    }
}

@Preview(showBackground = true)
@Composable
private fun bugLoggingMechPreview() {
    MaterialTheme { BugLogggingMechanismScreen(rememberNavController()) }

}



