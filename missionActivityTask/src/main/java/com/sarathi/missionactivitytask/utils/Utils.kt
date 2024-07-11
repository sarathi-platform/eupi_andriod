package com.sarathi.missionactivitytask.utils

import android.net.Uri
import android.os.Environment
import androidx.compose.ui.graphics.Color
import com.nudge.core.BLANK_STRING
import com.nudge.core.getFileNameFromURL
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.ui.theme.greenOnline
import java.io.File

fun statusColor(status: String): Color = when (status) {
    StatusEnum.Active.name -> Color.Green
    StatusEnum.Inactive.name -> Color.Red
    StatusEnum.INPROGRESS.name -> Color.Gray
    StatusEnum.COMPLETED.name -> greenOnline
    StatusEnum.PENDING.name -> Color.Gray
    StatusEnum.NOT_STARTED.name -> Color.Gray
    StatusEnum.NOT_AVAILABLE.name -> Color.Gray
    else -> {
        Color.Gray
    }
}

fun String.getImagePathFromString(): String {
    return try {
        this.split("|").first()
    } catch (ex: Exception) {
        return BLANK_STRING
    }
}

fun isFilePathExists(filePath: String): Boolean {
    val fileName = getFileNameFromURL(filePath)
    return File(
        "${
            CoreAppDetails.getContext()
                ?.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath
        }/${fileName}"
    ).exists()
}

fun getFilePath(filePath: String): File {
    val fileName = getFileNameFromURL(filePath)
    return File(
        "${
            CoreAppDetails.getContext()
                ?.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath
        }/${fileName}"
    )
}

fun getFilePathUri(filePath: String): Uri? {
    if (filePath.isEmpty()) {
        return null
    }
    val isFilePathExists = isFilePathExists(filePath)
    return if (isFilePathExists) Uri.fromFile(getFilePath(filePath)) else null
}