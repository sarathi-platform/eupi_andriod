package com.patsurvey.nudge.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nudge.core.KEY_PARENT_ENTITY_ADDRESS
import com.nudge.core.KEY_PARENT_ENTITY_DADA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_DIDI_ID
import com.nudge.core.KEY_PARENT_ENTITY_DIDI_NAME
import com.nudge.core.KEY_PARENT_ENTITY_TOLA_ID
import com.nudge.core.KEY_PARENT_ENTITY_TOLA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_VILLAGE_ID
import com.nudge.core.enums.EventName
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.video.VideoItem
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.download.FileType
import com.patsurvey.nudge.model.dataModel.WeightageRatioModal
import com.patsurvey.nudge.model.request.AnswerDetailDTOListItem
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.regex.Pattern
import kotlin.math.roundToInt
import kotlin.system.exitProcess


fun Modifier.visible(visible: Boolean) = if (visible) this else this.then(Invisible)
private object Invisible : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {}
    }
}

fun dpToPx(iContext: Context, dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        iContext.resources.displayMetrics
    )
        .toInt()
}

fun getDeviceId(context: Context) : String{
    return Settings.Secure.getString(context.contentResolver,
        Settings.Secure.ANDROID_ID) ?: ""
}

fun getUniqueIdForEntity(): String {
    return UUID.randomUUID().toString().replace("-", "") + "|" + System.currentTimeMillis()
}

fun findCompleteValue(status:String): StepStatus {
    return when(status){
        BLANK_STRING->StepStatus.NOT_STARTED
        INPROGRESS_STRING->StepStatus.INPROGRESS
        COMPLETED_STRING->StepStatus.COMPLETED
        else -> {StepStatus.NOT_STARTED}
    }
}

fun getStepStatusFromOrdinal(status : Int) : String{
    return when(status){
        StepStatus.NOT_STARTED.ordinal -> StepStatus.NOT_STARTED.name
        StepStatus.INPROGRESS.ordinal -> StepStatus.INPROGRESS.name
        StepStatus.COMPLETED.ordinal -> StepStatus.COMPLETED.name
        else -> {
            StepStatus.NOT_STARTED.name
        }
    }
}
fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Modifier.debounceClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    clickDebounceWindow: Long = 1000L,
    onClick: () -> Unit,
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {
    val debounceClickState = remember {
        MutableSharedFlow<() -> Unit>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }

    var lastEventTime by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        debounceClickState.transform {
            // Only emit click events if the clickDebounce
            // millis have passed since the last click event
            val now = System.currentTimeMillis()
            if (now - lastEventTime > clickDebounceWindow) {
                emit(it)
                lastEventTime = now
            }
        }.collect { clickEvent ->
            clickEvent.invoke()
        }
    }

    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { debounceClickState.tryEmit(onClick) },
        role = role,
        indication = LocalIndication.current,
        interactionSource = remember { MutableInteractionSource() }
    )
}

fun Context.setScreenOrientation(orientation: Int) {
    val activity = this.findActivity() ?: return
    activity.requestedOrientation = orientation
    if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        hideSystemUi()
    } else {
        showSystemUi()
    }
}

fun Context.hideSystemUi() {
    val activity = this.findActivity() ?: return
    val window = activity.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Context.showSystemUi() {
    val activity = this.findActivity() ?: return
    val window = activity.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())
}

fun uriFromFile(context:Context, file:File): Uri {
    try {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
        } else {
            Uri.fromFile(file)
        }
    } catch (ex: Exception) {
        return Uri.EMPTY
        Log.e("uriFromFile", "exception", ex)
    }
}

fun openSettings(context: Context) {
    val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
    }
    appSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    (context as MainActivity).startActivity(appSettingsIntent)
}

