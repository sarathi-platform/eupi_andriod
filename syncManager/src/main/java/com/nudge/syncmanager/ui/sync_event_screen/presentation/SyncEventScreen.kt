package com.nudge.syncmanager.ui.sync_event_screen.presentation


import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.syncmanager.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SyncEventScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var ProgBarState by remember { mutableStateOf(0.1f) }

    var selectedIndex by remember { mutableStateOf(0) }

//    val loaderState = viewModel.loaderState

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier),
        topBar = {
            TopAppBar(
                title = {
                        Text(text = "Sync",
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp
                            ),
                           )


                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
                backgroundColor = Color.White
            )
        }
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(10.dp)
                .fillMaxWidth()

        ) {
            Button(onClick = {}, shape = RoundedCornerShape(30), colors = ButtonDefaults.buttonColors(Color(0xFFFF222E50)),
                modifier = Modifier.fillMaxWidth()) {
                Text(text = "Sync",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color =  Color(0xFFEFF2FC)
                    ),)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable { },
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ) {
                    Spacer(modifier = Modifier.padding(5.dp))

                    Row {
                        Icon(
                            painter = painterResource(
                                id =  R.drawable.sync_icon
                            ),
                            contentDescription = "sync icon",
                            tint = Color.Black,
                            modifier = Modifier.height(20.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Text(

                            text = "BaseLine",
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp
                            ),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(
                                id =  R.drawable.right_arrow
                            ),
                            contentDescription = "home icon",
                            tint = Color.Black,
                            modifier = Modifier.height(20.dp)
                        )


                    }

                    Spacer(modifier = Modifier.padding(15.dp))

                    LinearProgressIndicator(
                        progress = animateFloatAsState(
                            targetValue = ProgBarState,
                            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
                        ).value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(RoundedCornerShape(8.dp)),


                        backgroundColor = Color(0xFFFFF7F7F7),
                        color = Color(0xFFFF222E50),


                        )

//


                }
                }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable { },
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ) {
                    Spacer(modifier = Modifier.padding(5.dp))

                    Row {
                        Icon(
                            painter = painterResource(
                                id =  R.drawable.sync_icon
                            ),
                            contentDescription = "sync",
                            tint = Color.Black,
                            modifier = Modifier.height(20.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Text(

                            text = "Image",
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp
                            ),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(
                                id =  R.drawable.right_arrow
                            ),
                            contentDescription = "home icon",
                            tint = Color.Black,
                            modifier = Modifier.height(20.dp)
                        )


                    }

                    Spacer(modifier = Modifier.padding(15.dp))

                    LinearProgressIndicator(
                        progress = animateFloatAsState(
                            targetValue = ProgBarState,
                            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
                        ).value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        backgroundColor = Color(0xFFFFF7F7F7),
                        color = Color(0xFFFF222E50),


                        )

//


                }
            }


//
        }

    }
}
@Preview(showBackground = true)
@Composable
fun SyncEventScreenPreview(){
    SyncEventScreen(navController = rememberNavController())
}