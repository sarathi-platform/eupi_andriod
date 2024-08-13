package com.nudge.core.compression

import android.content.Context
import android.net.Uri
import com.nudge.core.utils.CoreLogger

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


object ZipManager {
    private const val BUFFER_SIZE = 6 * 1024

    @Throws(IOException::class)
    fun zip(files: List<Pair<String, Uri?>>, zipFile: Uri?, context: Context) {
        var origin: BufferedInputStream? = null
        val out = ZipOutputStream(
            BufferedOutputStream(
                context.contentResolver.openOutputStream(
                    zipFile!!,
                    "wa"
                )
            )
        )
        try {
            val data = ByteArray(BUFFER_SIZE)
            for (i in files.indices) {
                try {
                val fi = (context.contentResolver.openInputStream(files[i].second!!))
                origin = BufferedInputStream(fi, BUFFER_SIZE)

                    val entry = ZipEntry(files[i].first)
                    out.putNextEntry(entry)
                    var count: Int
                    while (origin.read(data, 0, BUFFER_SIZE).also { count = it } != -1) {
                        out.write(data, 0, count)
                    }
                } catch (exception: Exception) {
                    CoreLogger.e(
                        context,
                        "ZipManager",
                        "${files[i].second?.path} ${exception.printStackTrace()}"
                    )
                } finally {
                    origin?.close()
                }
            }
        } finally {
            out.close()
        }
    }
}