var videoList = listOf(
    VideoItem(
        id = 1,
        title = "Logging into Sarathi app",
        description = "How to login to Sarathi app",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M2LoggingIntoSarathiApp.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M2.png"
    ),
    VideoItem(
        id = 2,
        title = "App overview",
        description = "Brief introduction to Sarathi app",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M3AppOverview.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M3.png"
    ),
    VideoItem(
        id = 3,
        title = "Transect Walk",
        description = "How to use Sarathi app during Transect Walk",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M4TransectWalk.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M4.png"
    ),
    VideoItem(
        id = 4,
        title = "Social Mapping",
        description = "How to enter didi details during Social Mapping",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M5SocialMapping.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M5.png"
    ),
    VideoItem(
        id = 5,
        title = "Participatory Wealth Ranking",
        description = "How to record Didi's wealth ranking",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6ParticipatoryWealthRanking.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6.png"
    ),VideoItem(
        id = 6,
        title = "PAT Survey - Overview",
        description = "Introduction to PAT survey on Sarathi app",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M7PATSurveyOverview.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M7.png"
    ),VideoItem(
        id = 7,
        title = "Filling PAT Survey - Part 1",
        description = "How to fill PAT survey - Part 1",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8FillingPATSurveyPart1.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8.1.png"
    ),
    VideoItem(
        id = 8,
        title = "Filling PAT Survey - Part 2",
        description = "How to fill PAT Survey - Part 2",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8FillingPATSurveyPart2.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8.2.png"
    ),
    VideoItem(
        id = 9,
        title = "Digital Form B and Submit for VO endorsement",
        description = "How to download Digital Form B and submit for VO endorsement",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M9DigitalFormBAndSubmitForVOEndorsement.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M9.png"
    ),
    VideoItem(
        id = 10,
        title = "VO Endorsement",
        description = "How to record VO endorsement",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M10VOEndorsement.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M10.png"
    ),
    VideoItem(
        id = 11,
        title = "Syncing data",
        description = "How to sync/ upload data from Sarathi app",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M11SyncingData.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M11.png"
    ),


    VideoItem(
        id = 12,
        title = "সারথি অ্যাপে লগ ইন করুন",
        description = "সারথি অ্যাপে কিভাবে লগইন করবেন",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M2Bengali.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M2.png"
    ),
    VideoItem(
        id = 13,
        title = "অ্যাপের সংক্ষিপ্ত বিবরণ",
        description = "সারথি অ্যাপের সংক্ষিপ্ত পরিচয়",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M3Bengali.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M3.png"
    ),
    VideoItem(
        id = 14,
        title = "গ্রামের ভ্রমণ",
        description = "গ্রাম ভ্রমণের সময় কিভাবে সারথি অ্যাপ ব্যবহার করবেন",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M4Bengali.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M4.png"
    ),
    VideoItem(
        id = 15,
        title = "সামাজিক মানচিত্র",
        description = "সামাজিক মানচিত্রয়ের সময় কীভাবে দিদির বিবরণ প্রবেশ করানো যায়",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M5Bengali.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M5.png"
    ),
    VideoItem(
        id = 16,
        title = "অংশগ্রহণমূলক সম্পদ র\u200C্যাঙ্কিং",
        description = "দিদির সম্পদের র্যাঙ্কিং কিভাবে রেকর্ড করবেন",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6Bengali.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6.png"
    ),VideoItem(
        id = 17,
        title = "PAT সার্ভে - সংক্ষিপ্ত বিবরণ",
        description = "সারথি অ্যাপে PAT সার্ভে সূচনা",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M7PATBengali.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M7.png"
    ),VideoItem(
        id = 18,
        title = "PAT সার্ভে পূরণ করা-অংশ 1",
        description = "PAT সার্ভে কিভাবে পূরণ করা যায়-প্রথম অংশ",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8PATBengali1.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8.1.png"
    ),
    VideoItem(
        id = 19,
        title = "PAT সার্ভে পূরণ করা-অংশ 2",
        description = "PAT সার্ভে কিভাবে পূরণ করা যায়-দ্বিতীয় অংশ",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8PATBengali2.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8.2.png"
    ),
    VideoItem(
        id = 20,
        title = "ডিজিটাল ফর্ম B এবং ভিও অনুমোদন",
        description = "কিভাবে ডিজিটাল ফর্ম B ডাউনলোড করতে হবে এবং ভিও অনুমোদনের জন্য জমা দিতে হবে",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M9Bengali.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M9.png"
    ),
    VideoItem(
        id = 21,
        title = "ভিও অনুমোদন",
        description = "কিভাবে ভিও অনুমোদন রেকর্ড করা যায়",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M10VOBengali.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M10.png"
    ),
    VideoItem(
        id = 22,
        title = "ডেটা সিঙ্ক করা",
        description = "সারথি অ্যাপ থেকে কীভাবে ডেটা সিঙ্ক / আপলোড করা যায়",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M11Bengali.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M11.png"
    ),


    VideoItem(
        id = 23,
        title = "সাৰথি এপত লগ ইন কৰা",
        description = "সাৰথি এপত কেনেকৈ লগইন কৰিব",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M2Assamese.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M2.png"
    ),
    VideoItem(
        id = 24,
        title = "এপৰ সংক্ষিপ্তত",
        description = "সাৰথি এপৰ চমু পৰিচয়",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M3Assamese.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M3.png"
    ),
    VideoItem(
        id = 25,
        title = "গ্রামের ভ্রমণ",
        description = "গ্রাম ভ্রমণের সময় কিভাবে সারথি অ্যাপ ব্যবহার করবেন",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M4Assamese.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M4.png"
    ),
    VideoItem(
        id = 26,
        title = "সামাজিক মানচিত্র",
        description = "সামাজিক মানচিত্রৰ সময়ত বাইদেউৰ বিৱৰণ কেনেকৈ দিব",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M5Assamese.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M5.png"
    ),
    VideoItem(
        id = 27,
        title = "অংশগ্রহণমূলক সম্পদ ৰেংকিং",
        description = "বাইদেউৰ সম্পদৰ ৰেংকিং কেনেকৈ ৰেকৰ্ড কৰিব",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6Assamese.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6.png"
    ),VideoItem(
        id = 28,
        title = "PAT সমীক্ষা - সংক্ষিপ্তত",
        description = "সাৰথি এপত PAT সমীক্ষাৰ পৰিচয়",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M7Assamese.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M7.png"
    ),VideoItem(
        id = 29,
        title = "PAT সমীক্ষা পূৰণ কৰা - ভাগ ১",
        description = "PAT সমীক্ষা কেনেকৈ পূৰণ কৰিব - ভাগ ১",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8PATAssamesePart1.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8.1.png"
    ),
    VideoItem(
        id = 30,
        title = "PAT সমীক্ষা পূৰণ কৰা - ভাগ ২",
        description = "PAT সমীক্ষা কেনেকৈ পূৰণ কৰিব - ভাগ ২",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8PATAssamesePart2.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8.2.png"
    ),
    VideoItem(
        id = 31,
        title = "ডিজিটেল ফৰ্ম B আৰু VO অনুমোদনৰ বাবে জমা দিবলৈ",
        description = "কডিজিটেল ফৰ্ম B কেনেকৈ ডাউনলোড কৰি VO অনুমোদনৰ বাবে জমা দিব",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M9Assamese.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M9.png"
    ),
    VideoItem(
        id = 32,
        title = "VO অনুমোদন",
        description = "VO অনুমোদন কেনেকৈ ৰেকৰ্ড কৰিব লাগে",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M10Assamese.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M10.png"
    ),
    VideoItem(
        id = 33,
        title = "বিবৰণ ছিঙ্ক কৰা",
        description = "সাৰথি এপৰ পৰা কেনেকৈ ডাটা ছিংক বা আপলোড কৰিব",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M11Assamese.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M11.png"
    ),
    VideoItem(
        id = 34,
        title = "सारथी ऐप में लॉग इन करें",
        description = "सारथी ऐप में लॉग इन कैसे करें",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M2Hindi.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M2.png"
    ),
    VideoItem(
        id = 35,
        title = "ऐप सिंहावलोकन",
        description = "सारथी ऐप का संक्षिप्त परिचय",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M3Hindi.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M3.png"
    ),
    VideoItem(
        id = 36,
        title = "ट्रांसेक्ट वॉक",
        description = "ट्रांसेक्ट वॉक के दौरान सारथी ऐप का उपयोग कैसे करें",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M4Hindi.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M4.png"
    ),
    VideoItem(
        id = 37,
        title = "सामाजिक मानचित्रण",
        description = "सोशल मैपिंग के दौरान दीदी विवरण कैसे दर्ज करें",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M5Hindi.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M5.png"
    ),
    VideoItem(
        id = 38,
        title = "सहभागी धन रैंकिंग",
        description = "दीदी की संपत्ति रैंकिंग कैसे दर्ज करें",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6Hindi.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6.png"
    ),
    VideoItem(
        id = 39,
        title = "पीएटी सर्वेक्षण - अवलोकन",
        description = "सारथी ऐप पर पीएटी सर्वेक्षण का परिचय",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M7Hindi.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M7.png"
    ),
    VideoItem(
        id = 40,
        title = "PAT सर्वेक्षण भरना - भाग 1",
        description = "PAT सर्वेक्षण कैसे भरें - भाग 1",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8HindiPart1.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8.1.png"
    ),
    VideoItem(
        id = 41,
        title = "PAT सर्वेक्षण भरना - भाग 2",
        description = "PAT सर्वेक्षण कैसे भरें - भाग 2",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8HindiPart2.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M8.2.png"
    ),
    VideoItem(
        id = 42,
        title = "डिजिटल फॉर्म बी और वीओ समर्थन के लिए जमा करें",
        description = "डिजिटल फॉर्म बी कैसे डाउनलोड करें और वीओ अनुमोदन के लिए जमा करें",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M9Hindi.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M9.png"
    ),
    VideoItem(
        id = 43,
        title = "वीओ अनुमोदन",
        description = "वीओ समर्थन कैसे रिकॉर्ड करें",
        url = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M10Hindi.mp4",
        thumbUrl = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M10.png"
    )

    /*VideoItem(
        id = 24,
        title = "Video 1",
        description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV. For \$35.  Find out more at google.com/chromecast.",
        url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        thumbUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
    ),
    VideoItem(
        id = 25,
        title = "Video 2",
        description = "Supporting description",
        url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg"
    ),
    VideoItem(
        id = 26,
        title = "Video 3",
        description = "Supporting description",
        url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        thumbUrl =     "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg"
    ),
    VideoItem(
        id = 27,
        title = "Video 4",
        description = "Supporting description",
        url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg"
    ),*/

)
@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun AnimatedScaleInTransition(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { -300 }, // small slide 300px
            animationSpec = tween(
                durationMillis = EXPANSTION_TRANSITION_DURATION,
                easing = FastOutLinearInEasing // interpolator
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -300 },
                animationSpec = tween(
                durationMillis = EXPANSTION_TRANSITION_DURATION,
        easing = LinearEasing
       )
     ),
        content = content
    )
}

