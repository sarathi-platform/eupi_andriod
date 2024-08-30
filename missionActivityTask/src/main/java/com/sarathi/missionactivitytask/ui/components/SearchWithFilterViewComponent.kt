package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_40_dp
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.missionactivitytask.R

@Composable
fun SearchWithFilterViewComponent(
    placeholderString: String,
    modifier: Modifier = Modifier,
    filterSelected: Boolean = false,
    showFilter: Boolean = true,
    onFilterSelected: (Boolean) -> Unit,
    onSearchValueChange: (String) -> Unit
) {
    var searchString by remember {
        mutableStateOf(BLANK_STRING)
    }

    val focusManager = LocalFocusManager.current

    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier),
            color = Color.White,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                CustomOutlineTextField(
                    value = searchString,
                    onValueChange = {
                        searchString = it
                        onSearchValueChange(it)
                    },
                    placeholder = {
                        Text(
                            text = placeholderString, style = TextStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ), color = placeholderGrey
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    singleLine = true,
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = textColorDark,
                        backgroundColor = Color.White,
                        focusedIndicatorColor = borderGrey,
                        unfocusedIndicatorColor = borderGrey,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(40.dp),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_search),
                            tint = placeholderGrey,
                            contentDescription = "seach icon",
                            modifier = Modifier.absolutePadding(top = 3.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchString.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = textColorDark,
                                modifier = Modifier
                                    .absolutePadding(top = 2.dp)
                                    .clickable {
                                        searchString = BLANK_STRING
                                        onSearchValueChange(searchString)
                                    }
                            )
                        }
                    }
                )
                if (showFilter) {
                    GroupByIcon(
                        modifier = Modifier,
                        startPadding = dimen_20_dp,
                        groupingSelected = filterSelected,
                        focusManager = focusManager
                    ) {
                        onFilterSelected(it)
                    }
                }
            }
        }
    }
}

@Composable
fun GroupByIcon(
    modifier: Modifier,
    startPadding: Dp = 0.dp,
    size: Pair<Dp, Dp> = Pair(dimen_40_dp, dimen_40_dp),
    groupingSelected: Boolean,
    focusManager: FocusManager,
    onFilterSelected: (Boolean) -> Unit
) {
    Spacer(modifier = Modifier.width(startPadding))
    Card(modifier = Modifier
        .height(size.first)
        .width(size.second)
        .background(
            color = if (!groupingSelected) white else blueDark,
            shape = RoundedCornerShape(
                roundedCornerRadiusDefault
            )
        )
        .border(
            1.dp,
            color = (if (!groupingSelected) Color.LightGray else blueDark),
            shape = RoundedCornerShape(roundedCornerRadiusDefault)
        )
        .clip(RoundedCornerShape(roundedCornerRadiusDefault))
        .clickable {
            focusManager.clearFocus()
            onFilterSelected(groupingSelected)
        }
        .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (!groupingSelected) white else blueDark,
                    shape = RoundedCornerShape(
                        roundedCornerRadiusDefault
                    )
                )
                .clip(RoundedCornerShape(roundedCornerRadiusDefault)),
            contentAlignment = Alignment.Center
        ) {
            AppImageViewComponent(
                resource = if (!groupingSelected) R.drawable.ic_search_filter_unselected
                else R.drawable.ic_search_filter_selected,
                modifier = Modifier
                    .background(
                        if (!groupingSelected) Color.White else blueDark
                    )
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchWithFilterViewComponentPreview() {
    Surface(modifier = Modifier.padding(horizontal = 16.dp)) {
        SearchWithFilterViewComponent(
            placeholderString = "Search",
            filterSelected = true,
            onFilterSelected = {

            },
            onSearchValueChange = {

            }
        )
    }
}