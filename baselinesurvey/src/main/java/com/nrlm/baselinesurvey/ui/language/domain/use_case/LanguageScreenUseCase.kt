package com.nrlm.baselinesurvey.ui.language.domain.use_case

data class LanguageScreenUseCase(
    val getLanguageListFromDbUseCase: GetLanguageListFromDbUseCase,
    val getSelectedVillageUseCase: GetSelectedVillageUseCase,
    val saveSelectedVillageUseCase: SaveSelectedVillageUseCase,
    val getVillageDetailUseCase: GetVillageDetailUseCase,
    val saveSelectedLanguageUseCase: SaveSelectedLanguageUseCase,
)