fun fromWeightageRatio(list: List<WeightageRatioModal>): String {
    val type: Type = object : TypeToken<List<WeightageRatioModal?>?>() {}.type
    return Gson().toJson(list, type)
}

fun toWeightageRatio(listInString: String): List<WeightageRatioModal> {
    val type =
        object : TypeToken<List<WeightageRatioModal?>?>() {}.type
    val gson = GsonBuilder().disableHtmlEscaping().create()
    return gson.fromJson(listInString, type)
}


fun checkStringOperator(operator:String) = when(operator){
      "==" ->Operator.EQUAL_TO
      "=" ->Operator.EQUAL_TO
      "<" ->Operator.LESS_THAN
      "<=" ->Operator.LESS_THAN_EQUAL_TO
      ">" ->Operator.MORE_THAN
      ">=" ->Operator.MORE_THAN_EQUAL_TO
      else->{}
  }
fun stringToDouble(string: String):Double{
    var doubleAmount=0.0
    if(string!=null){
        doubleAmount = if(string.isEmpty())
            0.0
        else string.toDouble()
    }
   return doubleAmount
}

fun stringToInt(string: String):Int{
    var intValue=0
    if(string!=null){
        intValue = if(string.isEmpty())
            0
        else string.toInt()
    }
    return intValue
}
fun doubleToString(double: Double):String{
    var string= BLANK_STRING
    if(double>0.0){
        string = double.toString()
    }
    return string
}
fun calculateScore(list: List<WeightageRatioModal>,totalAmount:Double,isRatio:Boolean):Double{
    var score:Double = 0.0
    run breaking@{
        list.forEach {
            when(checkStringOperator(it.operator)){
                Operator.EQUAL_TO -> {

                    if(totalAmount == (if(!isRatio) stringToDouble(it.weightage) else stringToDouble(it.ratio))){
                        score = it.score.toDouble()
                        return@breaking
                    }
                }
                Operator.LESS_THAN -> {
                    if(totalAmount < (if(!isRatio) stringToDouble(it.weightage) else stringToDouble(it.ratio))){
                        score = it.score.toDouble()
                        return@breaking
                    }
                }
                Operator.LESS_THAN_EQUAL_TO ->{
                    if(totalAmount <= (if(!isRatio) stringToDouble(it.weightage) else stringToDouble(it.ratio))){
                        score = it.score.toDouble()
                        return@breaking
                    }
                }
                Operator.MORE_THAN -> {
                    if(totalAmount > (if(!isRatio) stringToDouble(it.weightage) else stringToDouble(it.ratio))){
                        score = it.score.toDouble()
                        return@breaking
                    }
                }
                Operator.MORE_THAN_EQUAL_TO -> {
                    if(totalAmount >= (if(!isRatio) stringToDouble(it.weightage) else stringToDouble(it.ratio))){
                        score = it.score.toDouble()
                        return@breaking
                    }
                }
                else -> {
                    score =0.0
                }
            }
        }
    }
    return score
}

