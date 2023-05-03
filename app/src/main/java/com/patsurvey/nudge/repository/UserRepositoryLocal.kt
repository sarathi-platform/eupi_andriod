package com.patsurvey.nudge.repository

import com.patsurvey.nudge.database.UserEntity
import com.patsurvey.nudge.database.dao.UserDao
import javax.inject.Inject

class UserRepositoryLocal @Inject constructor(
    private val userDao: UserDao
) {
    fun getUserDetails() = userDao.getUserDetail()
    fun insertUser(user: UserEntity) = userDao.insertUser(user)
}