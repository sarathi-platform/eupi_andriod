package com.patsurvey.nudge.activities.ui.progress.domain.repository.impls

import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchPatQuestionRepository
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import javax.inject.Inject

class FetchPatQuestionRepositoryImpl @Inject constructor(
    private val languageListDao: LanguageListDao,
    private val questionListDao: QuestionListDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : FetchPatQuestionRepository {


}