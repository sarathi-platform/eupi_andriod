package com.patsurvey.nudge.activities

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.BlueButton

//@Preview
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProgressScreen(
    modifier: Modifier = Modifier,
    stepsNavHostController: NavHostController,
) {

    val context = LocalContext.current
    val villageName = arrayOf("Kanpur", "Lucknow", "Noida", "Hapur", "Meerut")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Select Village") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp, start = 16.dp, end = 16.dp)
            .then(modifier)
    ) {
        Column(
            Modifier
                .background(Color.White)
        ) {

            Text(
                text = "Akhilesh Negi",
                color = textColorDark,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = largeTextStyle
            )
            Text(
                text = "ID: 234567",
                color = textColorDark,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = smallTextStyle
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .background(Color.Transparent)
                    .clip(RoundedCornerShape(6.dp))
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                TextField(
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            painterResource(id = R.drawable.baseline_keyboard_arrow_down),
                            contentDescription = "drop down menu icon",
                            tint = blueDark
                        )
//                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .border(1.dp, dropDownBg)
                        .background(dropDownBg)
                        .clip(RoundedCornerShape(6.dp))
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = blueDark,
                        disabledTextColor = Color.Transparent,
                        backgroundColor = dropDownBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(6.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    Modifier.fillMaxWidth().background(dropDownBg)
                ) {
                    villageName.forEach { item ->
                        DropdownMenuItem(
                            content = { Text(text = item, color = blueDark, modifier = Modifier.fillMaxWidth().background(dropDownBg), style = smallTextStyle) },
                            onClick = {
                                selectedText = item
                                expanded = false
                                Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().background(dropDownBg)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())

            ) {
                StepsBox(
                    boxTitle = "Transect Walk",
                    stepNo = 1,
                    isCompleted = false,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    stepsNavHostController.navigate(ScreenRoutes.TRANSECT_WALK_SCREEN.route)
                }
                StepsBox(
                    boxTitle = "Social Mapping",
                    stepNo = 2,
                    isCompleted = false,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Log.i("Progress screen", "opening Social Mapping")
                    context.startActivity(Intent(context, SocialMappingActivity::class.java))
                }
                StepsBox(
                    boxTitle = "Participatory " +
                            "Wealth Ranking",
                    stepNo = 3,
                    isCompleted = false,
                    modifier = Modifier
                        .fillMaxWidth()
                ){

                }
                StepsBox(
                    boxTitle = "Pat Survey",
                    stepNo = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                }
                StepsBox(
                    boxTitle = "VO Endorsement",
                    stepNo = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                }
                StepsBox(
                    boxTitle = "BMP Approval",
                    stepNo = 6,
                    shouldBeActive = false,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                }
            }
        }
    }
}

@Composable
fun StepsBox(
    modifier: Modifier = Modifier,
    boxTitle: String,
    stepNo: Int,
    isCompleted: Boolean = false,
    shouldBeActive: Boolean = true,
    onclick: () -> Unit
) {
    val dividerMargins = 32.dp
    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .border(
                width = 0.dp,
                color = Color.Transparent,
            )
            .then(modifier)
    ) {
        val (step_no, stepBox, divider1, divider2) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = if (isCompleted) green else greyBorder,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(Color.White)
                .constrainAs(stepBox) {
                    start.linkTo(parent.start)
                    top.linkTo(step_no.bottom, margin = -16.dp)
                }

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isCompleted) greenLight else Color.White)
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(modifier = Modifier.absolutePadding(left = 10.dp).weight(1.2f)) {
                    Text(
                        text = boxTitle/* "Transect Walk"*/,
                        color = textColorDark,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, end = 48.dp),
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = mediumTextStyle
                    )
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(size = 10.dp)
                        ) {
                            drawCircle(
                                color = if (isCompleted) greenDark else greyIndicator,
                            )
                        }
                        Text(
                            text = if (isCompleted) "Completed" else "Not Started",
                            color = if (isCompleted) greenDark else textColorDark,
                            style = smallerTextStyle,
                            modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)

                        )
                    }
                    if (isCompleted)
                        Spacer(modifier = Modifier.height(20.dp))
                }

                if (!isCompleted) {
                    BlueButton(
                        buttonText = "Start Now",
                        isArrowRequired = true,
                        shouldBeActive = shouldBeActive,
                        modifier = Modifier.padding(end = 14.dp).weight(0.8f),
                        onClick = {
                            onclick()
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = greyBorder,
                    shape = RoundedCornerShape(100.dp)
                )
                .background(Color.White)
                .padding(6.dp)
                .constrainAs(step_no) {
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(parent.top)
                }
        ) {
            Text(
                text = "Step $stepNo",
                color = textColorDark,
                style = smallerTextStyleNormalWeight,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 16.dp)

            )
        }

        if (stepNo < 5) {
            Divider(
                color = greyBorder,
                modifier = Modifier
                    .height(10.dp)  //fill the max height
                    .width(1.dp)
                    .constrainAs(divider1) {
                        start.linkTo(parent.start, margin = dividerMargins)
                        top.linkTo(stepBox.bottom)
                    }
                    .padding(vertical = 2.dp)
            )

            Divider(
                color = greyBorder,
                modifier = Modifier
                    .height(10.dp)  //fill the max height
                    .width(1.dp)
                    .constrainAs(divider2) {
                        start.linkTo(parent.start, margin = dividerMargins)
                        top.linkTo(divider1.bottom)
                    }
                    .padding(vertical = 2.dp)
            )
        }
    }
}