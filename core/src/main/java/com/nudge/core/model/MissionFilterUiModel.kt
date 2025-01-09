package com.nudge.core.model

data class FilterUiModel(
    val type: FilterType,
    val filterValue: String,
    val filterLabel: String,
    val imageFileName: String?
) {

    companion object {

        fun getAllFilter(
            filterValue: String,
            filterLabel: String,
            imageFileName: String?
        ): FilterUiModel {
            return FilterUiModel(
                type = FilterType.ALL,
                filterValue = filterValue,
                filterLabel = filterLabel,
                imageFileName = imageFileName
            )
        }

        fun getGeneralFilter(
            filterValue: String,
            filterLabel: String,
            imageFileName: String?
        ): FilterUiModel {
            return FilterUiModel(
                type = FilterType.GENERAL,
                filterValue = filterValue,
                filterLabel = filterLabel,
                imageFileName = imageFileName
            )
        }

    }

}

sealed class FilterType {
    object ALL : FilterType()
    object GENERAL : FilterType()
    data class OTHER(val filterValue: Any) : FilterType()
}
