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
import com.nudge.core.EXPANSTION_TRANSITION_DURATION
import com.nudge.core.KEY_PARENT_ENTITY_ADDRESS
import com.nudge.core.KEY_PARENT_ENTITY_DADA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_DIDI_ID
import com.nudge.core.KEY_PARENT_ENTITY_DIDI_NAME
import com.nudge.core.KEY_PARENT_ENTITY_TOLA_ID
import com.nudge.core.KEY_PARENT_ENTITY_TOLA_NAME
import com.nudge.core.KEY_PARENT_ENTITY_VILLAGE_ID
import com.nudge.core.LAST_SYNC_TIME
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
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.download.FileType
import com.patsurvey.nudge.model.dataModel.WeightageRatioModal
import com.patsurvey.nudge.model.request.AnswerDetailDTOListItem
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.utils.NudgeCore.getVoNameForState
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
import java.io.InputStreamReader
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

fun getDeviceId(context: Context): String {
    return Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: ""
}

fun getUniqueIdForEntity(): String {
    return UUID.randomUUID().toString().replace("-", "") + "|" + System.currentTimeMillis()
}

fun findCompleteValue(status: String): StepStatus {
    return when (status) {
        BLANK_STRING -> StepStatus.NOT_STARTED
        INPROGRESS_STRING -> StepStatus.INPROGRESS
        COMPLETED_STRING -> StepStatus.COMPLETED
        else -> {
            StepStatus.NOT_STARTED
        }
    }
}

fun getStepStatusFromOrdinal(status: Int): String {
    return when (status) {
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

inline fun Modifier.debounceClickable(
    debounceInterval: Long = 500,
    crossinline onClick: () -> Unit,
): Modifier {
    var lastClickTime = 0L
    var clickCount = 0
    return clickable() {
        Log.d("Utils", "debounceClickable, initial clickCount = $clickCount")
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastClickTime) < debounceInterval) {
            clickCount = clickCount++
            return@clickable
        }
        lastClickTime = currentTime
        Log.d(
            "Utils",
            "debounceClickable: buttonClicked lastClickTime = $lastClickTime, clickCount = $clickCount"
        )
        onClick()
    }
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
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

fun uriFromFile(context: Context, file: File): Uri {
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
    val appSettingsIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:${context.packageName}")
    ).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
    }
    appSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    (context as MainActivity).startActivity(appSettingsIntent)
}

fun <T> convertJsonFileFromAssets(context: Context, fileName: String, clazz: Class<T>): List<T> {
    val assetManager = context.assets
    val inputStream = assetManager.open(fileName)
    val reader = InputStreamReader(inputStream)
    val gson = Gson()
    val listType = object : TypeToken<List<T>>() {}.type
    return gson.fromJson(reader, listType)
}

var videoList =
    convertJsonFileFromAssets(
        context = NudgeCore.getAppContext(),
        fileName = "trainingVideoList.json",
        clazz = VideoItem::class.java
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


fun checkStringOperator(operator: String) = when (operator) {
    "==" -> Operator.EQUAL_TO
    "=" -> Operator.EQUAL_TO
    "<" -> Operator.LESS_THAN
    "<=" -> Operator.LESS_THAN_EQUAL_TO
    ">" -> Operator.MORE_THAN
    ">=" -> Operator.MORE_THAN_EQUAL_TO
    else -> {}
}

fun stringToDouble(string: String): Double {
    var doubleAmount = 0.0
    if (string != null) {
        doubleAmount = if (string.isEmpty())
            0.0
        else string.toDouble()
    }
    return doubleAmount
}

fun stringToInt(string: String): Int {
    var intValue = 0
    if (string != null) {
        intValue = if (string.isEmpty())
            0
        else string.toInt()
    }
    return intValue
}

fun doubleToString(double: Double): String {
    var string = BLANK_STRING
    if (double > 0.0) {
        string = double.toString()
    }
    return string
}

fun calculateScore(list: List<WeightageRatioModal>, totalAmount: Double, isRatio: Boolean): Double {
    var score: Double = 0.0
    run breaking@{
        list.forEach {
            when (checkStringOperator(it.operator)) {
                Operator.EQUAL_TO -> {

                    if (totalAmount == (if (!isRatio) stringToDouble(it.weightage) else stringToDouble(
                            it.ratio
                        ))
                    ) {
                        score = it.score.toDouble()
                        return@breaking
                    }
                }

                Operator.LESS_THAN -> {
                    if (totalAmount < (if (!isRatio) stringToDouble(it.weightage) else stringToDouble(
                            it.ratio
                        ))
                    ) {
                        score = it.score.toDouble()
                        return@breaking
                    }
                }

                Operator.LESS_THAN_EQUAL_TO -> {
                    if (totalAmount <= (if (!isRatio) stringToDouble(it.weightage) else stringToDouble(
                            it.ratio
                        ))
                    ) {
                        score = it.score.toDouble()
                        return@breaking
                    }
                }

                Operator.MORE_THAN -> {
                    if (totalAmount > (if (!isRatio) stringToDouble(it.weightage) else stringToDouble(
                            it.ratio
                        ))
                    ) {
                        score = it.score.toDouble()
                        return@breaking
                    }
                }

                Operator.MORE_THAN_EQUAL_TO -> {
                    if (totalAmount >= (if (!isRatio) stringToDouble(it.weightage) else stringToDouble(
                            it.ratio
                        ))
                    ) {
                        score = it.score.toDouble()
                        return@breaking
                    }
                }

                else -> {
                    score = 0.0
                }
            }
        }
    }
    return score
}

fun getFileNameFromURL(url: String): String {
    return url.substring(url.lastIndexOf('/') + 1, url.length)
}

fun getAuthImageFileNameFromURL(url: String): String {
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
    } catch (ex: Exception) {
        NudgeLogger.e("Utils", "roundOffDecimal -> exception", ex)
        0.00
    }


}

