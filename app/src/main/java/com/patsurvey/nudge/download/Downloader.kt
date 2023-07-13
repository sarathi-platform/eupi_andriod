package com.patsurvey.nudge.download

import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.TrainingVideoEntity

interface Downloader {

    fun downloadFile(videoItem: TrainingVideoEntity, fileType: FileType): Long
    fun downloadImageFile(imageUrl: String, fileType: FileType): Long
    fun downloadAuthorizedImageFile(imageUrl: String, fileType: FileType,prefRepo: PrefRepo): Long

}