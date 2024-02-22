package com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_CASTE_LIST
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.model.datamodel.CasteModel
import javax.inject.Inject

class CasteListRepositoryImpl @Inject constructor(
    val prefRepo: PrefRepo
): CasteListRepository  {
    override fun getCasteList(): List<CasteModel> {
        val casteList = prefRepo.getPref(PREF_CASTE_LIST, BLANK_STRING)
        return if((casteList?.isEmpty() == true) || casteList.equals("[]")) emptyList()
        else{
            Gson().fromJson(casteList, object : TypeToken<List<CasteModel>>() {}.type)
        }
    }
}