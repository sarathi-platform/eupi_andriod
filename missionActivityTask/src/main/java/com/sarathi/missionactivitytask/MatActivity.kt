package com.sarathi.missionactivitytask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sarathi.missionactivitytask.ui.mission_screen.viewmodel.MissionScreenViewModel
import com.sarathi.missionactivitytask.ui.theme.The_nudgeTheme
import com.sarathi.missionactivitytask.ui.theme.white
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatActivity : ComponentActivity() {
    private val mViewModel: MissionScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            The_nudgeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = white
                ) {
                    ConstraintLayout() {
                        val (networkBanner, mainContent) = createRefs()
//                        if (mViewModel.isLoggedIn()) {
//                            NetworkBanner(
//                                modifier = Modifier
//                                    .constrainAs(networkBanner) {
//                                        top.linkTo(parent.top)
//                                        start.linkTo(parent.start)
//                                        end.linkTo(parent.end)
//                                        width = Dimension.fillToConstraints
//                                    },
//                                isOnline = BaselineCore.isOnline.value
//                            )
//                        }
                        Box(modifier = Modifier
                            .constrainAs(mainContent) {
                                top.linkTo(if (true) networkBanner.bottom else parent.top)
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom)
                                height = Dimension.fillToConstraints
                            }
                            .fillMaxSize()) {
//                            RootNavigationGraph(navController = rememberNavController())
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CheckMissionScreen(){
//    BasicMissionCard(
//        title = "CSG disbursement to Didi's",
//        status = StatusEnum.InProgress,
//        pendingCount = 2,
//        totalCount = 10,
//        primaryButtonText = "Start",
//        countStatusText = "ActivityResponse Pending",
//        topHeaderText = "Due on 22nd March",
//        needToShowProgressBar = true,
//    )
}