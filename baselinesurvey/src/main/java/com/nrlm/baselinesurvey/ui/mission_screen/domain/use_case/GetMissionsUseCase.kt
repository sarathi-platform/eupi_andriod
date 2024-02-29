//package com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case
//
//import com.nrlm.baselinesurvey.model.response.MissionResponseModel
//import com.nrlm.baselinesurvey.ui.mission_screen.domain.repository.MissionScreenRepository
//import javax.inject.Inject
//
//class GetMissionsUseCase @Inject constructor(private val repository: MissionScreenRepository) {
//    suspend operator fun invoke(): List<MissionResponseModel>? {
//        return repository.getMissions()
//    }
//}