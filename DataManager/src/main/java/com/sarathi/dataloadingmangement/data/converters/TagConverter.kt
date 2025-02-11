package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.DELEGATE_COMM

class TagConverter {
    @TypeConverter
    fun toTagList(tagList: String?): List<Int> {
        if (tagList == null) {
            return emptyList<Int>()
        }
        return tagList.split(DELEGATE_COMM).map { it.toInt() }
    }

    @TypeConverter
    fun fromTagList(tagList: List<Int>?): String? {
        if (tagList.isNullOrEmpty()) {
            return null
        }

        val gson = Gson()
        val type = object :
            TypeToken<List<Int?>?>() {}.type
        return gson.toJson(tagList, type)
    }
}