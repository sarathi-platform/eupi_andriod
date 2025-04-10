package com.patsurvey.nudge.activities.ui.selectlanguage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.SarathiLogoTextView
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.navigation.AuthScreen
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.navigation.home.SettingScreens
import com.patsurvey.nudge.utils.*

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LanguageScreen(
    viewModel: LanguageViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    pageFrom:String
) {
    val context = LocalContext.current
    val networkErrorMessage = viewModel.networkErrorMessage.value
    if(networkErrorMessage.isNotEmpty()){
        showCustomToast(context,networkErrorMessage)
        viewModel.networkErrorMessage.value = BLANK_STRING
    }

    if (pageFrom == ARG_FROM_HOME){
        val permissionsState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(
            key1 = lifecycleOwner,
            effect = {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
        )
    }

    BackHandler {
        if (pageFrom == ARG_FROM_HOME){
            if(viewModel.languageRepository.prefRepo.settingOpenFrom() == PageFrom.VILLAGE_PAGE.ordinal)
                navController.popBackStack()
            else
              (context as? Activity)?.finish()
        }
        else
            navController.popBackStack()
    }
    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_16dp)
            )
            .padding(vertical = dimensionResource(id = R.dimen.padding_32dp))
            .then(modifier)
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SarathiLogoTextView()

            Text(
                text = stringResource(id = R.string.choose_language),
                color = textColorBlueLight,
                fontSize = 18.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                modifier=Modifier.padding(vertical = dimensionResource(id = R.dimen.dp_20))
                )
            Column(modifier = Modifier) {
                viewModel.languageList?.value?.let {
                    LazyColumn {

                        itemsIndexed(items = it) { index, item ->
                            LanguageItem(languageModel = item, index, viewModel.languagePosition.value) { i ->
                                viewModel.languagePosition.value = i
                            }
                        }
                        item { 
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }

        }

        Button(
            onClick = {
                try {
                    val isLanguageVillageAvailable= mutableStateOf(true)
                    viewModel.languageList.value?.get(viewModel.languagePosition.value)?.let {
                        it.id?.let { languageId->
                            viewModel.languageRepository.prefRepo.saveAppLanguageId(languageId)
                            if(!pageFrom.equals(ARG_FROM_HOME,true)){
                                viewModel.updateSelectedVillage(languageId){
                                    isLanguageVillageAvailable.value=false
//                                    viewModel.prefRepo.saveAppLanguageId(2)
                                }
                            }
                        }
                        it.langCode?.let { code ->
                            if(isLanguageVillageAvailable.value){
                                viewModel.languageRepository.prefRepo.saveAppLanguage(code)
                                (context as MainActivity).setLanguage(code)
                            }else{
                                viewModel.languageRepository.prefRepo.saveAppLanguage("en")
                                (context as MainActivity).setLanguage("en")
                            }
                        }
                    }
                    if(viewModel.languageRepository.prefRepo.settingOpenFrom() == PageFrom.VILLAGE_PAGE.ordinal){
                        navController.popBackStack(AuthScreen.AUTH_SETTING_SCREEN.route, false)
                    }else {
                        if (pageFrom.equals(ARG_FROM_HOME, true))
                            navController.navigate(ScreenRoutes.LOGIN_SCREEN.route)
                        else {
                            viewModel.languageRepository.prefRepo.savePref(PREF_OPEN_FROM_HOME, false)
                            navController.popBackStack(SettingScreens.SETTING_SCREEN.route, false)
                        }
                    }
                } catch (ex: Exception) {
                    NudgeLogger.e("LanguageScreen", "Continue Button click", ex)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(blueDark),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = stringResource(id = R.string.continue_text),
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(blueDark)
            )
        }
    }

    LaunchedEffect(key1 = Unit){
        viewModel.languageList.value?.mapIndexed{index, languageEntity ->
            if(languageEntity.langCode.equals(viewModel.languageRepository.prefRepo.getAppLanguage(),true)){
                viewModel.languagePosition.value=index
            }
        }

    }
}

@Composable
fun LanguageItem(
    languageModel: LanguageEntity,
    index: Int,
    selectedIndex: Int,
    onClick: (Int) -> Unit
) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 0.dp)
                .height(dimensionResource(id = R.dimen.height_60dp))
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = if (index == selectedIndex) languageItemActiveBorderBg else languageItemInActiveBorderBg,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(if (index == selectedIndex) languageItemActiveBg else Color.White)
                .clickable {
                    onClick(index)
                }
        ) {
            Text(
                text = languageModel.localName ?: languageModel.language,
                color = blueDark,
                fontSize = 18.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

}