fun getFileNameFromURL(url: String): String{
    return url.substring(url.lastIndexOf('/') + 1, url.length)
}
fun getAuthImageFileNameFromURL(url: String): String{
    return url.substring(url.lastIndexOf('=') + 1, url.length)
}

data class DottedShape(
    val step: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(Path().apply {
        val stepPx = with(density) { step.toPx() }
        val stepsCount = (size.width / stepPx).roundToInt()
        val actualStep = size.width / stepsCount
        val dotSize = Size(width = actualStep / 2, height = size.height)
        for (i in 0 until stepsCount) {
            addRect(
                Rect(
                    offset = Offset(x = i * actualStep, y = 0f),
                    size = dotSize
                )
            )
        }
        close()
    })
}

fun roundOffDecimal(number: Double): Double? {
    return try {
        val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
        df.roundingMode = RoundingMode.CEILING
        df.format(number).toDouble()
    }catch (ex:Exception){
        NudgeLogger.e("Utils", "roundOffDecimal -> exception", ex)
        0.00
    }


}

fun roundOffDecimalPoints(number: Double): String {
    return String.format(Locale.ENGLISH,"%.2f", number)
}

fun getImagePath(context: Context, imagePath:String): File {
    val imageName = getFileNameFromURL(imagePath)
    return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${imageName}")
}

