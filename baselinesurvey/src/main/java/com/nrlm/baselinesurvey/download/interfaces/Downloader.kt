package com.nrlm.baselinesurvey.download.interfaces

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.download.utils.FileType

interface Downloader {

//    fun downloadFile(videoItem: TrainingVideoEntity, fileType: FileType): Long
    fun downloadImageFile(imageUrl: String, fileType: FileType): Long
    fun downloadAuthorizedImageFile(imageUrl: String, fileType: FileType, prefRepo: PrefRepo): Long

}