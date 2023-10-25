package com.nrlm.baselinesurvey.ui.section_screen.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.firstSampleList
import com.nrlm.baselinesurvey.utils.secondSampleList

class SectionListScreenRepositoryImpl(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService
): SectionListScreenRepository {
    override fun getSectionsList(): List<Sections> {
        return firstSampleList
//        return secondSampleList
    }
}