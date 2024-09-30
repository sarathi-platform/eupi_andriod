package com.patsurvey.nudge.activities.settings.domain.use_case

import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity

class GetAllPoorDidiForVillageUseCase(
    private val repository: SettingBSRepository
) {
    suspend fun getAllPoorDidiForVillage(villageId:Int):List<DidiEntity>{
        return repository.getAllPoorDidiForVillage(villageId)
    }

    suspend fun getAllDidiForVillage(villageId: Int):List<DidiEntity>{
        return repository.getAllDidiForVillage(villageId)
    }

    suspend fun getAllStepsForVillage(villageId: Int):List<StepListEntity>{
        return repository.getAllStepsForVillage(villageId)
    }
}