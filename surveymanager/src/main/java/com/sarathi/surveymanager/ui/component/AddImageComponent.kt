package com.sarathi.surveymanager.ui.component

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.sarathi.missionactivitytask.ui.theme.borderGreyLight
import com.sarathi.missionactivitytask.ui.theme.largeTextStyle
import com.sarathi.missionactivitytask.ui.theme.white
import com.sarathi.surveymanager.R
import java.io.File


@Preview(showSystemUi = true)
@Composable
fun AddImageComponent(
    isImageAvailable: Boolean = true, photoUri: MutableState<Uri> = mutableStateOf(
        Uri.EMPTY
    )
) {
    val context = LocalContext.current as Activity
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyGridState = rememberLazyGridState()
    var imageList = listOf(
        ImageEntity(R.drawable.ic_mission_inprogress),
        ImageEntity(R.drawable.ic_mission_inprogress),
        ImageEntity(R.drawable.ic_mission_inprogress),
        ImageEntity(R.drawable.ic_mission_inprogress),
        ImageEntity(R.drawable.ic_mission_inprogress)
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
                            openCamera(context)
                            // Toast.makeText(context, "Mobile Number:", Toast.LENGTH_LONG).show()
                        })
                    }
                }
                itemsIndexed(imageList) { _index, image ->
                    ImageView()
                }
            }
        }
    }


}


@Composable
fun getPhotoUri() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val photoFile = remember {
        File(context.cacheDir, "photo.jpg").apply {
            createNewFile()
            deleteOnExit()
        }
    }

    val photoUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        photoFile
    )

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = photoUri
        }
    }
}


fun openCamera(activity: Activity) {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    activity.startActivityForResult(cameraIntent, 300)
}

@Composable
fun ImageView() {
    Image(
        // painter = rememberAsyncImagePainter(getPhotoUri()),
        painter = painterResource(id = R.drawable.ic_mission_inprogress),
        contentDescription = null,
        modifier = Modifier
            .size(150.dp)
            .padding(10.dp)
            .background(Color.Gray, shape = RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

data class ImageEntity(var resId: Int)

