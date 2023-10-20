package com.nrlm.baselinesurvey.ui.language.use_case

import com.nrlm.baselinesurvey.MainActivity
import com.nrlm.baselinesurvey.ui.language.repository.LanguageScreenRepository

class SaveSelectedLanguageUseCase(private val repository: LanguageScreenRepository) {

    fun saveSelectedLanguageId(languageId: Int) {
        repository.saveSelectedLanguageId(languageId)
    }

    fun saveSelectedLanguageCode(mainActivity: MainActivity, languageCode: String) {
        repository.saveSelectedLanguageCode(mainActivity, languageCode)
    }

}
