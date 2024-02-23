package com.nrlm.baselinesurvey.ui.language.domain.use_case

import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepository

class GetLanguageScreenOpenFromUserCase(private val repository:LanguageScreenRepository) {

    operator fun invoke():Boolean{
        return repository.getLanguageScreenOpenFrom()
    }
}