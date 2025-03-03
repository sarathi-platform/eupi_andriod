package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.mat.response.MoneyJournalConfigResponse
import java.lang.reflect.Type

class MoneyJournalConfigResponseConverter {
    @TypeConverter
    fun fromMoneyJournalConfigResponse(moneyJournalConfigResponse: MoneyJournalConfigResponse?): String {
        if (moneyJournalConfigResponse == null)
            return BLANK_STRING
        val type: Type = object : TypeToken<MoneyJournalConfigResponse?>() {}.type
        return Gson().toJson(moneyJournalConfigResponse, type)
    }

    @TypeConverter
    fun toMoneyJournalConfigResponse(moneyJournalConfigResponseInString: String?): MoneyJournalConfigResponse? {
        if (moneyJournalConfigResponseInString == null || moneyJournalConfigResponseInString.equals(
                "null",
                false
            )
        )
            return null
        val type =
            object : TypeToken<MoneyJournalConfigResponse?>() {}.type
        return Gson().fromJson(moneyJournalConfigResponseInString, type)
    }
}