fun getAuthImagePath(context: Context, imagePath:String): File {
    val imageName = getAuthImageFileNameFromURL(imagePath)
    return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${imageName}")
}

fun getImageRequestBody(sourceFile: File) : RequestBody? {
    var requestBody: RequestBody? = null
    Thread {
        val mimeType = getMimeType(sourceFile);
        if (mimeType == null) {
            Log.e("file error", "Not able to get mime type")
            return@Thread
        }
        try {
            requestBody = sourceFile.path.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("File upload", "failed")
        }
    }.start()

    return requestBody;
}

private fun getMimeType(file: File): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}

fun updateLastSyncTime(prefRepo:PrefRepo,lastSyncTime:String){
    val saveSyncTime= prefRepo.getPref(LAST_SYNC_TIME,0L)
    if(saveSyncTime>0){
        val compValue=lastSyncTime.toLong().compareTo(saveSyncTime)
        if(compValue>0){
            prefRepo.savePref(LAST_SYNC_TIME,lastSyncTime.toLong())
        }

    }else prefRepo.savePref(LAST_SYNC_TIME,lastSyncTime.toLong())
}


@Composable
fun BulletList(
    modifier: Modifier = Modifier,
    lineSpacing: Dp = 0.dp,
    items: List<String>,
) {
    Column(modifier = modifier) {
        items.forEach {
            Row {
                Text(
                    text = "\u2022",
                    textAlign = TextAlign.Start,
                    style = buttonTextStyle,
                    maxLines = 1,
                    color = textColorDark,
                )
                Text(
                    text = it,
                    textAlign = TextAlign.Start,
                    style = smallTextStyleMediumWeight,
                    color = textColorDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp)
                )
            }
            if (lineSpacing > 0.dp && it != items.last()) {
                Spacer(modifier = Modifier.height(lineSpacing))
            }
        }
    }
}

fun compressImage(imageUri: String, activity: Context,name:String): String? {
    var filename: String? = ""
    try {
        val filePath = imageUri /*getRealPathFromURI(imageUri, activity)*/
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas: Canvas
        if (scaledBitmap != null) {
            canvas = Canvas(scaledBitmap)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(
                bmp,
                middleX - bmp.width / 2,
                middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG)
            )
        }
        val exif: ExifInterface
        try {
            exif = filePath?.let { ExifInterface(it) }!!
            val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90F)
            } else if (orientation == 3) {
                matrix.postRotate(180F)
            } else if (orientation == 8) {
                matrix.postRotate(270F)
            }
            if (scaledBitmap != null) {
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap,
                    0,
                    0,
                    scaledBitmap.width,
                    scaledBitmap.height,
                    matrix,
                    true
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val out: FileOutputStream
        filename = name
        try {
            val path =File("${activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath}",filename).absolutePath
            out = FileOutputStream(path)
           val success= scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
            return if(success == true){
                path
            }else BLANK_STRING
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return BLANK_STRING
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }
    return inSampleSize
}

private fun getRealPathFromURI(contentURI: String, activity: Context): String? {
    val contentUri = Uri.parse(contentURI)
    val cursor = activity.contentResolver.query(contentUri, null, null, null, null)
    return if (cursor == null) {
        contentUri.path
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        cursor.getString(idx)
    }
}

