package com.patsurvey.nudge.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.UserEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.utils.DIDI_TABLE

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getUserDetail(): LiveData<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Query("DELETE from user_table")
    fun deleteAllUserDetail()

}