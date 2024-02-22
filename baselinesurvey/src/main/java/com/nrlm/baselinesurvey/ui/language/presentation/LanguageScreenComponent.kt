package com.nrlm.baselinesurvey.ui.language.presentation

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.nrlm.baselinesurvey.ARG_FROM_HOME
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_CODE
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.activity.MainActivity
import com.nrlm.baselinesurvey.ONE_SECOND
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.SarathiLogoTextViewComponent
import com.nrlm.baselinesurvey.ui.language.viewModel.LanguageScreenViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.textColorBlueLight
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.navigation.AuthScreen
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LanguageScreenComponent(
    viewModel: LanguageScreenViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    pageFrom: String
) {

    val context = LocalContext.current
    val networkErrorMessage = viewModel.networkErrorMessage.value
    if(networkErrorMessage.isNotEmpty()){
        showCustomToast(context,networkErrorMessage)
        viewModel.networkErrorMessage.value = BLANK_STRING
    }

    val loaderState = viewModel.loaderState.value

    LaunchedEffect(key1 = Unit){
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.init()
        delay(ONE_SECOND)
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))

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
        /*if (pageFrom == ARG_FROM_HOME){
            if(viewModel.languageRepository.prefRepo.settingOpenFrom() == PageFrom.VILLAGE_PAGE.ordinal)
                navController.popBackStack()
            else
                (context as? Activity)?.finish()
        }
        else*/
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

            SarathiLogoTextViewComponent()

            Text(
                text = stringResource(id = R.string.choose_language),
                color = textColorBlueLight,
                fontSize = 18.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                modifier=Modifier.padding(vertical = dimensionResource(id = R.dimen.dp_20))
            )
            Column(modifier = Modifier) {
                LoaderComponent(visible = loaderState.isLoaderVisible)
                AnimatedVisibility(visible = !loaderState.isLoaderVisible, enter = fadeIn(), exit = fadeOut()) {
                    if (viewModel.languagesState.value.languageList.isNotEmpty()) {
                        LazyColumn {
                            itemsIndexed(items = viewModel.languagesState.value.languageList) { index, item ->
                                LanguageItemComponent(languageState = viewModel.languagesState.value, itemIndex = index) {index ->
                                    viewModel.onEvent(LanguageSelectionEvent.ToggleSelectedLanguageId(index))
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                try {

                    viewModel.languagesState.value.let { languagesState ->
                        viewModel.onEvent(LanguageSelectionEvent.SaveSelectedLanguage)
                        if (!pageFrom.equals(ARG_FROM_HOME, true)) {
                            viewModel.onEvent(
                                LanguageSelectionEvent.UpdateSelectedVillage(
                                    languagesState.languageList[languagesState.selectedLanguageId].id
                                )
                            )
                        }
                        if (viewModel.isLanguageVillageAvailable.value) {
                            viewModel.onEvent(LanguageSelectionEvent.ChangeAppLanguage((context as MainActivity),
                                languagesState.languageList[languagesState.selectedLanguageId].langCode ?: DEFAULT_LANGUAGE_CODE
                            ))
                        } else {
                            viewModel.onEvent(LanguageSelectionEvent.ChangeAppLanguage((context as MainActivity),
                                DEFAULT_LANGUAGE_CODE
                            ))
                        }
                    }
                    if(viewModel.getLanguageScreenOpenFrom()){
                        navController.popBackStack()
                    }else {
                        navController.navigate(AuthScreen.LOGIN.route)
                    }
                } catch (ex: Exception) {
                    BaselineLogger.e("LanguageScreen", "Continue Button click", ex)
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
}