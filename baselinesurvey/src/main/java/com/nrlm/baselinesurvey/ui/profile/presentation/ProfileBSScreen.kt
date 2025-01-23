package com.nrlm.baselinesurvey.ui.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.common_profile.CommonProfileScreen
import com.nrlm.baselinesurvey.ui.profile.viewmodel.ProfileBSViewModel

@Composable
fun ProfileBSScreen(
     viewModel:ProfileBSViewModel?=null,
     navController: NavController
){
    CommonProfileScreen(
        title = viewModel?.stringResource(R.string.profile) ?: BLANK_STRING,
        userDetailList = viewModel?.userDetailList ?: emptyList()
    ) {
        navController.popBackStack()
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileBSScreenPreview(){
    ProfileBSScreen(navController = rememberNavController())
}
