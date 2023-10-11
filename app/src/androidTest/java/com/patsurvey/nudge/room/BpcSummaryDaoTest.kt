package com.patsurvey.nudge.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.NudgeDatabase
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BpcSummaryDaoTest {
    private lateinit var nudgeDatabase: NudgeDatabase
    private lateinit var bpcSummaryDao: BpcSummaryDao

    @Before
    fun setup() {
        nudgeDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NudgeDatabase::class.java
        ).allowMainThreadQueries().build()
        bpcSummaryDao = nudgeDatabase.bpcSummaryDao()
    }

    @Test
    fun insert() {
        insertQuery()
        val result = bpcSummaryDao.getBpcSummaryForAllVillage()
        Assert.assertEquals(1, result?.size)
    }

    @Test
    fun getBpcSummaryForVillage() {
        insertQuery()
        val result = bpcSummaryDao.getBpcSummaryForVillage(1)
        Assert.assertEquals(1, result?.villageId)
    }

    @Test
    fun getBpcSummaryForAllVillage() {
        insertQuery()
        val result = bpcSummaryDao.getBpcSummaryForAllVillage();
        Assert.assertEquals(101, result?.get(0)?.cohortCount ?: 0)
    }

    @Test
    fun deleteForVillage() {
        insertQuery()
        bpcSummaryDao.deleteForVillage(101)
        val result = bpcSummaryDao.getBpcSummaryForAllVillage()
        Assert.assertEquals(1, result?.size)
    }

    @Test
    fun deleteAllSummary() {
        insertQuery()
        bpcSummaryDao.deleteAllSummary()
        val result = bpcSummaryDao.getBpcSummaryForAllVillage()
        Assert.assertEquals(0, result?.size)
    }

    @After
    fun tearDown() {
        nudgeDatabase.close()
    }

    private fun insertQuery() {
        val bpcSummary = BpcSummaryEntity(
            cohortCount = 101,
            mobilisedCount = 1,
            poorDidiCount = 1,
            sentVoEndorsementCount = 1,
            voEndorsedCount = 1,
            villageId = 1
        )
        bpcSummaryDao.insert(bpcSummary)
    }
}