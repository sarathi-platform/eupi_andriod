package com.patsurvey.nudge.domain.usecases.tola

import com.patsurvey.nudge.activities.ui.transect_walk.TransectWalkRepository
import javax.inject.Inject

class UpdateTolaUseCase @Inject constructor(
    private val transectWalkRepository: TransectWalkRepository
) {

    suspend operator fun invoke(
        id: Int,
        name: String,
        lat: Double,
        longitude: Double,
        villageId: Int
    ) {
        transectWalkRepository.updateTola(
            id = id,
            name = name,
            lat = lat,
            longitude = longitude,
            villageId = villageId
        )
    }
}