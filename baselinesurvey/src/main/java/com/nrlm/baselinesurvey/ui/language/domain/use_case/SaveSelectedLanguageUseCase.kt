package com.nrlm.baselinesurvey.ui.language.domain.use_case

import com.nrlm.baselinesurvey.activity.MainActivity
import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepository

class SaveSelectedLanguageUseCase(private val repository: LanguageScreenRepository) {

    fun saveSelectedLanguageId(languageId: Int) {
        repository.saveSelectedLanguageId(languageId)
    }

    fun saveSelectedLanguageCode(mainActivity: MainActivity, languageCode: String) {
        repository.saveSelectedLanguageCode(mainActivity, languageCode)
    }

}
