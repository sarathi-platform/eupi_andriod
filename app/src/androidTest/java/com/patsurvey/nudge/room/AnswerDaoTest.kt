package com.patsurvey.nudge.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.patsurvey.nudge.database.NudgeDatabase
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.utils.BLANK_STRING
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AnswerDaoTest {
    private lateinit var nudgeDatabase: NudgeDatabase
    private lateinit var answerDao: AnswerDao

    @Before
    fun setup() {
        nudgeDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), NudgeDatabase::class.java
        ).allowMainThreadQueries().build()
        answerDao = nudgeDatabase.answerDao()
    }

    @Test
    fun insertAnswer() {
        insertQuery()
        val result = answerDao.getAllAnswer()
        Assert.assertEquals(1, result.size)
    }

    @Test
    fun getAllAnswerForDidi() {
        insertQuery()
        val result = answerDao.getAllAnswerForDidi(1)
        Assert.assertEquals(1, result.size)
    }

    @Test
    fun getAnswerForDidi() {
        insertQuery()
        val result = answerDao.getAnswerForDidi(BLANK_STRING, 1)
        Assert.assertEquals(1, result.size)
    }

    @Test
    fun isAlreadyAnswered() {
        insertQuery()
        val result = answerDao.isAlreadyAnswered(1, 1, BLANK_STRING)
        Assert.assertEquals(1, result)
    }

    @Test
    fun updateAnswer() {
        insertQuery()
        answerDao.updateAnswer(
            1,
            1,
            1,
            BLANK_STRING,
            1,
            0,
            BLANK_STRING,
            BLANK_STRING,
            1.0,
            BLANK_STRING,
            BLANK_STRING,
            BLANK_STRING
        )
        val result = answerDao.getAllAnswer()
        Assert.assertEquals(1, result.size)
    }

    @Test
    fun deleteAnswerTable() {
        insertQuery()
        answerDao.deleteAnswerTable()
        val result = answerDao.getAllAnswer()
        Assert.assertEquals(0, result.size)
    }

    @After
    fun tearDown() {
        nudgeDatabase.close()
    }

    private fun insertQuery() {
        answerDao.insertAnswer(
            SectionAnswerEntity(
                id = 0,
                optionId = 1,
                didiId = 1,
                optionValue = 1,
                answerValue = BLANK_STRING,
                questionId = 1,
                actionType = BLANK_STRING,
                totalAssetAmount = 1.0,
                type = BLANK_STRING,
                summary = BLANK_STRING,
                villageId = 1,
                weight = 0,
                assetAmount = BLANK_STRING,
                questionFlag = BLANK_STRING
            )
        )
    }
}