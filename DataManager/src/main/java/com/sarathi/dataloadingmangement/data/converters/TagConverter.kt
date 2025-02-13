package com.sarathi.dataloadingmangement.data.converters

import android.text.TextUtils
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.DELEGATE_COMM

class TagConverter {
    @TypeConverter
    fun fromIntListToJson(intList: List<Int?>?): String? {
        if (intList == null) {
            return null
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<Int?>?>() {}.type
        return gson.toJson(intList, type)
    }

    @TypeConverter
    fun fromJsonToIntList(intList: String?): List<Int>? {
        if (TextUtils.isEmpty(intList)) {
            return emptyList()
        }
        if (intList!!.contains("[") && intList.contains("]")) {
        val gson = Gson()
        val type = object :
            TypeToken<List<Int?>?>() {}.type
        return gson.fromJson<List<Int>>(intList, type)
        }
        return intList.split(DELEGATE_COMM).map { it.toInt() }

    }
}