fun formatDateAndTime(page:String,lastSyncTime: String):String{
    return try {
        val currentTime = if(lastSyncTime.isEmpty()) 0L else lastSyncTime.toLong()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        dateFormat.format(currentTime)
    }catch (ex:Exception){
        ex.message?.let { NudgeLogger.d("$page DateTimeParse Issue : ", it) }
        BLANK_STRING
    }
}

fun findImageFilePath(uri:String):String{
    if(uri.isNotEmpty()){
        return if(uri.contains("|")){
            if(uri.split("|")[0].contains("content:/")) uri.split("|")[0].replace("content:/","file:/") else uri.split("|")[0]
        }else{
            if(uri.contains("content:/")) uri.replace("content:/","file:/") else uri
        }

    }
    return BLANK_STRING
}

fun findImageLocationFromPath(uri:String):List<String>{
    if(uri.isNotEmpty()){
        if(uri.contains("|")) {
            val path: List<String> = uri.split("|")
            if (path[0].contains("content:/"))
                path[0].replace("content:/", "file:/")
            return path
        } else if (uri.contains("content:/")) {
            uri.replace("content:/", "file:/")
        } else {
            return listOf(uri, "0.0, 0.0")
        }
    }
    return ArrayList()
}

fun findStepNameForSelectedLanguage(context: Context,stepId:Int):String{
   return when(stepId){
       40-> context.getString(R.string.step_transect_walk)
       41-> context.getString(R.string.step_social_mapping)
       43-> context.getString(R.string.step_pat_survey)
       44-> context.getString(R.string.step_vo_endorsement)
       45-> context.getString(R.string.step_bpc_verification)
       46-> context.getString(R.string.step_participatory_wealth_ranking)
       else -> {
           BLANK_STRING}
   }
}

fun onlyNumberField(value:String):Boolean{
    if(value.isDigitsOnly() && value != "_" && value != "N"){
        return true
    }
    return false
}

fun changeMilliDateToDate(millDate:Long):String?{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return dateFormat.format(millDate)
}

fun longToString(value:Long):String{
    return try {
        value.toString()
    }catch (ex:Exception){
        BLANK_STRING
    }
}

fun intToString(value:Int):String{
    return try {
        value.toString()
    }catch (ex:Exception){
        BLANK_STRING
    }
}

fun formatRatio(ratio:String):String{
    return if(ratio.isNullOrEmpty() || ratio.equals("Nan",true))
        "0.00"
    else ratio

}

@Composable
fun ShowLoadingDialog(
) {
    Dialog(
        onDismissRequest = { /*setShowDialog(false)*/ },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false
        ),
    ) {
        Surface(
            modifier = Modifier
                .width(80.dp)
                .height(80.dp),
            color = Color.White,
        ) {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        CircularProgressIndicator(
                            color = blueDark,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(15.dp))

                }
            }
        }
//      }
    }
}
fun singleClick(onClick: () -> Unit): () -> Unit {
    var latest: Long = 0
    return {
        val now = System.currentTimeMillis()
        if (now - latest >= 350) {
            onClick()
            latest = now
        }
    }
}
fun containsEmoji(str: String?) =
    Pattern
        .compile("\\p{So}+", Pattern.CASE_INSENSITIVE)
        .matcher(str.toString()).find()



fun updateStepStatus(stepsListDao: StepsListDao, didiDao: DidiDao,didiId:Int,prefRepo: PrefRepo, printTag:String){
       CoroutineScope(Dispatchers.IO).launch {
        val step = stepsListDao.getStepByOrder(4,prefRepo.getSelectedVillage().id)
        NudgeLogger.d(printTag, "setPATSurveyINProgress -> stepsList: $step \n\n")
           didiDao.updatePATEditStatus(didiId,true)
        NudgeLogger.d(printTag, "setPATSurveyINProgress -> stepsListDao.markStepAsCompleteOrInProgress before " +
                "stepId = ${step.id},\n" +
                "isComplete = StepStatus.INPROGRESS.ordinal,\n" +
                "villageId = ${prefRepo.getSelectedVillage().id} \n")

        stepsListDao.markStepAsCompleteOrInProgress(
            stepId = step.id,
            isComplete = StepStatus.INPROGRESS.ordinal,
            villageId = prefRepo.getSelectedVillage().id
        )
        NudgeLogger.d(printTag, "setPATSurveyINProgress -> stepsListDao.markStepAsCompleteOrInProgress after " +
                "stepId = ${step.id},\n" +
                "isComplete = StepStatus.INPROGRESS.ordinal,\n" +
                "villageId = ${prefRepo.getSelectedVillage().id} \n")
        stepsListDao.updateNeedToPost(step.id, prefRepo.getSelectedVillage().id, true)
    }
}

