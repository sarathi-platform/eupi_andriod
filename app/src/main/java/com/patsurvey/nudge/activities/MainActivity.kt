package com.patsurvey.nudge.activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.rememberNavController
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.nudge.core.enums.NetworkSpeed
import com.patsurvey.nudge.R
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.ui.theme.Nudge_Theme
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.analytics.AnalyticsHelper
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.download.AndroidDownloader
import com.patsurvey.nudge.navigation.navgraph.RootNavigationGraph
import com.patsurvey.nudge.smsread.SmsBroadcastReceiver
import com.patsurvey.nudge.utils.ConnectionMonitor
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.SENDER_NUMBER
import com.patsurvey.nudge.utils.showCustomToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnLocaleChangedListener {
    private val localizationDelegate = LocalizationActivityDelegate(this)

    @Inject
    lateinit var sharedPrefs: PrefRepo


    private lateinit var connectionLiveData: ConnectionMonitor

    private val mViewModel: MainActivityViewModel by viewModels()

    val isLoggedInLive: MutableLiveData<Boolean> = MutableLiveData(false)
    val isOnline = mutableStateOf(true)
    val connectionSpeedType = mutableStateOf("")
    val connectionSpeed = mutableStateOf(0)
    val isBackFromSummary = mutableStateOf(false)
    val isFilterApplied = mutableStateOf(false)

    var downloader: AndroidDownloader? = null
    var quesImageList = mutableListOf<String>()
    private val REQ_USER_CONSENT = 200
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        /*val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())*/
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            setTheme(R.style.Android_starter_project_blow_lollipop)
        }
        super.onCreate(savedInstanceState)
        getSyncEnabled()
        setContent {
            Nudge_Theme {
                val snackState = rememberSnackBarState()
                val onlineStatus = remember { mutableStateOf(false) }

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
                                isOnline = isOnline.value
                            )
                        }
                        Box(modifier = Modifier.constrainAs(mainContent){
                            top.linkTo(if (mViewModel.isLoggedIn.value) networkBanner.bottom else parent.top)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        }) {
                           RootNavigationGraph(navController = rememberNavController(),sharedPrefs)
                        }
                    }
                }

                isLoggedInLive.observe(this) { isLoggedIn ->
                    mViewModel.isLoggedIn.value = isLoggedIn
                }
                LaunchedEffect(key1 = RetryHelper.tokenExpired.value) {
                    if (RetryHelper.tokenExpired.value) {
                        mViewModel.tokenExpired.value = true
                        RetryHelper.generateOtp { success, message, mobileNumber ->
                            if (success) {
                                mViewModel.tokenExpired.value = true
                                snackState.addMessage(
                                    message = getString(R.string.otp_send_to_mobile_number_message_for_relogin)
                                        .replace("{MOBILE_NUMBER}", mobileNumber, true),
                                    isSuccess = true, isCustomIcon = false
                                )
                            }
                        }
                    }
                }
                if (mViewModel.tokenExpired.value) {
                    ShowOptDialogForVillageScreen(
                        modifier = Modifier,
                        context = LocalContext.current,
                        viewModel = mViewModel,
                        snackState = snackState,
                        setShowDialog = {
                            mViewModel.tokenExpired.value = false
                        },
                        positiveButtonClicked = {
                            RetryHelper.updateOtp(mViewModel.baseOtpNumber) { success, message ->
                                if (success){
                                    RetryHelper.tokenExpired.value = false
                                    RetryHelper.autoReadOtp.value = ""
                                    mViewModel.tokenExpired.value = false
                                    showCustomToast(this, getString(R.string.session_restored_message))
                                }
                                else {
                                    showCustomToast(this, message)
                                }
                            }
                        }
                    )
                }
            }
        }

        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()

        downloader = AndroidDownloader(applicationContext)

        RetryHelper.init(
            prefRepo = mViewModel.prefRepo,
            apiService = mViewModel.apiService,
            tolaDao = mViewModel.tolaDao,
            stepsListDao = mViewModel.stepsListDao,
            villageListDao = mViewModel.villegeListDao,
            didiDao = mViewModel.didiDao,
            answerDao = mViewModel.answerDao,
            numericAnswerDao = mViewModel.numericAnswerDao,
            questionDao = mViewModel.questionDao,
            castListDao = mViewModel.casteListDao,
            bpcSummaryDao = mViewModel.bpcSummaryDao,
            poorDidiListDao = mViewModel.poorDidiListDao,
            languageListDao = mViewModel.languageListDao
        )

        AnalyticsHelper.init(context = applicationContext, mViewModel.prefRepo, mViewModel.apiService)

        connectionLiveData = ConnectionMonitor(this)
        connectionLiveData.observe(this) { isNetworkAvailable ->
            isOnline.value = isNetworkAvailable.isOnline && (isNetworkAvailable.speedType != NetworkSpeed.POOR|| isNetworkAvailable.speedType != NetworkSpeed.UNKNOWN)
            connectionSpeed.value = isNetworkAvailable.connectionSpeed
            connectionSpeedType.value = isNetworkAvailable.speedType.toString()
            NudgeCore.updateIsOnline(isNetworkAvailable.isOnline
                    && (isNetworkAvailable.speedType != NetworkSpeed.POOR || isNetworkAvailable.speedType != NetworkSpeed.UNKNOWN))
        }

        startSmartUserConsent()


    }


    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(SENDER_NUMBER)
    }

    private fun registerBroadcastReceiver(context: Context) {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver?.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener{
            override fun onSuccess(intent: Intent?) {

                val client = SmsRetriever.getClient(context)
                client.startSmsUserConsent(null)
                startActivityForResult(intent!!, REQ_USER_CONSENT)

            }

            override fun onFailure() {
                Log.d("MainActivity", "SmsBroadcastReceiverListener: onFailure: OTP Read time-out.")
            }

        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }

    }

    private fun getOtpFromMessage(message: String?) {
        val optPattern = "(\\d{6}).*?(\\d{10})".toRegex()
//        val optPattern = Pattern.compile("(|^)\\d{6}")
        val matchResult = optPattern.find(message!!)
        if (matchResult != null){
            val otp = matchResult.groupValues[1]
            val mobileNumber = matchResult.groupValues[2]

            RetryHelper.autoReadOtp.value = otp

//            Toast.makeText(this, "OTP: $otp", Toast.LENGTH_LONG).show()
//            Toast.makeText(this, "Mobile Number: $mobileNumber", Toast.LENGTH_LONG).show()
        } else {
            println("No match found.")
        }
    }


    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver(this)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

    fun exitApplication(){
        this.finish()
    }

    override fun onDestroy() {
        Log.d("MainActivity", "onDestroy: called")
        AnalyticsHelper.cleanup()
        connectionLiveData.removeObservers(this)
        applicationContext.cacheDir.deleteRecursively()
        RetryHelper.cleanUp()
        super.onDestroy()
    }

    override fun onAfterLocaleChanged() {

    }

    override fun onBeforeLocaleChanged() {
    }

    override fun onResume() {
        localizationDelegate.onResume(applicationContext)
        super.onResume()
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

    fun getSyncEnabled() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    mViewModel.saveSyncEnabledFromRemoteConfig(
                        remoteConfig.get("syncEnabled").asBoolean()
                    )


                }
            }
    }
}