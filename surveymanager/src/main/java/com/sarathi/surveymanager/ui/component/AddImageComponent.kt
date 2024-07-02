package com.sarathi.surveymanager.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nudge.core.getFileNameFromURL
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_100_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_150_dp
import com.nudge.core.ui.theme.dimen_200_dp
import com.nudge.core.ui.theme.dimen_24_dp
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


@SuppressLint("UnrememberedMutableState")
@Composable
fun AddImageComponent(
    isMandatory: Boolean = false,
    maxCustomHeight: Dp = 200.dp,
    title: String = BLANK_STRING,
    isEditable: Boolean = true,
    filePaths: List<String> = listOf(),
    fileNamePrefix: String,
    onImageSelection: (selectValue: String, isDeleted: Boolean) -> Unit,


    ) {
    val context = LocalContext.current
    val innerState: LazyGridState = rememberLazyGridState()
    var imageList by remember { mutableStateOf(getSavedImageUri(context, filePaths)) }
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {

                imageList = (imageList + currentImageUri)
                onImageSelection(currentImageUri?.path ?: BLANK_STRING, false)
            }
        }
    )

    val boxModifier = if (imageList.isNotEmpty()) {
        Modifier
            .size(dimen_150_dp)
            .padding(dimen_5_dp)
    } else {
        Modifier
            .fillMaxSize()
            .height(dimen_200_dp)
    }
    Column(modifier = Modifier.padding(bottom = dimen_30_dp, start = dimen_5_dp)) {
        if (title.isNotBlank()) {
            QuestionComponent(
                title = title,
                isRequiredField = isMandatory,
                subTitle = "Signed & Sealed Physical Format D"
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .heightIn(min = dimen_100_dp, maxCustomHeight)
        ) {
            LazyVerticalGrid(
                state = innerState,
                columns = GridCells.Fixed(if (imageList.isEmpty()) 1 else 2),
                modifier = Modifier
                    .heightIn(min = 110.dp, max = maxCustomHeight),
                horizontalArrangement = Arrangement.Center
            ) {
                item {
                    Box(
                        modifier =
                        boxModifier
                            .clickable(enabled = isEditable) {
                                currentImageUri = getImageUri(
                                    context, "${fileNamePrefix}${
                                        System.currentTimeMillis()
                                    }.png",
                                    true
                                )

                                cameraLauncher.launch(
                                    currentImageUri
                                )
                            }
                            .background(white)
                            .dottedBorder(
                                dotSpacing = 16f,
                                dotRadius = 6f,
                                dotColor = dotedBorderColor
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.add_image),
                            style = largeTextStyle.copy(blueDark),
                        )
                    }
                }
                itemsIndexed(imageList) { _, image ->
                    image?.let {
                        ImageView(it, isEditable) { uri ->
                            imageList = (imageList - uri)

                            onImageSelection(uri.path ?: BLANK_STRING, true)
                        }
                    }
                }
            }
        }
    }


}

fun getImageUri(context: Context, fileName: String, isNewImage:Boolean): Uri? {
    var file = File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath}/${fileName}")
  if(!isNewImage && !file.exists())
  {
      file=File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${fileName}")
  }
    return CoreAppDetails.getApplicationDetails()?.applicationID?.let {
        uriFromFile(
            context, file,
            it
        )
    }
}

fun getSavedImageUri(
    context: Context, filePaths: List<String> = listOf(),
): List<Uri?> {
    val uriList: ArrayList<Uri?> = ArrayList<Uri?>()
    filePaths.forEach {
        if (it.isNotEmpty()) {
            uriList.add(getImageUri(context = context, getFileNameFromURL(it),false))
        }
    }
    return uriList
}

@Composable
fun ImageView(uri: Uri, isEditable: Boolean, onDelete: (fileUri: Uri) -> Unit) {
    Box(
        modifier = Modifier
            .size(dimen_150_dp)
            .padding(dimen_5_dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimen_6_dp))
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.ic_delete_icon),
            contentDescription = null,
            modifier = Modifier
                .padding(dimen_10_dp)
                .align(Alignment.BottomEnd)
                .clickable(enabled = isEditable) {
                    onDelete(uri)
                }
                .size(dimen_24_dp),
            colorFilter = ColorFilter.tint(redDark)
        )
    }
}

fun Modifier.dottedBorder(dotSpacing: Float, dotRadius: Float, dotColor: Color) = this.drawBehind {
    drawDottedLine(dotColor, dotRadius, dotSpacing, 0f, 0f, size.width, 0f)       // Top border
    drawDottedLine(
        dotColor,
        dotRadius,
        dotSpacing,
        0f,
        size.height,
        size.width,
        size.height
    ) // Bottom border
    drawDottedLine(dotColor, dotRadius, dotSpacing, 0f, 0f, 0f, size.height)     // Left border
    drawDottedLine(
        dotColor,
        dotRadius,
        dotSpacing,
        size.width,
        0f,
        size.width,
        size.height
    ) // Right border
}

fun DrawScope.drawDottedLine(
    color: Color,
    dotSize: Float,
    spacing: Float,
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float
) {
    val totalLength = if (startX == endX) size.height else size.width
    var currentPosition = 0f
    while (currentPosition < totalLength) {
        drawRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(
                x = if (startX == endX) startX - dotSize / 2 else currentPosition,
                y = if (startY == endY) startY - dotSize / 2 else currentPosition
            ),
            size = androidx.compose.ui.geometry.Size(dotSize, dotSize)
        )
        currentPosition += spacing
    }
}

