package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white


@Composable
fun ToolbarWithMenuComponent(title:String,
                             modifier: Modifier,
                             navController:NavController?= rememberNavController(),
                             onBackIconClick:()->Unit,
                             onBottomUI: @Composable ()->Unit,
                             onContentUI: @Composable (PaddingValues)->Unit){
    Scaffold(
        modifier = modifier,
        containerColor = white,
        topBar = {
            TopAppBar(

                title = {
                    Row(modifier=Modifier) {
                        IconButton(
                            onClick = { onBackIconClick()},
                            modifier = Modifier
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back Button", tint = Color.Black)
                        }
                        Row(  modifier = Modifier.align(Alignment.CenterVertically).padding(bottom = 5.dp)
                            .fillMaxWidth()) {
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
                        navController?.navigate(Graph.SETTING_GRAPH)
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
        onContentUI(it)
    }
}

@Preview(showBackground = true)
@Composable
fun ToolbarWithMenuComponent(){
    ToolbarWithMenuComponent(title = "Mission Summary", modifier = Modifier, onBackIconClick = {

    }, onContentUI = {

    }, onBottomUI = {})
}