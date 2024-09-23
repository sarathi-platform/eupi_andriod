package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

import androidx.room.TypeConverters
import com.sarathi.dataloadingmangement.data.converters.ValidationConverter
import com.sarathi.dataloadingmangement.model.response.Validation

data class LivelihoodEventUiModel(
    val name: String,
    val eventType: String,
    val id: Int,
    val livelihoodId: Int,
    val originalName: String,
    @TypeConverters(ValidationConverter::class)
    val validations: Validation?
)

fun List<LivelihoodEventUiModel>?.find(eventId: Int): LivelihoodEventUiModel? {

    if (eventId == -1)
        return null

    val index = this?.map { it.id }?.indexOf(eventId)
    index?.let {
        if (index == -1)
            return null

        return this?.get(it)

    } ?: return null

}