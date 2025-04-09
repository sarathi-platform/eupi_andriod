package com.patsurvey.nudge.activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.rememberNavController
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.nudge.core.APP_REDIRECT_LINK
import com.nudge.core.APP_UPDATE_IMMEDIATE
import com.nudge.core.APP_UPDATE_REQUEST_CODE
import com.nudge.core.APP_UPDATE_TYPE
import com.nudge.core.BLANK_STRING
import com.nudge.core.CoreObserverInterface
import com.nudge.core.CoreObserverManager
import com.nudge.core.IS_APP_NEED_UPDATE
import com.nudge.core.IS_IN_APP_UPDATE
import com.nudge.core.LATEST_VERSION_CODE
import com.nudge.core.MINIMUM_VERSION_CODE
import com.nudge.core.enums.SyncAlertType
import com.nudge.core.helper.ProvideTranslationHelper
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.helper.TranslationHelper
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.notifications.NotificationHandler
import com.nudge.core.redirectToLink
import com.nudge.core.ui.commonUi.componet_.component.ShowCustomDialog
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.checkForAppUpdates
import com.nudge.core.utils.setupAppUpdateListeners
import com.nudge.core.utils.unregisterAppUpdateListeners
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.R
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.ui.theme.Nudge_Theme
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.analytics.AnalyticsHelper
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.download.AndroidDownloader
import com.patsurvey.nudge.navigation.RootNavigationGraph
import com.patsurvey.nudge.navigation.selection.finishActivity
import com.patsurvey.nudge.smsread.SmsBroadcastReceiver
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.QUESTION_IMAGE_LINK_KEY
import com.patsurvey.nudge.utils.SENDER_NUMBER
import com.patsurvey.nudge.utils.showCustomDialog
import com.patsurvey.nudge.utils.showCustomToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnLocaleChangedListener, CoreObserverInterface {

    private var TAG = MainActivity::class.java.simpleName

    private val localizationDelegate = LocalizationActivityDelegate(this)

    @Inject
    lateinit var sharedPrefs: PrefRepo

    @Inject
    lateinit var translationHelper: TranslationHelper

    private val mViewModel: MainActivityViewModel by viewModels()

    private lateinit var appUpdateManager: AppUpdateManager

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
        CoreAppDetails.setApplicationDetails(
            CoreAppDetails.ApplicationDetails(
                packageName = packageName,
                applicationID = BuildConfig.APPLICATION_ID,
                activity = this,
                buildVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                buildEnvironment = BuildConfig.FLAVOR.uppercase(Locale.ENGLISH)
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            mViewModel.sendAppOpenEvent(this)

        appUpdateManager = AppUpdateManagerFactory.create(this)

        CoreObserverManager.addObserver(this)
        setContent {
            ProvideTranslationHelper(translationHelper) {
                LaunchedEffect(Unit) {
                    translationHelper.initTranslationHelper(TranslationEnum.CommonStrings)
                }
                Nudge_Theme {
                    val snackState = rememberSnackBarState()
                    val onlineStatus = remember { mutableStateOf(false) }

                    val notificationHandler: NotificationHandler =
                        NotificationHandler(context = this)

                    val localContext = LocalContext.current

                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp)
                            .background(blueDark),
                    ) {
                        mViewModel.setV2TheameEnable()
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
                            Box(modifier = Modifier.constrainAs(mainContent) {
                                top.linkTo(if (mViewModel.isLoggedIn.value) networkBanner.bottom else parent.top)
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom)
                                height = Dimension.fillToConstraints
                            }) {
                                RootNavigationGraph(
                                    navController = rememberNavController(),
                                    sharedPrefs
                                )
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

                    LaunchedEffect(Unit) {
                        // TODO move this code to Mission and Village screens.
//                    delay(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))

                        mViewModel.onEvent(CommonEvents.CheckEventLimitThreshold { result ->
                            if (result == SyncAlertType.SOFT_ALERT) {
                                notificationHandler?.createSoftAlertNotification(
                                    mViewModel.showSoftLimitAlert(
                                        title = localContext.getString(R.string.warning_text),
                                        message = localContext.getString(R.string.notification_alert_message)
                                    )
                                )
                            }

                            if (result == SyncAlertType.HARD_ALERT) {
                                mViewModel.onEvent(DialogEvents.ShowAlertDialogEvent(showDialog = true))
                            }
                        })
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
                                    if (success) {
                                        RetryHelper.tokenExpired.value = false
                                        RetryHelper.autoReadOtp.value = ""
                                        mViewModel.tokenExpired.value = false
                                        showCustomToast(
                                            this,
                                            getString(R.string.session_restored_message)
                                        )
                                    } else {
                                        showCustomToast(this, message)
                                    }
                                }
                            }
                        )
                    }

                    if (mViewModel.showUpdateDialog.value) {
                        showCustomDialog(
                            title = translationHelper.getString(R.string.update_available),
                            message = translationHelper.getString(R.string.version_available_message),
                            negativeButtonTitle = translationHelper.getString(R.string.app_update_cancel),
                            positiveButtonTitle = translationHelper.getString(R.string.app_update),
                            onNegativeButtonClick = {
                                if (mViewModel.appUpdateType.value == AppUpdateType.IMMEDIATE)
                                    finishActivity()

                                mViewModel.showUpdateDialog.value = false
                                mViewModel.isAppLinkOpen.value = false
                            },
                            onPositiveButtonClick = {
                                mViewModel.isAppLinkOpen.value = true
                                mViewModel.showUpdateDialog.value = false
                            }
                        )

                    }

                    if (mViewModel.isAppLinkOpen.value) {
                        sharedPrefs.getPref(APP_REDIRECT_LINK, BLANK_STRING)?.let { url ->
                            if (url.isNotEmpty()) {
                                localContext.redirectToLink(url)
                            } else {
                                showCustomToast(
                                    localContext,
                                    translationHelper.getString(R.string.invalid_url)
                                )
                            }
                        }
                    }

                    if (mViewModel.showHardEventLimitAlert.value.showDialog) {
                        val alertModel = mViewModel.showHardLimitAlert(
                            title = localContext.getString(R.string.alert_dialog_title_text),
                            message = localContext.getString(R.string.hard_threshold_alert_message)
                        )
                        ShowCustomDialog(
                            title = alertModel.alertTitle,
                            message = alertModel.alertMessage,
                            icon = alertModel.alertIcon,
                            positiveButtonTitle = stringResource(R.string.ok),
                            negativeButtonTitle = stringResource(R.string.cancel),
                            onPositiveButtonClick = {
                                // TODO navigation to sync screen.
                                mViewModel.onEvent(DialogEvents.ShowAlertDialogEvent(showDialog = false))
                            },
                            onNegativeButtonClick = {
                                mViewModel.onEvent(DialogEvents.ShowAlertDialogEvent(showDialog = false))
                            }
                        )
                    }
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
        )

        AnalyticsHelper.init(context = applicationContext, mViewModel.prefRepo, mViewModel.apiService)

        mViewModel.isOnline.observe(this) { isNetworkAvailable ->
            isOnline.value = isNetworkAvailable
            NudgeCore.updateIsOnline(isNetworkAvailable)
        }

        startSmartUserConsent()


    }

    fun validateAppVersionAndCheckUpdate() {
        mViewModel.appUpdateType.value = getAppUpdateType(
            sharedPrefs.getPref(APP_UPDATE_TYPE, APP_UPDATE_IMMEDIATE)
                ?: APP_UPDATE_IMMEDIATE
        )
        val currentAppVersion = BuildConfig.VERSION_CODE
        val minAppVersion = sharedPrefs.getPref(MINIMUM_VERSION_CODE, currentAppVersion)
        mViewModel.isInAppUpdate.value = sharedPrefs.getPref(IS_IN_APP_UPDATE, false)
        if (sharedPrefs.getPref(IS_APP_NEED_UPDATE, false)) {
            CoreLogger.d(
                CoreAppDetails.getApplicationContext(),
                "validateAppVersion",
                "UpdateDetails : CurrVersion: $currentAppVersion" +
                        ":minAppVersion : $minAppVersion" +
                        ":appUpdateType: ${mViewModel.appUpdateType.value}" +
                        ":isInAppUpdate: ${mViewModel.isInAppUpdate.value}" +
                        ":showUpdateDialog: ${mViewModel.showUpdateDialog.value}"
            )
            if (currentAppVersion < minAppVersion) {
                mViewModel.appUpdateType.value = AppUpdateType.IMMEDIATE
            }
            mViewModel.showUpdateDialog.value = false
            if (currentAppVersion < sharedPrefs.getPref(LATEST_VERSION_CODE, currentAppVersion)) {
                if (mViewModel.isInAppUpdate.value) {
                    checkForAppUpdates(
                        appUpdateManager = appUpdateManager,
                        appUpdateType = mViewModel.appUpdateType.value
                    )
                } else {
                    mViewModel.showUpdateDialog.value = true
                }
            }
        }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && applicationInfo.targetSdkVersion >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerReceiver(smsBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(smsBroadcastReceiver, intentFilter)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        } else if (requestCode == APP_UPDATE_REQUEST_CODE && resultCode != RESULT_OK) {
            if (mViewModel.appUpdateType.value == AppUpdateType.IMMEDIATE) {
                finishActivity()
            }
            showCustomToast(this, translationHelper.getString(R.string.str_app_update_fail))
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
        validateAppVersionAndCheckUpdate()
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
        mViewModel.isOnline.removeObservers(this)
        applicationContext.cacheDir.deleteRecursively()
        CoreObserverManager.removeObserver(this)
        RetryHelper.cleanUp()
        super.onDestroy()
        unregisterAppUpdateListeners(
            appUpdateManager = appUpdateManager,
            updateType = mViewModel.appUpdateType.value,
            translationHelper = translationHelper
        )
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val configOverride = Configuration(newBase?.resources?.configuration)
        configOverride.fontScale = 1.0f
        applyOverrideConfiguration(configOverride)
    }

    override fun onAfterLocaleChanged() {

    }

    override fun onBeforeLocaleChanged() {
    }

    override fun onResume() {
        localizationDelegate.onResume(applicationContext)
        super.onResume()
        setupAppUpdateListeners(
            appUpdateManager = appUpdateManager,
            updateType = mViewModel.appUpdateType.value,
            translationHelper = translationHelper
        )
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


    override fun updateMissionActivityStatusOnGrantInit(onSuccess: (isSuccess: Boolean) -> Unit) {
        mViewModel.updateBaselineStatusOnInit() {
            onSuccess(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(QUESTION_IMAGE_LINK_KEY, ArrayList(quesImageList))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (quesImageList.isEmpty()) {
            quesImageList =
                savedInstanceState.getStringArrayList(QUESTION_IMAGE_LINK_KEY)?.toMutableList()
                    ?: mutableListOf()
            NudgeLogger.d(TAG, "onRestoreInstanceState: $quesImageList")
        }
    }
    fun getAppUpdateType(type: String): Int {
        return if (type == APP_UPDATE_IMMEDIATE) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE
    }
}