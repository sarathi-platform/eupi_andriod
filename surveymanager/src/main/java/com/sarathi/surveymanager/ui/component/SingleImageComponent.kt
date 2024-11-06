package com.sarathi.surveymanager.ui.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nudge.core.getFileNameFromURL
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.openSettings
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_150_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_300_dp
import com.nudge.core.ui.theme.dimen_30_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dotedBorderColor
import com.nudge.core.ui.theme.largeTextStyle
import com.nudge.core.ui.theme.redDark
import com.nudge.core.ui.theme.white
import com.nudge.core.uriFromFile
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.surveymanager.R
import java.io.File


@SuppressLint("UnrememberedMutableState", "UnusedBoxWithConstraintsScope")
@Composable
fun SingleImageComponent(
    isMandatory: Boolean = false,
    maxCustomHeight: Dp = 200.dp,
    title: String = BLANK_STRING,
    isEditable: Boolean = true,
    filePaths: String,
    fileNamePrefix: String,
    subtitle: String? = null,
    areMultipleImagesAllowed: Boolean = true,
    onImageSelection: (selectValue: String, isDeleted: Boolean) -> Unit,
) {
    val context = LocalContext.current
    var image by remember { mutableStateOf(getImageUri(context = context, getFileNameFromURL(filePaths), false)) }

    var currentImageUri by remember { mutableStateOf<Uri?>(null) }
    val shouldRequestPermission = remember {
        mutableStateOf(false)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                image =  currentImageUri
                onImageSelection(currentImageUri?.path ?: BLANK_STRING, false)
            }
        }
    )
    LaunchedEffect(key1 = context) {
        requestCameraPermission(context as Activity) {
            shouldRequestPermission.value = it
        }
    }

    val boxModifier =
        Modifier
            .fillMaxSize()
            .height(dimen_300_dp)

    Column(modifier = Modifier.padding(bottom = dimen_30_dp, start = dimen_5_dp)) {
        if (title.isNotBlank()) {
            QuestionComponent(
                title = title,
                isRequiredField = isMandatory,
                subTitle = subtitle ?: "Signed & Sealed Physical Format D"
            )
        }

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
                .height(dimen_300_dp)
        ) {
            Box(
                modifier =
                        boxModifier
                            .clickable(
                                enabled = isEditable
                            ) {

                                requestCameraPermission(context as Activity) {
                                    shouldRequestPermission.value = it

                                    if (!it) {
                                        currentImageUri = getSingleImageUri(
                                            context, "${fileNamePrefix}${
                                                System.currentTimeMillis()
                                            }.png",
                                            true
                                        )

                                        cameraLauncher.launch(
                                            currentImageUri
                                        )
                                    }
                                }


                            }
                            .background(white)
                            .dottedBorder(
                                dotSpacing = 16f,
                                dotRadius = 6f,
                                dotColor = dotedBorderColor
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        if (image?.path==null || image?.path=="/external_files/Pictures")
                        {
                            Text(
                                stringResource(R.string.add_image),
                                style = largeTextStyle.copy(blueDark),
                            )
                        }else
                        {
                            Box(
                                modifier = Modifier.fillMaxSize()
                                    .height(dimen_150_dp)
                                    .padding(dimen_5_dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(image),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(dimen_6_dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.ic_delete_icon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(dimen_10_dp)
                                        .align(Alignment.BottomEnd)
                                        .clickable(enabled = isEditable) {
                                            image=null
                                            onImageSelection(image?.path ?: BLANK_STRING, true)
                                        }
                                        .size(dimen_24_dp),
                                    colorFilter = ColorFilter.tint(redDark)
                                )
                            }

                        }
                    }
                }
    }
    if (shouldRequestPermission.value) {
        ShowCustomDialog(
            message = stringResource(R.string.permission_dialog_prompt_message),
            negativeButtonTitle = stringResource(R.string.no),
            positiveButtonTitle = stringResource(R.string.yes),
            onNegativeButtonClick = {
                shouldRequestPermission.value = false
            },
            onPositiveButtonClick = {
                openSettings()

                shouldRequestPermission.value = false

            }
        )
    }
}

fun getSingleImageUri(context: Context, fileName: String, isNewImage: Boolean): Uri? {
    var file =
        File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath}/${fileName}")
    if (!isNewImage && !file.exists()) {
        file =
            File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${fileName}")
    }

    return CoreAppDetails.getApplicationDetails()?.applicationID?.let {
        uriFromFile(
            context, file,
            it
        )
    }
}




