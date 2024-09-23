package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

import androidx.room.TypeConverters
import com.sarathi.dataloadingmangement.data.converters.ValidationConverter
import com.sarathi.dataloadingmangement.model.response.Validation

data class ProductAssetUiModel(
    val name: String,
    val originalName: String,
    val id: Int,
    @TypeConverters(ValidationConverter::class)
    val validation: Validation?
)