fun addDefaultLanguage(languageListDao: LanguageListDao) {
    languageListDao.insertAll(
        listOf(
            LanguageEntity(
                id = 2,
                language = "English",
                langCode = "en",
                orderNumber = 1,
                localName = "English"
            ),
            LanguageEntity(
                1,
                language = "Hindi",
                langCode = "hi",
                orderNumber = 2,
                localName = "हिंदी"
            ),
            LanguageEntity(
                3,
                language = "Bengali",
                langCode = "bn",
                orderNumber = 3,
                localName = "বাংলা"
            ),
            LanguageEntity(
                4,
                language = "Assamese",
                langCode = "as",
                orderNumber = 4,
                localName = "অসমীয়া"
            ),
            LanguageEntity(
                5,
                language = "Bodo",
                langCode = "be",
                orderNumber = 5,
                localName = "बर'"
            )
        )
    )
}
fun getFormSubPath(formName: String, pageNumber: Int): String {
    return "${formName}_page_$pageNumber"
}

fun getFormPathKey(subPath: String,villageId: Int): String {
    return "${PREF_FORM_PATH}_${villageId}_${subPath}"
}

fun getVideoPath(context: Context, videoItemId: Int, fileType: FileType): File {
    return File("${context.getExternalFilesDir(if (fileType == FileType.VIDEO) Environment.DIRECTORY_MOVIES else if (fileType == FileType.IMAGE) Environment.DIRECTORY_DCIM else Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${videoItemId}.mp4")
}

fun getEmitLanguageList(defaultLanguageVillageList: List<VillageEntity>, localLanguageVillageList: List<VillageEntity>, localLanguageId: Int): List<VillageEntity> {
    val listToEmit = mutableListOf<VillageEntity>()
    val tempList = mutableListOf<VillageEntity>()
    tempList.addAll(defaultLanguageVillageList)
    tempList.addAll(localLanguageVillageList)
    tempList.forEach {
        if (it.languageId == localLanguageId)
            listToEmit.add(it)
        else
            listToEmit.add(getVillageItemById(defaultLanguageVillageList, id = it.id))
    }
    return listToEmit
}

fun getVillageItemById(villageList: List<VillageEntity>, id: Int): VillageEntity {
    return villageList[villageList.map { it.id }.indexOf(id)]
}

fun calculateMatchPercentage(didiList: List<DidiEntity>, questionPassingScore: Int): Int {
    val matchedCount = didiList.filter {
        (it.score ?: 0.0) >= questionPassingScore.toDouble()
                && (it.crpScore ?: 0.0) >= questionPassingScore.toDouble()
    }.size

    return if (didiList.isNotEmpty() && matchedCount != 0) ((matchedCount.toFloat() / didiList.size.toFloat()) * 100).toInt() else 0

}

fun List<DidiEntity>.getNotAvailableDidiCount(): Int {
    return this.filter {
        it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
    }.size
}

fun <T> getParentEntityMapForEvent(eventItem: T, eventName: EventName): Map<String, String> {
    return when (eventName) {

        EventName.ADD_TOLA -> {
            emptyMap()
        }

        EventName.UPDATE_TOLA -> {
            val tolaEntity = (eventItem as TolaEntity)
            mapOf(
                KEY_PARENT_ENTITY_TOLA_ID to eventItem.serverId.toString(),
                KEY_PARENT_ENTITY_VILLAGE_ID to eventItem.villageId.toString()
            )
        }

        EventName.DELETE_TOLA -> {
            val tolaEntity = (eventItem as TolaEntity)
            mapOf(
                KEY_PARENT_ENTITY_TOLA_NAME to tolaEntity.name,
                KEY_PARENT_ENTITY_VILLAGE_ID to tolaEntity.villageId.toString()
            )
        }

        EventName.ADD_DIDI -> {
            val didiEntity = (eventItem as DidiEntity)
            mapOf(
                KEY_PARENT_ENTITY_TOLA_NAME to didiEntity.cohortName,
                KEY_PARENT_ENTITY_VILLAGE_ID to didiEntity.villageId.toString()
            )
        }

        EventName.UPDATE_DIDI -> {
            val didiEntity = (eventItem as DidiEntity)
            mapOf(
                KEY_PARENT_ENTITY_DIDI_ID to didiEntity.id.toString(),
                KEY_PARENT_ENTITY_VILLAGE_ID to didiEntity.villageId.toString()
            )
        }

        EventName.DELETE_DIDI, EventName.SAVE_WEALTH_RANKING, EventName.NOT_AVAILBLE_PAT_SCORE, EventName.REJECTED_PAT_SCORE, EventName.INPROGRESS_PAT_SCORE, EventName.COMPLETED_PAT_SCORE, EventName.SAVE_PAT_ANSWERS, EventName.SAVE_VO_ENDORSEMENT -> {
            val didiEntity = (eventItem as DidiEntity)
            mapOf(
                KEY_PARENT_ENTITY_DIDI_NAME to didiEntity.name,
                KEY_PARENT_ENTITY_DADA_NAME to didiEntity.guardianName,
                KEY_PARENT_ENTITY_ADDRESS to didiEntity.address,
                KEY_PARENT_ENTITY_TOLA_NAME to didiEntity.cohortName
            )
        }

        else -> {
            emptyMap()
        }
    }
}

