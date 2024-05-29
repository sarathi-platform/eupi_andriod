package com.nudge.syncmanager.ui.sync_event_screen.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.syncmanager.ui.sync_event_screen.theme.blueDark
import com.nudge.syncmanager.ui.sync_event_screen.theme.buttonBgColor
import com.nudge.syncmanager.ui.sync_event_screen.theme.dimen_10_dp
import com.nudge.syncmanager.R
import com.nudge.syncmanager.ui.common_sync_ui.CommonSyncScreen

@Composable
fun SyncEventScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var ProgBarState by remember { mutableStateOf(0.91f) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier),
        topBar = {
            TopAppBar(
                title = {
                        Text(text =stringResource( R.string.sync),
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
                .padding(dimen_10_dp)
                .fillMaxWidth()

        ) {
            Button(onClick = {}, shape = RoundedCornerShape(30), colors = ButtonDefaults.buttonColors(
                blueDark
            ),
                modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource( R.string.sync),
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color =  buttonBgColor
                    ),)
            }
            CommonSyncScreen(title =stringResource( R.string.baseLine) , ProgBarState =ProgBarState ) {

            }
            CommonSyncScreen(title =stringResource( R.string.image) , ProgBarState =ProgBarState ) {

            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SyncEventScreenPreview(){
    SyncEventScreen(navController = rememberNavController())
}