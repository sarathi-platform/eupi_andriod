package com.nrlm.baselinesurvey.activity

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.compose.rememberNavController
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener
import com.nrlm.baselinesurvey.activity.viewmodel.MainActivityViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.ui.theme.The_nudgeTheme
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.ConnectionMonitor
import com.nrlm.baselinesurvey.navigation.navgraph.RootNavigationGraph
import com.nrlm.baselinesurvey.ui.common_components.NetworkBanner
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnLocaleChangedListener {
    private val localizationDelegate = LocalizationActivityDelegate(this)

    @Inject
    lateinit var sharedPrefs: PrefRepo

    private val mViewModel: MainActivityViewModel by viewModels()

    private lateinit var connectionLiveData: ConnectionMonitor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            The_nudgeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConstraintLayout() {
                        val (networkBanner, mainContent) = createRefs()
                        if (mViewModel.isLoggedIn()) {
                            NetworkBanner(
                                modifier = Modifier
                                    .constrainAs(networkBanner) {
                                        top.linkTo(parent.top)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                        width = Dimension.fillToConstraints
                                    },
                                isOnline = BaselineCore.isOnline.value
                            )
                        }
                        Box(modifier = Modifier.constrainAs(mainContent){
                            top.linkTo(if (mViewModel.isLoggedIn()) networkBanner.bottom else parent.top)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        }) {
                            RootNavigationGraph(navController = rememberNavController(), sharedPrefs)
                        }
                    }
                }
            }
        }

        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()

        connectionLiveData = BaselineCore.getConnectionMonitorLive()
        connectionLiveData.observe(this) { isNetworkAvailable ->
            BaselineCore.isOnline.value = isNetworkAvailable
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        connectionLiveData.removeObservers(this)
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

    override fun onAfterLocaleChanged() {

    }

    override fun onBeforeLocaleChanged() {

    }

}