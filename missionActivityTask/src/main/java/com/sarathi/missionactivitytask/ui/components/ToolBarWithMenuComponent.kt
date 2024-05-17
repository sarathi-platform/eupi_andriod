package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.basic_content.component.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.theme.blueDark
import com.sarathi.missionactivitytask.ui.theme.largeTextStyle
import com.sarathi.missionactivitytask.ui.theme.textColorDark
import com.sarathi.missionactivitytask.ui.theme.white


@Composable
fun ToolBarWithMenuComponent(
    title: String,
    modifier: Modifier,
    isSearch: Boolean = false,
    iconResId: Int = R.drawable.arrow_left,
    navController: NavController? = rememberNavController(),
    onBackIconClick: () -> Unit,
    onSearchValueChange: (String) -> Unit,
    onBottomUI: @Composable () -> Unit,
    onContentUI: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = white,
        topBar = {
            TopAppBar(

                title = {
                    Row(modifier = Modifier) {
                        IconButton(
                            onClick = { onBackIconClick() },
                            modifier = Modifier
                        ) {
                            Icon(
                                painter = painterResource(id = iconResId),
                                contentDescription = "Back Button",
                                tint = Color.Black
                            )
                        }
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(bottom = 5.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = title,
                                style = largeTextStyle,
                                color = textColorDark,
                                textAlign = TextAlign.Center,

                                )
                        }

                    }
                },
                actions = {
                    IconButton(onClick = {
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.more_icon),
                            contentDescription = "more action button",
                            tint = blueDark,
                            modifier = Modifier
                                .padding(10.dp)
                        )
                    }
                },
                backgroundColor = Color.White,
                elevation = 10.dp
            )
        },
        bottomBar = {

            onBottomUI()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 75.dp)
        ) {
            if (isSearch) {
                SearchWithFilterViewComponent(placeholderString = "Search",
                    filterSelected = false,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    showFilter = false,
                    onFilterSelected = {},
                    onSearchValueChange = { queryTerm ->
                    })
            }
            onContentUI(it)
        }


    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMatToolBarWithMenuComponent() {
//    ToolBarWithMenuComponent(title = "Mission Summary", modifier = Modifier, onBackIconClick = {
//
//    }, onContentUI = {
//
//    }, onBottomUI = {})
}