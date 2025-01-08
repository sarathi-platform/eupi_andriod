package com.nudge.core.model

data class FilterUiModel(
    val type: FilterType,
    val filterTitle: String,
    val imageFileName: String?
) {

    companion object {

        fun getAllFilter(title: String, imageFileName: String?): FilterUiModel {
            return FilterUiModel(FilterType.ALL, title, imageFileName)
        }

        fun getGeneralFilter(title: String, imageFileName: String?): FilterUiModel {
            return FilterUiModel(FilterType.GENERAL, title, imageFileName)
        }

    }

}

sealed class FilterType {
    object ALL : FilterType()
    object GENERAL : FilterType()
    data class OTHER(val filterValue: Any) : FilterType()
}
