package com.patsurvey.nudge.activities.ui.progress.domain.repository

import com.nudge.core.preference.CorePrefRepo
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import javax.inject.Inject

class FetchCasteListRepositoryImpl @Inject constructor(
    private val languageListDao: LanguageListDao,
    private val casteListDao: CasteListDao,
    private val corePrefRepo: CorePrefRepo
) : FetchCasteListRepository {

}