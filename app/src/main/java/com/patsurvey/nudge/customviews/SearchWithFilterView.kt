package com.patsurvey.nudge.customviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.utils.BLANK_STRING

@Composable
fun SearchWithFilterView(
    placeholderString: String
){
    var searchString by remember {
        mutableStateOf(BLANK_STRING)
    }
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = Color.White,
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround) {
                Card(modifier = Modifier.weight(1f)
                    .border(
                        dimensionResource(id = R.dimen.dp_1),
                        Color.LightGray,
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
                    )) {

                TextField(
                    value = searchString, onValueChange ={
                        searchString=it
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    textStyle = TextStyle(
                        color = blueDark
                    ),
                    placeholder = { Text(text = placeholderString) },
                )
                }

                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.dp_20)))
                Card(modifier = Modifier
                    .border(
                        dimensionResource(id = R.dimen.dp_1),
                        Color.LightGray,
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
                    )){
                Image(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Negative Button",
                    modifier = Modifier
                        .absolutePadding(top = 2.dp)
                        .height(dimensionResource(id = R.dimen.filter_image_height))
                        .width(dimensionResource(id = R.dimen.filter_image_width)),
                    colorFilter = ColorFilter.tint(blueDark)
                )
                }
            }
        }
    }
}