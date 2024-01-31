package com.patsurvey.nudge.activities.survey

import android.net.Uri
import com.nudge.core.enums.EventFormatterName
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventswriter.EventWriterFactory
import com.nudge.core.eventswriter.IEventFormatter
import com.nudge.core.eventswriter.entities.EventV1
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.NudgeCore
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class PatDidiSummaryRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val answerDao: AnswerDao,
    val apiService: ApiService,
    val casteListDao: CasteListDao,
    val stepsListDao: StepsListDao
) :BaseRepository() {

    fun getAppLanguageId(): Int? {
        return prefRepo.getAppLanguageId()
    }

    fun getAllCasteForLanguage(): List<CasteEntity>? {
        return casteListDao.getAllCasteForLanguage(prefRepo.getAppLanguageId() ?: 2)
    }

    fun saveDidiLocalImagePath(finalPathWithCoordinates: String, didiId: Int) {
        didiDao.saveLocalImagePath(path = finalPathWithCoordinates, didiId = didiId)
    }

    fun updateDidiSHGFlag(didiId: Int, shgFlag: Int) {
        didiDao.updateDidiShgStatus(didiId = didiId, shgFlag = shgFlag)
    }
    fun updateDidiAbleBodiedFlag(didiId:Int, ableBodiedFlag:Int){
        didiDao.updateDidiAbleBodiedStatus(didiId = didiId, ableBodiedFlag = ableBodiedFlag)
    }
    suspend fun uploadDidiImage(image: MultipartBody.Part,didiId: RequestBody,location:RequestBody,userType:RequestBody): ApiResponseModel<Object> {
        return apiService.uploadDidiImage(didiId =  didiId, image = image, location = location, userType = userType)
    }

    fun updateNeedToPostImage(didiId: Int,needsToPostImage:Boolean){
        didiDao.updateNeedsToPostImage(didiId, needsToPostImage)
    }


}