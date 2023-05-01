package com.patsurvey.nudge.activities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.*

@Preview
@Composable
fun TransectWalkScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    var showAddTolaBox by remember { mutableStateOf(false) }
    val tolaList = remember { mutableStateListOf<Tola>() }
    var tolaToBeEdited: Tola by remember { mutableStateOf(Tola()) }

    val localDensity = LocalDensity.current
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {
        val (bottomActionBox, mainBox) = createRefs()

        Box(modifier = Modifier
            .constrainAs(mainBox) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
            .padding(top = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp)
            ) {
                if (tolaList.isNotEmpty() || showAddTolaBox) {
                    Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.home_icn),
                            contentDescription = null,
                            tint = textColorDark,
                        )
                        Text(
                            text = "Sundar Pahar",
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = textColorDark,
                            style = smallTextStyle
                        )
                    }
                    Row(
                        modifier = Modifier
                            .absolutePadding(left = 4.dp)
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        Text(
                            text = "VO:",
                            modifier = Modifier,
                            color = textColorDark,
                            style = smallTextStyle
                        )
                        Text(
                            text = "Sundar Pahar Mahila Mandal",
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = textColorDark,
                            style = smallTextStyle
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (tolaList.isNotEmpty() || showAddTolaBox) {
                        Text(
                            text = stringResource(id = R.string.transect_wale_title),
                            style = largeTextStyle,
                            color = blueDark,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (tolaList.isNotEmpty()) {
                        Spacer(modifier = Modifier.padding(14.dp))
                        ButtonOutline(
                            modifier = Modifier.weight(1f),
                        ) {
                            if (!showAddTolaBox)
                                showAddTolaBox = true
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    if (tolaList.isNotEmpty()) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = textColorDark,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = NotoSans
                                    )
                                ) {
                                    append("Showing")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = textColorDark,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = NotoSans
                                    )
                                ) {
                                    append(" ${tolaList.size}")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = textColorDark,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = NotoSans
                                    )
                                ) {
                                    append(" added Tolas")
                                }
                            }
                        )
                    }

                    AnimatedVisibility(visible = showAddTolaBox) {
                        AddTolaBox(
                            tolaName = tolaToBeEdited.name,
                            isLocationAvailable = (tolaToBeEdited.location.lat != null && tolaToBeEdited.location.long != null),
                            onSaveClicked = { name, location ->

                                if (tolaToBeEdited.name.isNotEmpty()) {
                                    val index = tolaList.indexOf(tolaToBeEdited)
                                    tolaList.remove(tolaToBeEdited)
                                    tolaList.add(
                                        index, Tola(
                                            name,
                                            location ?: LocationCoordinates()
                                        )
                                    )
                                } else {
                                    tolaList.add(Tola(name, location ?: LocationCoordinates()))
                                }

                                tolaToBeEdited = Tola()
                                showAddTolaBox = false
                            },
                            onCancelClicked = {
                                showAddTolaBox = false
                            }
                        )
                    }

                    if (tolaList.isEmpty() && !showAddTolaBox) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(id = R.string.transect_wale_title),
                                    style = largeTextStyle,
                                    color = blueDark,
                                    modifier = Modifier,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = textColorDark,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Normal,
                                                fontFamily = NotoSans
                                            )
                                        ) {
                                            append("No Tolas Added")
                                        }
                                    },
                                    modifier = Modifier.padding(top = 32.dp)
                                )
                                BlueButtonWithIcon(
                                    buttonText = stringResource(id = R.string.add_tola),
                                    icon = Icons.Default.Add
                                ) {
                                    if (!showAddTolaBox)
                                        showAddTolaBox = true
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = bottomPadding),
                            contentPadding = PaddingValues(10.dp)
                        ) {
                            items(tolaList) { tola ->
                                Box(modifier = Modifier.padding(vertical = 10.dp)) {

                                    TolaBox(
                                        tolaName = tola.name,
                                        isLocationAvailable = (tola.location.lat != null && tola.location.long != null),
                                        deleteButtonClicked = {
                                            tolaList.remove(tola)
                                        },
                                        editButtonClicked = {
                                            tolaToBeEdited = tola
                                            showAddTolaBox = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (tolaList.isNotEmpty()) {
            DoubleButtonBox(
                modifier = Modifier
                    .constrainAs(bottomActionBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .onGloballyPositioned { coordinates ->
                        bottomPadding = with(localDensity) {
                            coordinates.size.height.toDp()
                        }
                    },

                positiveButtonText = stringResource(id = R.string.mark_complete_text),
                negativeButtonRequired = false,
                positiveButtonOnClick = {
                    if (tolaList.isNotEmpty()) {
                        //TODO: mark tola complete
                    } else {
                        //TODO: Show error
                    }
                },
                negativeButtonOnClick = {
//
                }
            )
        }
    }
}