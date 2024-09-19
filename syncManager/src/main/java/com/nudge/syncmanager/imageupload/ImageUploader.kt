package com.nudge.syncmanager.imageupload

interface ImageUploader {
    suspend fun  uploadImage(filePath:String,fileName:String):String
}