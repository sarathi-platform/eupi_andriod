package com.nrlm.baselinesurvey.ui.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel?.getAllUserDetails(context)
    }
    CommonProfileScreen(title = stringResource(id = R.string.profile),
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
