package com.patsurvey.nudge.repository

import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.VillageListDao
import javax.inject.Inject

class ConfigRepositoryLocal @Inject constructor(
    private val villageDao: VillageListDao
) {
    fun insertVillage(village: VillageEntity) = villageDao.insertVillage(village)
    fun insertVillageList(villageList: List<VillageEntity>) = villageDao.insertAll(villageList)
    fun getAllVillages() = villageDao.getAllVillages()
    fun getVillage(id: Int) = villageDao.getVillage(id)

}