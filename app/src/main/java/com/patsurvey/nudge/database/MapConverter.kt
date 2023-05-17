package com.patsurvey.nudge.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class  MapConverter {
        @TypeConverter
        fun fromString(value: String): Map<Int, Boolean> {
            val mapType = object : TypeToken<Map<Int, Boolean>>() {}.type
            return Gson().fromJson(value, mapType)
        }
        @TypeConverter
        fun fromStringMap(map: Map<Int, Boolean>): String {
            val gson = Gson()
            return gson.toJson(map)
        }
}