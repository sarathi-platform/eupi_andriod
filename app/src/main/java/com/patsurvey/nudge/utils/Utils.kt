package com.patsurvey.nudge.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.video.VideoItem
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.WeightageRatioModal
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.transform
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.lang.reflect.Type
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

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
        title = "Video 1",
        description = "Introducing Chromecast. The easiest way to enjoy online video and music on your TV. For \$35.  Find out more at google.com/chromecast.",
        url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        thumbUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
    ),
    VideoItem(
        id = 2,
        title = "Video 2",
        description = "Supporting description",
        url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg"
    ),
    VideoItem(
        id = 3,
        title = "Video 3",
        description = "Supporting description",
        url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        thumbUrl =     "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg"
    ),
    VideoItem(
        id = 4,
        title = "Video 4",
        description = "Supporting description",
        url = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        thumbUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg"
    )

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
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}

fun roundOffDecimalPoints(number: Double): String {
    return String.format("%.2f", number)
}

fun getImagePath(context: Context, imagePath:String): File {
    val imageName = getFileNameFromURL(imagePath)
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