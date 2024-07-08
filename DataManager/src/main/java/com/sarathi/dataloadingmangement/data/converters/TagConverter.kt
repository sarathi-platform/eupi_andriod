package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.sarathi.dataloadingmangement.DELEGATE_COMM

class TagConverter {
    @TypeConverter
    fun toTagList(tagList: String?): List<Int> {
        if (tagList == null) {
            return emptyList<Int>()
        }
        return tagList.split(DELEGATE_COMM).map { it.toInt() }
    }
}