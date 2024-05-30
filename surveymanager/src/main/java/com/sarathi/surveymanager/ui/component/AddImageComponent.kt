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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.uriFromFile
import com.sarathi.surveymanager.theme.borderGreyLight
import com.sarathi.surveymanager.theme.largeTextStyle
import com.sarathi.surveymanager.theme.white
import java.io.File


@SuppressLint("UnrememberedMutableState")
@Preview(showSystemUi = true)
@Composable
fun AddImageComponent(
    isImageAvailable: Boolean = true
) {
    val context = LocalContext.current
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyGridState = rememberLazyGridState()
    var imageList by remember { mutableStateOf(listOf<Uri?>()) }
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                imageList = (imageList + currentImageUri)
            }
        }
    )
    Column {
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
                        max = maxHeight
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                item {
                    Box(
                        modifier = Modifier
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
                        Text("+ Add Image", style = largeTextStyle, modifier = Modifier.clickable {
                            currentImageUri = getImageUri(
                                context, "${
                                    System.currentTimeMillis().toString()
                                }.jpg"
                            )
                            cameraLauncher.launch(
                                currentImageUri
                            )
                        })
                    }
                }
                itemsIndexed(imageList) { _index, image ->
                    image?.let { ImageView(it) }
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

@Composable
fun ImageView(uri: Uri) {
    Image(
        painter = rememberAsyncImagePainter(uri),
        contentDescription = null,
        modifier = Modifier
            .size(150.dp)
            .padding(10.dp)
            .background(Color.Gray, shape = RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

data class ImageEntity(var imageUri: Uri, var isImageAvailable: Boolean)

