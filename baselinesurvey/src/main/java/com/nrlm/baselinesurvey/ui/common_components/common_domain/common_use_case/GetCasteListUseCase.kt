package com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case

import com.nrlm.baselinesurvey.model.datamodel.CasteModel
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.CasteListRepository

class GetCasteListUseCase(private val repository: CasteListRepository) {

    operator fun invoke(): List<CasteModel> {
        val selectedLanguage = repository.getSelectedLanguage()
        return repository.getCasteList().filter { it.languageId == selectedLanguage }
    }

}
