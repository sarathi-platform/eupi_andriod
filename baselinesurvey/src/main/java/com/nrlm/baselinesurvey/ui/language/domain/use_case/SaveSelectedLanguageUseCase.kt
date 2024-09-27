package com.nrlm.baselinesurvey.ui.language.domain.use_case

import android.content.Context
import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepository

class SaveSelectedLanguageUseCase(private val repository: LanguageScreenRepository) {

    fun saveSelectedLanguageId(languageId: Int) {
        repository.saveSelectedLanguageId(languageId)
    }

    fun saveSelectedLanguageCode(mainActivity: Context, languageCode: String) {
        repository.saveSelectedLanguageCode(mainActivity, languageCode)
    }

}