fun roundOffDecimalFloat(number: Float): Float? {
    return try {
        val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
        df.roundingMode = RoundingMode.CEILING
        df.format(number).toFloat()
    } catch (ex: Exception) {
        NudgeLogger.e("Utils", "roundOffDecimal -> exception", ex)
        0.00f
    }


}

fun roundOffDecimalPoints(number: Double): String {
    return String.format(Locale.ENGLISH, "%.2f", number)
}

fun getImagePath(context: Context, imagePath: String): File {
    val imageName = getFileNameFromURL(imagePath)
    return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${imageName}")
}

fun getAuthImagePath(context: Context, imagePath: String): File {
    val imageName = getAuthImageFileNameFromURL(imagePath)
    return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${imageName}")
}

fun getImageRequestBody(sourceFile: File): RequestBody? {
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

fun updateLastSyncTime(prefRepo: PrefRepo, lastSyncTime: String) {
    val saveSyncTime = prefRepo.getPref(LAST_SYNC_TIME, 0L)
    if (saveSyncTime > 0) {
        val compValue = lastSyncTime.toLong().compareTo(saveSyncTime)
        if (compValue > 0) {
            prefRepo.savePref(LAST_SYNC_TIME, lastSyncTime.toLong())
        }

    } else prefRepo.savePref(LAST_SYNC_TIME, lastSyncTime.toLong())
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

fun compressImage(imageUri: String, activity: Context, name: String): String? {
    var filename: String? = ""
    try {
        val filePath = imageUri
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
            val path = File(
                "${activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath}",
                filename
            ).absolutePath
            out = FileOutputStream(path)
            val success = scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
            return if (success == true) {
                path
            } else BLANK_STRING
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

fun formatDateAndTime(page: String, lastSyncTime: String): String {
    return try {
        val currentTime = if (lastSyncTime.isEmpty()) 0L else lastSyncTime.toLong()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        dateFormat.format(currentTime)
    } catch (ex: Exception) {
        ex.message?.let { NudgeLogger.d("$page DateTimeParse Issue : ", it) }
        BLANK_STRING
    }
}

fun findImageFilePath(uri: String): String {
    if (uri.isNotEmpty()) {
        return if (uri.contains("|")) {
            if (uri.split("|")[0].contains("content:/")) uri.split("|")[0].replace(
                "content:/",
                "file:/"
            ) else uri.split("|")[0]
        } else {
            if (uri.contains("content:/")) uri.replace("content:/", "file:/") else uri
        }

    }
    return BLANK_STRING
}

fun findImageLocationFromPath(uri: String): List<String> {
    if (uri.isNotEmpty()) {
        if (uri.contains("|")) {
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

fun findStepNameForSelectedLanguage(context: Context, stepId: Int, stateId: Int): String {
    return when (stepId) {
        40 -> context.getString(R.string.step_transect_walk)
        41 -> context.getString(R.string.step_social_mapping)
        43 -> context.getString(R.string.step_pat_survey)
        44 -> getVoNameForState(context, stateId, R.plurals.step_vo_endorsement)
        45 -> context.getString(R.string.step_bpc_verification)
        46 -> context.getString(R.string.step_participatory_wealth_ranking)
        else -> {
            BLANK_STRING
        }
    }
}

fun onlyNumberField(value: String): Boolean {
    if (value.isDigitsOnly() && value != "_" && value != "N") {
        return true
    }
    return false
}

fun changeMilliDateToDate(millDate: Long): String? {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    return dateFormat.format(millDate)
}

fun longToString(value: Long): String {
    return try {
        value.toString()
    } catch (ex: Exception) {
        BLANK_STRING
    }
}

fun intToString(value: Int): String {
    return try {
        value.toString()
    } catch (ex: Exception) {
        BLANK_STRING
    }
}

fun formatRatio(ratio: String): String {
    return if (ratio.isNullOrEmpty() || ratio.equals("Nan", true))
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
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

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(15.dp)
                    )

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


fun updateStepStatus(
    stepsListDao: StepsListDao,
    didiDao: DidiDao,
    didiId: Int,
    prefRepo: PrefRepo,
    printTag: String
) {
    CoroutineScope(Dispatchers.IO).launch {
        val step = stepsListDao.getStepByOrder(4, prefRepo.getSelectedVillage().id)
        NudgeLogger.d(printTag, "setPATSurveyINProgress -> stepsList: $step \n\n")
        didiDao.updatePATEditStatus(didiId, true)
        NudgeLogger.d(
            printTag,
            "setPATSurveyINProgress -> stepsListDao.markStepAsCompleteOrInProgress before " +
                    "stepId = ${step.id},\n" +
                    "isComplete = StepStatus.INPROGRESS.ordinal,\n" +
                    "villageId = ${prefRepo.getSelectedVillage().id} \n"
        )

        stepsListDao.markStepAsCompleteOrInProgress(
            stepId = step.id,
            isComplete = StepStatus.INPROGRESS.ordinal,
            villageId = prefRepo.getSelectedVillage().id
        )
        NudgeLogger.d(
            printTag,
            "setPATSurveyINProgress -> stepsListDao.markStepAsCompleteOrInProgress after " +
                    "stepId = ${step.id},\n" +
                    "isComplete = StepStatus.INPROGRESS.ordinal,\n" +
                    "villageId = ${prefRepo.getSelectedVillage().id} \n"
        )
        stepsListDao.updateNeedToPost(step.id, prefRepo.getSelectedVillage().id, true)
    }
}


fun getFormSubPath(formName: String, pageNumber: Int): String {
    return "${formName}_page_$pageNumber"
}

fun getFormPathKey(subPath: String, villageId: Int): String {
    return "${PREF_FORM_PATH}_${villageId}_${subPath}"
}

fun getVideoPath(context: Context, videoItemId: Int, fileType: FileType): File {
    return File("${context.getExternalFilesDir(if (fileType == FileType.VIDEO) Environment.DIRECTORY_MOVIES else if (fileType == FileType.IMAGE) Environment.DIRECTORY_DCIM else Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${videoItemId}.mp4")
}

fun getEmitLanguageList(
    defaultLanguageVillageList: List<VillageEntity>,
    localLanguageVillageList: List<VillageEntity>,
    localLanguageId: Int
): List<VillageEntity> {
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

        EventName.ADD_DIDI,
        EventName.SHG_FLAG_EVENT,
        EventName.ABLE_BODIED_FLAG_EVENT -> {
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

fun isFilePathExists(context: Context, filePath: String): Boolean {
    val fileName = getFileNameFromURL(filePath)
    return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${fileName}").exists()
}

fun getSortedList(index:Int, didiList:List<DidiEntity>):List<DidiEntity>{
    return  when(index) {
        0->didiList.sortedBy { it.createdDate }
        1->didiList.sortedByDescending { it.createdDate }
        2->didiList.sortedBy { it.modifiedDate }
        3->didiList.sortedByDescending { it.modifiedDate }
        4->didiList.sortedBy { it.name.lowercase() }
        5->didiList.sortedByDescending { it.name.lowercase() }
        else -> {
            didiList
        }
    }
}

fun getSortedMap(didiSortedIndex:Int,tolaMapList:Map<String,List<DidiEntity>>):Map<String,List<DidiEntity>>{
    var fMapList= mutableMapOf<String,List<DidiEntity>>()

    tolaMapList.forEach(){ mapEntry-> fMapList.put(mapEntry.key, getSortedList(didiSortedIndex,mapEntry.value))
    }
   return fMapList
}



