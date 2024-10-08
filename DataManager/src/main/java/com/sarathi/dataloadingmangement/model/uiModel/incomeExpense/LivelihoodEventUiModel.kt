package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

data class LivelihoodEventUiModel(
    val name: String,
    val eventType: String,
    val id: Int,
    val livelihoodId: Int,
    val originalName: String,
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