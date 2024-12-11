package com.nudge.core.utils

import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.nudge.core.BLANK_STRING
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.SummaryFileDto
import java.io.File
import java.io.FileWriter

object FileManager {

    private val TAG = FileManager::class.java.simpleName

    fun getDirectory(mobileNo: String, fileType: FileType): File {
        val context = CoreAppDetails.getApplicationContext()
        return when (fileType) {
            FileType.DOCUMENTS -> {
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                    "$SARATHI_DIRECTORY_NAME/$mobileNo"
                )
            }

            else -> {
                // Check for Image directory path.
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DCIM),
                    "$SARATHI_DIRECTORY_NAME/$mobileNo"
                )
            }
        }

    }

    fun createFile(
        fileNameWithExtension: String,
        fileDirectory: File,
    ): File? {
        val context = CoreAppDetails.getApplicationContext()

        try {

            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs()
            }

            return File(fileDirectory, fileNameWithExtension)

        } catch (ex: Exception) {
            CoreLogger.e(
                context,
                TAG,
                "createFile -> exception: ${ex.message}",
                ex,
                true
            )
            return null
        }
    }

    fun createFile(
        fileNameWithoutExtension: String,
        fileNameWithExtension: String,
        fileDirectory: File,
    ): Pair<String, Uri> {

        val fileToBeCreated = createFile(fileNameWithExtension, fileDirectory)

        return fileToBeCreated?.let {
            Pair(fileNameWithoutExtension, it.toUri())
        } ?: Pair(fileNameWithoutExtension, Uri.EMPTY)

    }

    fun getFileUri(fileNameWithoutExtension: String, file: File): Pair<String, Uri> {
        return Pair(fileNameWithoutExtension, file.toUri())
    }

    fun <T> writeToFile(
        fileNameWithoutExtension: String,
        file: File?,
        content: List<T>,
        insertBlankRowForEmptyEntry: Boolean
    ): Pair<String, Uri> {
        val context = CoreAppDetails.getApplicationContext()
        var writerStream: FileWriter? = null

        if (file == null)
            return Pair(fileNameWithoutExtension, Uri.EMPTY)

        try {

            writerStream = FileWriter(file, true)

            if (content.isEmpty())
                return Pair(fileNameWithoutExtension, Uri.EMPTY)


            val first = content.firstOrNull() ?: return Pair(fileNameWithoutExtension, Uri.EMPTY)

            when (first) {
                is SummaryFileDto -> {
                    (content as List<SummaryFileDto>).forEach {
                        if (insertBlankRowForEmptyEntry && it.summaryKey == BLANK_STRING && it.summaryCount == BLANK_STRING) {
                            writerStream.appendLine(BLANK_STRING)
                            writerStream.appendLine(BLANK_STRING)
                        } else {
                            var data = it.summaryKey
                            if (it.summaryCount != BLANK_STRING)
                                data = data + ": " + it.summaryCount
                            writerStream.appendLine(data)
                        }
                    }
                }

                else -> {
                    (content as List<String>).forEach {
                        writerStream.appendLine(it)
                    }
                }
            }

            writerStream?.close()

            return Pair(fileNameWithoutExtension, file.toUri())

        } catch (ex: Exception) {
            CoreLogger.e(
                context,
                TAG,
                "writeToFile -> exception: ${ex.message}",
                ex,
                true
            )
            return Pair(fileNameWithoutExtension, Uri.EMPTY)
        }
    }

    /*fun <T> writeToFile(file: File, content: T, insertBlankRowForEmptyEntry: Boolean): Pair<String, Uri> {
        val context = CoreAppDetails.getApplicationContext()
        var writerStream: FileWriter? = null
        try {

            writerStream = FileWriter(file, true)

            when(content) {
                is List<SummaryFileDto> -> {

                }
            }

        } catch (ex: Exception) {
            CoreLogger.e(
                context,
                TAG,
                "writeToFile -> exception: ${ex.message}",
                ex,
                true
            )
        }
    }*/

    fun deleteFile(
        fileNameWithExtension: String,
        fileDirectory: File
    ) {

        val context = CoreAppDetails.getApplicationContext()
        val fileToDelete = File(fileDirectory, fileNameWithExtension)
        try {

            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs()
            }

            if (fileToDelete.exists() && fileToDelete.isFile) {
                if (fileToDelete.delete()) {
                    CoreLogger.d(
                        context,
                        TAG,
                        "deleteFile -> file Deleted :" + fileToDelete.getPath()
                    );
                } else {
                    CoreLogger.d(
                        context,
                        TAG,
                        "deleteFile -> file not Deleted :" + fileToDelete.getPath()
                    );
                }
            }
        } catch (ex: Exception) {
            CoreLogger.e(
                context,
                TAG,
                "deleteFile -> file: ${fileToDelete.name}, exception: ${ex.message}",
                ex,
                true
            )
        }

    }

}

enum class FileType {

    DOCUMENTS,
    IMAGE;

}