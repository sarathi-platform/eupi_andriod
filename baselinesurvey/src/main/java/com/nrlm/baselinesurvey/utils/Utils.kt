package com.nrlm.baselinesurvey.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_CODE
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_LOCAL_NAME
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_NAME
import com.nrlm.baselinesurvey.MainActivity
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

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

fun getAuthImageFileNameFromURL(url: String): String{
    return url.substring(url.lastIndexOf('=') + 1, url.length)
}

fun getFileNameFromURL(url: String): String{
    return url.substring(url.lastIndexOf('/') + 1, url.length)
}

fun getImagePath(context: Context, imagePath:String): File {
    val imageName = getFileNameFromURL(imagePath)
    return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${imageName}")
}

fun getDefaultLanguage(): LanguageEntity {
    return LanguageEntity(
        id = DEFAULT_LANGUAGE_ID,
        language = DEFAULT_LANGUAGE_NAME,
        langCode = DEFAULT_LANGUAGE_CODE,
        orderNumber = 1,
        localName = DEFAULT_LANGUAGE_LOCAL_NAME
    )
}
fun showCustomToast(
    context: Context?,
    msg: String){
    Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
}

fun onlyNumberField(value:String):Boolean{
    if(value.isDigitsOnly() && value != "_" && value != "N"){
        return true
    }
    return false
}

fun changeMilliDateToDate(millDate:Long):String?{
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    return dateFormat.format(millDate)
}

fun longToString(value:Long):String{
    return try {
        value.toString()
    }catch (ex:Exception){
        BLANK_STRING
    }
}

fun intToString(value:Int):String{
    return try {
        value.toString()
    }catch (ex:Exception){
        BLANK_STRING
    }
}

fun stringToInt(string: String):Int{
    var intValue=0
    if(string!=null){
        intValue = if(string.isEmpty())
            0
        else string.toInt()
    }
    return intValue
}

fun setKeyboardToPan(context: MainActivity) {
    context.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
}

fun setKeyboardToReadjust(context: MainActivity) {
    context.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
}
