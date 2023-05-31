package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.TrainingVideoEntity
import com.patsurvey.nudge.utils.TRAINING_VIDEO_TABLE

@Dao
interface TrainingVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(trainingVideos: List<TrainingVideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trainingVideo: TrainingVideoEntity)

    @Query("SELECT * from $TRAINING_VIDEO_TABLE")
    fun getVideoList(): List<TrainingVideoEntity>

    @Query("SELECT * from $TRAINING_VIDEO_TABLE where id = :id")
    fun getVideo(id: Int) : TrainingVideoEntity

    @Query("Update $TRAINING_VIDEO_TABLE set isDownload = 2 where id = :id")
    fun setVideoAsDownloaded(id: Int)

    @Query("Update $TRAINING_VIDEO_TABLE set isDownload = :videoStatus where id = :id")
    fun updateVideoDownloadStatus(id: Int, videoStatus: Int)

}