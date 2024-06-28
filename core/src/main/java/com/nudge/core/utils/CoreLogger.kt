package com.nudge.core.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.text.format.Formatter.formatShortFileSize
import android.util.Log
import android.util.Log.e
import com.nudge.core.BuildConfig.DEBUG
import com.nudge.core.model.CoreAppDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FilenameFilter
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level

object CoreLogger {

    fun d(context: Context = CoreAppDetails.getContext()!!, tag: String, msg: String) {
        CoroutineScope(Dispatchers.IO).launch {
            LogWriter.log(context, Level.FINE.intValue(), tag, msg)
            Log.d(tag, msg)
        }
    }

    fun i(context: Context = CoreAppDetails.getContext()!!, tag: String, msg: String) {
        CoroutineScope(Dispatchers.IO).launch {
            LogWriter.log(context, Level.INFO.intValue(), tag, msg)
            if (DEBUG) Log.i(tag, msg)
        }
    }

    fun e(context: Context, tag: String, msg: String) {
        CoroutineScope(Dispatchers.IO).launch {
            LogWriter.log(context, Level.SEVERE.intValue(), tag, msg)
            if (DEBUG) Log.e(tag, msg)
        }
    }

    fun e(
        context: Context = CoreAppDetails.getContext()!!,
        tag: String,
        msg: String,
        ex: Throwable?,
        stackTrace: Boolean = DEBUG,
        lineCount: Int = 60
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            var trace = ""
            if (stackTrace) {
                trace = " STACKTRACE\n"
                var line = 0
                ex?.stackTrace?.forEach {
                    line = line.inc()
                    if (line >= lineCount) return@forEach
                    trace += "${it.className}(${it.fileName}:${it.lineNumber})}\n"
                }
            }
            e(context, tag, "$msg${execeptionStr(ex)}$trace")
        }
    }

    fun execeptionStr(ex: Throwable?): String {
        return if (ex != null) " ${ex.javaClass.simpleName}${if (ex.message != null) ": \"${ex.message}\"" else ""}" else ""
    }

    fun cleanup(context: Context, checkForSize: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            LogWriter.cleanup(context, checkForSize)
        }
    }

}

class EchoPacket(
    val level: Int,
    val tag: String,
    val message: String = "",
    val timestamp: Date = Date()
)

object LogWriter {
    private const val TAG = "syslog"
    //private const val TAG_WIDTH_MAX = 75

    private const val FILE_NAME_PREFIX = "syslog_"
    private const val FILE_NAME_SUFFIX = ".log"

    // Reduced logging file size from 10 MB to 1 MB as per SC-194 (Point no. 2 in description)
    private val SUPPORT_LOG_SIZE_MAX =
        (if (DEBUG) 20L else 10L) * 1024 * 1024 // collect up to 10 MB of log files // 20MB for debug builds
    private const val SUPPORT_LOG_FILE_NAME_PREFIX = "nudge_log_"
    private const val SUPPORT_LOG_FILE_NAME_SUFFIX = ".txt"

    private val LOG_SEPARATOR_DOUBLE = "=".repeat(256)

    private var isInitializing = AtomicBoolean()
    private var isRunning = false
    private var isQuitting = false

    //private var syslog: Any? = null
    private var syslogThread: Thread? = null
    private var syslogFile: File? = null
    private var syslogStream: FileOutputStream? = null
    private var syslogQueue: ArrayBlockingQueue<EchoPacket>? = null

    private val syslogFileNameTimeStampFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_", Locale.US)
    private val syslogMessageTimeStampFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    private var tagDevice: String? = null

    private val syslogFileFilter = FilenameFilter { dir, name ->
        name != null && name.startsWith(FILE_NAME_PREFIX) && name.endsWith(FILE_NAME_SUFFIX)
    }

    private val stitcherlogFileFilter = FilenameFilter { dir, name ->
        name != null && name.startsWith(SUPPORT_LOG_FILE_NAME_PREFIX)
    }


    init {
        tagDevice = "${Build.SERIAL} ${Build.MODEL}"
        syslogQueue = ArrayBlockingQueue(8192)
    }

