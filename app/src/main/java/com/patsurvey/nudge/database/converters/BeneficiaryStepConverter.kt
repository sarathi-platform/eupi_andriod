package com.patsurvey.nudge.database.converters

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class BeneficiaryStepConverter {

    @TypeConverter
    fun fromBeneficiaryProcessStatus(list: List<BeneficiaryProcessStatusModel>): String {
        val type: Type = object : TypeToken<List<BeneficiaryProcessStatusModel?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toBeneficiaryProcessStatus(listInString: String): List<BeneficiaryProcessStatusModel> {
        val type =
            object : TypeToken<List<BeneficiaryProcessStatusModel?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }

 }