private fun getAllAnswersForDidi(didiId: Int, answerDao: AnswerDao): List<SectionAnswerEntity> {
    return answerDao.getAllNeedToPostQuesForDidi(didiId)
}

private fun getAllNumericAnswersForDidi(
    didiId: Int,
    numericAnswerDao: NumericAnswerDao
): List<NumericAnswerEntity> {
    return numericAnswerDao.getAllAnswersForDidi(didiId)
}

private fun getSurveyId(questionId: Int, questionListDao: QuestionListDao): Int {
    val questionList = questionListDao.getQuestion(questionId)
    if (questionList != null) {
        return questionList.surveyId ?: 0
    }
    return 0
}

suspend fun getPatSummarySaveEventPayload(
    didiEntity: DidiEntity,
    answerDao: AnswerDao,
    numericAnswerDao: NumericAnswerDao,
    questionListDao: QuestionListDao,
    prefRepo: PrefRepo
): PATSummarySaveRequest {
    val sectionAnswerEntityList = getAllAnswersForDidi(didiEntity.id, answerDao)
    val numericAnswerEntityList = getAllNumericAnswersForDidi(didiEntity.id, numericAnswerDao)
    val answerDetailDTOListItem = AnswerDetailDTOListItem.getAnswerDetailDtoListItem(
        sectionAnswerEntityList,
        numericAnswerEntityList
    )
    val patSummarySaveRequest = PATSummarySaveRequest.getPatSummarySaveRequest(
        didiEntity = didiEntity,
        answerDetailDTOList = answerDetailDTOListItem,
        languageId = (prefRepo.getAppLanguageId() ?: 2),
        surveyId = getSurveyId(
            sectionAnswerEntityList?.firstOrNull()?.questionId ?: 0,
            questionListDao
        ),
        villageEntity = prefRepo.getSelectedVillage(),
        userType = if ((prefRepo.getPref(PREF_KEY_TYPE_NAME, "") ?: "").equals(
                BPC_USER_TYPE,
                true
            )
        ) USER_BPC else USER_CRP
    )

    return patSummarySaveRequest
}

suspend fun getPatScoreSaveEvent(
    didiEntity: DidiEntity,
    questionListDao: QuestionListDao,
    prefRepo: PrefRepo,
    tolaDeviceId: String,
    tolaServerId: Int
): EditDidiWealthRankingRequest {
    val passingMark = questionListDao.getPassingScore()
    val patScoreSaveRequest = EditDidiWealthRankingRequest.getRequestPayloadForPatScoreSave(
        didiEntity,
        passingMark,
        isBpcUserType = prefRepo.isUserBPC(),
        tolaDeviceId = tolaDeviceId,
        tolaServerId = tolaServerId


    )
    return patScoreSaveRequest
}

fun getPatScoreEventName(didi: DidiEntity, isBpcUserType: Boolean): EventName {
    val result: EventName =
        if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
            EventName.NOT_AVAILBLE_PAT_SCORE
        } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
            EventName.INPROGRESS_PAT_SCORE
        } else {
            if (didi.forVoEndorsement == 0 || didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal) EventName.REJECTED_PAT_SCORE else {
                if (isBpcUserType)
                    EventName.COMPLETED_PAT_SCORE
                else
                    EventName.COMPLETED_PAT_SCORE
            }
        }
    return result
}

fun restartApp(context: Context) {

    context.startActivity(
        Intent(NudgeCore.getAppContext().applicationContext, MainActivity::class.java)
    )
    exitProcess(0)
}