    fun log(context: Context, level: Int, tag: String, message: String) {
        try {
            if (isInitializing.compareAndSet(
                    false,
                    true
                ) && !isQuitting && (syslogThread == null || !syslogThread!!.isAlive)
            ) {
                Log.i(TAG, "initializing logs...")
                syslogFile = File.createTempFile(
                    FILE_NAME_PREFIX + syslogFileNameTimeStampFormat.format(
                        Date()
                    ),
                    FILE_NAME_SUFFIX,
                    context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                )
                syslogStream = FileOutputStream(syslogFile!!, true)

                syslogThread = Thread {
                    try {
                        isInitializing.set(false)
                        isQuitting = false
                        isRunning = true
                        do {
                            var packet: EchoPacket?
                            try {
                                packet = syslogQueue?.take()
                                //if (packet instanceof QuitPacket) throw new InterruptedException(packet.message)
                                if (packet != null) log(packet)
                            } catch (ex: Exception) {
                                Log.e(TAG, "log process packet exception", ex)
                            }
                        } while (isRunning)
                    } catch (ex: Exception) {
                        Log.e(TAG, "log thread", ex)
                    } finally {
                        if (syslogStream != null) try {
                            syslogStream!!.close()
                        } catch (ignore: Exception) {
                        } finally {
                            syslogStream = null
                        }
                        if (syslogFile != null && syslogFile!!.length() < 32) try {
                            syslogFile?.delete()
                        } catch (ignore: Exception) {
                        } finally {
                            syslogFile = null
                        }
                        syslogQueue = null
                        syslogThread = null
                        isQuitting = false
                    }
                }

                syslogThread?.priority = Thread.NORM_PRIORITY - 1
                syslogThread?.start()

//                emitPreamble()
            }

            if (!isQuitting && syslogQueue != null && !Thread.currentThread().isInterrupted) {
                syslogQueue?.offer(EchoPacket(level, tag, message), 1234L, TimeUnit.MILLISECONDS)
            } else {
                log(context, level, tag, message)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "log", ex)
        }
    }

    private fun log(packet: EchoPacket) {
        synchronized(TAG) {
            try {
                val bytes =
                    "${syslogMessageTimeStampFormat.format(packet.timestamp)} ${packet.tag} ${packet.message}\n".toByteArray()
                syslogStream?.write(bytes, 0, bytes.size)
            } catch (fault: Throwable) {
                Log.println(packet.level, packet.tag, "${packet.message} exception: $fault")
            }
        }
    }

    private suspend fun getSyslogFile(
        context: Context,
        output: File,
        after: String = "",
        logFileNames: ArrayList<String>? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val logDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (logDir != null) {
                val catalog: Array<File> = logDir.listFiles(syslogFileFilter) as Array<File>
                if (catalog.isEmpty())
                    return@withContext false
                catalog.sortWith { f1, f2 -> f2.name.compareTo(f1.name) }
                val logs = ArrayList<File>()
                val logFile = File(
                    logDir, SUPPORT_LOG_FILE_NAME_PREFIX + SimpleDateFormat(
                        "yyyy_MM_dd_HH_mm_ss",
                        Locale.US
                    ).format(Date()) + SUPPORT_LOG_FILE_NAME_SUFFIX
                )
                if (logFile.isFile) logFile.delete()

                var total = 0L
                for (file in catalog) {
                    val length = file.length()
                    if (length == 0L) continue // skip empty log files

                    if (after.isNotEmpty() && file.name <= after)
                        continue

                    total += length
                    if (total > SUPPORT_LOG_SIZE_MAX && logs.size >= 2) {
                        break //make sure we log the 2nd most recent log file too, even if it's truncated
                    }
                    logs.add(file)
                    logFileNames?.add(file.name)
                }
                if (logs.isEmpty() && catalog.isNotEmpty())
                    logs.add(catalog[0])

                if (logs.isEmpty())
                    return@withContext false
                try {
                    var totalBytes = 0
                    for (file in logs) {
                        if (file.length() == 0L)
                            continue

                        output.appendBytes("\n\nFile Name: ${file.name}\n".toByteArray())
                        val inputStream =
                            withContext(Dispatchers.IO) {
                                FileInputStream(file.absolutePath)
                            }
                        val inputStreamReader = InputStreamReader(inputStream)
                        val buffReader = BufferedReader(inputStreamReader)
                        try {
                            var bytesToSkipAtStartOfLogFile =
                                file.length() - (SUPPORT_LOG_SIZE_MAX - totalBytes)
                            /*if (bytesToSkipAtStartOfLogFile > 0) { //For a long log file just log the preamble and then the end of the log file.
                                val preamble = getPreamble()
                                for(log in preamble) {
                                    val preambleLog = "$log\n"
                                    output.appendBytes(preambleLog.toByteArray())
                                    totalBytes += preambleLog.length
                                }
                            }*/

                            var line: String? =
                                withContext(Dispatchers.IO) {
                                    buffReader.readLine()
                                }
                            while (line != null) {

                                if (bytesToSkipAtStartOfLogFile <= 0) {
                                    line += "\n"
                                    output.appendBytes(line.toByteArray())
                                    totalBytes += line.length
                                } else {
                                    bytesToSkipAtStartOfLogFile -= line.length //skip the start of a long log file
                                }

                                if (totalBytes > SUPPORT_LOG_SIZE_MAX)
                                    break

                                line = buffReader.readLine()

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            try {
                                inputStreamReader.close()
                                inputStream.close()
                            } catch (ex: Exception) {
                                e(TAG, "getSyslogFile close", ex)
                            }
                        }

                        if (totalBytes > SUPPORT_LOG_SIZE_MAX)
                            break
                    }
                    return@withContext true
                } catch (fault: Exception) {
                    Log.println(Log.ERROR, TAG, fault.toString())
                }
            }

        } catch (ex: Exception) {
            e(TAG, "getSyslogFile", ex)
        }
        return@withContext false
    }
    fun getSupportLogFileName(): String {
        return SUPPORT_LOG_FILE_NAME_PREFIX + SimpleDateFormat(
            "yyyy_MM_dd_HH_mm_ss",
            Locale.US
        ).format(
            Date()
        ) + SUPPORT_LOG_FILE_NAME_SUFFIX
    }

