package com.sarathi.dataloadingmangement.domain.use_case

import javax.inject.Inject


class SearchScreenUseCase @Inject constructor(
    val getTaskUseCase: GetTaskUseCase,
    val getSectionListUseCase: GetSectionListUseCase,
    val fetchSurveyDataUseCase: FetchSurveyDataFromDB
)
