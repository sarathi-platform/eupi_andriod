package com.patsurvey.nudge.download

import com.patsurvey.nudge.activities.video.VideoItem
import com.patsurvey.nudge.database.TrainingVideoEntity

interface Downloader {

    fun downloadFile(videoItem: TrainingVideoEntity, fileType: FileType): Long

}