    fun cleanup(context: Context, checkForSize: Boolean) {
        try {
            if (DEBUG) Log.d(TAG, "cleanup checkForSize: $checkForSize")
            val catalog: MutableList<File> = ArrayList()

            val logDir = context.cacheDir

            try {
                catalog.addAll(logDir.listFiles(stitcherlogFileFilter))
                catalog.addAll(logDir.listFiles(syslogFileFilter))
            } catch (ex: Exception) {
                e(TAG, "cleanup listFiles", ex)
            }

            if (catalog.isEmpty()) return

            if (checkForSize) {
                catalog.sortWith(Comparator { o1, o2 ->
                    o2.name.compareTo(o1.name) //stitcher_log files are sorted last so will always be deleted
                })
                var bytesOfLogFiles = 0L
                var removedBytes = 0L
                var fileIndex = 0
                for (logFile in catalog) {
                    if (logFile.isFile) {
                        val length = logFile.length()
                        bytesOfLogFiles += length
                        if (fileIndex++ < 2) continue
                        //delete oldest files where the newer files total > SUPPORT_LOG_SIZE_MAX, except for the current file
                        if (logFile.name.contains(SUPPORT_LOG_FILE_NAME_PREFIX) ||
                            (bytesOfLogFiles > SUPPORT_LOG_SIZE_MAX && !TextUtils.equals(
                                syslogFile?.name,
                                logFile.name
                            ))
                        ) {

                            logFile.delete()
                            // Added logging for deleting log files
                            if (DEBUG) CoreLogger.d(
                                context,
                                TAG,
                                "Deleted logfile ${logFile.name} of size ${
                                    formatShortFileSize(
                                        context,
                                        length
                                    )
                                }"
                            )
                            removedBytes += length
                        }
                    }
                }

                CoreLogger.d(
                    context,
                    TAG,
                    "Logs on Disk: ${
                        formatShortFileSize(
                            context,
                            bytesOfLogFiles - removedBytes
                        )
                    } Cleanup Removed: ${formatShortFileSize(context, removedBytes)}"
                )
            }
        } catch (ex: Exception) {
            e(TAG, "cleanup $checkForSize", ex)
        }
    }

    suspend fun buildLogFile(appContext: Context,onFailed:()->Unit): File? {
        try {

            val logDir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val logFile = File(logDir, getSupportLogFileName())
            if (logFile.isFile) logFile.delete()

            if (!getSyslogFile(context = appContext, output = logFile)) {
                withContext(Dispatchers.Main) {
                    onFailed()
                }
                return null
            }
            return logFile
        } catch (ex: Exception) {
            e(TAG, "buildSupportLogAndShare", ex)
            withContext(Dispatchers.Main) {
                onFailed()
            }
            return null
        }
    }

}