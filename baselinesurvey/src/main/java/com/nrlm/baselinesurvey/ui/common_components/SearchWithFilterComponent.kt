package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGrey
import com.nrlm.baselinesurvey.ui.theme.placeholderGrey
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white

@OptIn(ExperimentalMaterial3Api::class)
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
                    colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
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
                                        searchString = ""
                                        onSearchValueChange(searchString)
                                    }
                            )
                        }
                    }
                )
                if (showFilter) {
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.dp_20)))
                    Card(modifier = Modifier
                        .height(/*dimensionResource(id = R.dimen.filter_image_height)*/40.dp)
                        .width(/*dimensionResource(id = R.dimen.filter_image_width)*/40.dp)
                        .background(color = if (!filterSelected) white else blueDark, shape = RoundedCornerShape(
                            roundedCornerRadiusDefault))
                        .border(
                            dimensionResource(id = R.dimen.dp_1),
                            color = (if (!filterSelected) Color.LightGray else blueDark),
                            shape = RoundedCornerShape(roundedCornerRadiusDefault)
                        )
                        .clip(RoundedCornerShape(roundedCornerRadiusDefault))
                        .clickable {
                            focusManager.clearFocus()
                            onFilterSelected(filterSelected)
                        }) {
                        AppImageView(
                            resource = if (!filterSelected) R.drawable.ic_search_filter_unselected
                            else R.drawable.ic_search_filter_selected,
                            modifier = Modifier
                                .background(
                                    if (!filterSelected) Color.White else blueDark
                                )
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                        )
                    }
                }
            }
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