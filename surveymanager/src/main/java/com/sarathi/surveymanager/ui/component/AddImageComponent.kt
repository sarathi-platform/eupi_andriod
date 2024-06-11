package com.sarathi.surveymanager.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nudge.core.getFileNameFromURL
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.ui.events.theme.borderGreyLight
import com.nudge.core.ui.events.theme.dimen_10_dp
import com.nudge.core.ui.events.theme.dimen_24_dp
import com.nudge.core.ui.events.theme.largeTextStyle
import com.nudge.core.ui.events.theme.redDark
import com.nudge.core.ui.events.theme.white
import com.nudge.core.uriFromFile
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.surveymanager.R
import java.io.File


@SuppressLint("UnrememberedMutableState")
@Preview(showSystemUi = true)
@Composable
fun AddImageComponent(
    isMandatory: Boolean = false,
    maxCustomHeight: Dp = 200.dp,
    title: String = BLANK_STRING,
    isEditable: Boolean = true,
    filePaths: List<String> = listOf(),
    onImageSelection: (selectValue: String, isDeleted: Boolean) -> Unit,


    ) {
    val context = LocalContext.current
    val outerState: LazyListState = rememberLazyListState()
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
    Column(modifier = Modifier.padding(bottom = 30.dp)) {
        if (title.isNotBlank()) {
            QuestionComponent(title = title, isRequiredField = isMandatory)
        }
        BoxWithConstraints(
            modifier = Modifier
                .scrollable(
                    state = outerState,
                    Orientation.Vertical,
                )
        ) {
            LazyVerticalGrid(
                userScrollEnabled = false,
                state = innerState,
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .wrapContentWidth()
                    .heightIn(
                        min = 110.dp,
                        max = maxCustomHeight
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                item {
                    Box(
                        modifier =
                        Modifier
                            .clickable(enabled = isEditable) {
                                currentImageUri = getImageUri(
                                    context, "${
                                        System.currentTimeMillis()
                                    }.jpg"
                                )

                                cameraLauncher.launch(
                                    currentImageUri
                                )
                            }
                            .size(150.dp)
                            .background(white)
                            .padding(10.dp)
                            .border(
                                width = 2.dp,
                                color = borderGreyLight,
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "+ Add Image", style = largeTextStyle,
                        )
                    }
                }
                itemsIndexed(imageList) { _, image ->
                    image?.let {
                        ImageView(it) { uri ->
                            imageList = (imageList - uri)

                            onImageSelection(uri.path ?: BLANK_STRING, true)
                        }
                    }
                }
            }
        }
    }


}

fun getImageUri(context: Context, fileName: String): Uri? {
    val file =
        File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${fileName}")
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
            uriList.add(getImageUri(context = context, getFileNameFromURL(it)))
        }
    }
    return uriList
}

@Composable
fun ImageView(uri: Uri, onDelete: (fileUri: Uri) -> Unit) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .padding(10.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.ic_delete_icon),
            contentDescription = null,
            modifier = Modifier
                .padding(dimen_10_dp)
                .align(Alignment.BottomEnd)
                .clickable {
                    onDelete(uri)
                }
                .size(dimen_24_dp),
            colorFilter = ColorFilter.tint(redDark)
        )
    }
}

