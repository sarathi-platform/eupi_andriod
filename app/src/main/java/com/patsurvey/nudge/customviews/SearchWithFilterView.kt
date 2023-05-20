package com.patsurvey.nudge.customviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.CustomOutlineTextField
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.borderGrey
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.utils.BLANK_STRING

@Composable
fun SearchWithFilterView(
    placeholderString: String,
    modifier: Modifier = Modifier,
    filterSelected: Boolean = false,
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
//                Card(
//                    modifier = Modifier
//                        .weight(1f)
//                        .border(
//                            dimensionResource(id = R.dimen.dp_1),
//                            Color.LightGray,
//                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
//                        )
//                ) {
                    CustomOutlineTextField(
                        value = searchString,
                        onValueChange = {
                            searchString = it
                            onSearchValueChange(it)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = textColorDark,
                            backgroundColor = Color.White,
                            focusedIndicatorColor = borderGrey,
                            unfocusedIndicatorColor = borderGrey,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                        ),
                        textStyle = TextStyle(
                            color = blueDark
                        ),
                        maxLines = 1,
                        placeholder = { Text(text = placeholderString) },
                    )
//                }

                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.dp_20)))
                Card(modifier = Modifier
                    .height(dimensionResource(id = R.dimen.filter_image_height))
                    .width(dimensionResource(id = R.dimen.filter_image_width))
                    .background(color = Color.White)
                    .border(
                        dimensionResource(id = R.dimen.dp_1),
                        color = (if (!filterSelected) Color.LightGray else blueDark),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
                    )
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
                            .padding(horizontal = 15.dp)
                            .padding(vertical = 15.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchWithFilterPreview() {
    SearchWithFilterView(placeholderString = "Search Didi", onFilterSelected = {

    }, onSearchValueChange = {

    })
}