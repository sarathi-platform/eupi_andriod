package com.nrlm.baselinesurvey.ui.language.use_case

import com.nrlm.baselinesurvey.ui.language.repository.LanguageScreenRepository
import javax.inject.Inject

data class LanguageScreenUseCase(
    val getLanguageListFromDbUseCase: GetLanguageListFromDbUseCase,
    val getSelectedVillageUseCase: GetSelectedVillageUseCase,
    val saveSelectedVillageUseCase: SaveSelectedVillageUseCase,
    val getVillageDetailUseCase: GetVillageDetailUseCase,
    val saveSelectedLanguageUseCase: SaveSelectedLanguageUseCase,
)