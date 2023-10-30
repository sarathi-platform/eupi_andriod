package com.nrlm.baselinesurvey.ui.question_screen.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.firstSampleList
import com.nrlm.baselinesurvey.utils.secondSampleList
import javax.inject.Inject

class QuestionScreenRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService
): QuestionScreenRepository {


    override fun getSections(sectionId: Int): Sections {
        val sectionList = firstSampleList
//        val sectionList = secondSampleList
        return sectionList[sectionList.map { it.sectionId }.indexOf(sectionId)]
    }


}