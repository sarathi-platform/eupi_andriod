package com.patsurvey.nudge.activities

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import android.Manifest
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import com.patsurvey.nudge.activities.ui.theme.Nudge_Theme
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.StartFlowNavigation
import com.patsurvey.nudge.navigation.VOHomeScreenFlowNavigation
import com.patsurvey.nudge.utils.ConnectionMonitor
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnLocaleChangedListener {
    private val localizationDelegate = LocalizationActivityDelegate(this)
    @Inject
    lateinit var sharedPrefs: PrefRepo


    private lateinit var connectionLiveData: ConnectionMonitor

    private val mViewModel: MainActivityViewModel by viewModels()

    val isLoggedInLive: MutableLiveData<Boolean> = MutableLiveData(false)
    val isOnline = mutableStateOf(false)

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()
        super.onCreate(savedInstanceState)
        setContent {
            Nudge_Theme {

                val onlineStatus = remember { mutableStateOf(false) }

                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
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

                val navController = rememberNavController()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp)
                        .background(blueDark),
                ) {
                    ConstraintLayout() {
                        val (networkBanner, mainContent) = createRefs()
                        if (mViewModel.isLoggedIn.value) {
                            NetworkBanner(
                                modifier = Modifier
                                    .constrainAs(networkBanner) {
                                        top.linkTo(parent.top)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                        width = Dimension.fillToConstraints
                                    },
                                isOnline = onlineStatus.value
                            )
                        }
                        Box(modifier = Modifier.constrainAs(mainContent){
                            top.linkTo(if (mViewModel.isLoggedIn.value) networkBanner.bottom else parent.top)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        }) {
                            if (mViewModel.isLoggedIn())
                                VOHomeScreenFlowNavigation(
                                    navController = navController,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            else {
                                StartFlowNavigation(navController = navController)
                            }
                        }
                    }

                }
                connectionLiveData = ConnectionMonitor(this)
                connectionLiveData.observe(this) { isNetworkAvailable ->
                    onlineStatus.value = isNetworkAvailable
                    isOnline.value = isNetworkAvailable
                }

                isLoggedInLive.observe(this) { isLoggedIn ->
                    mViewModel.isLoggedIn.value = isLoggedIn
                }
            }
        }
    }

    override fun onDestroy() {
        connectionLiveData.removeObservers(this)
        super.onDestroy()
    }

    override fun onAfterLocaleChanged() {

    }

    override fun onBeforeLocaleChanged() {
    }

    public override fun onResume() {
        super.onResume()
        localizationDelegate.onResume(this)
    }

    override fun attachBaseContext(newBase: Context) {
        applyOverrideConfiguration(localizationDelegate.updateConfigurationLocale(newBase))
        super.attachBaseContext(newBase)
    }

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    override fun getResources(): Resources {
        return localizationDelegate.getResources(super.getResources())
    }

    fun setLanguage(language: String?) {
        localizationDelegate.setLanguage(this, language!!)
    }

    fun setLanguage(locale: Locale?) {
        localizationDelegate.setLanguage(this, locale!!)
    }

    val currentLanguage: Locale
        get() = localizationDelegate.getLanguage(this)
}