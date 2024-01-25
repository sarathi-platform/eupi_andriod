package com.nrlm.baselinesurvey.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nrlm.baselinesurvey.model.datamodel.BeneficiaryProcessStatusModel
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
