package com.nrlm.baselinesurvey.ui.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
    CommonProfileScreen(title = stringResource(id = R.string.profile),
        userName = viewModel?.userName?.value?: BLANK_STRING,
        userEmail = viewModel?.userEmail?.value?: BLANK_STRING,
        userMobile =viewModel?.userMobileNumber?.value?: BLANK_STRING ,
        userIdentity =viewModel?.userIdentityNumber?.value?: BLANK_STRING) {
        navController.popBackStack()
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileBSScreenPreview(){
    ProfileBSScreen(navController = rememberNavController())
}
