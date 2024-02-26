package com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository

import com.nrlm.baselinesurvey.model.datamodel.CasteModel

interface CasteListRepository {

    fun getCasteList(): List<CasteModel>

    fun getSelectedLanguage(): Int
}